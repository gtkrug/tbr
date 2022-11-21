package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.FactoryLoader;
import edu.gatech.gtri.trustmark.v1_0.impl.io.IOUtils
import edu.gatech.gtri.trustmark.v1_0.impl.io.json.TrustInteroperabilityProfileJsonDeserializer
import edu.gatech.gtri.trustmark.v1_0.impl.io.json.TrustmarkDefinitionJsonDeserializer
import edu.gatech.gtri.trustmark.v1_0.impl.io.json.TrustmarkJsonDeserializer
import edu.gatech.gtri.trustmark.v1_0.impl.io.json.TrustmarkStatusReportJsonDeserializer
import edu.gatech.gtri.trustmark.v1_0.io.ResolveException
import edu.gatech.gtri.trustmark.v1_0.io.Serializer
import edu.gatech.gtri.trustmark.v1_0.io.SerializerFactory;
import edu.gatech.gtri.trustmark.v1_0.io.TrustInteroperabilityProfileResolver
import edu.gatech.gtri.trustmark.v1_0.io.TrustmarkDefinitionResolver
import edu.gatech.gtri.trustmark.v1_0.io.TrustmarkResolver
import edu.gatech.gtri.trustmark.v1_0.io.TrustmarkStatusReportResolver
import edu.gatech.gtri.trustmark.v1_0.model.Extension
import edu.gatech.gtri.trustmark.v1_0.model.Trustmark
import edu.gatech.gtri.trustmark.v1_0.io.hash.HashFactory;
import edu.gatech.gtri.trustmark.v1_0.model.AbstractTIPReference;
import edu.gatech.gtri.trustmark.v1_0.model.TrustInteroperabilityProfile;
import edu.gatech.gtri.trustmark.v1_0.model.TrustInteroperabilityProfileReference
import edu.gatech.gtri.trustmark.v1_0.model.TrustmarkDefinition;
import edu.gatech.gtri.trustmark.v1_0.model.TrustmarkDefinitionRequirement
import edu.gatech.gtri.trustmark.v1_0.model.TrustmarkStatusReport;
import edu.gatech.gtri.trustmark.v1_0.service.RemoteException
import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import org.dom4j.Element
import org.dom4j.tree.DefaultElement
import org.springframework.security.crypto.codec.Hex
import org.springframework.web.multipart.MultipartFile
import shared.views.EmailService
import tm.binding.registry.util.TBRProperties;
import tm.binding.registry.util.UrlEncodingUtil
import org.json.JSONArray;
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import tm.binding.registry.util.UrlUtilities


public abstract class TrustmarkBinder {
    private static final Logger log = LoggerFactory.getLogger(TrustmarkBinder.class);

    def emailService
    def providerService
    def groovyPageRenderer

    public static final String TRUSTMARKS_FIND_BY_RECIPIENT_TAT_ENDPOINT = "public/trustmarks/find-by-recipient/";

    RemoteArtifactStaleness remoteArtifactStaleness = new RemoteArtifactStaleness();

    public boolean hasRemoteArtifactsStalenessMessages() {
        return remoteArtifactStaleness.hasMessages()
    }

    public TrustmarkBinder(ProviderService providerService, EmailService emailService, PageRenderer groovyPageRenderer) {
        this.providerService = providerService
        this.emailService = emailService
        this.groovyPageRenderer = groovyPageRenderer
    }
    
    Set<String> collectTdsFromConformanceTargetTips(Set<ConformanceTargetTip> conformanceTargetTips, Boolean monitoringProgress) {
        Set<String> tdSet = new HashSet<String>();

        // The one and only TIP resolver
        TrustInteroperabilityProfileResolver resolver = FactoryLoader.getInstance(TrustInteroperabilityProfileResolver.class);

        // collect TDs recursively for all conformance target TIPS
        conformanceTargetTips.forEach( conformanceTargetTip -> {

            // operation has been cancelled
            if (monitoringProgress) {
                if (!providerService.isExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)) {
                    // exit operation
                    log.info("Bind trustmarks operation canceled...");
                    return;
                }
            }

            Set<String> processedTipsSet = new HashSet<String>();

            String conformanceTargetTipIdentifier = conformanceTargetTip.conformanceTargetTipIdentifier;

            if (monitoringProgress) {
                providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Processing TIP: ${conformanceTargetTipIdentifier}");
            }

            // get all TDs  for the conformance target TIP
            try {

                resolveTipAndTds(resolver, conformanceTargetTipIdentifier, conformanceTargetTipIdentifier,
                        tdSet, processedTipsSet, monitoringProgress);

            } catch (Exception e) {
                log.error("Error resolving the conformance target tip: ${conformanceTargetTipIdentifier}")
            }
        });

