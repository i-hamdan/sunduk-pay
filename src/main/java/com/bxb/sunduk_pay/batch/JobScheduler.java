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
 * Schedules and launches the currency exchange-rate import job at fixed intervals.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class JobScheduler {

    /** Spring Batch JobLauncher to run jobs */
    private final JobLauncher jobLauncher;

    /** Job instance that imports currency exchange rates */
    private final Job importExchangeRatesJob;

    /**
     * Runs the import job periodically.
     * Adjust the cron expression as needed.
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void runJob() {
        log.info("JobScheduler triggered to run importExchangeRatesJob.");

        try {
            // add timestamp to make job instance unique
            final JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.debug("Starting job 'importExchangeRatesJob' with params: {}",
                    params);

            if (jobLauncher == null || importExchangeRatesJob == null) {
                log.error("JobLauncher or Job is null. Cannot run job.");
                return;
            }

            jobLauncher.run(importExchangeRatesJob, params);
            log.info("Job 'importExchangeRatesJob' execution triggered successfully.");

        } catch (Exception e) {
            log.error("Error while running scheduled job: {}", e.getMessage(), e);
        }
    }
}
