package tm.binding.registry

import grails.core.GrailsApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sun.security.x509.X500Name

import javax.servlet.ServletContext
import tm.binding.registry.util.TBRProperties

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.regex.Pattern

class BootStrap {

    protected static final Logger log = LoggerFactory.getLogger(BootStrap.class)
    public static final String TBR_CONFIG_FILE = "/WEB-INF/config/tbr_config.properties"

    //==================================================================================================================
    //  Services/Injected Beans
    //==================================================================================================================
    GrailsApplication grailsApplication

    def init = { servletContext ->
        log.debug("Starting Trustmark Binding Registry Tool...")

        Properties props = readProps(servletContext)

        checkSecurityInit()

        // create a certificate only once
        if (SigningCertificate.getCount() == 0 ) {
            createDefaultSigningCertificate(props)
        }

        printInitMessage()
    }

    def destroy = {
    }

    private Properties readProps(ServletContext servletContext) {
        Properties props = TBRProperties.getProperties()
        servletContext.setAttribute("tbrConfigProps", props)
        return props
    }

    private void printInitMessage(){
        def config = grailsApplication.config

        String msg = """
--------------------------------------------------------------------------------------------------------------------
|  GTRI Trustmark Binding Registry Tool
|
|    TMF API Information
|
|    Configuration Information (@see /WEB-INF/config/tbr_config.properties)
|
--------------------------------------------------------------------------------------------------------------------

"""
        log.info(msg)
    }

    private void checkSecurityInit() {
        Role.withTransaction {
            log.debug "Checking security..."
            List<Role> roles = Role.findAll()
            if (roles.size() == 0) {
                log.info "Creating security roles..."
                log.debug("Adding role[@|cyan ${Role.ROLE_ADMIN}|@]...")
                new Role(authority: Role.ROLE_ADMIN).save(failOnError: true)
                log.debug("Adding role[@|cyan ${Role.ROLE_ORG_ADMIN}|@]...")
                new Role(authority: Role.ROLE_ORG_ADMIN).save(failOnError: true)
                log.debug("Adding role[@|cyan ${Role.ROLE_USER}|@]...")
                new Role(authority: Role.ROLE_USER).save(failOnError: true)
                log.debug("Adding role[@|cyan ${Role.ROLE_REVIEWER}|@]...")
                new Role(authority: Role.ROLE_REVIEWER).save(failOnError: true)
            } else {
                log.debug "Successfully found @|green ${roles.size()}|@ roles."
            }
        }

        if (User.count() == 0) {
            log.info "Creating default users..."
            User.withTransaction {
                createSingleUser()
            }
        } else {
            log.debug("Found @|green ${User.count()}|@ users in the database already.")
        }
    }


    private void createSingleUser() {
        // organization
        Organization organization =  new Organization(
                name: grailsApplication.config.org.name,
                displayName: grailsApplication.config.org.abbreviation,
                siteUrl: grailsApplication.config.org.identifier
        )
        organization.save(true)

        // contact
        Contact contact = new Contact(
                firstName: grailsApplication.config.org.contact.'1'.firstname,
                lastName: grailsApplication.config.org.contact.'1'.lastname,
                email: grailsApplication.config.org.contact.'1'.email,
                phone: grailsApplication.config.org.contact.'1'.phone,
                type: ContactType.ADMINISTRATIVE,
                organization: organization
        )
        contact.save(true)

        // user
        User user = new User(
                // user
                username: grailsApplication.config.tbr.org.user,
                password: grailsApplication.config.tbr.org.pswd,
                name: grailsApplication.config.tbr.org.username,
                accountExpired: false,
                accountLocked: false,
                passwordExpired: false,
                contact: contact
        )
        user.save(failOnError: true)

        String rolesForThisUser = "ROLE_ADMIN, ROLE_ORG_ADMIN, ROLE_REVIEWER"

        for (String roleName : rolesForThisUser.split(Pattern.quote(","))) {
            roleName = roleName.trim()
            Role role = Role.findByAuthority(roleName)
            UserRole.create(user, role, true)
        }

        log.debug "Successfully created user: @|cyan " + user.name + "|@ <@|magenta " + user.username + "|@>"
    }

