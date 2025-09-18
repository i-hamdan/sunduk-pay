package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.factoryPattern.WalletOperationFactory;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.WalletService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Controller for wallet operations, including balance checking,
 * transaction downloads, and wallet CRUD operations.
 */
@Log4j2
@RestController
@RequestMapping("/wallet")
public class WalletController {

    /** Factory for wallet operations. */
    private final WalletOperationFactory walletFactory;

    /** Service for wallet-related operations. */
    private final WalletService walletService;

    /** Max age of Stripe checkout URL cookie in seconds. */
    private static final int COOKIE_MAX_AGE = 300; // 5 minutes

    /**
     * Constructor-based dependency injection.
     *
     * @param walletFactoryParam wallet operation factory
     * @param walletServiceParam wallet service
     */
    public WalletController(final WalletOperationFactory walletFactoryParam,
                            final WalletService walletServiceParam) {
        this.walletFactory = walletFactoryParam;
        this.walletService = walletServiceParam;
    }

    /**
     * Returns the current balance of the specified wallet.
     *
     * @param walletId the wallet ID
     * @return wallet balance as a string
     */
    @GetMapping("/showBalance/{walletId}")
    public ResponseEntity<String> showBalance(@PathVariable final String walletId) {
        final String balance = walletService.showBalance(walletId);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    /**
     * Adds dummy transactions to the wallet.
     *
     * @param request main wallet request containing transaction data
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
    @PostMapping("/downloadPdf/{walletId}")
    public void downloadTransactions(
            @PathVariable final String walletId,
            final HttpServletResponse response) throws IOException {
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
    @PostMapping
    public ResponseEntity<MainWalletResponse> walletApi(
            @RequestBody final MainWalletRequest mainWalletRequest,
            final HttpServletResponse response) {

        final MainWalletResponse walletResponse = walletService.walletCrud(mainWalletRequest);

        // Set cookie with Stripe checkout URL if available
        if (walletResponse.getCheckoutUrl() != null) {
            final Cookie urlCookie = new Cookie("stripe_checkout_url", walletResponse.getCheckoutUrl());
            urlCookie.setPath("/");
            urlCookie.setHttpOnly(false);
            urlCookie.setSecure(true);
            urlCookie.setMaxAge(COOKIE_MAX_AGE);
            response.addCookie(urlCookie);
        }

        return new ResponseEntity<>(walletResponse, HttpStatus.OK);
    }
}
