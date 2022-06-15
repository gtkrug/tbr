package tm.binding.registry

import tm.binding.registry.util.TBRCronJobPeriod
import tm.binding.registry.util.TBRProperties
import edu.gatech.gtri.trustmark.v1_0.impl.util.TrustmarkMailClientImpl
import org.apache.commons.lang.StringUtils
import java.security.cert.X509Certificate

/**
 * Scans all the trust fabric certificates and checks the expiration.
 * For expired certificates, an email will be sent to the TBR administrator
 * using the email address that the administrator has configured and to
 * the trust fabric's main pint of contact.
 */

class CheckForExpiredCertificatesWithinTrustFabricObjectsJob {

    //==================================================================================================================
    // Job Specifics
    //==================================================================================================================
    static concurrent = false
    def description = "Scans all the signing and encrypting certificates withing each provider/system trust fabric \n" +
            "and checks the certificate expiration. Then, for expired certificates, notify via email the TBR admin \n" +
            "and the trust fabric's main point of contact."
    def groovyPageRenderer

    public static final Integer jobStartingHour = 1

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
            checkForTrustFabricCertificateExpiration()

        }catch(Throwable t){
            log.error("Unable to execute ${this.getClass().getSimpleName()} cron job.", t)
        }
    }

    void checkForTrustFabricCertificateExpiration() {
        log.info("Executing ${this.getClass().getSimpleName()}: checkForTrustFabricCertificateExpiration...")

        Integer expirationWarningPeriodInDays = Integer.parseInt(TBRProperties.getProperties().getProperty(
                "tbr.trust-fabric.certificate.default.expirationWarningPeriod")) ?: 60

        X509CertificateService certService = new X509CertificateService()

        def providers = Provider.findAll()
        providers.each { provider ->

            if (StringUtils.isNotEmpty(provider.saml2MetadataXml)) {
                String beginCert = "-----BEGIN CERTIFICATE-----\n";
                String endCert = "\n-----END CERTIFICATE-----";

                // check signing certificate
                if (StringUtils.isNotEmpty(provider.signingCertificate)) {
                    String signingCertificate = beginCert + provider.signingCertificate + endCert
                    X509Certificate x509SigningCertificate = certService.convertFromPem(signingCertificate)

                    checkForCertificateExpiration(provider, x509SigningCertificate, expirationWarningPeriodInDays)
                }

                // check encrypting certificate
                if (StringUtils.isNotEmpty(provider.encryptionCertificate)) {
                    String encryptingCertificate = beginCert + provider.encryptionCertificate + endCert
                    X509Certificate x509EncryptingCertificate = certService.convertFromPem(encryptingCertificate)

                    checkForCertificateExpiration(provider, x509EncryptingCertificate, expirationWarningPeriodInDays)
                }
            }
        }
    }

    void checkForCertificateExpiration(Provider provider, X509Certificate x509Certificate, Integer expirationWarningPeriodInDays) {
        log.info("Checking expiration for ${x509Certificate.subjectDN.toString()}...")

        Calendar now = Calendar.getInstance()

        Date notAfter = x509Certificate.getNotAfter()
        Calendar expiration = Calendar.getInstance()
        expiration.setTime(notAfter)

        Calendar expirationWarning = Calendar.getInstance()
        expirationWarning.setTime(notAfter)
        expirationWarning.add(Calendar.DATE, -expirationWarningPeriodInDays)

        if(expiration.getTimeInMillis() < now.getTimeInMillis()) {
            log.warn("Detected an expired trust fabric certificate...")

            String emailSubject = "The following signing X509 certificate has expired: ${x509Certificate.subjectDN.toString()}."
            String message = expiredMessage(x509Certificate)

            // email administrator
            emailCertificateSubscriber(TBRProperties.getAdminEmail(), x509Certificate.subjectDN.toString(), emailSubject, message)

            // email primary point of contact
            Contact pointOfContact = getPrimaryPointOfContact(provider)
            if (pointOfContact) {
                emailCertificateSubscriber(pointOfContact.email, x509Certificate.subjectDN.toString(), emailSubject, message)
            }

        } else if(expirationWarning.getTimeInMillis() < now.getTimeInMillis()) {
            log.warn("Certificate about to expire...")

            String emailSubject = "The following signing X509 certificate is about to expire: ${x509Certificate.subjectDN.toString()}."
            String message = expirationWarningMessage(x509Certificate)

            // email administrator
            emailCertificateSubscriber(TBRProperties.getAdminEmail(), x509Certificate.subjectDN.toString(), emailSubject, message)

            // email primary point of contact
            Contact pointOfContact = getPrimaryPointOfContact(provider)
            if (pointOfContact) {
                emailCertificateSubscriber(pointOfContact.email, x509Certificate.subjectDN.toString(), emailSubject, message)
            }
        }
    }

    // email tbr admin / trust fabric's main point of contact
    void emailCertificateSubscriber(String email, String certDistinguishedName, String subject, String message) {

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

    Contact getPrimaryPointOfContact(Provider provider) {
        provider.contacts.each { contact ->
            if (contact.type == ContactType.SUPPORT) {
                return contact // priority
            } else if (contact.type == ContactType.TECHNICAL) {
                return contact
            }
        }

        return null
    }

    String expirationWarningMessage(X509Certificate x509Certificate) {
        StringBuilder sb = new StringBuilder();
        sb.append("The following Trust Fabric Signing Certificate is about to expire: ${x509Certificate.subjectDN.toString()} ");

        log.info("CheckForExpiredCertificatesWithinTrustFabricObjectsJob::expirationWarningMessage: ${sb.toString()}...")

        return sb.toString();
    }

    String expiredMessage(X509Certificate x509Certificate) {
        StringBuilder sb = new StringBuilder();
        sb.append("The following Trust Fabric Signing Certificate has expired: ${x509Certificate.subjectDN.toString()} ");

        log.info("CheckForExpiredCertificatesWithinTrustFabricObjectsJob::expiredMessage: ${sb.toString()}...")

        return sb.toString();
    }

}/* End CheckForExpiredCertificatesWithinTrustFabricObjectsJob */
