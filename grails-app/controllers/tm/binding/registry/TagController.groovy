package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class TagController {

    def springSecurityService

    AdministrationService administrationService

    def index() { }

    def add()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def tags = []
        tags.add(administrationService.addTag(params.pId, params.tag))

        withFormat  {
            json {
                render tags as JSON
            }
        }
    }

    def delete() {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Provider provider = administrationService.deleteTags(params.ids, params.pid)

        withFormat  {
            json {
                render provider as JSON
            }
        }
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def list() {
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

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
