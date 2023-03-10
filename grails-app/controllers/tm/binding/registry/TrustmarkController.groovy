package tm.binding.registry

import grails.converters.JSON
import org.springframework.security.access.prepost.PreAuthorize

class TrustmarkController {

    AdministrationService administrationService

    def index() { }

    @PreAuthorize('permitAll()')
    def list() {

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

    @PreAuthorize('permitAll()')
    def listbyOrganization() {
        log.info("listbyOrganization for organization id: " + params.id)


        def trustmarks = administrationService.listTrustmarksByOrganization(params.id)

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
