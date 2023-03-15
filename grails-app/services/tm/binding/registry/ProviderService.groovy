package tm.binding.registry

import grails.gorm.transactions.Transactional
import org.apache.commons.lang.StringUtils
import org.grails.web.util.WebUtils


@Transactional
class ProviderService {

    def deserializeService
    def emailService
    def groovyPageRenderer

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
            case 'CERTIFICATE':
                type = ProviderType.CERTIFICATE.toString()
                break;
            case 'OIDC_RP':
                type = ProviderType.OIDC_RP.toString()
                break;
            case 'OIDC_OP':
                type = ProviderType.OIDC_OP.toString()
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

        Map messageMap = [:]

        Provider provider = Provider.get(Integer.parseInt(args[0]))

        def trustmarkRecipientIdentifiers = TrustmarkRecipientIdentifier.findAllByTrustmarkRecipientIdentifierUrl(args[1])

        boolean alreadyExists = false
        for (def trustmarkRecipientIdentifier : trustmarkRecipientIdentifiers) {
            if (provider.trustmarkRecipientIdentifiers.contains(trustmarkRecipientIdentifier)) {
                alreadyExists = true;
                break;
            }
        }

        if (alreadyExists) {
            messageMap["WARNING"] = "Trustmark Recipient Identifier ${args[1]} already exists!"
        } else {

            TrustmarkRecipientIdentifier trustmarkRecipientIdentifier = new TrustmarkRecipientIdentifier(
                    trustmarkRecipientIdentifierUrl: args[1], organization: provider.organization)

            Provider.withTransaction {
                provider.trustmarkRecipientIdentifiers.add(trustmarkRecipientIdentifier)
                trustmarkRecipientIdentifier.save(true)

                provider.save(true)
            }
        }

        return messageMap
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
            log.error("Error encountered during the trustmark bind all for systems process: ${t.message}", t);
        }

        long overallStopTime = System.currentTimeMillis()
        log.info("Successfully Executed ${this.getClass().getSimpleName()} in ${(overallStopTime - overallStartTime)}ms.")

    }

    def bindTrustmarks(Integer id, boolean monitoringProgress = true) {

        final Provider provider = Provider.get(id)

        TrustmarkBinder binder = new SystemTrustmarkBinder(this, emailService, groovyPageRenderer);

        binder.bindTrustmarks(provider, monitoringProgress)

        Map status = ["SUCCESS": "Successfully finished the trustmark binding process."]

        if (binder.hasRemoteArtifactsStalenessMessages()) {
            status.put("WARNING", "Remote artifacts could not be downloaded, using cached artifacts. " +
                    "An email has been sent to the TBR administrator(s) with more details.")
        }

        return status
    }
}
