package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.FactoryLoader
import edu.gatech.gtri.trustmark.v1_0.impl.io.IOUtils
import edu.gatech.gtri.trustmark.v1_0.impl.model.TrustInteroperabilityProfileReferenceImpl
import edu.gatech.gtri.trustmark.v1_0.impl.model.TrustmarkDefinitionRequirementImpl
import edu.gatech.gtri.trustmark.v1_0.io.TrustInteroperabilityProfileResolver
import edu.gatech.gtri.trustmark.v1_0.model.AbstractTIPReference
import edu.gatech.gtri.trustmark.v1_0.model.TrustInteroperabilityProfile
import org.json.JSONArray
import org.json.JSONObject

class ProviderService {
    public static final String TRUSTMARKS_FIND_BY_RECIPIENT_TAT_ENDPOINT = "public/trustmarks/find-by-recipient/"

    def add(String... args) {
        log.info("add -> ${args[0]} ${args[1]} ${args[2]} ${args[3]}")

        Provider provider = new Provider()
        try  {
            Organization org = Organization.get(Integer.parseInt(args[3]))

            provider.providerType = ProviderType.valueOf(args[0])
            provider.organization = org
            provider.name = args[1]
            provider.entityId = args[2]

            provider.save(true)

            org.providers.add(provider)
            org.save(true)

        }  catch (NumberFormatException nfe)  {
            provider = new Provider(type: ProviderType.valueOf(args[0])
                                    , name: args[1]
                                    , entityId: args[2])
            provider.save(true)
        }

        return provider
    }

