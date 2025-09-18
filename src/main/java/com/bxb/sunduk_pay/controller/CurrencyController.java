package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.request.CurrencyRequest;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.service.CurrencyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for currency conversion operations.
 */
@RestController
@Log4j2
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
public class CurrencyController {

    /** Service for handling currency operations. */
    private final CurrencyService currencyService;

    /**
     * Constructor-based dependency injection.
     *
     * @param currencyServiceParam the service handling currency operations
     */
    public CurrencyController(final CurrencyService currencyServiceParam) {
        this.currencyService = currencyServiceParam;
    }

    /**
     * Converts an amount from one currency to another.
     *
     * @param currencyRequest the currency conversion request
     * @return the conversion response wrapped in ResponseEntity
     */
    @PostMapping("/convert")
    public ResponseEntity<CurrencyResponse> convert(
            @RequestBody final CurrencyRequest currencyRequest) {

        log.info(
                "Currency conversion API called: from = {},"
                        + " to = {}, amount = {}",
                currencyRequest.getFromCurrency(),
                currencyRequest.getToCurrency(),
                currencyRequest.getAmount()
        );

        final CurrencyResponse response = currencyService.convertCurrency(currencyRequest);

        log.debug("Conversion successful. Response: {}", response);

        return ResponseEntity.ok(response);
    }
}
