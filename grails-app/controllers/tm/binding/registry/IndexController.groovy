package tm.binding.registry

import java.nio.charset.StandardCharsets

class IndexController {

    def springSecurityService

    DeserializeService deserializeService

    RegistrantService registrantService

    def index() {
        log.info "Loading home page for user: @|cyan ${springSecurityService.currentUser ?: 'anonymous'}|@"

        User user = springSecurityService.currentUser
        if(params.filename != null)  {
            byte[] buffer = new byte[params.filename.size]
            params.filename.getInputStream().read(buffer)
            String xmlString = new String(buffer, StandardCharsets.UTF_8)
            log.info("File Name: ${params.filename.originalFilename}  ${params.filename.size}")
            deserializeService.deserialize(xmlString)
        }
        String orgname = "ALL"

        Registrant registrant = registrantService.findByUser(user)
        Organization organization = null
        if(registrant)  {
            organization = Organization.get(registrant.organizationId)
            orgname = organization.name

            // If registrant's role is ROLE_ORG_ADMIN then redirect to its organization view
            if (registrant.user.isOrgAdmin()) {
                session.setAttribute("user", registrant.user)
                return redirect(controller:'organization', action:'view', id: organization.id)
            } else {
                session.setAttribute("user", null)
            }
        }

        [
                firstTimeLogin: (UserRole.countByRole(Role.findByAuthority(Role.ROLE_ADMIN)) == 0)
                , user : springSecurityService.currentUser
                , registrant: registrant
                , organization: organization
                , orgname: orgname
        ]
    }

    def administer() {
        User user = springSecurityService.currentUser
        log.info("administer user -> ${user.name}")
    }

    def manage() {
        User user = springSecurityService.currentUser
        log.info("manage user -> ${user.name}")
        [
           user: user,
           registrant: registrantService.findByUser(user)
        ]
    }
}
