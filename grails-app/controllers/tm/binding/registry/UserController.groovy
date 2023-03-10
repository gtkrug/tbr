package tm.binding.registry

import grails.converters.JSON
import org.gtri.fj.data.Option
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

@PreAuthorize('hasAuthority("tbr-admin")')
class UserController {

    def userService

    def administrationService

    def administer() { }

    def get()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        User user = User.findById(params.id)

        withFormat  {
            json {
                render user.toJsonMap(false) as JSON
            }
        }
    }

    def update()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        def users = []

        User user = userService.update(params.id, params.organizationId)
        users.add(user.toJsonMap())
        withFormat  {
            json {
                render users as JSON
            }
        }
    }

    def list()  {

        boolean isEditable = false

        if (administrationService.isLoggedIn()) {
            Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

            isEditable = userOption.isSome() && userOption.some().isAdmin()

            log.info("user -> ${userOption.some().name}")
        }

        Map results = [:]

        results.put("editable", isEditable)

        def users = userService.list(params.id)

        results.put("records", users)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }
}
