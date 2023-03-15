package tm.binding.registry

import grails.converters.JSON
import org.gtri.fj.data.Option
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

@PreAuthorize('hasAuthority("tbr-admin")')
class ContactController {

    ContactService contactService

    AdministrationService administrationService

    def index() { }

    def administer() { }

    def add()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())
        log.info("user -> ${userOption.some().name}")

        Contact contact = contactService.add(params.lname
                , params.fname
                , params.email
                , params.phone
                , params.organizationId
                , params.type
        )

        if(params.pId != null)  {
            administrationService.addContactToProvider(contact, params.pId)
        }

        withFormat  {
            json {
                render contact as JSON
            }
        }
    }

    def get()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())
        log.info("user -> ${userOption.some().name}")

        Contact contact = contactService.get(params.id)

        withFormat  {
            json {
                render contact as JSON
            }
        }
    }

    def delete()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())
        log.info("user -> ${userOption.some().name}")

        Contact contact = contactService.delete(params.ids, params.pid)

        withFormat  {
            json {
                render contact as JSON
            }
        }
    }

    def update()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())
        log.info("user -> ${userOption.some().name}")

        Contact contact = contactService.update(params.id, params.lname, params.fname, params.email, params.phone, params.type)

        withFormat  {
            json {
                render contact as JSON
            }
        }
    }

    @PreAuthorize('permitAll()')
    def list()  {

        // compute editable based on current user role and whether the use is assigned an organization
        boolean isReadOnly = administrationService.isReadOnly( Long.parseLong(params.id))

        Map results = [:]
        results.put("editable", !isReadOnly)

        def contacts = []

        if(params.id != null)  {
            contacts = contactService.list(params.id)
        }

        if(params.pid != null)  {
            contacts = administrationService.listContacts(params.pid)
        }

        results.put("records", contacts)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }

    def types()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())
        log.info("user -> ${userOption.some().name}")

        def types = contactService.types()

        withFormat  {
            json {
                render types as JSON
            }
        }
    }
}
