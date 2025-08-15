package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;

public interface PaymentService {

    MainWalletResponse createCheckoutSession(String userId, Double amount, TransactionType transactionType, WalletWrapper targetWallet, WalletWrapper sourceWallet);
}
