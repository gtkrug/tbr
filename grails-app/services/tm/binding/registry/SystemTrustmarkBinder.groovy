package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.model.TrustmarkStatusCode
import edu.gatech.gtri.trustmark.v1_0.model.TrustmarkStatusReport
import grails.gsp.PageRenderer
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import shared.views.EmailService

import java.util.concurrent.Executor
import java.util.concurrent.Executors


public class SystemTrustmarkBinder extends TrustmarkBinder{
    private static final Logger log = LoggerFactory.getLogger(SystemTrustmarkBinder.class);

    private static final int NUMBER_OF_EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
    private static final Executor executor = Executors.newFixedThreadPool(2 * NUMBER_OF_EXECUTOR_THREADS)


    public SystemTrustmarkBinder(ProviderService providerService, EmailService emailService, PageRenderer groovyPageRenderer) {
         super(providerService, emailService, groovyPageRenderer)
    }

    public void bindTrustmarks(Provider provider, Boolean monitoringProgress) {
        Objects.requireNonNull(providerService)

        log.info("** Starting bindTrustmarks: class: ${this.getClass().getSimpleName()}...");
        long overallStartTime = System.currentTimeMillis();

        try {

            Organization org = provider.organization;

            // Get assessment tool URLs
            Set<AssessmentRepository> assessmentToolUrls = org.assessmentRepos;

            // Collect Trustmark Recipient Identifier urls
            Set<TrustmarkRecipientIdentifier> recipientIdentifiers = collectRecipientIdentifiers(org, provider);

            // Get the conformance target tips
            Set<ConformanceTargetTip> conformanceTargetTips = provider.conformanceTargetTips;

            // process each conformance target tip

            // collect all TDs for this CTT and its children TIPs
            if (conformanceTargetTips.size() > 0 && assessmentToolUrls.size() && recipientIdentifiers.size() > 0) {

                // remove previously bound trustmarks for this provider system
                provider.trustmarks.clear();

                if (monitoringProgress) {
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0");
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_STATUS_VAR, "PRE-PROCESSING");
                }

                // collection of unique TDs for all conformance target tips
                Set<String> tdSet = collectTdsFromConformanceTargetTips(conformanceTargetTips, monitoringProgress);

                log.info("** Numbers of TDs processed: " + tdSet.size());

                // For each Assessment tool url,
                //      for each recipient identifier,
                //           get all trustmarks

                if (monitoringProgress) {
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Querying trustmarks for all registered trustmark recipients against all assessment tools registered for the organization: ${org.name}");
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0");
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_STATUS_VAR, "RUNNING");
                }

                // Filter out trustmarks not in current TD set
                SystemTrustmarkDefinitionUriFilter tdFilter = new SystemTrustmarkDefinitionUriFilter(tdSet);

                List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> trustmarks = collectTrustmarksForAllRecipientIdentifiers(
                        tdFilter, assessmentToolUrls, recipientIdentifiers, monitoringProgress);

                log.info("##### trustmarks TOTAL size: ${trustmarks.size()}")

                List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> bindingTrustmarks = mapTrustmarksToConformanceTargetTipTDs(trustmarks, tdSet, monitoringProgress);

                log.info("##### bindingTrustmarks TOTAL size: ${bindingTrustmarks.size()}")

                if (monitoringProgress) {
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Binding trustmarks...");
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "0");
                }

                saveTrustmarks(bindingTrustmarks, provider, monitoringProgress);

                log.info("Successfully bound " + bindingTrustmarks.size() + " trustmarks to provider: " + provider.name);
            } // end if (conformanceTargetTips.size() > 0)
        }
        catch (Throwable t) {
            log.info("Error encountered during the trustmark binding process: " + t.getMessage());
        }

        long overallStopTime = System.currentTimeMillis();
        log.info("** Successfully Executed ${this.getClass().getSimpleName()} in ${(overallStopTime - overallStartTime)}ms.");

        if (remoteArtifactStaleness.hasMessages()) {
            // sends an email to TBR admin for all problems found
            final List<RemoteArtifactStalenessMessage> remoteArtifactStalenessMessageList =
                    new ArrayList<>(this.remoteArtifactStaleness.getArtifactMessages())
            final List<RemoteArtifactStalenessMessage> remoteServerStalenessMessageList =
                    new ArrayList<>(this.remoteArtifactStaleness.getServerMessages())
            Runnable remoteArtifactsStaleness = new Runnable() {
                @Override
                void run() {
                    sendRemoteArtifactStalenessEmail(remoteArtifactStalenessMessageList, remoteServerStalenessMessageList)
                }
            };
            executor.execute(remoteArtifactsStaleness);
        }

        if (monitoringProgress) {
            providerService.stopExecuting(ProviderService.BIND_TRUSTMARKS_EXECUTING_VAR);
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "100");
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Successfully bound trustmarks in ${(overallStopTime - overallStartTime)}ms.");
            providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_STATUS_VAR, "SUCCESS");
        }

    }

    private void saveTrustmarks(List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> bindingTrustmarks, Provider provider, Boolean monitoringProgress) {
        Integer currentTrustmarkIndex = 0;
        Integer totalToBeBoundTrustmarks = bindingTrustmarks.size();

        bindingTrustmarks.forEach(trustmark -> {

            if (monitoringProgress) {
                providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Resolving trustmark status report...");
            }

            TrustmarkStatusReport tsr = resolveTrustmarkStatusReport(trustmark)

            if (tsr.status == TrustmarkStatusCode.ACTIVE) {
                if (monitoringProgress) {
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_MESSAGE_VAR, "Saving trustmark...");
                }

                // save to db
                Trustmark tm = new Trustmark();
                tm.name = trustmark.getTrustmarkDefinitionReference().name
//                            ConformanceTargetTip tip = ConformanceTargetTip.findByConformanceTargetTipIdentifier(pair.getValue());
                tm.conformanceTargetTipId = -1;
                tm.status = tsr.status.name()
                tm.url = trustmark.identifier
                tm.trustmarkDefinitionURL = trustmark.getTrustmarkDefinitionReference().getIdentifier().toString()

                tm.provisional = false
                String hasExceptions = getProviderExtension(trustmark, "has-exceptions")
                if (StringUtils.isNotEmpty(hasExceptions)) {
                    tm.provisional = Boolean.parseBoolean(hasExceptions)
                }

                tm.assessorComments = ""
                if (tm.provisional) {
                    tm.assessorComments = getProviderExtension(trustmark, "exception-details")
                }

                tm.save(failOnError: true, flush: true);

                provider.trustmarks.add(tm);

                if (monitoringProgress) {
                    // update progress percentage
                    int percent = (int) Math.floor(((double) currentTrustmarkIndex++ / (double) totalToBeBoundTrustmarks) * 100.0d);
                    providerService.setAttribute(ProviderService.BIND_TRUSTMARKS_PERCENT_VAR, "" + percent);
                }
            }
        })

        provider.save(failOnError: true, flush: true);
    }
}
