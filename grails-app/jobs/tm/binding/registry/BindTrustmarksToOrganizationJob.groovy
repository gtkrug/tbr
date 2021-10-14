package tm.binding.registry

import edu.gatech.gtri.trustmark.v1_0.impl.io.IOUtils
import grails.gorm.transactions.Transactional
import org.quartz.JobExecutionContext
import tm.binding.registry.util.TBRCronJobPeriod

/**
 * Binds trustmarks to registered organizations based on all trustmark recipient IDs associated with the organization
 * and all the organization's registered systems from all registered assessment tool repositories.
 * Created by robert on 10/8/21.
 */
@Transactional
class BindTrustmarksToOrganizationJob {

    public static final Integer jobStartingHour = 2

    static String cronExpressionTrigger() {
        return TBRCronJobPeriod.getExpressionTrigger(jobStartingHour)
    }

    static triggers = {
        cron cronExpression: cronExpressionTrigger()
    }

    //==================================================================================================================
    // Job Specifics
    //==================================================================================================================
    def concurrent = false
    def description = "Binds trustmarks to registered orgnizations providers based on all trustmark" +
            "recipient identifiers from registered assessment tools."

    def sessionFactory

    def organizationService

    //==================================================================================================================
    // Execute entry point
    //==================================================================================================================

    def execute() {
        log.info("Starting ${this.getClass().getSimpleName()}...")
        long overallStartTime = System.currentTimeMillis()

        organizationService.bindTrustmarksToAllOrganizations()

        long overallStopTime = System.currentTimeMillis()
        log.info("Successfully Executed ${this.getClass().getSimpleName()} in ${(overallStopTime - overallStartTime)}ms.")

    }//end execute()

}/* End tm.binding.registry.BindTrustmarksJobToOrganization */
