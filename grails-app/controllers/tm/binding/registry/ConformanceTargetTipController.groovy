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

        log.info("add conformance target tip id -> ${params.identifier}")

        def results = [:]

        def messageMap = [:]

        def conformanceTargetTips = []

        Provider provider = Provider.get(Integer.parseInt(params.pId))

        ConformanceTargetTip tip = ConformanceTargetTip.findByConformanceTargetTipIdentifier(params.identifier)

        if (tip != null && provider.conformanceTargetTips.contains(tip)) {
            messageMap.put("WARNING", "WARNING: Conformance target TIP \"${tip.name}\" already exists." )
        } else {
            try {
                conformanceTargetTips.add(administrationService.addConformanceTargetTip(params.pId, params.identifier))

                messageMap.put("SUCCESS", "SUCCESS: Successfully added conformance target TIP.")

            } catch (Throwable t) {
                log.error("Unable to add TIP: ${params.identifier}")
                messageMap.put("ERROR", "ERROR: Failed to find conformance target TIP at URL: ${params.identifier}.")
            }
        }

        results.put("status", messageMap)
        results.put("conformanceTargetTips", conformanceTargetTips)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }

    def delete() {
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

        Provider provider = administrationService.deleteConformanceTargetTips(params.ids, params.pid)

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
        results.put("editable", springSecurityService.isLoggedIn())

        def conformanceTargetTips = administrationService.listConformanceTargetTips(params.id)

        results.put("records", conformanceTargetTips)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }
}
