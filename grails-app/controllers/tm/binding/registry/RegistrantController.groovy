package tm.binding.registry

import grails.converters.JSON

class RegistrantController {

    def springSecurityService

    def passwordService

    def registrantService

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
                , params.roleId
        )

        if(params.notifyRegistrant) {
            // email registrant to reset their password
            def result = passwordService.resetPassword(params.email)
        }

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

//    id: regId
//    , lname: lname
//    , fname: fname
//    , email: email
//    , phone: phone
//    , organizationId: orgId
//    , roleId: roleId

    def update()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def registrants = []

        Registrant registrant = registrantService.update(params.id
                , params.lname
                , params.fname
                , params.email
                , params.phone
                , params.organizationId
                , params.roleId
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

        Map results = [:]
        results.put("editable", springSecurityService.isLoggedIn())

        def registrants = registrantService.list(params.id)

        results.put("records", registrants)

        withFormat  {
            json {
                render registrants as JSON
            }
        }
    }

    def roles()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Map results = [:]
        results.put("editable", springSecurityService.isLoggedIn())

        def roles = registrantService.roles(params.name)

        results.put("records", roles)

        withFormat  {
            json {
                render roles as JSON
            }
        }
    }
}
