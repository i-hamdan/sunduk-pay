package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.request.TransactionRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction-create")
    public ResponseEntity<String> makeTransaction(@RequestBody TransactionRequest request){
       return new ResponseEntity<>(transactionService.createTransaction(request),HttpStatus.CREATED);
    }

    @GetMapping("/transaction-getInfoById/{id}")
    public ResponseEntity<TransactionResponse> getInfoById(@PathVariable String id){
        return new ResponseEntity<>(transactionService.getInfoById(id),HttpStatus.OK);
    }
    }

