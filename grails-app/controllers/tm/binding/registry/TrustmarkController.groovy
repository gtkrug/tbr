package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class TrustmarkController {

    def springSecurityService

    AdministrationService administrationService

    def index() { }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def list() {
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

        def trustmarks = administrationService.listTrustmarks(params.id)

        // sort ascending by name
        trustmarks.sort( { a, b ->
            a.name <=> b.name
        })

        withFormat  {
            json {
                render trustmarks as JSON
            }
        }
    }
}
