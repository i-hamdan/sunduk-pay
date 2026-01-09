package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
/**
 * Controller for handling investment-related API requests.
 */
@RestController
@RequiredArgsConstructor

public class InvestmentController {
    /** Service for handling investment operations. **/

    private final InvestmentService investmentService;
/**
     * Handles investment API requests.
     *
     * @param request the investment request payload
     * @return ResponseEntity containing the investment response
     */
@PostMapping("/investments")
    public ResponseEntity<InvestmentResponse> investmentApi(
            @RequestBody final InvestmentRequest request) {
    return ResponseEntity.ok(investmentService.investmentApi(request));
}
}

