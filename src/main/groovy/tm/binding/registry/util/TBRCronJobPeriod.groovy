package tm.binding.registry.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory


class TBRCronJobPeriod {

    private static final Logger log = LoggerFactory.getLogger(TBRCronJobPeriod.class)

    // startingHour => (0 <= startingHour <=23)
    static String getExpressionTrigger(Integer startingHour) {

        try {
            Integer periodInDays = Integer.parseInt(
                    TBRProperties.getProperties().getProperty("tbr.cron.job.period.in.hours") ?: "1")
            return String.format("0 0 %d/%s * * ?", startingHour, periodInDays)
        } catch (Throwable t) {
            log.error("TBRCronJobPeriod::getExpressionTrigger failed: ${t.message}", t)

            return getDefaultExpressionTrigger()
        }
    }

    static String getDefaultExpressionTrigger() {
        return "0 0 1 * * ?"
    }
}

