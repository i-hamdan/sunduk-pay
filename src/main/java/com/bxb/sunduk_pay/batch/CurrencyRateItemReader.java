package com.bxb.sunduk_pay.batch;

import com.bxb.sunduk_pay.util.CurrencyPair;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
    /**
 * ItemReader implementation that reads CurrencyPair values one by one.
 * This reader iterates over all defined CurrencyPair enum values.
 */
@Log4j2
@Component
public class CurrencyRateItemReader implements ItemReader<CurrencyPair>,
            StepExecutionListener {

    /** Iterator over all CurrencyPair enum values. */
    private  Iterator<CurrencyPair> currencyPairs;
/**
     * Initializes the iterator before the step execution begins.
     * This method is called once per step execution.
     * @param stepExecution the current step execution context
 **/
    @Override
    public void beforeStep(final StepExecution stepExecution) {
        this.currencyPairs = Arrays.asList(CurrencyPair
                .values()).iterator();
        log.info("Initialized CurrencyPairs for new step execution");
        }
        /**
     * Reads the next CurrencyPair from the iterator.
     * Returns null when all pairs have been read.
     * @return the next CurrencyPair or null if none left
     * @throws Exception if an error occurs during reading
     */
    @Override
    public CurrencyPair read() throws Exception {
        if (currencyPairs.hasNext()) {
            CurrencyPair nextPair = currencyPairs.next();
            log.info("Reading CurrencyPair: {}", nextPair);
            return nextPair;
        }
        log.info("No more CurrencyPairs to read. Returning null.");
        return null;
    }
    }

