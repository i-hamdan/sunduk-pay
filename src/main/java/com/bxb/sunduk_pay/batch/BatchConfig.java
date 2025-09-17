package com.bxb.sunduk_pay.batch;
import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.util.CurrencyPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
@Configuration
@RequiredArgsConstructor
@Log4j2
public class BatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final CurrencyRateItemReader reader;
    private final CurrencyRatesItemProcessor processor;
    private final CurrencyRatesItemWriter writer;


    @Bean
    public Step fetchRatesStep(JobRepository jobRepository) {
        log.info("Creating Step: fetchRatesStep");
        return new StepBuilder("fetchRatesStep", jobRepository)
                .<CurrencyPair, CurrencyRates>chunk(100, transactionManager) // Reader output, Processor output
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importExchangeRatesJob(Step fetchRatesStep, JobRepository jobRepository) {
        log.info("Creating Job: ExchangeRatesJob");

        return new JobBuilder("ExchangeRatesJob", jobRepository)
                .start(fetchRatesStep)
                .build();
    }
}
































//    @Bean
//    public JobLauncher jobLauncher(JobRepository jobRepository, TaskExecutor taskExecutor) {
//        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
//        jobLauncher.setJobRepository(jobRepository);
//
//        return jobLauncher;
//    }
//    @Bean
//    @Primary
//    public JobRepository jobRepository(
//            JobInstanceMongoRepo jobInstanceRepository,
//            JobExecutionMongoRepo jobExecutionRepository,
//            StepExecutionMongoRepo stepExecutionRepository
//    ) {
//        return new CustomJobRespository(jobInstanceRepository, jobExecutionRepository, stepExecutionRepository);
//    }