        return tdSet;
    }

    List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> collectTrustmarksForAllRecipientIdentifiers(TrustmarkDefinitionUriFilter tdFilter,
            Set<AssessmentRepository> assessmentToolUrls,
            Set<TrustmarkRecipientIdentifier> recipientIdentifiers,
            Boolean monitoringProgress) {

        log.info("collectTrustmarksForAllRecipientIdentifiers...");

        List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> trustmarks = new ArrayList<>();

        Integer totalTrustmarkQueries = assessmentToolUrls.size() + recipientIdentifiers.size();
        Integer currentTrustmarkQueryIndex = 0;

        // collect total number of trustmarks

        assessmentToolUrls.forEach(assessmentToolUrl -> {
            final String tatUrl = ensureTrailingSlash(assessmentToolUrl.repoUrl) + TRUSTMARKS_FIND_BY_RECIPIENT_TAT_ENDPOINT;

            log.info("processing repo: ${tatUrl}...");

            recipientIdentifiers.forEach(recipientIdentifier -> {
                log.info("processing trustmark recipient indentifier: ${recipientIdentifier.trustmarkRecipientIdentifierUrl}...");

                if (monitoringProgress) {
                    if (!providerService.isExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)) {
                        // exit operation
                        log.info("Bind trustmarks operation canceled...");
                        return;
                    }
                }

                String encodedRecipientId = encodeTrustmarkRecipientIdentifier(recipientIdentifier.trustmarkRecipientIdentifierUrl)

                // append the recipient id encoded url
                String recipientIdentifierQueryUrl = tatUrl + encodedRecipientId;

                log.info("recipientIdentifierQueryUrl: ${recipientIdentifierQueryUrl}...");

                // get the trustmarks from the TAT
                JSONObject trustmarksJson = null;
                try {
                    trustmarksJson = IOUtils.fetchJSON(recipientIdentifierQueryUrl);

                    // process each trustmark and the transfer to main collection

                    List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> trustmarksFromJson = resolveTrustmarks(tdFilter,
                            trustmarksJson.getJSONArray("trustmarks"), assessmentToolUrl, recipientIdentifier)

                    trustmarks.addAll(trustmarksFromJson);

                } catch (RemoteException e) {
                    log.error("Failed to query trustmarks for ${recipientIdentifierQueryUrl} at ${assessmentToolUrl.repoUrl}");

                    // get trustmarks from cache
                    List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> trustmarksFromCache = resolveTrustmarksFromCache(assessmentToolUrl, recipientIdentifier)

                    trustmarks.addAll(trustmarksFromCache);
                }

                if (monitoringProgress) {
                    // update progress percentage
                    int percent = (int) Math.floor(((double) currentTrustmarkQueryIndex++ / (double) totalTrustmarkQueries) * 100.0d);
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "" + percent);
                }
            });
        });

        return trustmarks;
    }

    List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> resolveTrustmarksFromCache(AssessmentRepository assessmentToolUrl,
                                                                                    TrustmarkRecipientIdentifier recipientIdentifier) {
        final List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> trustmarks = new ArrayList<>();

        Set<TrustmarkUri> mostRecentlyCachedTrustmarkUris = new HashSet<>()

        Set<TrustmarkUri> trustmarkUris = TrustmarkUri.findAllByAssessmentRepositoryUrlAndTrustmarkRecipientIdentifierUrl(assessmentToolUrl.repoUrl, recipientIdentifier.trustmarkRecipientIdentifierUrl)
        trustmarkUris.forEach(tmUri -> {

            TrustmarkUri trustmarkUri = TrustmarkUri.findByUri(tmUri.uri,
                    [max: 1, sort: "retrievalTimestamp", order: "desc", offset: 0])

            mostRecentlyCachedTrustmarkUris.add(trustmarkUri)
        })

        TrustmarkResolver resolver = FactoryLoader.getInstance(TrustmarkResolver.class);

        mostRecentlyCachedTrustmarkUris.forEach(tmUri -> {

            Trustmark trustmark = resolveTrustmark(resolver, tmUri.uri, assessmentToolUrl,
                    recipientIdentifier)

            trustmarks.add(trustmark)
        })

        return trustmarks
    }

    List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> resolveTrustmarks(TrustmarkDefinitionUriFilter tdFilter, JSONArray trustmarksJsonArray,
            AssessmentRepository assessmentRepository, TrustmarkRecipientIdentifier trustmarkRecipientIdentifier) {
        log.info("resolveTrustmarks trustmarksJsonArray size: ${trustmarksJsonArray.length()}...");

        final List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> trustmarks = new ArrayList<>();

        TrustmarkResolver trustmarkpResolver = FactoryLoader.getInstance(TrustmarkResolver.class);

        trustmarksJsonArray.forEach(tm -> {
            JSONObject json = (JSONObject)tm

            String trustmarkDefinitionUrl = json.get("trustmarkDefinitionURL")

            // add if trustmark definition is present in current TD set
            if (tdFilter.filter(trustmarkDefinitionUrl)) {
                String trustmarkIdentifier = json.get("identifierURL")
                Trustmark trustmark = resolveTrustmark(trustmarkpResolver, trustmarkIdentifier, assessmentRepository, trustmarkRecipientIdentifier)

                trustmarks.add(trustmark)
            }
        })

        return trustmarks;
    }

    private Trustmark resolveTrustmark(TrustmarkResolver resolver, String trustmarkUriString,
                                       AssessmentRepository assessmentRepository,
                                       TrustmarkRecipientIdentifier trustmarkRecipientIdentifier) {

        log.info("Resolving Trustmark: ${trustmarkUriString}")

        final URI uri = new URI(trustmarkUriString);

        final Trustmark trustmark = resolver.resolve(
                uri,
                (tmUri, tm) -> {
                    log.info("Repo Online, trustmark OK: ${tm.name}...");

                    Serializer jsonSerializer = FactoryLoader.getInstance(SerializerFactory.class).getJsonSerializer();

                    // compute hash
                    HashFactory hasher = FactoryLoader.getInstance(HashFactory.class);
                    byte[] enc = hasher.hash(tm);
                    char[] enc2 = Hex.encode(enc);
                    String hash = new String(enc2);

                    List<TrustmarkUri> trustmarkUris = TrustmarkUri.findAllByUri(tmUri.toString(),
                            [max: 1, sort: "retrievalTimestamp", order: "desc", offset: 0])

                    TrustmarkUri trustmarkUri = null
                    if (trustmarkUris && trustmarkUris.size() > 0) {
                        trustmarkUri = trustmarkUris.get(0)
                    }

                    if (!trustmarkUri || !trustmarkUri.hash.equals(hash)) {
                        // TM changed, save new tm uri
                        log.info("TM has changed or has never been cached,  caching new TM...")

                        TrustmarkUri newCachedTmUri =  createTrustmarkUri(tm, tmUri.toString(), assessmentRepository,
                                trustmarkRecipientIdentifier, jsonSerializer, hasher, LocalDateTime.now())

                        newCachedTmUri.save(failOnError: true, flush: true)
                    }

                    return tm;
                },
                (tmUri, tmException, serverUri) -> {
                    log.info("TM ${tmUri.toString()} deleted, error: ${tmException.toString()}, server OK: ${serverUri.toString()}...")

                    List<TrustmarkUri> trustmarkUris = TrustmarkUri.findAllByUri(tmUri.toString(),
                            [max: 1, sort: "retrievalTimestamp", order: "desc", offset: 0])

                    TrustmarkUri trustmarkUri = null
                    if (trustmarkUris && trustmarkUris.size() > 0) {
                        trustmarkUri = trustmarkUris.get(0)
                    }

                    if (trustmarkUri) {
                        // create TM from content
                        TrustmarkJsonDeserializer deserializer = new TrustmarkJsonDeserializer()
                        Trustmark trustmark = deserializer.deserialize(trustmarkUri.content)

                        // test for staleness
                        if (testStalenessThreshold(trustmarkUri.retrievalTimestamp)) {
                            remoteArtifactStaleness.addTrustmarkStalenessMessage(new RemoteArtifactStalenessMessage("Trustmark",
                                    "The trustmark <${trustmark.name}> is no longer available at <${serverUri.toString()}>." +
                                            " The following error was reported: ${tmException.toString()}",
                                    tmUri.toString()))
                        }

                        return trustmark
                    }

                    return null;
                },
                (tmUri, tmException, serverUri, serverException) -> {

                    log.info("TM ${tmUri.toString()} error: ${tmException.toString()}, server failed: ${serverUri.toString()} with error: ${serverException.toString()}...")


                    // Note: The TPAT url test is done because the tf-api is not currently handling the context-path
                    // of a server. The test should be removed if and when tmf-api implements context-path handling
                    String tatUrl = UrlUtilities.artifactBaseUrl(tmUri.toString());
                    if (!UrlUtilities.checkTATStatusUrl(tatUrl)) {

                        List<TrustmarkAssessmentToolUri> trustmarkAssessmentToolUris = TrustmarkAssessmentToolUri.findAllByUri(
                                tatUrl, [max: 1, sort: "statusSuccessTimestamp", order: "desc", offset: 0])

                        TrustmarkAssessmentToolUri trustmarkAssessmentToolUri = null
                        if (trustmarkAssessmentToolUris && trustmarkAssessmentToolUris.size() > 0) {
                            trustmarkAssessmentToolUri = trustmarkAssessmentToolUris.get(0)
                        }

                        if (trustmarkAssessmentToolUri) {
                            // check for server staleness
                            if (testStalenessThreshold(trustmarkAssessmentToolUri.statusSuccessTimestamp)) {
                                remoteArtifactStaleness.addServerStalenessMessage(new RemoteArtifactStalenessMessage("Trustmark Assessment Tool",
                                        "The TAT is no longer available at <${tatUrl}>." +
                                                " The following error was reported: ${serverException.toString()}",
                                        tatUrl))
                            }
                        }
                    }

                    List<TrustmarkUri> trustmarkUris = TrustmarkUri.findAllByUri(tmUri.toString(),
                            [max: 1, sort: "retrievalTimestamp", order: "desc", offset: 0])

                    TrustmarkUri trustmarkUri = null
                    if (trustmarkUris && trustmarkUris.size() > 0) {
                        trustmarkUri = trustmarkUris.get(0)
                    }

                    if (trustmarkUri) {
                        // create trustmark from content
                        TrustmarkJsonDeserializer deserializer = new TrustmarkJsonDeserializer()
                        Trustmark trustmark = deserializer.deserialize(trustmarkUri.content)

                        // Note: The TPAT url test is done because the tf-api is not currently handling the context-path
                        // of a server. The test should be removed if and when tmf-api implements context-path handling
                        if (UrlUtilities.checkTATStatusUrl(tatUrl)) {
                            // check for artifact staleness
                            if (testStalenessThreshold(trustmarkUri.retrievalTimestamp)) {
                                remoteArtifactStaleness.addTrustmarkStalenessMessage(new RemoteArtifactStalenessMessage("Trustmark",
                                        "The TM <${trustmark.trustmarkDefinitionReference.name}> is no longer available at server <${tatUrl.toString()}>." +
                                                " The following error was reported: ${tmException.toString()}",
                                        tmUri.toString()))
                            }
                        }

                        return trustmark
                    }

                    return null;
                });

        return trustmark;
    }

    private String encodeTrustmarkRecipientIdentifier(String recipientIdentifierUrl) {
        String recipientIdBase64 = Base64.getEncoder().encodeToString(recipientIdentifierUrl.getBytes());
        return UrlEncodingUtil.encodeURIComponent(recipientIdBase64);
    }

    Set<TrustmarkRecipientIdentifier> collectRecipientIdentifiers(Organization org, Provider provider) {

        Set<TrustmarkRecipientIdentifier> recipientIdentifiers = getOrganizationRecipientIdentifiers(provider.organization);

        Set<TrustmarkRecipientIdentifier> systemRecipientIdentifiers = getSystemRecipientIdentifiers(provider)

        Set<TrustmarkRecipientIdentifier> mergedSet = new HashSet<>(recipientIdentifiers);

        // filter out repeated recipient identifiers
        systemRecipientIdentifiers.stream()
                .filter(o1 -> recipientIdentifiers.stream()
                        .noneMatch(o2 -> o1.trustmarkRecipientIdentifierUrl.equals(o2.trustmarkRecipientIdentifierUrl)))
                .forEach(mergedSet::add);

        return mergedSet;
    }

    Set<TrustmarkRecipientIdentifier> getSystemRecipientIdentifiers(Provider provider) {
        Set<TrustmarkRecipientIdentifier> recipientIdentifiers = new HashSet<>();

        // Add Trustmark Recipient Identifiers urls from this system provider
        recipientIdentifiers.addAll(provider.trustmarkRecipientIdentifiers)

        return recipientIdentifiers;
    }

    Set<TrustmarkRecipientIdentifier> getOrganizationRecipientIdentifiers(Organization org) {
        Set<TrustmarkRecipientIdentifier> recipientIdentifiers = new HashSet<>();

        // Get Trustmark Recipient Identifiers from parent organization
        recipientIdentifiers.addAll(org.trustmarkRecipientIdentifiers)

        return recipientIdentifiers;
    }

    List<Trustmark> mapTrustmarksToConformanceTargetTipTDs(List<Trustmark> trustmarks, Set<String> tdSet, Boolean monitoringProgress) {

        Integer totalNumberOfTrustmarksQueried = trustmarks.size()

        List<Trustmark> bindingTrustmarks = new ArrayList<>();

        if (monitoringProgress) {
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Identifying trustmarks to be bound...");
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0");
        }

        Integer currentTrustmarkIndex = 0;

        trustmarks.forEach(tm -> {
            if (monitoringProgress) {
                if (!providerService.isExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)) {
                    // exit operation
                    log.info("Bind trustmarks operation canceled...");
                    return bindingTrustmarks;
                }
            }

            // add if trustmark definitions match
            if (tdSet.contains(tm.trustmarkDefinitionReference.identifier.toString())) {
                bindingTrustmarks.add(tm);
            }

            if (monitoringProgress) {
                // update progress percentage
                int percent = (int) Math.floor(((double) currentTrustmarkIndex++ / (double) totalNumberOfTrustmarksQueried) * 100.0d);
                providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "" + percent);
            }

        })

        return bindingTrustmarks;
    }

    private void resolveTipAndTds(TrustInteroperabilityProfileResolver resolver, String tipUri, String conformanceTargetTipUri,
                            Set<String> tdSet, Set<String> processedTipsSet, boolean monitoringProgress) throws Exception {
        log.info("** Resolving TIP: " + tipUri);

        // TODO: Why we needed to ensure we prevent processing a TIP again?
        if (!processedTipsSet.contains(tipUri)) {

            processedTipsSet.add(tipUri);

            TrustInteroperabilityProfile tip = resolveTrustInteroperabilityProfile(resolver, tipUri);

            for (AbstractTIPReference abstractRef : tip.getReferences()) {

                if (monitoringProgress) {
                    // operation has been cancelled
                    if (!providerService.isExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR)) {
                        // exit operation
                        log.info("Bind trustmarks operation canceled...");
                        return;
                    }
                }

                // Resolve contained TIPs
                if (abstractRef.isTrustInteroperabilityProfileReference()) {
                    TrustInteroperabilityProfileReference tipRef = (TrustInteroperabilityProfileReference) abstractRef;

                    URI tipRefIdentifier = tipRef.getIdentifier();
                    log.info("TIP Reference Identifier: " + tipRefIdentifier);

                    // recurse over contained TIPs
                    resolveTipAndTds(resolver, tipRefIdentifier.toString(), conformanceTargetTipUri, tdSet, processedTipsSet, monitoringProgress);

                    // Collect TDs
                } else if (abstractRef.isTrustmarkDefinitionRequirement()) {

                    TrustmarkDefinitionRequirement tfReq = (TrustmarkDefinitionRequirement) abstractRef;

                    URI childTdRefIdentifier = tfReq.getIdentifier();

                    TrustmarkDefinitionResolver tdResolver = FactoryLoader.getInstance(TrustmarkDefinitionResolver.class);

                    TrustmarkDefinition td = resolveTrustmarkDefinition(tdResolver, childTdRefIdentifier.toString());

                    tdSet.add(childTdRefIdentifier.toString());
                }
            }
        }
    }


    private boolean testStalenessThreshold(LocalDateTime timestamp) {
        long stalenessThreshold = Long.parseLong(
                TBRProperties.getProperties().getProperty("tbr.remote.artifact.staleness.threshold.in.hours") ?: "168")

        long hourDiff = ChronoUnit.HOURS.between(timestamp, LocalDateTime.now())

//        log.info("hourDiff: ${hourDiff}")

        if (hourDiff >= stalenessThreshold) {
            return true;
        }

        return false;
    }

    private TrustInteroperabilityProfile resolveTrustInteroperabilityProfile(TrustInteroperabilityProfileResolver resolver, String tipUriString) {
        log.info("Resovling TIP: ${tipUriString}")

        final URI uri = new URI(tipUriString);

        final TrustInteroperabilityProfile tip = resolver.resolve(
                uri,
                (tipUri, tip) -> {

                    log.info("TIP ${tipUri.toString()} OK...")

                    Serializer jsonSerializer = FactoryLoader.getInstance(SerializerFactory.class).getJsonSerializer();

                    // compute hash
                    HashFactory hasher = FactoryLoader.getInstance(HashFactory.class);
                    byte[] enc = hasher.hash(tip);
                    char[] enc2 = Hex.encode(enc);
                    String hash = new String(enc2);

                    List<TrustInteropProfileUri> trustInteropProfileUris = TrustInteropProfileUri.findAllByUri(tipUri.toString(),
                            [max: 1, sort: "retrievalTimestamp", order: "desc", offset: 0])

                    TrustInteropProfileUri trustInteropProfileUri = null
                    if (trustInteropProfileUris && trustInteropProfileUris.size() > 0) {
                        trustInteropProfileUri = trustInteropProfileUris.get(0)
                    }

                    if (!trustInteropProfileUri || !trustInteropProfileUri.hash.equals(hash)) {
                        // Tip changed, save new tip uri
                        log.info("TIP has changed, caching new TIP...")

                        TrustInteropProfileUri newCacheTipUri =  createTrustInteropProfileUri(tip, tipUri.toString(),
                                jsonSerializer, hasher, LocalDateTime.now())

                        newCacheTipUri.save(failOnError: true, flush: true)
                    }

                    return tip;
                },
                (tipUri, tipException, serverUri) -> {

                    log.info("TIP ${tipUri.toString()} deleted, error: ${tipException.toString()}, server OK: ${serverUri.toString()}...")

                    List<TrustInteropProfileUri> trustInteropProfileUris = TrustInteropProfileUri.findAllByUri(tipUri.toString(),
                            [max: 1, sort: "retrievalTimestamp", order: "desc", offset: 0])

                    TrustInteropProfileUri trustInteropProfileUri = null
                    if (trustInteropProfileUris && trustInteropProfileUris.size() > 0) {
                        trustInteropProfileUri = trustInteropProfileUris.get(0)
                    }

                    if (trustInteropProfileUri) {

                        // create TIP from content
                        TrustInteroperabilityProfileJsonDeserializer deserializer = new TrustInteroperabilityProfileJsonDeserializer(false)
                        TrustInteroperabilityProfile trustInteroperabilityProfile = deserializer.deserialize(trustInteropProfileUri.content)

                        // test for staleness
                        if (testStalenessThreshold(trustInteropProfileUri.retrievalTimestamp)) {
                            remoteArtifactStaleness.addTrustInteroperabilityStalenessMessage(new RemoteArtifactStalenessMessage("Trust Interoperability Profile",
                                    "The TIP <${trustInteroperabilityProfile.name}> is no longer available at <${serverUri.toString()}>." +
                                            " The following error was reported: ${tipException.toString()}",
                                    tipUri.toString()))
                        }

                        return trustInteroperabilityProfile
                    }

                    return null;
                },
                (tipUri, tipException, serverUri, serverException) -> {

                    log.info("TIP ${tipUri.toString()} error: ${tipException.toString()}, server failed: ${serverUri.toString()} with error: ${serverException.toString()}...")

                    // check tpat staleness, do not use serverUri since it does not include the tpat's path
                    String tpatUrl = UrlUtilities.artifactBaseUrl(tipUri.toString());

                    // Note: The TPAT url test is done because the tf-api is not currently handling the context-path
                    // of a server. The test should be removed if and when tmf-api implements context-path handling
                    if (!UrlUtilities.checkTPATStatusUrl(tpatUrl)) {

                        List<TrustPolicyAuthoringToolUri> trustPolicyAuthoringToolUris = TrustPolicyAuthoringToolUri.findAllByUri(tpatUrl.toString(),
                                [max: 1, sort: "statusSuccessTimestamp", order: "desc", offset: 0])

                        TrustPolicyAuthoringToolUri trustPolicyAuthoringToolUri = null
                        if (trustPolicyAuthoringToolUris && trustPolicyAuthoringToolUris.size() > 0) {
                            trustPolicyAuthoringToolUri = trustPolicyAuthoringToolUris.get(0)
                        }

                        if (trustPolicyAuthoringToolUri) {
                            // check for server staleness
                            if (testStalenessThreshold(trustPolicyAuthoringToolUri.statusSuccessTimestamp)) {
                                remoteArtifactStaleness.addServerStalenessMessage(new RemoteArtifactStalenessMessage("Trust Policy Authoring Tool",
                                        "The TPAT is no longer available at <${tpatUrl}>." +
                                                " The following error was reported: ${serverException.toString()}",
                                        tpatUrl))
                            }
                        }
                    }

                    List<TrustInteropProfileUri> trustInteropProfileUris = TrustInteropProfileUri.findAllByUri(tipUri.toString(),
                            [max: 1, sort: "retrievalTimestamp", order: "desc", offset: 0])

                    TrustInteropProfileUri trustInteropProfileUri = null
                    if (trustInteropProfileUris && trustInteropProfileUris.size() > 0) {
                        trustInteropProfileUri = trustInteropProfileUris.get(0)
                    }

                    if (trustInteropProfileUri) {
                        // create TIP from content
                        TrustInteroperabilityProfileJsonDeserializer deserializer = new TrustInteroperabilityProfileJsonDeserializer(false)
                        TrustInteroperabilityProfile trustInteroperabilityProfile = deserializer.deserialize(trustInteropProfileUri.content)

                        // Note: The TPAT url test is done because the tf-api is not currently handling the context-path
                        // of a server. The test should be removed if and when tmf-api implements context-path handling
                        if (UrlUtilities.checkTPATStatusUrl(tpatUrl)) {
                            // check for artifact staleness
                            if (testStalenessThreshold(trustInteropProfileUri.retrievalTimestamp)) {
                                remoteArtifactStaleness.addTrustInteroperabilityStalenessMessage(new RemoteArtifactStalenessMessage("Trust Interoperability Profile",
                                        "The TIP <${trustInteroperabilityProfile.name}> is no longer available at server <${tpatUrl.toString()}>." +
                                                " The following error was reported: ${tipException.toString()}",
                                        tipUri.toString()))
                            }
                        }

                        return trustInteroperabilityProfile
                    }

                    return null;
                });
    }

    private TrustmarkDefinition resolveTrustmarkDefinition(TrustmarkDefinitionResolver resolver, String tdUriString) {
        log.info("Resolving TD: ${tdUriString}")

        final URI uri = new URI(tdUriString);

        final TrustmarkDefinition tip = resolver.resolve(
                uri,
                (tdUri, td) -> {

                    log.info("TD ${tdUri.toString()}: OK")

                    Serializer jsonSerializer = FactoryLoader.getInstance(SerializerFactory.class).getJsonSerializer();

                    // compute hash
                    HashFactory hasher = FactoryLoader.getInstance(HashFactory.class);
                    byte[] enc = hasher.hash(td);
                    char[] enc2 = Hex.encode(enc);
                    String hash = new String(enc2);

                    TrustmarkDefinitionUri trustmarkDefinitionUri = TrustmarkDefinitionUri.findByUri(tdUri.toString())

                    if (!trustmarkDefinitionUri || !trustmarkDefinitionUri.hash.equals(hash)) {
                        // TD changed, update td uri
                        log.info("TD has changed, updating cached TD...")

                        if (trustmarkDefinitionUri) {
                            // Serialize (use json)
                            StringWriter jsonWriter = new StringWriter();
                            jsonSerializer.serialize(td, jsonWriter);
                            String content = jsonWriter.toString();

                            trustmarkDefinitionUri.hash = hash
                            trustmarkDefinitionUri.content = content
                            trustmarkDefinitionUri.retrievalTimestamp = LocalDateTime.now()
                            trustmarkDefinitionUri.save(failOnError: true, flush: true)
                        } else {

                            TrustmarkDefinitionUri newCachedTdUri =  createTrustmarkDefinitionUri(td, tdUri.toString(),
                                    jsonSerializer, hasher, LocalDateTime.now())

                            newCachedTdUri.save(failOnError: true, flush: true)
                        }
                    }

                    return td;
                },
                (tdUri, tdException, serverUri) -> {

                    log.info("TD ${tdUri.toString()} deleted, error: ${tdException.toString()}, server OK: ${serverUri.toString()}...")

                    // td deleted, check if there is a cached version, email staled

                    TrustmarkDefinitionUri trustmarkDefinitionUri = TrustmarkDefinitionUri.findByUri(tdUri.toString())

                    if (trustmarkDefinitionUri) {
                        // create TD from content
                        TrustmarkDefinitionJsonDeserializer deserializer = new TrustmarkDefinitionJsonDeserializer(false)
                        TrustmarkDefinition trustmarkDefinition = deserializer.deserialize(trustmarkDefinitionUri.content)

                        // test for staleness
                        if (testStalenessThreshold(trustmarkDefinitionUri.retrievalTimestamp)) {
                            remoteArtifactStaleness.addTrustmarkDefinitionStalenessMessage(new RemoteArtifactStalenessMessage("Trustmark Definition",
                                    "The TD <${trustmarkDefinition.name}> is no longer available at <${serverUri.toString()}>." +
                                            " The following error was reported: ${tdException.toString()}",
                                    tdUri.toString()))
                        }

                        return trustmarkDefinition
                    }

                    return null;
                },
                (tdUri, tdException, serverUri, serverException) -> {

                    log.info("TD ${tdUri.toString()} error: ${tdException.toString()}, server failed: ${serverUri.toString()} with error: ${serverException.toString()}...")

                    // Note: The TPAT url test is done because the tf-api is not currently handling the context-path
                    // of a server. The test should be removed if and when tmf-api implements context-path handling
                    String tpatUrl = UrlUtilities.artifactBaseUrl(tdUri.toString());
                    if (!UrlUtilities.checkTPATStatusUrl(tpatUrl)) {

                        List<TrustPolicyAuthoringToolUri> trustPolicyAuthoringToolUris = TrustPolicyAuthoringToolUri.findAllByUri(tpatUrl.toString(),
                                [max: 1, sort: "statusSuccessTimestamp", order: "desc", offset: 0])

                        TrustPolicyAuthoringToolUri trustPolicyAuthoringToolUri = null
                        if (trustPolicyAuthoringToolUris && trustPolicyAuthoringToolUris.size() > 0) {
                            trustPolicyAuthoringToolUri = trustPolicyAuthoringToolUris.get(0)
                        }

                        if (trustPolicyAuthoringToolUri) {
                            // check for server staleness
                            if (testStalenessThreshold(trustPolicyAuthoringToolUri.statusSuccessTimestamp)) {
                                remoteArtifactStaleness.addServerStalenessMessage(new RemoteArtifactStalenessMessage("Trust Policy Authoring Tool",
                                        "The TPAT is no longer available at <${tpatUrl}>." +
                                                " The following error was reported: ${serverException.toString()}",
                                        tpatUrl))
                            }
                        }
                    }

                    TrustmarkDefinitionUri trustmarkDefinitionUri = TrustmarkDefinitionUri.findByUri(tdUri.toString())

                    if (trustmarkDefinitionUri) {

                        // create TIP from content
                        TrustmarkDefinitionJsonDeserializer deserializer = new TrustmarkDefinitionJsonDeserializer(false)
                        TrustmarkDefinition trustmarkDefinition = deserializer.deserialize(trustmarkDefinitionUri.content)

                        // Note: The TPAT url test is done because the tf-api is not currently handling the context-path
                        // of a server. The test should be removed if and when tmf-api implements context-path handling
                        if (UrlUtilities.checkTPATStatusUrl(tpatUrl)) {
                            // check for artifact staleness
                            if (testStalenessThreshold(trustmarkDefinitionUri.retrievalTimestamp)) {
                                remoteArtifactStaleness.addTrustmarkDefinitionStalenessMessage(new RemoteArtifactStalenessMessage("Trustmark Definition",
                                        "The TD <${trustmarkDefinition.name}> is no longer available at the server <${tpatUrl.toString()}>." +
                                                " The following error was reported: ${tdException.toString()}",
                                        tdUri.toString()))
                            }
                        }

                        return trustmarkDefinition
                    }

                    return null;
                });
    }

    private String ensureTrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    private TrustInteropProfileUri createTrustInteropProfileUri(TrustInteroperabilityProfile tip, String tipUrl,
                                                                               Serializer jsonSerializer, HashFactory hasher, LocalDateTime now) throws ResolveException, IOException {

        // Serialize (use json)
        StringWriter jsonWriter = new StringWriter();
        jsonSerializer.serialize(tip, jsonWriter);
        String content = jsonWriter.toString();

        // compute hash
        String hash = new String(Hex.encode(hasher.hash(tip)));

        // Create TIP URI
        TrustInteropProfileUri tipUri = new TrustInteropProfileUri(uri: tipUrl, hash: hash,
                content: content, retrievalTimestamp: now);

        return tipUri;
    }

    private TrustmarkDefinitionUri createTrustmarkDefinitionUri(TrustmarkDefinition td, String uri, Serializer jsonSerializer,
                                                             HashFactory hasher, LocalDateTime now) throws ResolveException, IOException {

        // Serialize (use json)
        StringWriter jsonWriter = new StringWriter();
        jsonSerializer.serialize(td, jsonWriter);
        String content = jsonWriter.toString();

        // compute hash
        String hash = new String(Hex.encode(hasher.hash(td)));

        // Create TIP URI
        TrustmarkDefinitionUri tdUri = new TrustmarkDefinitionUri(uri: uri, hash: hash,
                content: content, retrievalTimestamp: now);

        return tdUri;
    }

    private TrustmarkUri createTrustmarkUri(Trustmark tm, String uri, AssessmentRepository assessmentRepository,
                                            TrustmarkRecipientIdentifier trustmarkRecipientIdentifier, Serializer jsonSerializer,
                                            HashFactory hasher, LocalDateTime now) throws ResolveException, IOException {

        // Serialize (use json)
        StringWriter jsonWriter = new StringWriter();
        jsonSerializer.serialize(tm, jsonWriter);
        String content = jsonWriter.toString();

        // compute hash
        String hash = new String(Hex.encode(hasher.hash(tm)));

        // Create TM URI
        TrustmarkUri tmUri = new TrustmarkUri(uri: uri, hash: hash,
                content: content, retrievalTimestamp: now,
                assessmentRepositoryUrl: assessmentRepository.repoUrl,
                trustmarkRecipientIdentifierUrl: trustmarkRecipientIdentifier.trustmarkRecipientIdentifierUrl);

        return tmUri;
    }

    private TrustmarkStatusReportUri createTrustmarkStatusReportUri(TrustmarkStatusReport tsr, String uri, 
                                                                    String hash, LocalDateTime now) throws ResolveException, IOException {

        Serializer jsonSerializer = FactoryLoader.getInstance(SerializerFactory.class).getJsonSerializer();
        // Serialize (use json)
        StringWriter jsonWriter = new StringWriter();
        jsonSerializer.serialize(tsr, jsonWriter);
        String content = jsonWriter.toString();

        // Create TSR URI
        TrustmarkStatusReportUri tsrUri = new TrustmarkStatusReportUri(uri: uri, hash: hash,
                content: content, retrievalTimestamp: now);

        return tsrUri;
    }

    TrustmarkStatusReport resolveTrustmarkStatusReport(edu.gatech.gtri.trustmark.v1_0.model.Trustmark trustmark) {
        TrustmarkStatusReportResolver resolver = FactoryLoader.getInstance(TrustmarkStatusReportResolver.class);

        String trustmarkStatusReportUrl = trustmark.statusURL.toString()

        log.info("Resolving Trustmark Status Report: ${trustmarkStatusReportUrl}")

        final URL url = new URL(trustmarkStatusReportUrl);

        final TrustmarkStatusReport trustmarkStatusReport = resolver.resolve(
                url,
                (statusUrl, tsr) -> {

                    log.info("TSR ${statusUrl.toString()} OK...")

                    TrustmarkStatusReportUri trustmarkStatusReportUri = TrustmarkStatusReportUri.findByUri(statusUrl.toString())

                    // compute hash
                    HashFactory hasher = FactoryLoader.getInstance(HashFactory.class);
                    String hash = new String(Hex.encode(hasher.hash(tsr)));

                    if (!trustmarkStatusReportUri || !trustmarkStatusReportUri.hash.equals(hash)) {
                        // TSR changed, save new tsr uri
                        log.info("TSR has changed, caching new TSR...")

                        if (trustmarkStatusReportUri) {
                            Serializer jsonSerializer = FactoryLoader.getInstance(SerializerFactory.class).getJsonSerializer();
                            StringWriter jsonWriter = new StringWriter();
                            jsonSerializer.serialize(tsr, jsonWriter);
                            String content = jsonWriter.toString();

                            trustmarkStatusReportUri.hash = hash
                            trustmarkStatusReportUri.content = content
                            trustmarkStatusReportUri.retrievalTimestamp = LocalDateTime.now()

                            trustmarkStatusReportUri.save(failOnError: true, flush: true)
                        } else {
                            TrustmarkStatusReportUri newCachedTsrUri =  createTrustmarkStatusReportUri(tsr, statusUrl.toString(),
                                    hash, LocalDateTime.now())

                            newCachedTsrUri.save(failOnError: true, flush: true)
                        }
                    }

                    return tsr;
                },
                (statusUrl, tsrException, serverUri) -> {

                    log.info("TSR ${statusUrl.toString()} deleted, error: ${tsrException.toString()}, server OK: ${serverUri.toString()}...")

                    TrustmarkStatusReportUri trustmarkStatusReportUri = TrustmarkStatusReportUri.findByUri(statusUrl.toString())

                    if (trustmarkStatusReportUri) {
                        // create TM from content
                        TrustmarkStatusReportJsonDeserializer deserializer = new TrustmarkStatusReportJsonDeserializer()
                        TrustmarkStatusReport trustmarkStatusReport = deserializer.deserialize(trustmarkStatusReportUri.content)

                        // test for staleness
                        if (testStalenessThreshold(trustmarkStatusReportUri.retrievalTimestamp)) {
                            remoteArtifactStaleness.addTrustmarkStatusReportnStalenessMessage(new RemoteArtifactStalenessMessage("Trustmark Status Report",
                                    "The trustmark status report <${trustmarkStatusReport.identifier.toString()}> is no longer available at <${serverUri.toString()}>." +
                                            " The following error was reported: ${tsrException.toString()}",
                                    statusUrl.toString()))
                        }

                        return trustmarkStatusReport
                    }

                    return null;
                },
                (statusUrl, tsrException, serverUri, serverException) -> {

                    log.info("TSR ${statusUrl.toString()} error: ${tsrException.toString()}, server failed: ${serverUri.toString()}...")

                    // Note: The TPAT url test is done because the tf-api is not currently handling the context-path
                    // of a server. The test should be removed if and when tmf-api implements context-path handling
                    String tatUrl = UrlUtilities.artifactBaseUrl(statusUrl.toString());
                    if (!UrlUtilities.checkTATStatusUrl(tatUrl)) {

                        List<TrustmarkAssessmentToolUri> trustmarkAssessmentToolUris = TrustmarkAssessmentToolUri.findAllByUri(
                                tatUrl, [max: 1, sort: "statusSuccessTimestamp", order: "desc", offset: 0])

                        TrustmarkAssessmentToolUri trustmarkAssessmentToolUri = null
                        if (trustmarkAssessmentToolUris && trustmarkAssessmentToolUris.size() > 0) {
                            trustmarkAssessmentToolUri = trustmarkAssessmentToolUris.get(0)
                        }

                        if (trustmarkAssessmentToolUri) {
                            // check for server staleness
                            if (testStalenessThreshold(trustmarkAssessmentToolUri.statusSuccessTimestamp)) {
                                remoteArtifactStaleness.addServerStalenessMessage(new RemoteArtifactStalenessMessage("Trustmark Assessment Tool",
                                        "The TAT is no longer available at <${tatUrl}>." +
                                                " The following error was reported: ${serverException.toString()}",
                                        tatUrl))
                            }
                        }
                    }

                    TrustmarkStatusReportUri trustmarkStatusReportUri = TrustmarkStatusReportUri.findByUri(statusUrl.toString())

                    if (trustmarkStatusReportUri) {
                        // create TSR from content
                        TrustmarkStatusReportJsonDeserializer deserializer = new TrustmarkStatusReportJsonDeserializer()
                        TrustmarkStatusReport trustmarkStatusReport = deserializer.deserialize(trustmarkStatusReportUri.content)

                        // Note: The TPAT url test is done because the tf-api is not currently handling the context-path
                        // of a server. The test should be removed if and when tmf-api implements context-path handling
                        if (UrlUtilities.checkTATStatusUrl(tatUrl)) {
                            // check for artifact staleness
                            if (testStalenessThreshold(trustmarkStatusReportUri.retrievalTimestamp)) {
                                remoteArtifactStaleness.addTrustmarkStatusReportnStalenessMessage(new RemoteArtifactStalenessMessage("Trustmark Status Report",
                                        "The TSR is no longer available at server <${tatUrl.toString()}>." +
                                                " The following error was reported: ${tsrException.toString()}",
                                        statusUrl.toString()))
                            }
                        }

                        return trustmarkStatusReport
                    }

                    return null;
                });

        return trustmarkStatusReport
    }

    String getProviderExtension(edu.gatech.gtri.trustmark.v1_0.model.Trustmark trustmark, String extension) {
        Extension extensions = trustmark.getProviderExtension();
        List<Object> objs = extensions.getData();

        String extensionValue = "";

        for (Object o : objs) {
            if (o instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject)o;

                if (jsonObject != null && jsonObject.has("nief:TrustmarkProviderExtension")) {
                    JSONObject root = (JSONObject) jsonObject.get("nief:TrustmarkProviderExtension");

                    String ext = "nief:" + extension;
                    if (root != null && root.has(ext)) {
                        if (ext.equals("nief:has-exceptions")) {
                            Boolean hasExceptions = (boolean) root.get(ext);
                            extensionValue = hasExceptions.toString();
                        } else {
                            extensionValue = (String)root.get(ext);
                        }
                        break;
                    }
                }

            } else if (o instanceof DefaultElement) {
                DefaultElement elem = (DefaultElement) o;
                if (elem != null && elem.getName().equals("TrustmarkProviderExtension")) {
                    Element e = elem.element(extension);
                    extensionValue = e.getText();
                    break;
                }
            }
        }

        return extensionValue;
    }

    void sendRemoteArtifactStalenessEmail(List<RemoteArtifactStalenessMessage> remoteArtifactStalenessMessages,
                                          List<RemoteArtifactStalenessMessage> remoteServerStalenessMessages) {

        if (emailService.mailEnabled()) {

            Map model = [:]

            // admin
            String adminEmail = TBRProperties.getAdminEmail()

            // TBR name and url
            String tbrName = TBRProperties.getTbrName()
            String tbrUrl = TBRProperties.getBaseUrl()

            model.put("adminEmail", adminEmail)
            model.put("serverRecords", remoteServerStalenessMessages)
            model.put("artifactRecords", remoteArtifactStalenessMessages)
            model.put("tbrName", tbrName)
            model.put("tbrUrl", tbrUrl)

            String contentTemplate = "/templates/remoteArtifactsStalenessEmail"
            String subject = "Remote artifacts problems found during latest trustmark binding."

            String content = groovyPageRenderer.render(template: contentTemplate, model: model).toString()

            emailService.sendEmailWithContent(new ArrayList<MultipartFile>(), adminEmail, subject, content)
        } else {
            log.info("Email is disabled. Enable email in the properties file")
        }
    }

}
