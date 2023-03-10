package tm.binding.registry

import org.gtri.fj.data.Option
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import tm.binding.registry.util.TBRProperties

import java.nio.charset.StandardCharsets

class IndexController {

    AdministrationService administrationService

    DeserializeService deserializeService

    def index() {
        User user = User.findByUsername(((AbstractAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info "Loading home page for user: @|cyan ${user ?: 'anonymous'}|@"

        if(params.filename != null)  {
            byte[] buffer = new byte[params.filename.size]
            params.filename.getInputStream().read(buffer)
            String xmlString = new String(buffer, StandardCharsets.UTF_8)
            log.info("File Name: ${params.filename.originalFilename}  ${params.filename.size}")
            deserializeService.deserialize(xmlString)
        }
        String orgname = "ALL"

        Organization organization = null

        boolean isLoggedIn = administrationService.isLoggedIn()

        boolean isApiClientAuthorizationRequired = TBRProperties.getIsApiClientAuthorizationRequired()

        boolean hideOrganizations = !isLoggedIn && isApiClientAuthorizationRequired

        // TODO: Do we really use the firsTimeLogin logic?
        boolean firstTimeLogin = User.findAll()
                .stream()
                .anyMatch(usr -> !usr.isAdmin())

        [
                firstTimeLogin: false // See above
                , showOrganizations : !hideOrganizations
                , user : user
                , organization: organization
                , orgname: orgname
        ]
    }

    def administer() {

    }
}
