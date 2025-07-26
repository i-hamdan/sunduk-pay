package com.bxb.sunduk_pay.service;

import com.stripe.model.checkout.Session;

public interface StripeService {
    Session createCheckoutSession(String userId, Double amount) throws Exception;
    Session createPaymentSession(String userId, Double amount) throws Exception;
}