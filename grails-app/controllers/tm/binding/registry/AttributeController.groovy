package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN", "ROLE_USER"])
class AttributeController {

    def springSecurityService

    AdministrationService administrationService

    def index() { }

    def add() {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def attributes = []
        attributes.add(administrationService.addAttribute(params.name, params.value, params.pId))

        withFormat  {
            json {
                render attributes as JSON
            }
        }
    }

    def delete() {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Provider provider = administrationService.deleteAttributes(params.ids, params.pid)

        withFormat  {
            json {
                render provider as JSON
            }
        }
    }

    def list() {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def attributes = administrationService.listAttributes(params.id)

        withFormat  {
            json {
                render attributes as JSON
            }
        }
    }
}
