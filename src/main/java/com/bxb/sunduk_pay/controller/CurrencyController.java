package com.bxb.sunduk_pay.controller;


import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.service.CurrencyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@CrossOrigin(origins = "http://localhost:5174",allowCredentials = "true")

public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/convert")
    public ResponseEntity<CurrencyResponse> convert(@RequestParam String fromCurrency,
                                                    @RequestParam String toCurrency,
                                                    @RequestParam Double amount) {
        log.info("Currency conversion API called: from = {}, to = {}, amount = {}", fromCurrency, toCurrency, amount);
        CurrencyResponse response = currencyService.convertCurrency(fromCurrency, toCurrency, amount);
        log.debug("Conversion successful. Response: {}", response);
        return ResponseEntity.ok(response);
    }
}

