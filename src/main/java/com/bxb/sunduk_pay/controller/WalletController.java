package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.request.WalletRequest;

import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.StripeService;
import com.bxb.sunduk_pay.service.WalletService;
import com.stripe.model.checkout.Session;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class WalletController {
    private final StripeService stripeService;
    private final WalletService walletService;

    public WalletController(StripeService stripeService, WalletService walletService) {
        this.stripeService = stripeService;
        this.walletService = walletService;
    }

    // create wallet
    @PostMapping("/wallet-create")
    public ResponseEntity<String> createWallet(@RequestBody WalletRequest walletRequest) {
        return new ResponseEntity<>(walletService.createWallet(walletRequest), HttpStatus.CREATED);
    }

    //this api will return the current balance of a wallet by walletId
    @GetMapping("/wallet-showBalance/{walletId}")
    public ResponseEntity<String> showBalance(@PathVariable String walletId) {
        return new ResponseEntity<>(walletService.showBalance(walletId), HttpStatus.OK);
    }


    //this api will download all the transactions of a wallet.
    @PostMapping("/wallet-downloadPdf/{walletId}")
    public void downloadTransactions(@PathVariable String walletId, HttpServletResponse response) throws IOException {
        walletService.downloadTransactions(walletId, response);
    }

    @PostMapping("/wallet/add-money")
    public ResponseEntity<String> addMoneyToWallet(@RequestParam String userId, @RequestParam Double amount) {
        try {
            Session session = stripeService.createCheckoutSession(userId, amount);
            return ResponseEntity.ok(session.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initiate add-money checkout session");
        }
    }

    @PostMapping("/wallet-pay")
    public ResponseEntity<String> payFromWallet(@RequestParam String userId, @RequestParam Double amount) {
        try {
            Session session = stripeService.createPaymentSession(userId, amount);
            return ResponseEntity.ok(session.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initiate wallet payment session");
        }
    }

    @GetMapping("/wallet-transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(@RequestParam String uuid, @RequestParam String walletId) {
        //Here trim is used to remove any whitespace and new line character from requestParams
        return new ResponseEntity<>(walletService.getAllTransactions(uuid.trim(),walletId.trim()), HttpStatus.OK);
    }


}