    def get(String... args) {
        log.info("get -> ${args[0]}")

        Provider provider = null
        try  {
            provider = Provider.get(Integer.parseInt(args[0]))
        } catch (NumberFormatException nfe)  {
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
        try  {
            ids.forEach({s ->
                if(s.length() > 0)  {
                    Provider provider = Provider.get(Integer.parseInt(s))
                    organization.providers.remove(provider)
                    provider.delete()
                }
            })
            organization.save(true)
        } catch (NumberFormatException nfe)  {
            log.error("Invalid Attribute Id!")
        }
        return organization
    }

    def types()  {
        log.info("list ...")
        def types = ProviderType.values()
        return types
    }

    def list(String... args) {
        log.info("list -> ${args[0]}")

        def providers = []

        try  {
            int organizationId = Integer.parseInt(args[0])
            if(organizationId == 0)  {
                Provider.findAll().forEach({e -> providers.add(e.toJsonMap())})
            }  else {
                Provider.findAllByOrganization(Organization.get(organizationId)).forEach({e -> providers.add(e.toJsonMap())})
            }
        } catch (NumberFormatException nfe)  {
            Provider.findAll().forEach({e -> providers.add(e.toJsonMap())})
        }
        return providers
    }

    def listByType(String... args) {
        log.info("listByType -> ${args[0]}")

        def providers = []

        String type = args[0]

        switch(args[0])  {
            case 'SAML_SP':
                type = 'IDENTITY_PROVIDER'
                break;
            case 'SAML_IDP':
                type = 'SERVICE_PROVIDER'
                break;
        }
        ProviderType providerType = ProviderType.valueOf(type)
        if(providerType)  {
            Provider.findAllByProviderType(providerType).forEach({e -> providers.add(e.toJsonMap())})
        }

        return providers
    }

    def bindTrustmarks(String id) {
        log.info("Starting ${this.getClass().getSimpleName()}...")
        long overallStartTime = System.currentTimeMillis()

        try {
            Integer providerId = Integer.parseInt(id)

            Provider provider = Provider.get(providerId)

            Organization org = provider.organization

            // Get the conformance targe tips
            def conformanceTargetTips = provider.conformanceTargetTips

            // process each conformance target tip
            if (conformanceTargetTips.size() > 0) {

                // Get assessment tool URLs
                def assessmentToolUrls = org.assessmentRepos

                // Get Trustmark Recipient Identifiers
                def recipientIdentifiers = org.trustmarkRecipientIdentifier

                Set<String> processedTipsSet = new HashSet<String>()

                // collection of unique TDs for all conformance target tips
                Set<String> tdSet = new HashSet<String>()

                // The one and only TIP resolver
                TrustInteroperabilityProfileResolver resolver = FactoryLoader.getInstance(TrustInteroperabilityProfileResolver.class)

                // collect TDs recursively for all conformance target TIPS
                conformanceTargetTips.each {
                    String conformanceTargetTipIdentifier = it.conformanceTargetTipIdentifier

                    // get all TDs  for the conformance target TIP
                    resolveTip(resolver, conformanceTargetTipIdentifier, tdSet, processedTipsSet)
                }


                // For each Assessment tool url,
                //      for each recipient identifier,
                //           get all trustmarks and store them in a global set?

                ArrayList<JSONArray> trustmarks = new ArrayList<JSONArray>()

                assessmentToolUrls.each {
                    String tatUrl = ensureTrailingSlash(it.repoUrl)
                    tatUrl += TRUSTMARKS_FIND_BY_RECIPIENT_TAT_ENDPOINT


                    recipientIdentifiers.each {
                        // encode the recipient id url
                        String recipientId = it.trustmarkRecipientIdentifierUrl
                        String recipientIdBase64 = Base64.getEncoder().encodeToString(recipientId.getBytes())
                        String encodedRecipientId = encodeURIComponent(recipientIdBase64)

                        // append the recipient id encoded url
                        tatUrl += encodedRecipientId

                        // get the trustmarks from the TAT
                        JSONObject trustmarksJson = IOUtils.fetchJSON(tatUrl);
                        JSONArray trustmarksJsonArray = trustmarksJson.getJSONArray("trustmarks");

                        trustmarks.add(trustmarksJsonArray)
                    }
                }

                // cross map the Conformance target TIP TDs to the collection of trustmarks
                List<JSONObject> bindingTrustmarks = new ArrayList<JSONObject>();

                // iterate through each TAT's json response
                for (int i = 0; i < trustmarks.size(); i++) {
                    JSONArray trustmarksJsonArray = trustmarks.get(i)
                    for (int j = 0; j < trustmarksJsonArray.length(); j++) {
                        JSONObject trustmark = trustmarksJsonArray.getJSONObject(j);

                        // add if trustmark definitions match
                        if (tdSet.contains(trustmark.getString("trustmarkDefinitionURL")) == true) {
                            bindingTrustmarks.add(trustmark);
                        }
                    }
                }

                log.info("Binding Trustmarks total: " + bindingTrustmarks.size());

                for (int i = 0; i < bindingTrustmarks.size(); i++) {
                    JSONObject trustmark = bindingTrustmarks.get(i);

                    // save to db
                    Trustmark tm = new Trustmark()
                    tm.name = trustmark.get("name")
                    tm.provider = provider
                    tm.status = trustmark.get("trustmarkStatus")
                    tm.url = trustmark.get("identifierURL")
                    tm.provisional = trustmark.get("hasExceptions")
                    tm.assessorComments = trustmark.get("assessorComments")

                    tm.save(true)
                }

                log.info("Successfully bound " + bindingTrustmarks.size() + " trustmarks to provider: " + provider.name)
            }


        }
        catch(Throwable t) {
            log.error("Error encountered during the trustmark binding process!", t);
        }

        long overallStopTime = System.currentTimeMillis()
        log.info("Successfully Executed ${this.getClass().getSimpleName()} in ${(overallStopTime - overallStartTime)}ms.")

    }//end bindTrustmarks()

    private void resolveTip(TrustInteroperabilityProfileResolver resolver, String tipUrl,
                            Set<String> tdSet, Set<String> processedTipsSet) throws Exception {
        log.info("Resolving TIP: " + tipUrl);

        if (!processedTipsSet.contains(tipUrl)) {

            processedTipsSet.add(tipUrl)

            URL url = new URL(tipUrl + "?format=xml");

            TrustInteroperabilityProfile tip = resolver.resolve(url);

            for (AbstractTIPReference abstractRef : tip.getReferences()) {

                // Resolve contained TIPs
                if (abstractRef.isTrustInteroperabilityProfileReference()) {
                    TrustInteroperabilityProfileReferenceImpl tipRef = (TrustInteroperabilityProfileReferenceImpl) abstractRef;

                    URI tipRefIdentifier = tipRef.getIdentifier();
                    log.info("TIP Reference Identifier: " + tipRefIdentifier);

                    URL tipRefIdentifierUrl = new URL(tipRefIdentifier.toString());

                    // recurse over contained TIPs
                    resolveTip(resolver, tipRefIdentifierUrl.toString(), tdSet, processedTipsSet);

                // Collect TDs
                } else if (abstractRef.isTrustmarkDefinitionRequirement()) {

                    TrustmarkDefinitionRequirementImpl tfReqImpl = (TrustmarkDefinitionRequirementImpl) abstractRef;

                    URI childTipRefIdentifier = tfReqImpl.getIdentifier();
                    log.info("TD Requirement Identifier: " + childTipRefIdentifier);
                    log.info("TD Requirement Name: " + tfReqImpl.getName());

                    tdSet.add(childTipRefIdentifier.toString());
                }
            }
        }
    }

    private String ensureTrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    private String encodeURIComponent(String s)
    {
        String result = null;

        try
        {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }
        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = s;
        }

        return result;
    }

}
