package tm.binding.registry

import grails.converters.JSON

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

    def list() {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def tags = administrationService.listTags(params.id)

        withFormat  {
            json {
                render tags as JSON
            }
        }
    }
}
