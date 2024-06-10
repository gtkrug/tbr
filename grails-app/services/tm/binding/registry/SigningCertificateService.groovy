package tm.binding.registry

import grails.converters.JSON
import grails.converters.XML
import grails.gorm.transactions.Transactional
import org.apache.commons.lang.StringUtils
import tm.binding.registry.util.X500PrincipalWrapper
import tm.binding.registry.util.TBRProperties


import javax.servlet.ServletException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.cert.Certificate
import java.security.cert.X509Certificate

@Transactional
class SigningCertificateService {

    def x509CertificateService

    def serviceMethod(String... args) {
        log.info("service -> ${args[0]}")
    }

    // args:
    //   commonName // 0
    //   localityName // 1
    //   stateName // 2
    //   countryName // 3
    //   emailAddress // 4
    //   organizationName // 5
    //   organizationUnitName // 6
    //   validPeriod // 7
    //   keyLength // 8

    def add(String... args) {
        log.info("add -> ${args[0]}")

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA")

        Integer keyLength = Integer.parseInt(args[8])
        Integer validPeriod = Integer.parseInt(args[7])

        keyPairGenerator.initialize(keyLength)
        KeyPair keyPair = keyPairGenerator.generateKeyPair()

        // distinguished name
        X500PrincipalWrapper x500Name = new X500PrincipalWrapper(args[0], args[6], args[5],
                args[1], args[2], args[3])

        String distinguishedName = x500Name.getName()

        Certificate certificate = x509CertificateService.generateCertificate(distinguishedName,
                keyPair, validPeriod * 365, "SHA256withRSA")

        // public cert string
        String pemCert = x509CertificateService.convertToPem(certificate)

        // private key
        String pemKey = x509CertificateService.convertToPemPrivateKey(keyPair.getPrivate())

        // raw thumbprint
        String thumbprint = x509CertificateService.getThumbPrint(certificate)

        // viewable thumbprint
        String thumbprintWithColons = x509CertificateService.getThumbPrintWithColons(certificate)

        SigningCertificate signingCertificate = new SigningCertificate()
        signingCertificate.distinguishedName = certificate.subjectDN.name
        signingCertificate.commonName = x500Name.commonName
        signingCertificate.localityName = x500Name.locality
        signingCertificate.stateOrProvinceName = x500Name.state
        signingCertificate.countryName = x500Name.country
        signingCertificate.emailAddress = args[4]
        signingCertificate.organizationName = x500Name.organization
        signingCertificate.organizationalUnitName = x500Name.organizationalUnit
        signingCertificate.serialNumber = certificate.serialNumber.toString()
        signingCertificate.thumbPrint = thumbprint
        signingCertificate.thumbPrintWithColons = thumbprintWithColons
        signingCertificate.privateKeyPem = pemKey
        signingCertificate.x509CertificatePem = pemCert
        signingCertificate.status = SigningCertificateStatus.ACTIVE

        signingCertificate.validPeriod = validPeriod
        signingCertificate.keyLength = keyLength

        X509Certificate x509Certificate = x509CertificateService.convertFromPem(signingCertificate.x509CertificatePem)
        signingCertificate.expirationDate = x509Certificate.notAfter

        // URL: create a unique filename to create the downloadable file
        // filename: commonName-thumbprint.pem
        String filename = replaceNonAlphanumeric(x500Name.commonName, "-") + "-" + thumbprint + ".pem"
        signingCertificate.filename = filename

        // get the base url from the http request and append the controller
        String baseUrl = TBRProperties.getBaseUrl()

        signingCertificate.certificatePublicUrl = baseUrl + "/public/certificates/download/?id=" + filename

        // set the default certificate flag if this is the
        // first certificate for the TBR
        if (SigningCertificate.findAll().size() == 0) {
            signingCertificate.defaultCertificate = true
        }

        signingCertificate.save(failOnError: true, flush: true)

        return signingCertificate
    }

    def setDefaultCertificate(String... args) {

        log.debug("setDefaultCertificate -> ${args[0]}")

        Integer id = Integer.parseInt(args[0])

        SigningCertificate signingCertificate = SigningCertificate.findById(id)

        if (signingCertificate.status == SigningCertificateStatus.ACTIVE ) {
            SigningCertificate.findAll().each { SigningCertificate sc ->
                if (sc.id == id) {
                    sc.defaultCertificate = true
                } else {
                    sc.defaultCertificate = false
                }

                sc.save(failOnError: true, flush: true)

                log.debug("saved cert: ${sc.id}")
            }
        }

        return signingCertificate
    }

    def get(String... args) {
        log.info("get -> ${args[0]}")

        return SigningCertificate.get(Integer.parseInt(args[0]))
    }

    def list(String... args) {
        log.info("list -> ${args[0]}")
        def signingCertificates = []

        if(args[0] == "ALL")  {
            SigningCertificate.findAll().forEach({o -> signingCertificates.add(o.toJsonMap())})
            signingCertificates.sort( {o1, o2 ->
                o1.distinguishedName <=> o2.distinguishedName
            })
        } else {
            SigningCertificate.findAllByName(args[0]).forEach({o -> signingCertificates.add(o.toJsonMap())})
            signingCertificates.sort( {o1, o2 ->
                o1.distinguishedName <=> o2.distinguishedName
            })
        }
        return signingCertificates
    }

    def replaceNonAlphanumeric(String str, String replacement)
    {
        // replace all non-alphanumeric characters with replacement
        return str.replaceAll("[^a-zA-Z0-9]", replacement);
    }
}
