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
        "/public/trustmarks/find-by-organization/$organizationUri"(controller: 'publicApi', action: 'findByOrganization')
        "/public/organizations"(controller: 'publicApi', action: 'organizations')

        "/provider/generate-metadata/$id"(controller: "provider", action: "generateSaml2Metadata")

        "/system/view/$id"(controller: "provider", action: "view")
        "/system/signCertificate/$id"(controller: "provider", action: "signCertificate")
        "/system/encryptCertificate/$id"(controller: "provider", action: "encryptCertificate")
        "/system/saml2Metadata/$id"(controller: "provider", action: "saml2Metadata")

        "/documents/$id"(controller:'document', action: 'pdf')

        "/reset-password"(controller:'forgotPassword', action: 'index')

        "/public/documents/$id"(controller: 'publicApi', action: 'findDocs')
        "/public/documents/$name"(controller:'publicApi', action: 'pdfByName')

        "/public/signingcertificates/$id"(controller: 'publicApi', action: 'findSigningCertificates')

        "/public/status"(controller: 'publicApi', action: 'status')

        "500" (view:'/error')
        "404" (view:'/notFound')
    }
}
