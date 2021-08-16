package tm.binding.registry

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import org.apache.commons.lang.StringUtils
import org.dom4j.DocumentException
import org.grails.web.json.JSONArray
import grails.web.mapping.LinkGenerator
import javax.xml.bind.DatatypeConverter
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Secured(["ROLE_ADMIN","ROLE_ORG_ADMIN", "ROLE_USER"])
@Transactional
class ProviderController {

    LinkGenerator grailsLinkGenerator

    def springSecurityService

    def deserializeService

    def providerService

    def contactService

    def administrationService

    def index() { }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def view()  {
        log.info("** ProviderController::view id -> ${params.id}")
        log.info(params.id)
        Provider provider = providerService.get(params.id)

        Map messageMap = [:]

        if(params.filename != null)  {

            try {
                Organization organization = provider.organization
                byte[] buffer = new byte[params.filename.size]
                params.filename.getInputStream().read(buffer)
                String xmlString = new String(buffer, StandardCharsets.UTF_8)
                log.info("File Name: ${params.filename.originalFilename}  ${params.filename.size}")
                messageMap = deserializeService.deserialize(xmlString, provider)
            } catch (DocumentException de) {
                log.info("Error parsing metadata, error: ${de.message}")
                messageMap["ERROR"] = "Error parsing metadata."
                if(!provider.isAttached()) {
                    provider.attach()
                }
            } catch (Exception e) {
                log.info("Error parsing metadata, error: ${e.message}")
                messageMap["ERROR"] = "Error parsing metadata."
                if(!provider.isAttached()) {
                    provider.attach()
                }
            }
        }

        [provider: provider, successMessage: messageMap["SUCCESS"],
         warningMessage: messageMap["WARNING"], errorMessage: messageMap["ERROR"],
         isLoggedIn: springSecurityService.isLoggedIn()]
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def signCertificate()  {
        log.info(params.id)
        Provider provider = providerService.get(params.id)
        [provider: provider]
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def encryptCertificate()  {
        log.info(params.id)
        Provider provider = providerService.get(params.id)
        [provider: provider]
    }

    // view metadata xml
    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def saml2Metadata()  {
        log.info(params.id)
        Provider provider = providerService.get(params.id)
        String text = ""
        String contentType = ""

        if (StringUtils.isNotEmpty(provider.saml2MetadataXml)) {
            text = provider.saml2MetadataXml
            contentType = 'text/xml'
        } else {
            text = "SAML 2 Metadata has not been generated!"
            contentType = 'text/html'
        }

        return render(contentType: contentType, text: text)
    }

    def generateSaml2Metadata() {
        log.info("generateSaml2Metadata for ${params.id}")

        Provider provider = providerService.get(params.id)

        deserializeService.serialize(provider)

        String message = "Successfully serialized SAML 2 metadata."

        String metadataGeneratedDateString = ""
        if (StringUtils.isNotEmpty(provider.saml2MetadataXml)) {
            LocalDateTime metadataGeneratedDate =
                    provider.lastTimeSAMLMetadataGeneratedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            metadataGeneratedDateString = metadataGeneratedDate.format(fmt)
        }

        def model = [
                message: message,
                dateSAMLMetadataGenerated: metadataGeneratedDateString
        ]

        render model as JSON
    }

    def upload() {
        User user = springSecurityService.currentUser
        log.info("upload user -> ${user.name}")

        Provider provider = providerService.get(params.providerId)

        Map messageMap = [:]

        try {
            if (params.filename != null) {

                byte[] buffer = new byte[params.filename.size]
                params.filename.getInputStream().read(buffer)
                String xmlString = new String(buffer, StandardCharsets.UTF_8)
                log.info("File Name: ${params.filename.originalFilename}  ${params.filename.size}")

                messageMap = deserializeService.deserialize(xmlString, provider)
            }
        } catch (DocumentException de) {
            log.info("Error parsing ${params.filename.originalFilename}, error: ${de.message}")
            messageMap["ERROR"] = de.message
        } catch (Exception e) {
            log.info("Error parsing ${params.filename.originalFilename}, error: ${e.message}")
            messageMap["ERROR"] = "Error parsing ${params.filename.originalFilename}"
        }

        // prefix warning and error messages
        if (StringUtils.isNotEmpty(messageMap["WARNING"])) {
            messageMap["WARNING"] = "WARNING: " + messageMap["WARNING"]
        }

        if (StringUtils.isNotEmpty(messageMap["ERROR"])) {
            messageMap["ERROR"] = "ERROR: " + messageMap["ERROR"]
        }

        Map jsonResponse = [messageMap: messageMap, providerId: provider.id]

        render jsonResponse as JSON
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

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def list()  {
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

        Map results = [:]
        results.put("editable", springSecurityService.isLoggedIn())

        def providerBaseUrl = grailsLinkGenerator.link(controller: 'system', action: 'view')
        results.put("providerBaseUrl", providerBaseUrl)

        def providers = providerService.list(params.orgid)
        results.put("records", providers)

        render results as JSON
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def listIdpAttributes() {
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

        Provider provider = Provider.get(params.id)

        def idpAttributes = provider.idpAttributes

        render idpAttributes as JSON
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def protocolDetails() {
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

        Map results = [:]
        results.put("editable", springSecurityService.isLoggedIn())

        Provider provider = Provider.get(params.id)

        def signingCertificateUrl = grailsLinkGenerator.link(controller: 'provider', action: 'signCertificate', id: provider.id)
        def encryptionCertificateUrl = grailsLinkGenerator.link(controller: 'provider', action: 'encryptCertificate', id: provider.id)
        def viewMetadataUrl = grailsLinkGenerator.link(controller: 'provider', action: 'saml2Metadata', id: provider.id, absolute: true)

        String metadataGeneratedDateString = ""
        if (StringUtils.isNotEmpty(provider.saml2MetadataXml)) {
            LocalDateTime metadataGeneratedDate =
                    provider.lastTimeSAMLMetadataGeneratedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            metadataGeneratedDateString = metadataGeneratedDate.format(fmt)
            viewMetadataUrl = provider.saml2MetadataUrl
        }

        def protocolDetails = [
                entityId                         : provider.entityId,
                systemType                       : provider.providerType.name,
                nameIdFormats                    : provider.nameFormats,
                signingCertificateLink           : signingCertificateUrl,
                encryptionCertificateLink        : encryptionCertificateUrl,
                providerId                       : provider.id,
                hasSamlMetadataGenerated         : StringUtils.isNotEmpty(provider.saml2MetadataXml),
                viewSamlMetadataLink             : viewMetadataUrl,
                lastTimeSAMLMetadataGeneratedDate: metadataGeneratedDateString
        ]

        results.put("records", protocolDetails)

        render results as JSON
    }

    def types()  {
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

        def providerTypes = providerService.types()

        JSONArray jsonArray = new JSONArray()
        for (String s : providerTypes) {
            jsonArray.put(s)
        }

        render jsonArray as JSON
    }

    def trustmarkRecipientIdentifiers() {
        log.debug("trustmarkRecipientIdentifiers -> ${params.pid}")

        Map results = [:]
        results.put("editable", springSecurityService.isLoggedIn())

        def trustmarkRecipientIdentifiers = providerService.trustmarkRecipientIdentifiers(params.pid)
        results.put("records", trustmarkRecipientIdentifiers)

        withFormat {
            json {
                render results as JSON
            }
        }
    }

    def addTrustmarkRecipientIdentifier() {
        log.debug("system -> ${params.pid}")

        TrustmarkRecipientIdentifier trustmarkRecipientIdentifier = providerService.addTrustmarkRecipientIdentifier(params.pid, params.identifier)

        withFormat {
            json {
                render trustmarkRecipientIdentifier as JSON
            }
        }
    }

    def deleteTrustmarkRecipientIdentifiers() {

        Provider provider = providerService.deleteTrustmarkRecipientIdentifiers(params.ids, params.pid)

        withFormat {
            json {
                render provider as JSON
            }
        }
    }

    def getTrustmarkRecipientIdentifier() {
        log.debug("provider -> ${params.pid}")

        TrustmarkRecipientIdentifier trustmarkRecipientIdentifier = providerService.getTrustmarkRecipientIdentifier(params.pid, params.rid)

        withFormat {
            json {
                render trustmarkRecipientIdentifier as JSON
            }
        }
    }

    def updateTrustmarkRecipientIdentifier() {
        User user = springSecurityService.currentUser
        log.info("user -> ${user.name}")

        TrustmarkRecipientIdentifier trid = providerService.updateTrustmarkRecipientIdentifier(params.id, params.trustmarkRecipientIdentifier, params.providerId)

        withFormat {
            json {
                render trid as JSON
            }
        }
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def listContacts()  {
        if (springSecurityService.isLoggedIn()) {
            User user = springSecurityService.currentUser
            log.info("user -> ${user.name}")
        }

        Map results = [:]
        results.put("editable", springSecurityService.isLoggedIn())

        def orgContacts = []

        if(params.id != null)  {
            orgContacts = contactService.list(params.id)
        }

        def systemContacts = []
        if(params.pid != null)  {
            systemContacts = administrationService.listContacts(params.pid)
        }

        def contacts = []

        // cross-reference to set inSystem flag
        orgContacts.forEach({oc ->

            Map contact = [:]
            contact.put("contact", oc)

            def inSystem = false
            def c = systemContacts.find({cs ->
                oc.id == cs.id
            })

            if (c) {
                inSystem = true
            }

            contact.put("inSystem", inSystem)

            contacts.add(contact)
        })

        results.put("records", contacts)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }

    def addContactToSystem() {
        log.info("addContactToSystem -> ${params.contactId}, ${params.providerId}")

        Contact contact = Contact.get(Integer.parseInt(params.contactId))

        administrationService.addContactToProvider(contact, params.providerId)

        withFormat {
            json {
                render contact as JSON
            }
        }
    }

    def removeContactFromSystem() {
        log.info("removeContactFromSystem -> ${params.contactId}, ${params.providerId}")

        Contact contact = Contact.get(Integer.parseInt(params.contactId))

        administrationService.removeContactFromProvider(contact, params.providerId)

        withFormat {
            json {
                render contact as JSON
            }
        }
    }

    def bindTrustmarks() {
        User user = springSecurityService.currentUser
        log.info("** bindTrustmarks...")

        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "About to start the trustmark binding process...")
        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_STATUS_VAR, "RUNNING")
        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0")

        final Integer providerId = Integer.parseInt(params.id)

        // Do not start the bind trustmarks processing if it is already running
        if (!providerService.isExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)) {

            // initialize status
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Binding trustmarks")
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0")
            providerService.setExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)

            providerService.bindTrustmarks(providerId)
        }

        Provider provider = Provider.get(providerId)

        Map jsonResponse = [status                       : 'SUCCESS', message: 'Successfully finished trustmark binding process.',
                            numberOfTrustmarksBound      : provider.trustmarks.size(),
                            numberOfConformanceTargetTIPs: provider.conformanceTargetTips.size()]

        render jsonResponse as JSON
    }

    def initTrustmarkBindingState() {
        User user = springSecurityService.currentUser

        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "About to start the trustmark binding process...")
        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_STATUS_VAR, "RUNNING")
        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0")

        providerService.stopExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)

