package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN", "ROLE_USER"])
class ConformanceTargetTipController {

    def springSecurityService

    AdministrationService administrationService

    def index() { }

    def add()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def conformanceTargetTips = []
        conformanceTargetTips.add(administrationService.addConformanceTargetTip(params.pId, params.identifier))

        withFormat  {
            json {
                render conformanceTargetTips as JSON
            }
        }
    }

    def delete() {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Provider provider = administrationService.deleteConformanceTargetTips(params.ids, params.pid)

        withFormat  {
            json {
                render provider as JSON
            }
        }
    }

    def list() {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def conformanceTargetTips = administrationService.listConformanceTargetTips(params.id)

        withFormat  {
            json {
                render conformanceTargetTips as JSON
            }
        }
    }
}
