package tm.binding.registry

import grails.converters.JSON
import grails.converters.XML
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sun.security.x509.X500Name

import javax.servlet.ServletException

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.security.cert.CertificateFactory

@Transactional
@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN", "ROLE_USER"])
class SigningCertificatesController {

    // certificate valid period in years
    private static List<Integer> CERTIFICATE_VALID_PERIOD_INTERVALS = [5, 10]

    // key length
    private static List<Integer> KEY_LENGTH = [2048, 4096]

    def springSecurityService;

    def signingCertificateService

    def index() {
        redirect(action:'list')
    }

    def list(){
        log.debug("list -> ${params.name}")

        def signingCertificates = signingCertificateService.list(params.name)

        withFormat  {
            json {
                render signingCertificates as JSON
            }
        }
    }

    def administer() {
        log.debug("SigningCertificatesController::administer...")

        [certificateValidPeriodIntervalList: CERTIFICATE_VALID_PERIOD_INTERVALS, keyLengthList: KEY_LENGTH,]
    }

    def get()  {
        log.info("get -> ${params.id}")

        SigningCertificate cert = signingCertificateService.get(params.id)

        withFormat  {
            json {
                render cert as JSON
            }
        }
    }

    def add() {

        log.debug("add -> ${params.commonName}")

        SigningCertificate signingCertificate = signingCertificateService.add(params.commonName // 0
                , params.localityName // 1
                , params.stateName // 2
                , params.countryName // 3
                , params.emailAddress // 4
                , params.organizationName // 5
                , params.organizationUnitName // 6
                , params.validPeriod // 7
                , params.keyLength // 8
        )

        withFormat  {
            json {
                render signingCertificate as JSON
            }
        }
    }

    def view() {
        log.info("Viewing certificate: [${params.id}]...")

        // SigningCertificate domain object
        SigningCertificate cert = SigningCertificate.findById(params.id)
        if( !cert ) {
            log.info("cert == null...")
            throw new ServletException("No such certificate: ${params.id}")
        }

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509")

        // convert pem to certificate
        ByteArrayInputStream inStream = new ByteArrayInputStream(cert.x509CertificatePem.getBytes())
        X509Certificate x509Cert = (X509Certificate)certificateFactory.generateCertificate(inStream)

        if( !x509Cert ) {
            log.info("cert == null...")
            throw new ServletException("Could not create an X509 certificate from the database: ${params.id}")
        }

        String version = x509Cert.version.toString()
        String serialNumber = x509Cert.serialNumber.toString()
        String signatureAlgorithm = x509Cert.sigAlgName
        String issuer = x509Cert.issuerDN.name

        // Validity
        String notBefore = x509Cert.notBefore.toString()
        String notAfter = x509Cert.notAfter.toString()

        String subject = x509Cert.subjectDN.toString()

        // subject public Key info
        String publicKeyAlgorithm = x509Cert.publicKey.algorithm

        // certificate's key usage
        boolean[] keyUsage = x509Cert.getKeyUsage()

        ArrayList<String> certKeyUsageList = new ArrayList<String>()

        if (keyUsage) {
            for(int i = 0; i < keyUsage.size(); i++){
                if (keyUsage[i]) {
                    certKeyUsageList.add(X509CertificateService.KeyUsageStringList[i])
                }
            }
        }

        String keyUsageString = new String()

        for(int i = 0; i < certKeyUsageList.size(); i++){
            keyUsageString = keyUsageString.concat(certKeyUsageList[i])

            if (i < certKeyUsageList.size() - 1) {
                keyUsageString = keyUsageString.concat(", ")
            }
        }

        log.debug("Certificate thumbprint:");
        log.debug("$cert.thumbPrintWithColons");

        log.debug("Rendering signing certificate view [id=${cert.id}] " +
                "page for certificate #${cert.distinguishedName})...)");

        withFormat {
            html {
                [certId: cert.id, cert: cert, version: version, serialNumber: serialNumber,
                 signatureAlgorithm: signatureAlgorithm, issuer: issuer,
                 notBefore: notBefore, notAfter: notAfter, subject: subject,
                 publicKeyAlgorithm: publicKeyAlgorithm, keyUsageString: keyUsageString]
            }
        }
    }

    def setDefaultCertificate() {

        log.debug("add -> ${params.id}")

        SigningCertificate signingCertificate = signingCertificateService.setDefaultCertificate(params.id)

        withFormat  {
            json {
                render signingCertificate as JSON
            }
        }
    }

    /**
     * Called when the user clicks on the "Revoke" button on the view trustmark page.  Should mark the trustmark as
     * revoked, indicating that it is no longer valid.
     */
    @Transactional
    def revoke() {
        User user = springSecurityService.currentUser
        if( StringUtils.isEmpty(params.id) )
            throw new ServletException("Missing required parameter 'id'.")
        if( StringUtils.isEmpty(params.reason) )
            throw new ServletException("Missing required parameter 'reason'.")

        SigningCertificate certificate = SigningCertificate.findById(params.id)
        if( !certificate )
            throw new ServletException("Unknown certificate: ${params.id}")

        certificate.status = SigningCertificateStatus.REVOKED
        certificate.revokedReason = params.reason
        certificate.revokingUser = user
        certificate.revokedTimestamp = Calendar.getInstance().getTime()
        certificate.save(failOnError: true, flush: true)

        def responseData = [status: "SUCCESS", message: "Successfully revoked certificate ${certificate.id}",
                            certificate: [id: certificate.id, distinguishedName: certificate.distinguishedName,
                                          status: certificate.status.toString()]]
        withFormat {
            html {
                flash.message = "Successfully revoked certificate"
                return redirect(controller:'signingCertificates', action:'view', id: certificate.id)
            }
            xml {
                render responseData as XML
            }
            json {
                render responseData as JSON
            }
        }
    }

    def download() {

        if( StringUtils.isBlank(params.id) ){
            log.warn "Missing required parameter id"
            throw new ServletException("Missing required parameter: 'id")
        }

        X509CertificateService x509CertificateService = new X509CertificateService()

        SigningCertificate certificate = SigningCertificate.findById(params.id)

        String organizationName = certificate.organizationName.replaceAll("[^A-Za-z0-9]", "")
        String organizationalUnitName = certificate.organizationalUnitName.replaceAll("[^A-Za-z0-9]", "")

        String filename = certificate.filename

        response.setHeader("Content-length", certificate.x509CertificatePem.length().toString())

        String mimeType = "text/html"

        response.setContentType( "application-xdownload")
        response.setHeader("Content-Disposition", "attachment;filename=${filename}")
        response.getOutputStream() << new ByteArrayInputStream(certificate.x509CertificatePem.getBytes())
    }
}