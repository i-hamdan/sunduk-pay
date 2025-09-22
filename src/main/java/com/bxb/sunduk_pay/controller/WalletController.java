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
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


/**
 * Controller for wallet operations, including balance checking,
 * transaction downloads, and wallet CRUD operations.
 */


@Log4j2
@RestController
public class WalletController {
    private final WalletOperationFactory walletFactory;
    private final WalletService walletService;

    public WalletController(WalletOperationFactory walletFactory,
                            WalletService walletService) {
        this.walletFactory = walletFactory;
        this.walletService = walletService;
    }


    /**
     * Returns the current balance of the specified wallet.
     *
     * @param walletId the wallet ID
     * @return wallet balance as a string
     */

    //this api will return the current balance of a wallet by walletId
    @GetMapping("/wallet-showBalance/{walletId}")
    public ResponseEntity<String> showBalance(@PathVariable String walletId) {
        return new ResponseEntity<>(walletService.showBalance(walletId),
                HttpStatus.OK);
    }

    @PostMapping("/addTxns")
    public void addDummyData(@RequestBody MainWalletRequest request) {
        walletService.addDummy(request);
    }

    /**
     * Downloads all transactions of the specified wallet as a PDF.
     *
     * @param walletId wallet ID
     * @param response HTTP servlet response to write PDF
     * @throws IOException if PDF generation fails
     */


    //this api will download all the transactions of a wallet.
    @PostMapping("/wallet-downloadPdf/{walletId}")
    public void downloadTransactions(@PathVariable String walletId,
                                     HttpServletResponse response)
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
    public ResponseEntity<MainWalletResponse> walletApi(@RequestBody MainWalletRequest
                                                                    mainWalletRequest,
                                                        HttpServletResponse response) {

        MainWalletResponse walletResponse = walletService.walletCrud(mainWalletRequest);

        // Create cookie with checkout URL from response
        if (walletResponse.getCheckoutUrl() != null) {  // only set cookie if URL exists
            Cookie urlCookie = new Cookie("stripe_checkout_url",
                    walletResponse.getCheckoutUrl());
            urlCookie.setPath("/");
            urlCookie.setHttpOnly(false);
            urlCookie.setSecure(true);
            urlCookie.setMaxAge(300);
            response.addCookie(urlCookie);
        }

        return new ResponseEntity<>(walletResponse, HttpStatus.OK);
    }


}