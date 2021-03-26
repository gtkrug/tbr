package tm.binding.registry

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/" (controller: 'index', view:'/index')

        "/public/systems/$type" (controller: 'public', action: 'listByType')
        "/public/certificates/$filename"(controller:'public', action: 'download', id: '$filename')

        "500" (view:'/error')
        "404" (view:'/notFound')
    }
}
