package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.InsufficientBalanceException;

public class ValidationsImpl implements Validations{


    @Override
    public void validateBalance(Double balance, Double amount) {
        if (balance<amount){
            throw new InsufficientBalanceException("Insufficient Balance in source sub wallet");
        }
    }
}
