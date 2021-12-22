package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.FactoryLoader
import edu.gatech.gtri.trustmark.v1_0.impl.io.IOUtils
import edu.gatech.gtri.trustmark.v1_0.impl.model.TrustInteroperabilityProfileReferenceImpl
import edu.gatech.gtri.trustmark.v1_0.impl.model.TrustmarkDefinitionRequirementImpl
import edu.gatech.gtri.trustmark.v1_0.io.TrustInteroperabilityProfileResolver
import edu.gatech.gtri.trustmark.v1_0.model.AbstractTIPReference
import edu.gatech.gtri.trustmark.v1_0.model.TrustInteroperabilityProfile
import grails.gorm.transactions.Transactional
import org.apache.commons.lang.StringUtils
import org.json.JSONArray
import org.json.JSONObject
import tm.binding.registry.ProviderType
import org.grails.web.util.WebUtils
import tm.binding.registry.util.UrlEncodingUtil


@Transactional
class ProviderService {

    def deserializeService

    public static final String TRUSTMARKS_FIND_BY_RECIPIENT_TAT_ENDPOINT = "public/trustmarks/find-by-recipient/"

    // execution
    public static final String BIND_TRUSTMARKS_EXECUTING_VAR = ProviderService.class.simpleName + ".BIND_TRUSTMARKS_EXECUTING"

    // messaging
    public static final String BIND_TRUSTMARKS_STATUS_VAR = ProviderService.class.getName() + ".BIND_TRUSTMARKS_STATUS"
    public static final String BIND_TRUSTMARKS_PERCENT_VAR = ProviderService.class.getName() + ".BIND_TRUSTMARKS_PERCENT"
    public static final String BIND_TRUSTMARKS_MESSAGE_VAR = ProviderService.class.getName() + ".BIND_TRUSTMARKS_MESSAGE"

    // progress execution monitoring attributes management
    void setAttribute(String key, Object value) {
        try {
            WebUtils.retrieveGrailsWebRequest().currentRequest.session.setAttribute(key, value)
        } catch (IllegalStateException ise) {
            // do nothing since we are outside of a web request
        }
    }

    Object getAttribute(String key) {
        try {
            return WebUtils.retrieveGrailsWebRequest().currentRequest.session.getAttribute(key)
        } catch (IllegalStateException ise) {
            // do nothing since we are outside of a web request
        }

        return null
    }

    void removeAttribute(String key) {
        try {
            WebUtils.retrieveGrailsWebRequest().currentRequest.session.removeAttribute(key)
        } catch (IllegalStateException ise) {
            // do nothing since we are outside of a web request
        }
    }

    boolean isExecuting(String property) {
        String value = getAttribute(property)
        if (StringUtils.isBlank(value)) {
            value = "false"
        }

        return Boolean.parseBoolean(value);
    }

    void setExecuting(String property) {
        setAttribute(property, "true")
    }

    void stopExecuting(String property) {
        setAttribute(property, "false");
    }


    def add(String... args) {
        log.info("add -> ${args[0]} ${args[1]} ${args[2]} ${args[3]}")

//        For reference
//        args[0] -> provider type
//        args[1] -> provider name
//        args[2] -> provider entity id
//        args[3] -> organization id

        Provider provider = new Provider()
        try {
            Organization org = Organization.get(Integer.parseInt(args[3]))

            provider.providerType = ProviderType.fromString(args[0])
            provider.organization = org
            provider.name = args[1]
            provider.entityId = args[2]

            provider.save(true)

            org.providers.add(provider)
            org.save(true)

        } catch (NumberFormatException nfe) {
            provider = new Provider(type: ProviderType.fromString(args[0])
                    , name: args[1]
                    , entityId: args[2])
            provider.save(true)
        }

        return provider
    }

