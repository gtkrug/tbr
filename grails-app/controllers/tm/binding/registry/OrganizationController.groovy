package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import java.nio.charset.StandardCharsets

@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN", "ROLE_USER"])
class OrganizationController {

    OrganizationService organizationService

    DeserializeService deserializeService

    def index() { }
    
    def insert() { }

    def administer() { }

    def manage() { }

    def view() {
        log.info(params.id)
        Organization organization = organizationService.get(params.id)
        if(params.filename != null)  {
            byte[] buffer = new byte[params.filename.size]
            params.filename.getInputStream().read(buffer)
            String xmlString = new String(buffer, StandardCharsets.UTF_8)
            log.info("File Name: ${params.filename.originalFilename}  ${params.filename.size}")
            deserializeService.deserialize(xmlString, organization)
        }
        [organization: organization]
    }

    def add()  {
        log.debug("add -> ${params.name}")

        Organization organization = organizationService.add(params.name
                                                            , params.displayName
                                                            , params.siteUrl
                                                            , params.description
                                                            )

        withFormat  {
            json {
                render organization as JSON
            }
        }
    }

    def get()  {
        log.info("get -> ${params.id}")

        Organization organization = organizationService.get(params.id)

        withFormat  {
            json {
                render organization as JSON
            }
        }
    }

    def delete()  {
        log.info("delete -> ${params.ids}")

        Organization organization = organizationService.delete(params.ids)

        withFormat  {
            json {
                render organization as JSON
            }
        }
    }

    def update()  {
        log.info("update -> ${params.id}")

        Organization organization = organizationService.update(params.id, params.url, params.desc, params.display)

        withFormat  {
            json {
                render organization as JSON
            }
        }
    }

    def list()  {
        log.debug("list -> ${params.name}")

        def organizations = organizationService.list(params.name)

        withFormat  {
            json {
                render organizations as JSON
            }
        }
    }

    def repos()  {
        log.debug("repos -> ${params.oid}")

        def repos = organizationService.repos(params.oid)

        withFormat  {
            json {
                render repos as JSON
            }
        }
    }

    def addRepo()  {
        log.debug("repos -> ${params.orgid}")

        AssessmentRepository assessmentRepository = organizationService.addRepos(params.orgid, params.name)

        withFormat  {
            json {
                render assessmentRepository as JSON
            }
        }
    }

    def deleteRepo()  {
        log.debug("repos -> ${params.orgid} ${params.rid}")

        Organization organization = organizationService.deleteRepos(params.orgid, params.rid)

        withFormat  {
            json {
                render organization as JSON
            }
        }
    }

    def trustmarkRecipientIdentifiers()  {
        log.debug("trustmarkRecipientIdentifiers -> ${params.oid}")

        def trustmarkRecipientIdentifiers = organizationService.trustmarkRecipientIdentifiers(params.oid)

        withFormat  {
            json {
                render trustmarkRecipientIdentifiers as JSON
            }
        }
    }

    def addTrustmarkRecipientIdentifier()  {
        log.debug("organization -> ${params.orgid}")

        TrustmarkRecipientIdentifier trustmarkRecipientIdentifier = organizationService.addTrustmarkRecipientIdentifier(params.orgid, params.identifier)

        withFormat  {
            json {
                render trustmarkRecipientIdentifier as JSON
            }
        }
    }

    def deleteTrustmarkRecipientIdentifier()  {
        log.debug("repos -> ${params.orgid} ${params.tmrid}")

        Organization organization = organizationService.deleteTrustmarkRecipientIdentifier(params.orgid, params.tmrid)

        withFormat  {
            json {
                render organization as JSON
            }
        }
    }
}
