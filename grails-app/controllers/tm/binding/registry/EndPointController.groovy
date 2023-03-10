package tm.binding.registry

import grails.converters.JSON
import org.gtri.fj.data.Option
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

class EndPointController {

    EndpointService endpointService

    def index() {

    }

    def administer() {

    }

    /**
     * list of endpoints related to a specific organization
     * @return
     */
    @PreAuthorize('permitAll()')
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
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

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
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

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
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

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
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

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
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        List<EndpointType> types = EndpointType.values().toList()
        withFormat {
            json {
                render types as JSON
            }
        }
    }
}
