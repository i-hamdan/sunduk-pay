package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.factoryPattern.WalletOperationFactory;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.WalletService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@Log4j2
@RestController
public class WalletController {
    private final WalletOperationFactory walletFactory;
    private final WalletService walletService;

    public WalletController(WalletOperationFactory walletFactory, WalletService walletService) {
        this.walletFactory = walletFactory;
        this.walletService = walletService;
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


    @PostMapping("/wallet")
    public ResponseEntity<MainWalletResponse> walletApi(@RequestBody MainWalletRequest mainWalletRequest) {
        return new ResponseEntity<>(walletService.walletCrud(mainWalletRequest), HttpStatus.OK);
    }

}