    def get(String... args) {
        log.info("get -> ${args[0]}")

        Provider provider = null
        try {
            provider = Provider.get(Integer.parseInt(args[0]))
        } catch (NumberFormatException nfe) {
            provider = Provider.findByUrl(args[0])
        }
        return provider
    }

    def update(String... args) {
        log.info("update -> ${args[0]}")

        Provider provider = Provider.get(args[0])
        provider.save(true)
        return provider
    }

    def delete(String... args) {
        log.info("delete -> ${args[0]} ${args[1]}")

        List<String> ids = args[0].split(":")

        Organization organization = Organization.get(Integer.parseInt(args[1]))
        try {
            ids.forEach({ s ->
                if (s.length() > 0) {
                    Provider provider = Provider.get(Integer.parseInt(s))
                    organization.removeFromProviders(provider)
                    provider.delete()
                }
            })
            organization.save(true)
        } catch (NumberFormatException nfe) {
            log.error("Invalid Attribute Id!")
        }
        return organization
    }

    def types() {
        log.info("list ...")
        def types = ProviderType.values()
        return types
    }

    def list(String... args) {
        log.info("list -> ${args[0]}")

        def providers = []

        try {
            int organizationId = Integer.parseInt(args[0])
            if (organizationId == 0) {
                Provider.findAll().forEach({ e -> providers.add(e.toJsonMap()) })
            } else {
                Provider.findAllByOrganization(Organization.get(organizationId)).forEach({ e -> providers.add(e.toJsonMap()) })
            }
        } catch (NumberFormatException nfe) {
            Provider.findAll().forEach({ e -> providers.add(e.toJsonMap()) })
        }
        return providers
    }

    def listByType(String... args) {
        log.info("listByType -> ${args[0]}")

        def providers = []

        String type = args[0]

        switch (args[0]) {
            case 'SAML_SP':
                type = ProviderType.SAML_SP.toString()
                break;
            case 'SAML_IDP':
                type = ProviderType.SAML_IDP.toString()
                break;
        }

        ProviderType providerType = ProviderType.fromString(type)
        if (providerType) {
            Provider.findAllByProviderType(providerType).forEach({ e -> providers.add(e.toJsonMap()) })
        }

        return providers
    }

    def trustmarkRecipientIdentifiers(String... args) {
        log.info("trustmarkRecipientIdentifiers -> ${args[0]}")
        def trustmarkRecipientIdentifiers = []

        Provider provider = Provider.get(Integer.parseInt(args[0]))
        provider.trustmarkRecipientIdentifiers.forEach({ o -> trustmarkRecipientIdentifiers.add(o) })

        return trustmarkRecipientIdentifiers
    }

    def addTrustmarkRecipientIdentifier(String... args) {
        log.info("add trustmarkRecipientIdentifier -> ${args[0]} ${args[1]}")

        Provider provider = Provider.get(Integer.parseInt(args[0]))
        TrustmarkRecipientIdentifier trustmarkRecipientIdentifier = new TrustmarkRecipientIdentifier(
                trustmarkRecipientIdentifierUrl: args[1], organization: provider.organization)

        Provider.withTransaction {
            provider.trustmarkRecipientIdentifiers.add(trustmarkRecipientIdentifier)
            trustmarkRecipientIdentifier.save(true)

            provider.save(true)
        }

        return trustmarkRecipientIdentifier
    }

    def getTrustmarkRecipientIdentifier(String... args) {
        log.info("repos -> ${args[0]}")

        Provider provider = Provider.get(Integer.parseInt(args[0]))

        Integer trustmarkRecipientIdentifierId = Integer.parseInt(args[1])

        TrustmarkRecipientIdentifier trid = provider.trustmarkRecipientIdentifiers.find { element ->
            element.id == trustmarkRecipientIdentifierId
        }

        return trid
    }

