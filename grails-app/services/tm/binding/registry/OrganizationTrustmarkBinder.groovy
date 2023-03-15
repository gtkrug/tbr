package tm.binding.registry

import edu.gatech.gtri.trustmark.grails.email.service.EmailService
import edu.gatech.gtri.trustmark.v1_0.impl.io.json.TrustmarkStatusReportJsonDeserializer
import edu.gatech.gtri.trustmark.v1_0.model.TrustmarkStatusCode
import edu.gatech.gtri.trustmark.v1_0.model.TrustmarkStatusReport
import grails.gsp.PageRenderer
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.Executor
import java.util.concurrent.Executors

public class OrganizationTrustmarkBinder extends TrustmarkBinder {
    private static final Logger log = LoggerFactory.getLogger(OrganizationTrustmarkBinder.class);

    private static final int NUMBER_OF_EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
    private static final Executor executor = Executors.newFixedThreadPool(2 * NUMBER_OF_EXECUTOR_THREADS)

    public OrganizationTrustmarkBinder(EmailService emailService, PageRenderer groovyPageRenderer) {
        super(null, emailService, groovyPageRenderer)
    }

    public void bindTrustmarks(Organization organization) {
        log.info("** Starting bindTrustmarks: class: ${this.getClass().getSimpleName()}...")
        long overallStartTime = System.currentTimeMillis()

        try {
            // remove previously bound trustmarks for this organization
            organization.trustmarks.clear()

            // For each Assessment tool url,
            //      for each recipient identifier,
            //           get all trustmarks

            // Get assessment tool URLs
            def assessmentToolUrls = organization.assessmentRepos

            // Collect Trustmark Recipient Identifiers
            Set<TrustmarkRecipientIdentifier> recipientIdentifiers = getOrganizationRecipientIdentifiers(organization);

            OrganizationTrustmarkDefinitionUriFilter tdFilter = new OrganizationTrustmarkDefinitionUriFilter();

            List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> trustmarks = collectTrustmarksForAllRecipientIdentifiers(tdFilter,
                    assessmentToolUrls, recipientIdentifiers, false);

            saveTrustmarks(trustmarks, organization);

            log.info("Successfully bound " + trustmarks.size() + " trustmarks to organization: " + organization.name)
        }
        catch (Throwable t) {
            log.error("Error encountered during the trustmark binding process for organizations: ${t.message}", t);
        }

        long overallStopTime = System.currentTimeMillis()
        log.info("** Successfully Executed ${this.getClass().getSimpleName()} in ${(overallStopTime - overallStartTime)}ms.")

        if (remoteArtifactStaleness.hasMessages()) {
            // process remote artifact staleness
            // sends an email to TBR admin for all problems found
            final List<RemoteArtifactStalenessMessage> remoteArtifactStalenessMessageList =
                    new ArrayList<>(this.remoteArtifactStaleness.getArtifactMessages())
            final List<RemoteArtifactStalenessMessage> remoteServerStalenessMessageList =
                    new ArrayList<>(this.remoteArtifactStaleness.getServerMessages())
            Runnable remoteArtifactsStaleness = new Runnable() {
                @Override
                void run() {
                    // create
                    sendRemoteArtifactStalenessEmail(remoteArtifactStalenessMessageList, remoteServerStalenessMessageList)
                }
            };
            executor.execute(remoteArtifactsStaleness);
        }
    }

    private void saveTrustmarks(final List<edu.gatech.gtri.trustmark.v1_0.model.Trustmark> bindingTrustmarks,
                                final Organization organization) {
//        Integer currentTrustmarkIndex = 0
//        Integer totalToBeBoundTrustmarks = bindingTrustmarks.size()

        TrustmarkStatusReportJsonDeserializer deserializer = new TrustmarkStatusReportJsonDeserializer()

        Organization.withTransaction {
            bindingTrustmarks.forEach(trustmark -> {

                // retrieve TSR
                TrustmarkStatusReportUri trustmarkStatusReportUri = TrustmarkStatusReportUri.findByUri(trustmark.statusURL.toString())

                TrustmarkStatusReport tsr = deserializer.deserialize(trustmarkStatusReportUri.content)

                if (tsr.status == TrustmarkStatusCode.ACTIVE) {
                    // save to db
                    Trustmark tm = new Trustmark()
                    tm.name = trustmark.getTrustmarkDefinitionReference().name

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

                    tm.save(failOnError: true, flush: true)

                    organization.trustmarks.add(tm)
                }
            })
        }

        organization.save(failOnError: true, flush: true)
    }

}
