package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.web.mapping.LinkGenerator
import org.apache.commons.lang.StringUtils


@Secured("permitAll")
class ChangePasswordController {
    def passwordService

    LinkGenerator grailsLinkGenerator

    def index() {
        log.info("Request to reset password...")

        def result = [:]

        if( StringUtils.isEmpty(params.token) )  {
            log.warn("Uh-oh - params.token is empty.")
            result.status = "FAILURE"
            result.message = "Missing required parameter 'token' "
        } else {
            // find user from the token
            User user = null
            PasswordResetToken passwordResetToken = PasswordResetToken.findByToken(params.token)

            if (passwordResetToken) {
                user = passwordResetToken.user
            }

            if( user ){
                result.status = "SUCCESS"
                result.message = "Your password has been successfully reset, please check your email for the new password."

            }else{
                log.warn("No such user for the specified token ${params.token}")
                result.status = "FAILURE"
                result.message = "The system does not contain any user for the specified token ${params.token}"

                // redirect to login
                return redirect(controller:'login', action:'index')
            }
        }

        [token: params.token]

    }//end index()

    def changePassword() {
        log.info("Request to change password with token ${params.token}")

        def result = [:]

        if( StringUtils.isEmpty(params.newPassword) && StringUtils.isEmpty(params.confirmPassword))  {
            log.info("Uh-oh - params.newPassword and params.confirmPassword are empty.")
            result.status = "FAILURE"
            result.message = "Missing required parameter 'newPassword' and  'confirmPassword'"
        } else {
            // find user from the token
            PasswordResetToken passwordResetToken = PasswordResetToken.findByToken(params.token)

            if (isTokenValid(passwordResetToken)) {

                User user = passwordResetToken.user

                if (user) {
                    log.info("Changing password for user[${user.username}] to [$params.newPassword]...")
                    user.password = params.newPassword;
                    User.withTransaction {
                        user.save(failOnError: true);
                    }

                    result.status = "SUCCESS"
                    result.message = "Your password has been successfully changed."

                    // create login link
                    String loginUrl = grailsLinkGenerator.link(controller: 'login', action: 'index')
                    result.loginUrl = loginUrl

                } else {
                    log.warn("No such user exists for token ${params.token}")
                    result.status = "FAILURE"
                    result.message = "The reset password request user does not exist."
                }
            } else {
                log.warn("Token ${params.token} expired...")
                result.status = "FAILURE"
                result.message = "The password change period expired.'"
            }
        }

        withFormat {
            json {
                render result as JSON
            }
        }

    }//end changePassword()

    boolean isTokenValid(PasswordResetToken pwt) {

        if (pwt.getExpireDateTime() > pwt.getRequestDateTime()) {
            return true
        }

        return false
    }
}