    def updateTrustmarkRecipientIdentifier(String... args) {
        log.info("update -> ${args[0]}")

        TrustmarkRecipientIdentifier trid = null

        trid = TrustmarkRecipientIdentifier.get(Integer.parseInt(args[0]))
        trid.trustmarkRecipientIdentifierUrl = args[1]
        trid.save(true)

        return trid
    }

    def deleteTrustmarkRecipientIdentifiers(String... args) {
        log.info("delete -> ${args[0]}")
        List<String> ids = args[0].split(":")
        Provider provider = new Provider()
        TrustmarkRecipientIdentifier trid = new TrustmarkRecipientIdentifier()
        // if system is provided
        if (args[1]) {
            provider = Provider.get(Integer.parseInt(args[1]))
            try {
                ids.forEach({ s ->
                    if (s.length() > 0) {
                        trid = TrustmarkRecipientIdentifier.get(Integer.parseInt(s))
                        provider.trustmarkRecipientIdentifiers.remove(trid)
                        trid.delete()
                    }
                })
                provider.save(true)
            } catch (NumberFormatException nfe) {
                log.error("Invalid trustmark recipient identifier Id!")
            }
        } else {
            try {
                ids.forEach({ s ->
                    if (s.length() > 0) {
                        trid = TrustmarkRecipientIdentifier.get(Integer.parseInt(s))
                        // get all providers that share this trustmark recipient identifier
                        def providers = Provider.withCriteria(uniqueResult: false) {
                            trustmarkRecipientIdentifiers {
                                inList("id", [trid.id])
                            }
                        }
                        // remove the trustmark recipient identifier from all providers found in previous search
                        providers.each { p ->
                            p.trustmarkRecipientIdentifiers.remove(trid)
                            p.save(true)
                        }
                        // now delete trustmark recipient identifier
                        trid.delete()
                    }
                })
            } catch (NumberFormatException nfe) {
                log.error("Invalid trustmark recipient identifier Id!")
            }
        }
        return provider
    }

    // Partner systems tips
    def partnerSystemsTips(String... args) {
        log.info("partnerSystemsTips -> ${args[0]}")

        def partnerSystemsTips = []

        Provider provider = Provider.get(Integer.parseInt(args[0]))
        provider.partnerSystemsTips.forEach({o -> partnerSystemsTips.add(o)})

        return partnerSystemsTips
    }

    def deletePartnerSystemsTips(String... args) {

        List<String> ids = args[0].split(":")

        Provider provider = Provider.get(Integer.parseInt(args[1]))

        try {
            ids.forEach({ s ->
                if (s.length() > 0) {
                    PartnerSystemsTip tip = PartnerSystemsTip.findById(Integer.parseInt(s))
                    provider.partnerSystemsTips.remove(tip)
                    tip.delete()
                }
            })
            provider.save(true)
        } catch (NumberFormatException nfe) {
            log.error("Invalid partner systems tip Id!")
        }
    }

    def bindTrustmarksForAllProviders() {
        log.info("Starting ${this.getClass().getSimpleName()}...")
        long overallStartTime = System.currentTimeMillis()

        try {

            List<Provider> providers = Provider.findAll()

            providers.each { provider ->
                bindTrustmarks(provider.id, false)

                // do not generate metadata for an empty provider
                if (StringUtils.isNotEmpty(provider.entityId)) {
                    deserializeService.serialize(provider)
                }
            }
        }
        catch (Throwable t) {
            log.error("Error encountered during the trustmark bind all process: ${t.message}");
        }

        long overallStopTime = System.currentTimeMillis()
        log.info("Successfully Executed ${this.getClass().getSimpleName()} in ${(overallStopTime - overallStartTime)}ms.")

    }//end bindTrustmarksForAllProviders()

