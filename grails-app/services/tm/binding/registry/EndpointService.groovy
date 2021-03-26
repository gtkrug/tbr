package tm.binding.registry

import grails.gorm.transactions.Transactional

@Transactional
class EndpointService {

    def serviceMethod(String... args) {
        log.info("service -> ${args[0]}")

    }

    /**
     * add an endpoint under a provider
     * @param args
     * @return
     */
    def add(String... args) {
        log.info("add -> ${args[0]} ${args[1]} ${args[2]} ${args[3]}")

        Endpoint endpoint = null
        try  {
            Provider provider = Provider.get(Integer.parseInt(args[3]))

            endpoint = new Endpoint(name: args[0]
                    , url: args[1]
                    , binding: args[2]
                    , provider: provider)

            endpoint.save(true)

            provider.endpoints.add(endpoint)

            provider.save(true)

        }  catch (NumberFormatException nfe)  {
            log.error("Bad provider id ${args[3]}")
        }
        return endpoint
    }

    def get(String... args) {
        log.info("get -> ${args[0]}")

        Endpoint endpoint = null
        try  {
            endpoint = Endpoint.get(Integer.parseInt(args[0]))
        } catch (NumberFormatException nfe)  {
            endpoint = Endpoint.findByUrl(args[0])
        }
        return endpoint
    }

    def update(String... args) {
        log.info("update -> ${args[0]}")

        Endpoint endpoint = Endpoint.get(args[0])
        endpoint.save(true)
        return endpoint
    }

    def delete(String... args) {
        log.info("delete -> ${args[0]} ${args[1]}")

        List<String> ids = args[0].split(":")

        Provider provider = Provider.get(Integer.parseInt(args[1]))
        try  {
            ids.forEach({s ->
                if(s.length() > 0)  {
                    Endpoint endpoint = Endpoint.get(Integer.parseInt(s))
                    provider.endpoints.remove(endpoint)
                    endpoint.delete()
                }
            })
        } catch (NumberFormatException nfe)  {
            log.error("Invalid Endpoint Id!")
        }
        return provider
    }

    def list(String... args) {
        log.info("list -> ${args[0]}")

        def endpoints = []

        try  {
            Provider provider = Provider.get(Integer.parseInt(args[0]))
            if(provider != null)  {
                Endpoint.findAllByProvider(provider).forEach({e -> endpoints.add(e.toJsonMap())})
            }
        } catch (NumberFormatException nfe)  {
            Endpoint.findAll().forEach({e -> endpoints.add(e.toJsonMap())})
        }
        return endpoints
    }
}
