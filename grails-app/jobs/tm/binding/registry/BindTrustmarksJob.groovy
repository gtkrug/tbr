package tm.binding.registry

import grails.gorm.transactions.Transactional
import org.quartz.JobExecutionContext
import tm.binding.registry.util.TBRCronJobPeriod

/**
 * Binds trustmarks to registered organizations and to system providers based on conformance target trust
 * interoperability profiles from registered assessment tools.
 * Created by robert on 2/3/21.
 */
@Transactional
class BindTrustmarksJob {

    public static final Integer jobStartingHour = 1

    static String cronExpressionTrigger() {
        return TBRCronJobPeriod.getExpressionTrigger(jobStartingHour)
    }

    static triggers = {
        cron cronExpression: cronExpressionTrigger()
    }

    public static final String TRUSTMARKS_FIND_BY_RECIPIENT_TAT_ENDPOINT = "public/trustmarks/find-by-recipient/"

    //==================================================================================================================
    // Job Specifics
    //==================================================================================================================
//    def sessionRequired = true

    static concurrent = false
    def description = "Binds trustmarks to registered organizations and to system providers based on conformance target " +
            "trust interoperability profiles from registered assessment tools."

    def sessionFactory

    def providerService

    def organizationService

    //==================================================================================================================
    // Execute entry point
    //==================================================================================================================

    def execute() {
        log.info("Starting ${this.getClass().getSimpleName()}...")
        long overallStartTime = System.currentTimeMillis()

        // Serialize trustmark binding for organizations and systems to avoid GORM's optimistic locking issues related
        // to updating cached trustmark status reports

        organizationService.bindTrustmarksToAllOrganizations()

        providerService.bindTrustmarksForAllProviders()

        long overallStopTime = System.currentTimeMillis()
        log.info("Successfully Executed ${this.getClass().getSimpleName()} in ${(overallStopTime - overallStartTime)}ms.")

    }//end execute()
}/* End tm.binding.registry.BindTrustmarksToProviderJob */
