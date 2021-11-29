package tm.binding.registry

import org.dom4j.Element
import tm.binding.registry.util.TBRCronJobPeriod
import tm.binding.registry.util.TBRProperties
import edu.gatech.gtri.trustmark.v1_0.impl.util.TrustmarkMailClientImpl
import org.apache.commons.lang.StringUtils

import java.security.cert.X509Certificate
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

/**
 * Scans all the certificates and checks the expiration.
 * For expired certificates, the status will be changed to EXPIRED
 * and an email will be sent to the TBR administrator using the
 * email address that the administrator has configured.
 *
 * It will also scans for signed trust fabric objects that expires
 * after its signing certificate expiration date
 */

class CheckForExpiredCertificatesEventsJob {

    def deserializeService

    //==================================================================================================================
    // Job Specifics
    //==================================================================================================================
    static concurrent = false
    def description = "Scans all the certificates associated to each organization and checks the \n" +
            " certificate expiration. Then, for expired certificates, changes the status accordingly."
    def groovyPageRenderer

    public static final Integer jobStartingHour = 4

    static String cronExpressionTrigger() {
        return TBRCronJobPeriod.getExpressionTrigger(jobStartingHour)
    }

    static triggers = {
        cron cronExpression: cronExpressionTrigger()
    }

    //==================================================================================================================
    // Execute entry point
    //==================================================================================================================
    void execute() {
        log.info("Executing ${this.getClass().getSimpleName()}: check for certificate expiration events...")

        try {
            checkForExpiredCertificates()

            checkForTrustFabricExpirationEvent()

        }catch(Throwable t){
            log.error("Unable to execute ${this.getClass().getSimpleName()} cron job.", t)
        }
    }

    void checkForTrustFabricExpirationEvent() {
        log.info("Executing ${this.getClass().getSimpleName()}: checkForTrustFabricExpirationEvent...")

        Calendar now = Calendar.getInstance()

        def providers = Provider.findAll()
        providers.each { provider ->
            log.info("Processing provider (system): ${provider.name}")

            if (StringUtils.isNotEmpty(provider.saml2MetadataXml)) {
                Element rootNode = deserializeService.readXmlDocument(provider.saml2MetadataXml)
                String validUntil = rootNode.attributeValue("validUntil")

                // convert validUntil string to Date
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .appendPattern("[x][X]")
                        .toFormatter();

                Instant validUntilInstant= Instant.from(formatter.parse(validUntil));

                Date validUntilDate = Date.from(validUntilInstant);
                Calendar validUntilCalendar = Calendar.getInstance();
                validUntilCalendar.setTime(validUntilDate);

                SigningCertificate cert = SigningCertificate.findByDefaultCertificate(true)

                Date notAfter = cert.expirationDate
                Calendar expiration = Calendar.getInstance();
                expiration.setTime(notAfter);

                if (validUntilCalendar.getTimeInMillis() > expiration.getTimeInMillis()) {
                    log.warn("Metadata expires after certificate expiration...")

                    String distinguishedName = cert.distinguishedName

                    String emailSubject = "The expiration data for trust fabric object ${provider.name} (${provider.entityId}) is valid beyond the lifetime of the signing certificate."
                    String message = trustFabricExpirationWarningMessage(provider)

                    // email administrator
                    emailCertificateSubscriber(TBRProperties.getAdminEmail(), distinguishedName, emailSubject, message)
                }
            } else {
                log.info("Provider (system): ${provider.name} does not have SAML2 metadata generated...")
            }
        }

    }

