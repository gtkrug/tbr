package tm.binding.registry

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.apache.commons.lang.StringUtils
import org.dom4j.DocumentException
import org.grails.web.json.JSONArray
import grails.web.mapping.LinkGenerator
import org.gtri.fj.data.Option
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import tm.binding.registry.util.TBRProperties
import tm.binding.registry.util.X500PrincipalWrapper

import java.nio.charset.StandardCharsets
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@PreAuthorize('hasAnyAuthority("tbr-admin", "tbr-org-admin")')
@Transactional
class ProviderController {

    LinkGenerator grailsLinkGenerator

    def deserializeService

    def providerService

    def contactService

    def administrationService

    def x509CertificateService

    def signingCertificateService

    def index() { }


    @PreAuthorize('permitAll()')
    def view()  {
        log.info("** ProviderController::view id -> ${params.id}")
        log.info(params.id)
        Provider provider = providerService.get(params.id)
        if (!provider) {
            log.error("System with id ${params.id} does not exist!")
            return redirect(controller:'error', action:'notFound404')
        }

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
         isLoggedIn: SecurityContextHolder.getContext().getAuthentication().authenticated]
    }

    @PreAuthorize('permitAll()')
    def signCertificate()  {
        log.info(params.id)
        Provider provider = providerService.get(params.id)

        String text = ""
        String contentType = ""

        if (StringUtils.isNotEmpty(provider.signingCertificate)) {
            text = "-----BEGIN CERTIFICATE-----" + System.lineSeparator() +
                    provider.signingCertificate + System.lineSeparator() +
                    "-----END CERTIFICATE-----"
            contentType = 'text/plain'
        } else {
            text = "No signing certificate found!"
            contentType = 'text/html'
        }

        return render(contentType: contentType, text: text)
    }

    @PreAuthorize('permitAll()')
    def encryptCertificate()  {
        log.info(params.id)
        Provider provider = providerService.get(params.id)

        String text = ""
        String contentType = ""

        if (StringUtils.isNotEmpty(provider.encryptionCertificate)) {
            text = "-----BEGIN CERTIFICATE-----" + System.lineSeparator() +
                    provider.encryptionCertificate + System.lineSeparator() +
                    "-----END CERTIFICATE-----"
            contentType = 'text/plain'
        } else {
            text = "No encrypting certificate found!"
            contentType = 'text/html'
        }

        return render(contentType: contentType, text: text)
    }

    // view metadata xml
    @PreAuthorize('permitAll()')
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
            metadataGeneratedDateString = utcStringFromDate(provider.lastTimeSAMLMetadataGeneratedDate) + " UTC"

            log.info("metadataGeneratedDateString: ${metadataGeneratedDateString.toString()}")
        }

        def model = [
                message: message,
                dateSAMLMetadataGenerated: metadataGeneratedDateString
        ]

        render model as JSON
    }

    def upload() {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("upload user -> ${userOption.some().name}")

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

        Map jsonResponse = [messageMap: messageMap, providerId: provider.id, organizationId: provider.organization.id]

        render jsonResponse as JSON
    }

    def uploadCertificate() {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("upload user -> ${userOption.some().name}")

        Provider provider = providerService.get(params.providerId)

        Map messageMap = [:]

        try {
            if (params.filename != null) {

                byte[] buffer = new byte[params.filename.size]
                params.filename.getInputStream().read(buffer)
                String pemString = new String(buffer, StandardCharsets.UTF_8)
                log.info("File Name: ${params.filename.originalFilename}  ${params.filename.size}")

                // URL: create a unique filename to create the downloadable file
                // filename: commonName-thumbprint.pem
                X509Certificate x509Certificate = x509CertificateService.convertFromPem(pemString)

                X500PrincipalWrapper x500Name = new X500PrincipalWrapper(x509Certificate.getSubjectX500Principal().getName())
                String thumbprint = x509CertificateService.getThumbPrint(x509Certificate)

                // remove spaces from common name before creating filename
                String filename = signingCertificateService.replaceNonAlphanumeric(x500Name.commonName, "-") + "-" + thumbprint + ".pem"

                provider.systemCertificateFilename = filename

                // get the base url from the http request and append the controller
                String baseUrl = TBRProperties.getBaseUrl()

                provider.systemCertificateUrl = baseUrl + "/public/system-certificate/" + filename

                provider.systemCertificate = pemString
                messageMap["SUCCESS"] = "Successfully uploaded certificate."
            }
        } catch (Exception e) {
            log.info("Error parsing ${params.filename.originalFilename}, error: ${e.message}")
            messageMap["ERROR"] = "Error loading ${params.filename.originalFilename}"
        }

        // prefix warning and error messages
        if (StringUtils.isNotEmpty(messageMap["WARNING"])) {
            messageMap["WARNING"] = "WARNING: " + messageMap["WARNING"]
        }

        if (StringUtils.isNotEmpty(messageMap["ERROR"])) {
            messageMap["ERROR"] = "ERROR: " + messageMap["ERROR"]
        }

        Map jsonResponse = [messageMap: messageMap, providerId: provider.id, organizationId: provider.organization.id]

        render jsonResponse as JSON
    }

    @PreAuthorize('permitAll()')
    def certificateDetails() {

        Map results = [:]

        Provider provider = Provider.get(Integer.parseInt(params.id))

        results.put("readonly", administrationService.isReadOnly(provider.organizationId))

        String subject = ""
        String issuer = ""

        // Validity
        String notBefore = ""
        String notAfter = ""

        // download url
        String systemCertificateUrl = ""

        if (StringUtils.isNotEmpty(provider.systemCertificate)) {
            X509Certificate x509Certificate = x509CertificateService.convertFromPem(provider.systemCertificate)

            subject = x509Certificate.subjectDN.toString()
            issuer = x509Certificate.issuerDN.name

            // Validity
            notBefore = x509Certificate.notBefore.toString()
            notAfter = x509Certificate.notAfter.toString()

            //download url
            systemCertificateUrl = provider.systemCertificateUrl
        }

        def certificateDetails = [
                systemType                       : provider.providerType.name,
                providerId                       : provider.id,
                subject                          : subject,
                issuer                           : issuer,
                notBefore                        : notBefore,
                notAfter                         : notAfter,
                systemCertificateUrl             : systemCertificateUrl
        ]

        results.put("records", certificateDetails)

        render results as JSON
    }

    def uploadOidcMetadata() {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("upload user -> ${userOption.some().name}")

        Provider provider = providerService.get(params.providerId)

        Map messageMap = [:]

        try {
            if (params.filename != null) {

                byte[] buffer = new byte[params.filename.size]
                params.filename.getInputStream().read(buffer)
                String jsonString = new String(buffer, StandardCharsets.UTF_8)
                log.info("File Name: ${params.filename.originalFilename}  ${params.filename.size}")

                messageMap = deserializeService.deserializeOidcMetadata(jsonString,
                        params.filename.originalFilename, provider)

                messageMap["SUCCESS"] = "Successfully uploaded ${provider.providerType.name} metadata."
            }
        } catch (Exception e) {
            log.info("Error parsing ${params.filename.originalFilename}, error: ${e.message}")
            messageMap["ERROR"] = "Error loading ${params.filename.originalFilename}"
        }

        // prefix warning and error messages
        if (StringUtils.isNotEmpty(messageMap["WARNING"])) {
            messageMap["WARNING"] = "WARNING: " + messageMap["WARNING"]
        }

        if (StringUtils.isNotEmpty(messageMap["ERROR"])) {
            messageMap["ERROR"] = "ERROR: " + messageMap["ERROR"]
        }

        Map jsonResponse = [messageMap: messageMap, providerId: provider.id, organizationId: provider.organization.id]

        render jsonResponse as JSON
    }

    @PreAuthorize('permitAll()')
    def oidcDetails() {
        log.debug("oidcDetails -> ${params.id}")

        Map results = [:]

        Provider provider = Provider.get(Integer.parseInt(params.id))
        String jsonString = provider.openIdConnectMetadata

        results.put("editable", !administrationService.isReadOnly(provider.organizationId))

        Map metadata = [:]

        if (StringUtils.isNotEmpty(jsonString)) {
            OidcBaseMetadataProcessor metadataProcessor = deserializeService.createOidcMetadataProcessor(provider.providerType)

            Optional<Map<String, Object>> optionalMap = metadataProcessor.getLabelValueMap(jsonString)

            if (optionalMap.isPresent()) {
                metadata = optionalMap.get()
            }
        }

        def viewMetadataUrl = grailsLinkGenerator.link(controller: 'provider', action: 'oidcMetadata', id: provider.id, absolute: true)
        boolean hasOidcMetadata = StringUtils.isNotEmpty(jsonString)

        def oidcMetadata = [
                systemType: provider.providerType.name,
                uniqueId: provider.oidcUniqueId,
                openIdConnectMetadata: metadata,
                viewOidcMetadataLink: viewMetadataUrl,
                hasOidcMetadata: hasOidcMetadata
        ]
        results.put("records", oidcMetadata)

        withFormat {
            json {
                render results as JSON
            }
        }
    }

    // view oidc metadata json
    @PreAuthorize('permitAll()')
    def oidcMetadata()  {
        log.info(params.id)
        Provider provider = providerService.get(params.id)
        String text = ""
        String contentType = ""

        if (StringUtils.isNotEmpty(provider.openIdConnectMetadata)) {
            // serialize OIDC Connect
            text = deserializeService.serializeOidc(provider.openIdConnectMetadata, provider)
            contentType = 'text/json'
        }

        return render(contentType: contentType, text: text)
    }

    def add()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        Provider provider = providerService.add(params.type, params.name, params.entity, params.orgid)
        render provider as JSON
    }

    def delete()  {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        Organization organization = providerService.delete(params.ids, params.oid)
        render organization as JSON
    }

    @PreAuthorize('permitAll()')
    def list()  {

        Map results = [:]

        results.put("editable", !administrationService.isReadOnly(Integer.parseInt(params.orgid)))

        def providerBaseUrl = grailsLinkGenerator.link(controller: 'system', action: 'view')
        results.put("providerBaseUrl", providerBaseUrl)

        def providers = providerService.list(params.orgid)
        results.put("records", providers)

        render results as JSON
    }


    @PreAuthorize('permitAll()')
    def listIdpAttributes() {

        Provider provider = Provider.get(params.id)

        def idpAttributes = provider.idpAttributes

        render idpAttributes as JSON
    }

    @PreAuthorize('permitAll()')
    def protocolDetails() {

        Map results = [:]

        Provider provider = Provider.get(Integer.parseInt(params.id))

        results.put("editable", !administrationService.isReadOnly(provider.organizationId))

        def signingCertificateUrl = grailsLinkGenerator.link(controller: 'provider', action: 'signCertificate', id: provider.id)
        def encryptionCertificateUrl = grailsLinkGenerator.link(controller: 'provider', action: 'encryptCertificate', id: provider.id)
        def viewMetadataUrl = grailsLinkGenerator.link(controller: 'provider', action: 'saml2Metadata', id: provider.id, absolute: true)

        String metadataGeneratedDateString = ""
        if (StringUtils.isNotEmpty(provider.saml2MetadataXml)) {
            metadataGeneratedDateString = utcStringFromDate(provider.lastTimeSAMLMetadataGeneratedDate) + " UTC"

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

    private String utcStringFromDate(Date date) {
        LocalDateTime metadataGeneratedDate = LocalDateTime.ofInstant(
                date.toInstant(), ZoneOffset.UTC)

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        return metadataGeneratedDate.format(fmt)
    }

    def types()  {

        def providerTypes = providerService.types()

        JSONArray jsonArray = new JSONArray()
        for (String s : providerTypes) {
            jsonArray.put(s)
        }

        render jsonArray as JSON
    }

    @PreAuthorize('permitAll()')
    def trustmarkRecipientIdentifiers() {
        log.debug("trustmarkRecipientIdentifiers -> ${params.pid}")

        Map results = [:]

        Provider provider = Provider.get(Integer.parseInt(params.pid))

        results.put("editable", !administrationService.isReadOnly(provider.organizationId))

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

        Map results = [:]

        Map messageMap = providerService.addTrustmarkRecipientIdentifier(params.pid, params.identifier)

        results.put("status", messageMap)

        withFormat {
            json {
                render results as JSON
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
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        TrustmarkRecipientIdentifier trid = providerService.updateTrustmarkRecipientIdentifier(params.id, params.trustmarkRecipientIdentifier, params.providerId)

        withFormat {
            json {
                render trid as JSON
            }
        }
    }

    // partner systems tips
    @PreAuthorize('permitAll()')
    def partnerSystemsTips()  {
        log.debug("partnerSystemsTips -> ${params.pid}")

        Map results = [:]

        Provider provider = Provider.get(Integer.parseInt(params.pid))

        results.put("editable", !administrationService.isReadOnly(provider.organizationId))

        def partnerSystemsTips = providerService.partnerSystemsTips(params.pid)
        results.put("records", partnerSystemsTips)

        withFormat  {
            json {
                render results as JSON
            }
        }
    }

    def addPartnerSystemsTip() {
        Option<User> userOption = User.findByUsernameHelper(((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getName())

        log.info("user -> ${userOption.some().name}")

        log.info("add partner systems tip identifier -> ${params.identifier}")

        def results = [:]

        def messageMap = [:]

        def partnerSystemsTips = []

        Provider provider = Provider.get(Integer.parseInt(params.pId))

        PartnerSystemsTip tip = PartnerSystemsTip.findByPartnerSystemsTipIdentifier(params.identifier)
        boolean tipAlreadyExists = false

        if (tip) {
            PartnerSystemsTip tempTip = provider.partnerSystemsTips.stream()
                    .filter({ tempTip -> tip.partnerSystemsTipIdentifier.equals(tempTip.partnerSystemsTipIdentifier) })
                    .findAny()
                    .orElse(null)

            if(tempTip) {
                tipAlreadyExists = true
            }
        }

        if (tipAlreadyExists) {
            messageMap.put("WARNING", "WARNING: Partner system TIP \"${tip.name}\" already exists." )
        } else {
            try {
                partnerSystemsTips.add(administrationService.addPartnerSystemsTipForSystem(params.pId, params.identifier))

                messageMap.put("SUCCESS", "SUCCESS: Successfully added partner system TIP.")

            } catch (Throwable t) {
                log.error("Unable to add TIP: ${params.identifier}")
                messageMap.put("ERROR", "ERROR: Failed to find partner system TIP at URL: ${params.identifier}.")
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

    def deletePartnerSystemsTips() {

        Provider provider = providerService.deletePartnerSystemsTips(params.ids, params.pid)

        withFormat  {
            json {
                render provider as JSON
            }
        }
    }

    @PreAuthorize('permitAll()')
    def listContacts()  {

        // compute editable based on current user role and whether the use is assigned an organization
        boolean isReadOnly = administrationService.isReadOnly( Long.parseLong(params.id))

        Map results = [:]
        results.put("editable", !isReadOnly)

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
        log.info("** bindTrustmarks...")

        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "About to start the trustmark binding process...")
        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_STATUS_VAR, "RUNNING")
        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0")

        final Integer providerId = Integer.parseInt(params.id)

        Map status = [:]

        // Do not start the bind trustmarks processing if it is already running
        if (!providerService.isExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)) {

            // initialize status
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Binding trustmarks")
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0")
            providerService.setExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)

            status = providerService.bindTrustmarks(providerId)
        }

        Provider provider = Provider.get(providerId)

        Map jsonResponse = [status                       : status,
                            numberOfTrustmarksBound      : provider.trustmarks.size(),
                            numberOfConformanceTargetTIPs: provider.conformanceTargetTips.size()]

        render jsonResponse as JSON
    }

    def initTrustmarkBindingState() {

        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "About to start the trustmark binding process...")
        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_STATUS_VAR, "RUNNING")
        providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0")

        providerService.stopExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)

        Map jsonResponse = [status: 'SUCCESS', message: 'Successfully initialized trustmark binding process.']

        render jsonResponse as JSON
    }

    def cancelTrustmarkBindings() {

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
