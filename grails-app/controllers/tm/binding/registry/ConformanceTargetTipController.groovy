package tm.binding.registry

import grails.converters.JSON
import org.gtri.fj.data.Option

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

@PreAuthorize('hasAnyAuthority("tbr-admin", "tbr-org-admin")')
class ConformanceTargetTipController {

    AdministrationService administrationService

    def index() { }

    def add()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

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
        Provider provider = administrationService.deleteConformanceTargetTips(params.ids, params.pid)

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

        def conformanceTargetTips = administrationService.listConformanceTargetTips(params.id)

        results.put("records", conformanceTargetTips)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }
}
