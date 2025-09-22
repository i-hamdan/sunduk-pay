package com.bxb.sunduk_pay.batch;

import com.bxb.sunduk_pay.model.CurrencyRates;
import com.bxb.sunduk_pay.util.CurrencyPair;
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
 * Batch configuration class for setting up jobs and steps.
 * This class defines a job to fetch currency exchange rates
 * using a reader, processor, and writer.
 */

@Configuration
@Log4j2
public class BatchConfig {
/** Transaction manager for managing transactions in batch jobs.*/
    private final PlatformTransactionManager transactionManager;
/** Reader to read currency pairs.*/
    private final CurrencyRateItemReader reader;
/** Processor to process currency rates.*/
    private final CurrencyRatesItemProcessor processor;
  /** Writer to write currency rates in database.*/
    private final CurrencyRatesItemWriter writer;

    public BatchConfig(final PlatformTransactionManager transactionManager, final CurrencyRateItemReader reader, final CurrencyRatesItemProcessor processor, final CurrencyRatesItemWriter writer) {
        this.transactionManager = transactionManager;
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
    }

    /**
     * Defines a step to fetch currency rates.
     *
     * @param jobRepository the job repository
     * @return the configured step
     */


    @Bean
    public Step fetchRatesStep( final JobRepository jobRepository) {
        log.info("Creating Step: fetchRatesStep");
        return new StepBuilder("fetchRatesStep", jobRepository)
                .<CurrencyPair, CurrencyRates>chunk(100, transactionManager) // Reader output, Processor output
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    /** Defines a job to import exchange rates.
     *
     * @param fetchRatesStep the step to fetch rates
     * @param jobRepository  the job repository
     * @return the configured job
     */
    @Bean
    public Job importExchangeRatesJob ( final Step fetchRatesStep, final JobRepository jobRepository) {
        log.info("Creating Job: ExchangeRatesJob");

        return new JobBuilder("ExchangeRatesJob", jobRepository)
                .start(fetchRatesStep)
                .build();
    }
}


