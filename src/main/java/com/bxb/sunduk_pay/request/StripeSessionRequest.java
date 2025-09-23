package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import lombok.Builder;
import lombok.Data;

/**
 * This is a request class for creating a Stripe session.
 * It can be expanded with necessary fields and methods
 * to handle the request data as needed.
 */
@Data
@Builder
public class StripeSessionRequest {
    /** User ID associated with the Stripe session.*/
    private String userId;
    /** Amount for the transaction. */
    private Double amount;
    /** Name of the product or service. */
    private String productName;
    /** Type of transaction: CREDIT or DEBIT. */
    private TransactionType transactionType;
    /** URL to redirect upon successful payment. */
    private String successUrl;
    /** URL to redirect if the payment is cancelled.*/
    private String cancelUrl;
    /** Source wallet for the transaction. */
    private WalletWrapper sourceWallet;
    /** Target wallet for the transaction. */
    private WalletWrapper targetWallet;
}
