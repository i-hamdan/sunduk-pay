package com.bxb.sunduk_pay.batch;

import com.bxb.sunduk_pay.util.CurrencyPair;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Simple ItemReader that iterates over all {@link CurrencyPair} enum values once.
 */
@Log4j2
@Component
public class CurrencyRateItemReader implements ItemReader<CurrencyPair> {

    private final Iterator<CurrencyPair> currencyPairsIterator;

    public CurrencyRateItemReader() {
        // snapshot of enum values, so repeated runs start cleanly
        final List<CurrencyPair> allPairs = Arrays.asList(CurrencyPair.values());
        this.currencyPairsIterator = allPairs.iterator();
    }

    @Override
    public CurrencyPair read() throws UnexpectedInputException,
            ParseException,
            NonTransientResourceException {
        if (currencyPairsIterator.hasNext()) {
            final CurrencyPair nextPair = currencyPairsIterator.next();
            log.info("Reading CurrencyPair: {}", nextPair);
            return nextPair;
        }
        log.info("No more CurrencyPairs to read. Returning null.");
        return null; // Spring Batch signals end-of-input
    }
}
