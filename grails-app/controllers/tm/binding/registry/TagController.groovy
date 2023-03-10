package tm.binding.registry

import grails.converters.JSON
import org.gtri.fj.data.Option

//import grails.plugin.springsecurity.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

class TagController {

//    def springSecurityService

    AdministrationService administrationService

    def index() { }

    def add()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        def tags = []
        tags.add(administrationService.addTag(params.pId, params.tag))

        withFormat  {
            json {
                render tags as JSON
            }
        }
    }

    def delete() {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        Provider provider = administrationService.deleteTags(params.ids, params.pid)

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

        def tags = administrationService.listTags(params.id)

        results.put("records", tags)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }
}
