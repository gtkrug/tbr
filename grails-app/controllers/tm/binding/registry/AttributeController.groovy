package tm.binding.registry

import grails.converters.JSON
import org.gtri.fj.data.Option

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

@PreAuthorize('hasAnyAuthority("tbr-admin", "tbr-org-admin")')
class AttributeController {

    AdministrationService administrationService

    def index() { }

    def add() {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        def attributes = []
        attributes.add(administrationService.addAttribute(params.name, params.value, params.pId))

        withFormat  {
            json {
                render attributes as JSON
            }
        }
    }

    def delete() {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        Provider provider = administrationService.deleteAttributes(params.ids, params.pid)

        withFormat  {
            json {
                render provider as JSON
            }
        }
    }

    @PreAuthorize('permitAll()')
    def list() {

        Map results = [:]

        Provider provider = Provider.get(Integer.parseInt(params.id))

        results.put("editable", !administrationService.isReadOnly(provider.organizationId))

        def attributes = administrationService.listAttributes(params.id)

        results.put("records", attributes)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }
}
