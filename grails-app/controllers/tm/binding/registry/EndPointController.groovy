package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import java.nio.charset.StandardCharsets

class EndPointController {

    def springSecurityService

    EndpointService endpointService

    RegistrantService registrantService

    DeserializeService deserializeService

    def index() {
        User user = springSecurityService.currentUser
        log.info("index user -> ${user.name}")
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

    /**
     * list of endpoints related to a specific organization
     * @return
     */
    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def list()  {

        def endpoints = endpointService.list(params.id)

        withFormat {
            json {
                render endpoints as JSON
            }
        }
    }

    /**
     * get a specific endpoint based on id
     * @return
     */
    def get()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Endpoint endpoint = endpointService.get(params.id)
        withFormat {
            json {
                render endpoint.toJsonMap() as JSON
            }
        }
    }

    /**
     * takes a string of ids and removes each of them
     * @return
     */
    def delete()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Provider provider = endpointService.delete(params.ids, params.pid)
        withFormat {
            json {
                render provider.toJsonMap() as JSON
            }
        }
    }

    /**
     * add an endpoint
     * @return
     */
    def add()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Endpoint endpoint = endpointService.add(params.name
                                               , params.url
                                               , params.binding
                                               , params.pId
                                               )
        withFormat {
            json {
                render endpoint.toJsonMap() as JSON
            }
        }
    }

    /**
     * update an endpoint
     * @return
     */
    def update()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Endpoint endpoint = endpointService.update(params.name
                , params.url
                , params.binding
                , params.pId
        )
        withFormat {
            json {
                render endpoint.toJsonMap() as JSON
            }
        }
    }

    /**
     * list all endpoint types
     * @return
     */
    def etypes()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        List<EndpointType> types = EndpointType.values().toList()
        withFormat {
            json {
                render types as JSON
            }
        }
    }
}