    private void createDefaultSigningCertificate (Properties props){
        log.info("Loading default signing certificate...")

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA")

            Integer keyLength = Integer.parseInt(
                    props.getProperty("tbr.certificate.default.keylength") ?: "4096")
            keyPairGenerator.initialize(keyLength)
            KeyPair keyPair = keyPairGenerator.generateKeyPair()

            // distinguished name
            String distinguishedName = props.getProperty("tbr.certificate.default.distinguishedname")
                    ?: "CN=https://trustmarkinitiative.org/, OU=TI, O=Trustmark Initiative, L=Atlanta, ST=GA, C=US"

            // serial number
            String serialNumber = props.getProperty("tbr.certificate.default.serialNumber")
                    ?: "6038133832474291075"

            BigInteger sn = new BigInteger(serialNumber)

            X500Name x500Name = new X500Name(distinguishedName)

            X509CertificateService x509CertificateService = new X509CertificateService()

            int validPeriod = Integer.parseInt(
                    props.getProperty("tbr.certificate.default.validperiod") ?: "10")

            Certificate certificate = x509CertificateService.generateCertificate(distinguishedName,
                    keyPair, validPeriod * 365, "SHA256withRSA", sn)

            // public cert string
            String pemCert = x509CertificateService.convertToPem(certificate)

            // private key
            String pemKey = x509CertificateService.convertToPemPrivateKey(keyPair.getPrivate())

            // raw thumbprint
            String thumbprint = x509CertificateService.getThumbPrint(certificate)

            // viewable thumbprint
            String thumbprintWithColons = x509CertificateService.getThumbPrintWithColons(certificate)

            SigningCertificate signingCertificate = new SigningCertificate()
            signingCertificate.distinguishedName = distinguishedName
            signingCertificate.commonName = x500Name.commonName
            signingCertificate.localityName = x500Name.locality
            signingCertificate.stateOrProvinceName = x500Name.state
            signingCertificate.countryName = x500Name.country

            String email = props.getProperty("org.contact.1.email")
                    ?: "help@trustmarkinitiative.org"
            signingCertificate.emailAddress = email

            signingCertificate.organizationName = x500Name.organization
            signingCertificate.organizationalUnitName = x500Name.organizationalUnit
            signingCertificate.serialNumber = certificate.serialNumber.toString()
            signingCertificate.thumbPrint = thumbprint
            signingCertificate.thumbPrintWithColons = thumbprintWithColons
            signingCertificate.privateKeyPem = pemKey
            signingCertificate.x509CertificatePem = pemCert
            signingCertificate.validPeriod = validPeriod
            signingCertificate.keyLength = keyLength
            signingCertificate.status = SigningCertificateStatus.ACTIVE

            X509CertificateService certService = new X509CertificateService()
            X509Certificate x509Certificate = certService.convertFromPem(pemCert)
            signingCertificate.expirationDate = x509Certificate.notAfter

            // URL: create a unique filename to create the downloadable file
            // filename: commonName-thumbprint.pem
            String filename = x500Name.commonName + "-" + thumbprint + ".pem"
            signingCertificate.filename = filename

            String baseUrl = props.getProperty("tf.base.url") ?: "http://localhost:8082/tbr"

            baseUrl += "/public/certificates/download/?id="

            signingCertificate.certificatePublicUrl = baseUrl + filename

            signingCertificate.defaultCertificate = true

            signingCertificate.save(failOnError: true)

        }catch(Throwable t){
            log.error("Unable to create default signing certificate: " + t.message)
            // This is a recoverable error, so we just continue on ignoring this failure.
        }
    }
}
