//package com.bxb.sunduk_pay.controller;
//
//import com.bxb.sunduk_pay.service.PaymentService;
//import com.bxb.sunduk_pay.util.TransactionType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.concurrent.CompletableFuture;
//
//@RestController
//public class PaymentController {
//
//    private final PaymentService paymentService;
//
//    public PaymentController(PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }
//
//    @PostMapping("/checkout")
//    public CompletableFuture<ResponseEntity<String>> addMoneyToWallet(@RequestParam String uuid, @RequestParam Double amount, @RequestParam TransactionType transactionType) {
//        return paymentService.createCheckoutSession(uuid, amount, transactionType);
//    }
//}
