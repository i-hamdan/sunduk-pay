package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.util.TransactionType;
import com.stripe.model.checkout.Session;

public interface StripeService {
    Session createCheckoutSession(String userId, Double amount, TransactionType transactionType) throws Exception;

}