    void checkForExpiredCertificates() {
        log.info("Executing ${this.getClass().getSimpleName()}: checkForExpiredCertificates...")

        Calendar now = Calendar.getInstance()

        Integer expirationWarningPeriodInDays = Integer.parseInt(TBRProperties.getProperties().getProperty(
                "tbr.certificate.default.expirationWarningPeriod")) ?: 30

        X509CertificateService certService = new X509CertificateService()

        def certificates = SigningCertificate.findAll()
        certificates.each { cert ->

            X509Certificate x509Certificate = certService.convertFromPem(cert.x509CertificatePem)

            Date notAfter = x509Certificate.getNotAfter()
            Calendar expiration = Calendar.getInstance()
            expiration.setTime(notAfter)

            Calendar expirationWarning = Calendar.getInstance()
            expirationWarning.setTime(notAfter)
            expirationWarning.add(Calendar.DATE, -expirationWarningPeriodInDays)

            Date expWarnDate = expirationWarning.getTime()

            if(cert.status == SigningCertificateStatus.ACTIVE && expiration.getTimeInMillis() < now.getTimeInMillis()) {
                log.warn("Detected an expired certificate.  Updating status...")

                cert.status = SigningCertificateStatus.EXPIRED
                cert.revokedTimestamp = now.getTime()
                cert.revokedReason = "The Trust Binding Repository has automatically revoked this certificate because it has expired."

                log.info("Updating status of ${cert.distinguishedName}...")

                SigningCertificate.withTransaction {
                    cert.save(failOnError: true, flush: true)
                }

                String emailSubject = "The following signing X509 certificate has expired: ${cert.distinguishedName}."
                String message = expiredMessage(cert)

                // email certificate subscriber
                emailCertificateSubscriber(cert.emailAddress, cert.distinguishedName, emailSubject, message)

                // email organization's contact if email different from certificate's
                if (cert.emailAddress != TBRProperties.getAdminEmail()) {
                    emailCertificateSubscriber(TBRProperties.getAdminEmail(), cert.distinguishedName, emailSubject, message)
                }
            } else if(cert.status == SigningCertificateStatus.ACTIVE && expirationWarning.getTimeInMillis() < now.getTimeInMillis()) {
                log.warn("Certificate about to expire...")

                String emailSubject = "The following signing X509 certificate is about to expire: ${cert.distinguishedName}."
                String message = expirationWarningMessage(cert)

                // email certificate subscriber
                emailCertificateSubscriber(cert.emailAddress, cert.distinguishedName, emailSubject, message)

                // email organization's contact if email different from certificate's
                if (cert.emailAddress != TBRProperties.getAdminEmail()) {
                    emailCertificateSubscriber(TBRProperties.getAdminEmail(), cert.distinguishedName, emailSubject, message)
                }
            }
        }

    }

    // email certificate holder/ organization's contact
    void emailCertificateSubscriber(String email, String  certDistinguishedName, String subject, String message) {

        if( StringUtils.isEmpty(email) )  {
            log.warn("email is empty.")
        } else {
            log.info("Sending certificate expiration email to ${email}...")

            TrustmarkMailClientImpl emailClient = new TrustmarkMailClientImpl()

            emailClient.setUser(TBRProperties.getProperties().getProperty(TrustmarkMailClientImpl.SMTP_USER))
            emailClient.setPswd(TBRProperties.getProperties().getProperty(TrustmarkMailClientImpl.SMTP_PSWD))

            emailClient.setSmtpHost(TBRProperties.getProperties().getProperty(TrustmarkMailClientImpl.SMTP_HOST))
                    .setSmtpPort(TBRProperties.getProperties().getProperty(TrustmarkMailClientImpl.SMTP_PORT))
                    .setFromAddress(TBRProperties.getProperties().getProperty(TrustmarkMailClientImpl.FROM_ADDRESS))
                    .setSmtpAuthorization(Boolean.parseBoolean(TBRProperties.getProperties().getProperty(TrustmarkMailClientImpl.SMTP_AUTH)))
                    .setSubject(subject)
                    .addRecipient(email)
                    .setText(message)
        }
    }

    String trustFabricExpirationWarningMessage(Provider provider) {
        StringBuilder sb = new StringBuilder();
        sb.append("The expiration data for trust fabric object ${provider.name} (${provider.entityId}) is valid beyond the lifetime of the signing certificate.  " +
                "It is recommended that a new signing certificate be generated as soon as possible and this object resigned.")
        sb.append("Link: ${getProviderUrl(provider.id)}");

        log.info("CheckForExpiredCertificatesEventsJob::expirationWarningMessage: ${sb.toString()}...")

        return sb.toString();
    }

    String expirationWarningMessage(SigningCertificate cert) {
        StringBuilder sb = new StringBuilder();
        sb.append("The following Signing Certificate is about to expire: ${cert.distinguishedName} ");
        sb.append("Link: ${getSigningCertificateUrl(cert.id)}");

        log.info("CheckExpiredCertificatesJob::expirationWarningMessage: ${sb.toString()}...")

        return sb.toString();
    }

    String expiredMessage(SigningCertificate cert) {
        StringBuilder sb = new StringBuilder();
        sb.append("The following Signing Certificate has expired: ${cert.distinguishedName} ");
        sb.append("Link: ${getSigningCertificateUrl(cert.id)}");

        log.info("CheckExpiredCertificatesJob::expiredMessage: ${sb.toString()}...")

        return sb.toString();
    }

    private String getProviderUrl(int providerId) {
        StringBuilder sb = new StringBuilder()
        def baseAppUrl = TBRProperties.getProperties().getProperty("tf.base.url")
        sb.append(baseAppUrl)
        sb.append("/provider/view/${providerId}")

        return sb.toString()
    }

    private String getSigningCertificateUrl(int certId) {
        StringBuilder sb = new StringBuilder()
        def baseAppUrl = TBRProperties.getProperties().getProperty("tf.base.url")
        sb.append(baseAppUrl)
        sb.append("/signingCertificates/view/${certId}")

        return sb.toString()
    }
}/* End CheckExpiredCertificatesJob */
