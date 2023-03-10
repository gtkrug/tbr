package tm.binding.registry

import org.apache.commons.lang.StringUtils
import org.gtri.fj.data.Option
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import tm.binding.registry.util.TBRProperties

import javax.servlet.ServletException


class UserInfoTagLib {
    def administrationService

    static defaultEncodeAs = 'raw'

    def userProperName = {attrs, body ->

        Option<User> userOption = Option.none()

        if (administrationService.isLoggedIn()) {
            userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

            if (!userOption.isSome()) {
                throw new ServletException("Error getting current logged-in user.")
            }

            if (userOption.some().contact != null && StringUtils.isNotEmpty(userOption.some().contact.lastName) && StringUtils.isNotEmpty(userOption.some().contact.firstName)) {
                out << userOption.some().contact.lastName + ", " + userOption.some().contact.firstName
            } else {
                out << userOption.some().username
            }
        } else {
            out << "Anonymous user"
        }
    }

    def isUserNotAssignedToAnOrganization = {attrs, body ->

        Option<User> userOption = Option.none()

        if (administrationService.isLoggedIn()) {
            userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

            if (!userOption.isSome()) {
                throw new ServletException("Error getting current logged-in user.")
            }

            if (userOption.some().contact == null || (userOption.some().contact != null &&  userOption.some().contact.organization == null)) {
                out << body()
            }
        }
    }
}
