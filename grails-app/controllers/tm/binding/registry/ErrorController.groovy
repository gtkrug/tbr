package tm.binding.registry

import org.gtri.fj.data.Option

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken


class ErrorController {

    def administrationService

    @PreAuthorize('permitAll')
    def notAuthorized401(){

        Option<User> userOption = Option<User>.none()
        if (administrationService.isLoggedIn()) {
            userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())
        }

        log.warn("User[@|red ${userOption.some() ?: request.remoteAddr}|@] has requested unauthorized page: " +
                "${request.getAttribute('javax.servlet.error.request_uri')}")
    }//end notAuthorized401

    @PreAuthorize('permitAll')
    def notFound404(){

        Option<User> userOption = Option<User>.none()
        if (administrationService.isLoggedIn()) {
            userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())
        }
        log.warn("User[@|yellow ${userOption.some() ?: request.remoteAddr}|@] has requested unknown page: ${request.getAttribute('javax.servlet.error.request_uri')}")
    }//end notFound404

}//end ErrorController()
