package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import com.stripe.model.checkout.Session;

public interface StripeService {
    Session createCheckoutSession(String userId, Double amount, TransactionType transactionType, WalletWrapper targetWallet, WalletWrapper sourceWallet) throws Exception;

}