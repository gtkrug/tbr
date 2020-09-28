package tm.binding.registry

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/" (controller: 'index', view:'/index')
        "500" (view:'/error')
        "404" (view:'/notFound')
    }
}
