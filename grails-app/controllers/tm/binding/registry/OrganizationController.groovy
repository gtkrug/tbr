package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import java.nio.charset.StandardCharsets

@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN", "ROLE_USER"])
class OrganizationController {

    OrganizationService organizationService

    DeserializeService deserializeService

    def springSecurityService

    def index() { }
    
    def insert() { }

    def administer() { }

    def manage() { }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
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
        [organization: organization, isLoggedIn: springSecurityService.isLoggedIn()]
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

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
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

        Map results = [:]
        results.put("editable", springSecurityService.isLoggedIn())

        def repos = organizationService.repos(params.oid)
        results.put("records", repos)

        withFormat  {
            json {
                render results as JSON
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

    def getRepo()  {
        log.debug("repos -> ${params.orgid}")

        AssessmentRepository assessmentRepository = organizationService.getRepo(params.orgid, params.rid)

        withFormat  {
            json {
                render assessmentRepository as JSON
            }
        }
    }

    def updateRepo()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        AssessmentRepository assessmentRepository = organizationService.updateRepo(params.id, params.repoUrl, params.organizationId)

        withFormat  {
            json {
                render assessmentRepository as JSON
            }
        }
    }

    def deleteRepos()  {
        log.debug("repos -> ${params.orgid} ${params.rid}")

        Organization organization = organizationService.deleteRepos(params.ids, params.orgid)

        withFormat  {
            json {
                render organization as JSON
            }
        }
    }

    def trustmarkRecipientIdentifiers()  {
        log.debug("trustmarkRecipientIdentifiers -> ${params.oid}")

        Map results = [:]
        results.put("editable", springSecurityService.isLoggedIn())

        def trustmarkRecipientIdentifiers = organizationService.trustmarkRecipientIdentifiers(params.oid)
        results.put("records", trustmarkRecipientIdentifiers)

        withFormat  {
            json {
                render results as JSON
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

    def deleteTrustmarkRecipientIdentifiers()  {

        Organization organization = organizationService.deleteTrustmarkRecipientIdentifiers(params.ids, params.orgid)

        withFormat  {
            json {
                render organization as JSON
            }
        }
    }

    def getTrustmarkRecipientIdentifier()  {
        log.debug("repos -> ${params.orgid}")

        TrustmarkRecipientIdentifier trustmarkRecipientIdentifier = organizationService.getTrustmarkRecipientIdentifier(params.orgid, params.rid)

        withFormat  {
            json {
                render trustmarkRecipientIdentifier as JSON
            }
        }
    }

    def updateTrustmarkRecipientIdentifier()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        TrustmarkRecipientIdentifier trid = organizationService.updateTrustmarkRecipientIdentifier(params.id, params.trustmarkRecipientIdentifier, params.organizationId)

        withFormat  {
            json {
                render trid as JSON
            }
        }
    }
}