        Map jsonResponse = [status: 'SUCCESS', message: 'Successfully initialized trustmark binding process.']

        render jsonResponse as JSON
    }

    def cancelTrustmarkBindings() {
        User user = springSecurityService.currentUser

        // Check if operation is active, interrupt operation and wait for the thread to finish
        if (providerService.isExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)) {
            providerService.stopExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)

            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_STATUS_VAR, "CANCELLING")
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Cancelling the trustmark binding process...")
        }

        Integer providerId = Integer.parseInt(params.id)
        Provider provider = Provider.get(providerId)


        Map jsonResponse = [status                       : 'SUCCESS', message: 'Successfully canceled trustmark binding process.',
                            numberOfTrustmarksBound      : provider.trustmarks.size(),
                            numberOfConformanceTargetTIPs: provider.conformanceTargetTips.size()]

        render jsonResponse as JSON
    }

    def updateTrustmarkBindingDetails() {
        User user = springSecurityService.currentUser

        Integer providerId = Integer.parseInt(params.id)

        Provider provider = Provider.get(providerId)

        Map jsonResponse = [numberOfTrustmarksBound      : provider.trustmarks.size(),
                            numberOfConformanceTargetTIPs: provider.conformanceTargetTips.size()]

        render jsonResponse as JSON
    }

    def trustmarkBindingStatusUpdate() {

        Map jsonResponse = [:]

        jsonResponse.put("status", providerService.getAttribute(ProviderService.BIND_TRUSTMARKS_STATUS_VAR))

        String percentString = providerService.getAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR)

        int percentInt = 0
        if( StringUtils.isNotEmpty(percentString) ){
            percentInt = Integer.parseInt(percentString.trim())
        }
        jsonResponse.put("percent", percentInt)
        jsonResponse.put("message", providerService.getAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR))

        render jsonResponse as JSON
    }
}
