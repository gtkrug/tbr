package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured


@Secured("permitAll")
class ForgotPasswordController {

    def passwordService

    def index() {
        log.info("Showing forgot password page [IP: ${request.remoteAddr}]...");
    }

    def resetPassword() {
        log.info("Request to reset password for user[${params.email}] from IP[${request.remoteAddr}]")

        def result = passwordService.resetPassword(params.email)

        withFormat {
            html { throw new UnsupportedOperationException("NOT SUPPORTED"); }
            xml { throw new UnsupportedOperationException("NOT SUPPORTED"); }
            json {
                render result as JSON
            }
        }

    }//end resetPassword()
}
