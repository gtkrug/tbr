package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.impl.util.TrustmarkMailClientImpl
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import grails.plugin.springsecurity.annotation.Secured
import grails.util.Holders
import grails.web.mapping.LinkGenerator
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import tm.binding.registry.util.TBRProperties
import tm.binding.registry.util.PasswordUtil
import org.apache.commons.lang.StringUtils
import javax.servlet.ServletException
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.springframework.context.i18n.LocaleContextHolder as LCH

@Secured("permitAll")
@Transactional
class PasswordService {
    def emailService

    PageRenderer groovyPageRenderer

    def messageSource

    LinkGenerator grailsLinkGenerator

    def resetPassword(String email) {
        log.info("Request to reset password for user[${email}]")

        def result = [:]

        try {
            if (StringUtils.isEmpty(email)) {
                log.warn("Uh-oh - email is empty.")
                result.status = "FAILURE"
                result.message = "Missing required parameter 'email' "
            } else {
                // First try to find user with the email as the username
                User user = User.find("from User where lower(username) = :email", [email: email?.toLowerCase()])

                // find user by user's contact email
                if (!user) {
                    Contact contact = Contact.find("from Contact where lower(email) = :email", [email: email?.toLowerCase()])
                    user = User.findByContact(contact)
                }

                if (user) {
                    log.debug("Sending confirmation email to ${email}...")

                    def model = [:]

                    sendEmail(user, email, 'reset.password.subject', '[Trustmark Binding Registry Tool] Password Reset Successful',
                            "/templates/passwordReset", model)

                    result.status = "SUCCESS"
                    result.message = "Your password has been successfully reset, please check your email for the new password."
                } else {
                    log.warn("No such user ${params.email}")
                    result.status = "FAILURE"
                    result.message = "The system does not contain any user with email '${params.email}'"
                }
            }
        } catch(Throwable t) {
            log.error("Error reseting password: " + t.message)
        }

        return result
    }//end resetPassword()

    def newRegistrantEmail(String email, String organizationId) {
        log.info("Send notification for initial password to user[${email}]")

        def result = [:]

        try {
            if (StringUtils.isEmpty(email) ||
                    StringUtils.isEmpty(organizationId)) {
                log.warn("Uh-oh - email and/or organizationId are empty.")
                result.status = "FAILURE"
                result.message = "Missing required parameters 'email' and or 'organizationId'"
            } else {
                // First try to find user with the email as the username
                User user = User.find("from User where lower(username) = :email", [email: email?.toLowerCase()])

                // find user by user's contact email
                if (!user) {
                    Contact contact = Contact.find("from Contact where lower(email) = :email", [email: email?.toLowerCase()])
                    user = User.findByContact(contact)
                }

                if (user) {

                    if (emailService.mailEnabled()) {


                        log.debug("Sending confirmation email to ${email}...")

                        Organization organization = Organization.get(Long.parseLong(organizationId))

                        if (!organization) {
                            throw new ServletException("Missing organization [${organizationId}]")
                        }

                        def model = ["organizationName": organization.name]

                        sendEmail(user, email, 'new.registrant.subject', '[Trustmark Binding Registry Tool] New Registrant Account Successful',
                                "/templates/newRegistrantEmail", model)

                        result.status = "SUCCESS"
                        result.message = "Your registrant acoount has been successfully created, please check your email to create a new password."
                    } else {
                        log.warn("Email is disabled.")
                        result.status = "FAILURE"
                        result.message = "Email is disabled."
                    }
                } else {
                    log.warn("No such user ${params.email}")
                    result.status = "FAILURE"
                    result.message = "The system does not contain any user with email '${params.email}'"
                }
            }
        } catch(Throwable t) {
            log.error("Error creating new registrant accoount: " + t.message)
        }

        return result
    }//end resetPassword()

    void sendEmail(User user, String email, String messageProperty,
                   String messagePropertyDefault, String contentTemplate, Map model) {
        // admin
        String adminEmail = TBRProperties.getAdminEmail()
        model.put("adminEmail", adminEmail)

        // create password reset token
        String token = UUID.randomUUID().toString()
        createPasswordResetTokenForUser(token, user)

        // create password reset url
        String resetUrl = grailsLinkGenerator.link(controller: 'changePassword', action: 'index', absolute: true) + "?token=${token}"
        model.put("resetUrl", resetUrl)

        String tbrUrl = grailsLinkGenerator.serverBaseURL
        model.put("tbrUrl", tbrUrl)

        String message = messageSource.getMessage(messageProperty, null, messagePropertyDefault, LCH.locale)
        String content = groovyPageRenderer.render(template: contentTemplate, model: model).toString()

        emailService.sendEmailWithContent(new ArrayList<MultipartFile>(), email, message, content)
    }

    void createPasswordResetTokenForUser(String token, User user) {
        LocalDateTime requestDateTime = LocalDateTime.now(ZoneOffset.UTC)
        LocalDateTime expireDateTime = requestDateTime.plusHours(PasswordResetToken.EXPIRATION)

        PasswordResetToken passwordResetToken = new PasswordResetToken(
                token: token, user: user, requestDateTime: requestDateTime, expireDateTime: expireDateTime)

        PasswordResetToken.withTransaction {
            passwordResetToken.save(flush: true, failOnError: true)
        }
    }
}
