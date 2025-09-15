//package com.bxb.sunduk_pay.batch;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Log4j2
//@Component
//@RequiredArgsConstructor
//public class JobScheduler {
//    private final JobLauncher jobLauncher;
//    private final Job importExchangeRatesJob;
//   //A @Scheduled(cron = "*/30 * * * * *") // Every 30 seconds
//    //@Scheduled(cron = "0 0 */2 * * *") // Every 2 hours
//
//    public void runJob() {
//        try {
//            log.info(" Scheduler method triggered");
//
//            JobParameters params = new JobParametersBuilder()
//                    .addLong("timestamp", System.currentTimeMillis())
//                    .toJobParameters();
//
//            log.info(" Starting job: importExchangeRatesJob with params {}", params);
//            jobLauncher.run(importExchangeRatesJob, params);
//
//            log.info(" Job execution triggered successfully.");
//
//        } catch (Exception e) {
//            log.error(" Error while running scheduled job: {}", e.getMessage(), e);
//        }
//    }
//}