    def bindTrustmarks(Integer id, boolean monitoringProgress = true) {
        log.info("** Starting bindTrustmarks: class: ${this.getClass().getSimpleName()}...")
        long overallStartTime = System.currentTimeMillis()

        try {

            Provider provider = Provider.get(id)

            // remove previously bound trustmarks for this provider system
            provider.trustmarks.clear()

            Organization org = Organization.get(provider.organization.id)

            // Get the conformance targe tips
            def conformanceTargetTips = provider.conformanceTargetTips

            // The one and only TIP resolver
            TrustInteroperabilityProfileResolver resolver = FactoryLoader.getInstance(TrustInteroperabilityProfileResolver.class)

            // process each conformance target tip
            if (conformanceTargetTips.size() > 0) {

                if (monitoringProgress) {
                    setAttribute(BIND_TRUSTMARKS_PERCENT_VAR, "0")
                    setAttribute(BIND_TRUSTMARKS_STATUS_VAR, "PRE-PROCESSING")
                }

                // collection of unique TDs for all conformance target tips
                Map<String, String> tdSet = new HashMap<String, String>()

                // collect TDs recursively for all conformance target TIPS
                conformanceTargetTips.each { conformanceTargetTip ->

                    // operation has been cancelled
                    if (monitoringProgress) {
                        if (!isExecuting(BIND_TRUSTMARKS_EXECUTING_VAR)) {
                            // exit operation
                            log.info("Bind trustmarks operation canceled...")
                            return
                        }
                    }

                    Set<String> processedTipsSet = new HashSet<String>()

                    String conformanceTargetTipIdentifier = conformanceTargetTip.conformanceTargetTipIdentifier

                    if (monitoringProgress) {
                        setAttribute(BIND_TRUSTMARKS_MESSAGE_VAR, "Processing TIP: ${conformanceTargetTipIdentifier}")
                    }

                    // get all TDs  for the conformance target TIP
                    resolveTip(resolver, conformanceTargetTipIdentifier, conformanceTargetTipIdentifier,
                            tdSet, processedTipsSet, monitoringProgress)
                }

                log.info("** Numbers of TDs processed: ${tdSet.size()}")

                // For each Assessment tool url,
                //      for each recipient identifier,
                //           get all trustmarks

                // Get assessment tool URLs
                def assessmentToolUrls = org.assessmentRepos

                // Collect Trustmark Recipient Identifiers
                Set<TrustmarkRecipientIdentifier> recipientIdentifiers = new HashSet<TrustmarkRecipientIdentifier>()

                // Get Trustmark Recipient Identifiers from parent organization
                recipientIdentifiers.addAll(org.trustmarkRecipientIdentifiers)

                // Add Trustmark Recipient Identifiers from this system provider
                recipientIdentifiers.addAll(provider.trustmarkRecipientIdentifiers)

                if (monitoringProgress) {
                    setAttribute(BIND_TRUSTMARKS_MESSAGE_VAR, "Querying trustmarks for all registered trustmark recipients against all assessment tools registered for the organization: ${org.name}")
                    setAttribute(BIND_TRUSTMARKS_PERCENT_VAR, "0")
                    setAttribute(BIND_TRUSTMARKS_STATUS_VAR, "RUNNING")
                }

                ArrayList<JSONArray> trustmarks = new ArrayList<JSONArray>()

                Integer totalTrustmarkQueries = assessmentToolUrls.size() + recipientIdentifiers.size()
                Integer currentTrustmarkQueryIndex = 0

                // collect total number of trustmarks
                Integer totalNumberOfTrustmarksQueried = 0

                assessmentToolUrls.each { assessmentToolUrl ->
                    String tatUrl = ensureTrailingSlash(assessmentToolUrl.repoUrl)
                    tatUrl += TRUSTMARKS_FIND_BY_RECIPIENT_TAT_ENDPOINT

                    recipientIdentifiers.each { recipientIdentifier ->
                        if (monitoringProgress) {
                            if (!isExecuting(BIND_TRUSTMARKS_EXECUTING_VAR)) {
                                // exit operation
                                log.info("Bind trustmarks operation canceled...")
                                return
                            }
                        }

                        // encode the recipient id url
                        String recipientId = recipientIdentifier.trustmarkRecipientIdentifierUrl
                        String recipientIdBase64 = Base64.getEncoder().encodeToString(recipientId.getBytes())
                        String encodedRecipientId = UrlEncodingUtil.encodeURIComponent(recipientIdBase64)

                        // append the recipient id encoded url
                        String recipientIdentifierQueryUrl = tatUrl + encodedRecipientId

                        // get the trustmarks from the TAT
                        JSONObject trustmarksJson = IOUtils.fetchJSON(recipientIdentifierQueryUrl);
                        JSONArray trustmarksJsonArray = trustmarksJson.getJSONArray("trustmarks");

                        totalNumberOfTrustmarksQueried += trustmarksJsonArray.size()

                        trustmarks.add(trustmarksJsonArray)

                        // update progress percentage
                        int percent = (int) Math.floor(((double) currentTrustmarkQueryIndex++ / (double) totalTrustmarkQueries) * 100.0d)

                        if (monitoringProgress) {
                            setAttribute(BIND_TRUSTMARKS_PERCENT_VAR, "" + percent)
                        }
                    }
                }

                // cross map the Conformance target TIP TDs to the collection of trustmarks
                Map<JSONObject, String> bindingTrustmarks = new HashMap<JSONObject, String>();

                if (monitoringProgress) {
                    setAttribute(BIND_TRUSTMARKS_MESSAGE_VAR, "Identifying trustmarks to be bound...")
                    setAttribute(BIND_TRUSTMARKS_PERCENT_VAR, "0")
                }

                Integer currentTrustmarkIndex = 0

                // iterate through each TAT's json response
                for (int i = 0; i < trustmarks.size(); i++) {
                    JSONArray trustmarksJsonArray = trustmarks.get(i)
                    for (int j = 0; j < trustmarksJsonArray.length(); j++) {
                        if (monitoringProgress) {
                            if (!isExecuting(BIND_TRUSTMARKS_EXECUTING_VAR)) {
                                // exit operation
                                log.info("Bind trustmarks operation canceled...")
                                return
                            }
                        }

                        JSONObject trustmark = trustmarksJsonArray.getJSONObject(j);

                        // add if trustmark definitions match
                        if (tdSet.containsKey(trustmark.getString("trustmarkDefinitionURL")) == true) {
                            bindingTrustmarks.put(trustmark, tdSet.get(trustmark.getString("trustmarkDefinitionURL")));
                        }

                        // update progress percentage
                        int percent = (int) Math.floor(((double) currentTrustmarkIndex++ / (double) totalNumberOfTrustmarksQueried) * 100.0d)

                        if (monitoringProgress) {
                            setAttribute(BIND_TRUSTMARKS_PERCENT_VAR, "" + percent)
                        }
                    }
                }

                if (monitoringProgress) {
                    setAttribute(BIND_TRUSTMARKS_MESSAGE_VAR, "Binding trustmarks...")
                    setAttribute(BIND_TRUSTMARKS_PERCENT_VAR, "0")
                }

                currentTrustmarkIndex = 0
                Integer totalToBeBoundTrustmarks = bindingTrustmarks.size()

                Provider.withTransaction {
                    Iterator i = bindingTrustmarks.entrySet().iterator()
                    while (i.hasNext()) {
                        Map.Entry pair = (Map.Entry) i.next()
                        JSONObject trustmark = pair.getKey()

                        // only bind trustmarks with the "ACTIVE" status
                        if ("ACTIVE" == trustmark.get("trustmarkStatus")) {
                            // save to db
                            Trustmark tm = new Trustmark()
                            tm.name = trustmark.get("name")

                            ConformanceTargetTip tip = ConformanceTargetTip.findByConformanceTargetTipIdentifier(pair.getValue())
                            tm.conformanceTargetTipId = tip.id
                            tm.status = trustmark.get("trustmarkStatus")
                            tm.url = trustmark.get("identifierURL")
                            tm.trustmarkDefinitionURL = trustmark.getString("trustmarkDefinitionURL")
                            tm.provisional = trustmark.get("hasExceptions")
                            tm.assessorComments = trustmark.get("assessorComments")

                            tm.save(failOnError: true, flush: true)

                            provider.trustmarks.add(tm)

                            // update progress percentage
                            int percent = (int) Math.floor(((double) currentTrustmarkIndex++ / (double) totalToBeBoundTrustmarks) * 100.0d)

                            if (monitoringProgress) {
                                setAttribute(BIND_TRUSTMARKS_PERCENT_VAR, "" + percent)
                            }
                        }
                    }

                    provider.save(failOnError: true, flush: true)
                }

                log.info("Successfully bound " + bindingTrustmarks.size() + " trustmarks to provider: " + provider.name)
            }
        }
        catch (Throwable t) {
            log.error("Error encountered during the trustmark binding process: ${t.message}");
        }

        long overallStopTime = System.currentTimeMillis()
        log.info("** Successfully Executed ${this.getClass().getSimpleName()} in ${(overallStopTime - overallStartTime)}ms.")

        if (monitoringProgress) {
            stopExecuting(BIND_TRUSTMARKS_EXECUTING_VAR)
            setAttribute(BIND_TRUSTMARKS_PERCENT_VAR, "100")
            setAttribute(BIND_TRUSTMARKS_MESSAGE_VAR, "Successfully bound trustmarks in ${(overallStopTime - overallStartTime)}ms.")
            setAttribute(BIND_TRUSTMARKS_STATUS_VAR, "SUCCESS")
        }

    }//end bindTrustmarks()

