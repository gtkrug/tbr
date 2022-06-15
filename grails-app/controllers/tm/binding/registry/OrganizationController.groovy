package tm.binding.registry

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import java.nio.charset.StandardCharsets

@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN"])
class OrganizationController {

    OrganizationService organizationService

    DeserializeService deserializeService

    AdministrationService administrationService

    def springSecurityService

    def index() { }
    
    def insert() { }

    def administer() { }

    def manage() { }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def view() {
        log.info(params.id)
        Organization organization = organizationService.get(params.id)
        if (!organization) {
            log.error("Organization with id ${params.id} does not exist!")
            return redirect(controller:'error', action:'notFound404')
        }

        if(params.filename != null)  {
            byte[] buffer = new byte[params.filename.size]
            params.filename.getInputStream().read(buffer)
            String xmlString = new String(buffer, StandardCharsets.UTF_8)
            log.info("File Name: ${params.filename.originalFilename}  ${params.filename.size}")
            deserializeService.deserialize(xmlString, organization)
        }
        boolean isReadOnly = administrationService.isReadOnly(organization.id)

        [organization: organization, isLoggedIn: springSecurityService.isLoggedIn(), isReadOnly: isReadOnly]
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

        results.put("editable", !administrationService.isReadOnly(Integer.parseInt(params.oid)))

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

        Map results = [:]

        Map messageMap = organizationService.addRepos(params.orgid, params.name)

        results.put("status", messageMap)

        withFormat  {
            json {
                render results as JSON
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

        results.put("editable", !administrationService.isReadOnly(Integer.parseInt(params.oid)))

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

        Map results = [:]

        Map messageMap = organizationService.addTrustmarkRecipientIdentifier(params.orgid, params.identifier)

        results.put("status", messageMap)

        withFormat  {
            json {
                render results as JSON
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

    // Partner systems tips
    def partnerSystemsTips()  {
        log.debug("partnerSystemsTips -> ${params.oid}")

        Map results = [:]

        results.put("editable", !administrationService.isReadOnly(Integer.parseInt(params.oid)))

        def partnerSystemsTips = organizationService.partnerSystemsTips(params.oid)
        results.put("records", partnerSystemsTips)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }

    def addPartnerSystemsTip()  {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        log.info("add partner systems tip identifier -> ${params.identifier}")

        def results = [:]

        def messageMap = [:]

        def partnerSystemsTips = []

        Organization organization = Organization.get(Integer.parseInt(params.oid))

        PartnerSystemsTip tip = PartnerSystemsTip.findByPartnerSystemsTipIdentifier(params.identifier)
        boolean tipAlreadyExists = false

        if (tip) {
            PartnerSystemsTip tempTip = organization.partnerSystemsTips.stream()
                .filter({ tempTip -> tip.partnerSystemsTipIdentifier.equals(tempTip.partnerSystemsTipIdentifier) })
                .findAny()
                .orElse(null)

            if(tempTip) {
                tipAlreadyExists = true
            }
        }

        if (tipAlreadyExists) {
            messageMap.put("WARNING", "WARNING: Partner organization TIP \"${tip.name}\" already exists." )
        } else {
            try {
                partnerSystemsTips.add(administrationService.addPartnerSystemsTipForOrganization(params.oid, params.identifier))

                messageMap.put("SUCCESS", "SUCCESS: Successfully added partner organization TIP.")

            } catch (Throwable t) {
                log.error("Unable to add TIP: ${params.identifier}")
                messageMap.put("ERROR", "ERROR: Failed to find partner organization TIP at URL: ${params.identifier}.")
            }
        }

        results.put("status", messageMap)
        results.put("partnerSystemsTips", partnerSystemsTips)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }

    def deletePartnerSystemsTips()  {

        Organization organization = organizationService.deletePartnerSystemsTips(params.ids, params.oid)

        withFormat  {
            json {
                render organization as JSON
            }
        }
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def trustmarks() {
        log.info("listbyOrganization for organization id: " + params.id)

        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

        def trustmarks = administrationService.listTrustmarksByOrganization(params.id)

        // sort ascending by name
        trustmarks.sort( { a, b ->
            a.name <=> b.name
        })

        withFormat  {
            json {
                render trustmarks as JSON
            }
        }
    }

    def bindTrustmarks() {
        User user = springSecurityService.currentUser
        log.info("bindTrustmarks...")

        final Integer organizationId = Integer.parseInt(params.id)

        organizationService.bindTrustmarksToOrganization(organizationId)

        Organization organization = Organization.get(organizationId)

        Map jsonResponse = [status                       : 'SUCCESS', message: 'Successfully finished trustmark binding process.',
                            numberOfTrustmarksBound      : organization.trustmarks.size()]

        render jsonResponse as JSON
    }

    def updateTrustmarkBindingDetails() {
        User user = springSecurityService.currentUser

        Integer organizationId = Integer.parseInt(params.id)

        Organization organization = Organization.get(organizationId)

        Map jsonResponse = [numberOfTrustmarksBound: organization.trustmarks.size()]

        render jsonResponse as JSON
    }
}
