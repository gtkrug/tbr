package tm.binding.registry

import grails.converters.JSON

class TrustmarkController {

    def springSecurityService

    AdministrationService administrationService

    def index() { }

    def list() {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def trustmarks = administrationService.listTrustmarks(params.id)

        withFormat  {
            json {
                render trustmarks as JSON
            }
        }
    }
}
