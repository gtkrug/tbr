package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN", "ROLE_USER"])
class ContactController {

    def springSecurityService

    ContactService contactService

    RegistrantService registrantService

    AdministrationService administrationService

    def index() { }

    def administer() { }

    def manage() {
        User user = springSecurityService.currentUser
        log.info("manage user -> ${user.name}")

        [registrant: registrantService.findByUser(user)]
    }

    def add()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

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
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Contact contact = contactService.get(params.id)

        withFormat  {
            json {
                render contact as JSON
            }
        }
    }

    def delete()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Contact contact = contactService.delete(params.ids, params.pid)

        withFormat  {
            json {
                render contact as JSON
            }
        }
    }

    def update()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Contact contact = contactService.update(params.id, params.lname, params.fname, params.email, params.phone, params.type)

        withFormat  {
            json {
                render contact as JSON
            }
        }
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def list()  {
        boolean isAdmin = false
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")

            isAdmin = user.isAdmin()
        }

        Map results = [:]
        results.put("editable", isAdmin)

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
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def types = contactService.types()

        withFormat  {
            json {
                render types as JSON
            }
        }
    }
}