    private void resolveTip(TrustInteroperabilityProfileResolver resolver, String tipUri, String conformanceTargetTipUri,
                            Map<String, String> tdSet, Set<String> processedTipsSet, boolean monitoringProgress) throws Exception {
        log.info("** Resolving TIP: " + tipUri)

        if (!processedTipsSet.contains(tipUri)) {

            processedTipsSet.add(tipUri)

            URL url = new URL(tipUri + "?format=xml");

            TrustInteroperabilityProfile tip = resolver.resolve(url);

            Integer numberOfReferences = tip.getReferences().size()

            Integer currentTipReferenceIndex = 0;
            for (AbstractTIPReference abstractRef : tip.getReferences()) {

                if (monitoringProgress) {
                    // operation has been cancelled
                    if (!isExecuting(BIND_TRUSTMARKS_EXECUTING_VAR)) {
                        // exit operation
                        log.info("Bind trustmarks operation canceled...")
                        return
                    }
                }

                // Resolve contained TIPs
                if (abstractRef.isTrustInteroperabilityProfileReference()) {
                    TrustInteroperabilityProfileReferenceImpl tipRef = (TrustInteroperabilityProfileReferenceImpl) abstractRef;

                    URI tipRefIdentifier = tipRef.getIdentifier();
                    log.info("TIP Reference Identifier: " + tipRefIdentifier);

                    URL tipRefIdentifierUrl = new URL(tipRefIdentifier.toString());

                    // recurse over contained TIPs
                    resolveTip(resolver, tipRefIdentifierUrl.toString(), conformanceTargetTipUri, tdSet, processedTipsSet, monitoringProgress);

                    // Collect TDs
                } else if (abstractRef.isTrustmarkDefinitionRequirement()) {

                    TrustmarkDefinitionRequirementImpl tfReqImpl = (TrustmarkDefinitionRequirementImpl) abstractRef;

                    URI childTipRefIdentifier = tfReqImpl.getIdentifier();

                    tdSet.put(childTipRefIdentifier.toString(), conformanceTargetTipUri);
                }
            }
        }
    }

    private String ensureTrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

}
