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
    /**
 * ItemReader implementation that reads CurrencyPair values one by one.
 * This reader iterates over all defined CurrencyPair enum values.
 */
@Log4j2
@Component
public class CurrencyRateItemReader implements ItemReader<CurrencyPair> {

    private final Iterator<CurrencyPair> currencyPairs =
            Arrays.asList(CurrencyPair.values()).iterator();

    @Override
    public CurrencyPair read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (currencyPairs.hasNext()) {
            CurrencyPair nextPair = currencyPairs.next();
            log.info("Reading CurrencyPair: {}", nextPair);
            return nextPair;
        }
        log.info("No more CurrencyPairs to read. Returning null.");
        return null;
    }
    }

