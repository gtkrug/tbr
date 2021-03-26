package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import java.nio.charset.StandardCharsets

@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN", "ROLE_USER"])
class ProviderController {

    def springSecurityService

    ProviderService providerService

    def index() { }

    def view()  {
        log.info(params.id)
        Provider provider = providerService.get(params.id)
        [provider: provider]
    }

    def signCertificate()  {
        log.info(params.id)
        Provider provider = providerService.get(params.id)
        [provider: provider]
    }

    def encryptCertificate()  {
        log.info(params.id)
        Provider provider = providerService.get(params.id)
        [provider: provider]
    }

    def upload() {
        User user = springSecurityService.currentUser
        log.info("upload user -> ${user.name}")
        if(params.filename != null)  {
            byte[] buffer = new byte[params.filename.size]
            params.filename.getInputStream().read(buffer)
            String xmlString = new String(buffer, StandardCharsets.UTF_8)
            log.info("File Name: ${params.filename.originalFilename}  ${params.filename.size}")
            deserializeService.deserialize(xmlString)
        }
        [user: user]
    }

    def add()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Provider provider = providerService.add(params.type, params.name, params.entity, params.orgid)
        render provider as JSON
    }

    def delete()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        Organization organization = providerService.delete(params.ids, params.oid)
        render organization as JSON
    }

    def list()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def providers = providerService.list(params.orgid)
        render providers as JSON
    }

    def types()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        def providerTypes = providerService.types()

        render providerTypes as JSON
    }

    def bindTrustmarks() {
        User user = springSecurityService.currentUser
        log.debug("Starting the scan host job...")

        BindTrustmarksToProviderJob.triggerNow([id:params.id])

        Map jsonResponse = [status: 'SUCCESS', message: 'Started the trustmark binding process; trustmarks should be available once bound.'];
        render jsonResponse as JSON;
    }
}
