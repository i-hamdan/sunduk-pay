package com.bxb.sunduk_pay.controller;


import com.bxb.sunduk_pay.request.CurrencyRequest;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.service.CurrencyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@CrossOrigin(origins = "http://localhost:5174",allowCredentials = "true")


/**
 * Controller for currency conversion operations.
 */
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;
    /**
     * Converts an amount from one currency to another.
     *
     * @param currencyRequest the currency conversion request
     * @return the conversion response wrapped in ResponseEntity
     */
    @PostMapping("/convert")
    
    public ResponseEntity<CurrencyResponse> convert(@RequestBody CurrencyRequest
                                                                currencyRequest) {
        log.info("Currency conversion API called: from = {}, to = {}, amount = {}",
                currencyRequest.getFromCurrency(), currencyRequest.getToCurrency(),
                currencyRequest.getAmount());
        CurrencyResponse response = currencyService.convertCurrency
                (currencyRequest);
        log.debug("Conversion successful. Response: {}", response);
        return ResponseEntity.ok(response);
    }
}