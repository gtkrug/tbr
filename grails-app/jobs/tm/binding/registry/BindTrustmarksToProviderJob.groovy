package tm.binding.registry

import grails.gorm.transactions.Transactional

/**
 * Binds trustmarks to registered system providers based on conformance target trust interoperability profiles
 * from registered assessment tools.
 * Created by robert on 2/3/21.
 */
@Transactional
class BindTrustmarksToProviderJob {

    static triggers = {
    }

    public static final String TRUSTMARKS_FIND_BY_RECIPIENT_TAT_ENDPOINT = "public/trustmarks/find-by-recipient/"

    //==================================================================================================================
    // Job Specifics
    //==================================================================================================================
    def concurrent = false
    def description = "Binds trustmarks to registered system providers based on conformance target trust" +
            "interoperability profiles from registered assessment tools."

    def sessionFactory

    ProviderService providerService

    //==================================================================================================================
    // Execute entry point
    //==================================================================================================================

    def execute(context) {
        log.info("Starting ${this.getClass().getSimpleName()}...")
        long overallStartTime = System.currentTimeMillis()

        def id = context.mergedJobDataMap.get('id')

        providerService.bindTrustmarks(id)

        long overallStopTime = System.currentTimeMillis()
        log.info("Successfully Executed ${this.getClass().getSimpleName()} in ${(overallStopTime - overallStartTime)}ms.")

    }//end execute()
}/* End tm.binding.registry.BindTrustmarksToProviderJob */
