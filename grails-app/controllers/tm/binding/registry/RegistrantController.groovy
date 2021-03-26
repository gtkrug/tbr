package tm.binding.registry

import grails.converters.JSON

class RegistrantController {

    def springSecurityService

    RegistrantService registrantService

    def index() { }

    def insert() { }

    def administer() { }

    def manage() {
        User user = springSecurityService.currentUser
        log.info("manage user -> ${user.name}")
        [
                user: user,
                registrant: registrantService.findByUser(user)
        ]
    }

    def activate()  {
        User user = springSecurityService.currentUser
        if( user != null)  {
            log.info("user -> ${user.name}")
        }

        Registrant registrant = registrantService.activate(params.ids)

        withFormat  {
            json {
                render registrant.toJsonMap() as JSON
            }
        }
    }

    def deactivate()  {
        User user = springSecurityService.currentUser
        if( user != null)  {
            log.info("user -> ${user.name}")
        }

        Registrant registrant = registrantService.deactivate(params.ids)

        withFormat  {
            json {
                render registrant.toJsonMap() as JSON
            }
        }
    }

    def add()  {
        User user = springSecurityService.currentUser
        if( user != null)  {
            log.info("user -> ${user.name}")
        }

        def registrants = []
        Registrant registrant = registrantService.add(params.lname
                , params.fname
                , params.email
                , params.phone
                , params.pswd
                , params.organizationId
        )

        registrants.add(registrant.toJsonMap())
        withFormat  {
            json {
                render registrants as JSON
            }
        }
    }

    def get()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Registrant registrant = registrantService.get(params.id)

        withFormat  {
            json {
                render registrant.toJsonMap(false) as JSON
            }
        }
    }

    def delete()  {
        User user = springSecurityService.currentUser
        if( user != null)  {
            log.info("user -> ${user.name}")
        }

        Registrant registrant = registrantService.delete(params.ids)

        withFormat  {
            json {
                render registrant.toJsonMap() as JSON
            }
        }
    }

    def update()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def registrants = []

        Registrant registrant = registrantService.update(params.id
                , params.lname
                , params.fname
                , params.email
                , params.phone
                , params.tatUrl
                , params.recipientId
                , params.organizationId
                )
        registrants.add(registrant.toJsonMap())
        withFormat  {
            json {
                render registrants as JSON
            }
        }
    }

    def pswd()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Registrant registrant = registrantService.pswd(params.id
                , params.pswd0
                , params.pswd1
                , params.pswd2
        )

        withFormat  {
            json {
                render registrant.toJsonMap(false) as JSON
            }
        }
    }

    def list()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def registrants = registrantService.list(params.id)

        withFormat  {
            json {
                render registrants as JSON
            }
        }
    }
}
