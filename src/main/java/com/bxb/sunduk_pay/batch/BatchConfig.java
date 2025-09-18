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

/**
 * Spring Batch configuration class that defines the steps and jobs for currency rate import.
 */
@Configuration
@RequiredArgsConstructor
@Log4j2
public class BatchConfig {

    /** Transaction manager used by batch steps. */
    private final PlatformTransactionManager transactionManager;

    /** ItemReader for fetching currency pairs. */
    private final CurrencyRateItemReader reader;

    /** ItemProcessor for processing currency rates. */
    private final CurrencyRatesItemProcessor processor;

    /** ItemWriter for writing currency rates to the database. */
    private final CurrencyRatesItemWriter writer;

    /**
     * Defines the step that reads currency pairs, processes them, and writes the results.
     *
     * @param jobRepository Spring Batch JobRepository
     * @return configured Step
     */
    @Bean
    public Step fetchRatesStep(final JobRepository jobRepository) {
        log.info("Creating Step: fetchRatesStep");

        return new StepBuilder("fetchRatesStep", jobRepository)
                .<CurrencyPair, CurrencyRates>chunk(
                        100,  // Reader output, Processor output
                        transactionManager
                )
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    /**
     * Defines the batch job that runs the fetchRatesStep.
     *
     * @param fetchRatesStep Step to execute
     * @param jobRepository Spring Batch JobRepository
     * @return configured Job
     */
    @Bean
    public Job importExchangeRatesJob(
            final Step fetchRatesStep,
            final JobRepository jobRepository
    ) {
        log.info("Creating Job: ExchangeRatesJob");

        return new JobBuilder("ExchangeRatesJob", jobRepository)
                .start(fetchRatesStep)
                .build();
    }
}
