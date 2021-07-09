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

        "/provider/generate-metadata/$id"(controller: "provider", action: "generateSaml2Metadata")

        "/documents/$id"(controller:'document', action: 'pdf')

        "/public/documents/$id"(controller: 'publicApi', action: 'findDocs')
        "/public/documents/$name"(controller:'publicApi', action: 'pdfByName')

        "/public/signingcertificates/$id"(controller: 'publicApi', action: 'findSigningCertificates')

        "500" (view:'/error')
        "404" (view:'/notFound')
    }
}
