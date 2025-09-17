package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.request.PaginationRequest;
import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.response.WalletsResponse;
import com.bxb.sunduk_pay.service.WalletService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }


    @PostMapping("/wallet-create")
    public ResponseEntity<String> createWallet(@RequestBody WalletRequest walletRequest) {
        return new ResponseEntity<>(walletService.createWallet(walletRequest), HttpStatus.CREATED);
    }

    //this api will return recent transactions of a wallet, Pagination is implemented in this api.
    @PostMapping("/wallet-getRecentTransactions/{walletId}")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(@PathVariable String walletId, @RequestBody PaginationRequest request){
        return new ResponseEntity<>(walletService.getRecentTransactionsByWalletId(walletId,request),HttpStatus.OK);
    }

    //this returns a wallet's transaction history
    @GetMapping("/wallet-getInfoById/{id}")
    public ResponseEntity<WalletResponse> getInfo(@PathVariable String id) {
        return new ResponseEntity<>(walletService.getInfoByWalletId(id),HttpStatus.OK);
    }


    //this will return all the wallets of a user by userId
    @GetMapping("/wallet-getUserWallets/{uuid}")
    public ResponseEntity<List<WalletsResponse>> wallets(@PathVariable String uuid) {
        return new ResponseEntity<>(walletService.getAllWalletsByUuid(uuid),HttpStatus.OK);
    }


    //this api will return the current balance of a wallet by walletId
    @GetMapping("/wallet-showBalance/{walletId}")
    public ResponseEntity<String> showBalance(@PathVariable String walletId) {
        return new ResponseEntity<>(walletService.showBalance(walletId),HttpStatus.OK);
    }


    //this api will download all the transactions of a wallet.
    @PostMapping("/wallet-downloadPdf/{walletId}")
    public void downloadTransactions(@PathVariable String walletId, HttpServletResponse response) throws IOException {
        walletService.downloadTransactions(walletId, response);
    }


    @DeleteMapping("/wallet-delete/{walletId}")
    public ResponseEntity<String> delete(@PathVariable String walletId){
   return new ResponseEntity<>(walletService.deleteWallet(walletId),HttpStatus.OK);
    }
}