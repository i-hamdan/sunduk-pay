package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.factoryPattern.WalletOperationFactory;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.WalletService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


/**
 * Controller for wallet operations, including balance checking,
 * transaction downloads, and wallet CRUD operations.
 */


@Log4j2
@RestController
@RequiredArgsConstructor
public class WalletController {

    /**
     * Factory to get the appropriate wallet operation service
     * based on the request type.
     */
    private final WalletOperationFactory walletFactory;

    /**
     * Service to handle wallet-related business logic.
     */
    private final WalletService walletService;


    /** Cookie max age for Stripe checkout URL in seconds (5 minutes).
     */
    private static final int
            STRIPE_CHECKOUT_COOKIE_MAX_AGE_SECONDS = 300;

    /**
     * Returns the current balance of the specified wallet.
     *
     * @param walletId the wallet ID
     * @return wallet balance as a string
     */
    @GetMapping("/wallet-showBalance/{walletId}")
    public ResponseEntity<String> showBalance(@PathVariable String walletId) {
        return new ResponseEntity<>(walletService.showBalance(walletId),
                HttpStatus.OK);
    }

    /**
     * Adds dummy transaction data to the specified wallet.
     *
     * @param request request containing wallet ID and dummy data details
     */
    @PostMapping("/addTxns")
    public void addDummyData(@RequestBody final MainWalletRequest request) {
        walletService.addDummy(request);
    }

    /**
     * Downloads all transactions of the specified wallet as a PDF.
     *
     * @param walletId wallet ID
     * @param response HTTP servlet response to write PDF
     * @throws IOException if PDF generation fails
     */
    @PostMapping("/wallet-downloadPdf/{walletId}")
    public void downloadTransactions(@PathVariable final String walletId,
                                     final HttpServletResponse response)
            throws IOException {
        walletService.downloadTransactions(walletId, response);
    }

    /**
     * Handles wallet CRUD operations and sets Stripe checkout URL cookie
     * if a checkout URL is returned.
     *
     * @param mainWalletRequest request payload
     * @param response          HTTP servlet response to add cookies
     * @return main wallet response
     */
    @PostMapping("/wallet")
    public ResponseEntity<MainWalletResponse> walletApi(
                                                @RequestBody
                                               final MainWalletRequest
                                               mainWalletRequest,
                                      final HttpServletResponse response) {

        MainWalletResponse walletResponse = walletService.walletCrud(
                                                        mainWalletRequest);

        // Create cookie with checkout URL from response
        if (walletResponse.getCheckoutUrl() != null) {  // only set cookie if URL exists
            Cookie urlCookie = new Cookie("stripe_checkout_url",
                    walletResponse.getCheckoutUrl());
            urlCookie.setPath("/");
            urlCookie.setHttpOnly(false);
            urlCookie.setSecure(true);
            urlCookie.setMaxAge(STRIPE_CHECKOUT_COOKIE_MAX_AGE_SECONDS);
            response.addCookie(urlCookie);
        }

        return new ResponseEntity<>(walletResponse, HttpStatus.OK);
    }


}