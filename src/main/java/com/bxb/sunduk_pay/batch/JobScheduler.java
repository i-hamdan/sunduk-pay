package com.bxb.sunduk_pay.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Schedules and runs the importExchangeRatesJob at midnight every day.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class JobScheduler {
    /** Job launcher to run batch jobs. */
    private final JobLauncher jobLauncher;
    /** Job to import exchange rates. */
    private final Job importExchangeRatesJob;
    /**
     * Executes the scheduled job.
     * <p>
     * Subclasses overriding this method should ensure that:
     * <ul>
     *   <li>The scheduled execution timing
     *   ({@link Scheduled}) is preserved, or
     *       a safe alternative schedule is provided.</li>
     *   <li>They maintain uniqueness of {@link JobParameters}, for example
     *       by including a timestamp.</li>
     *   <li>Proper error handling and logging
     *   are kept to avoid silent failures.</li>
     * </ul>
     * </p>
     *
     * @see JobLauncher#run(Job, JobParameters)
     */
    @Scheduled(cron = "0 0 0 * * *")
    //@Scheduled(cron = "0,30 * * * * *")
    public void runJob() {
        try {
            log.info("Scheduler method triggered");

            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp",
                            System.currentTimeMillis())
                    .toJobParameters();

            log.info(
                    "Starting job: importExchangeRatesJob with params {}",
                    params
            );
            jobLauncher.run(importExchangeRatesJob, params);

            log.info(" Job execution triggered successfully.");

        } catch (Exception e) {
            log.error(" Error while running scheduled job: {}",
                    e.getMessage(),
                    e
            );
        }
    }
}


