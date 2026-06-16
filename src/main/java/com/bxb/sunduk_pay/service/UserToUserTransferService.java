package com.bxb.sunduk_pay.service;
import com.bxb.sunduk_pay.response.MainWalletResponse;

/**
 * Service interface for handling user-to-user transfers.
 */
public interface UserToUserTransferService {
    /**
     * Transfers funds between two users.
     *
     * @param senderId       the ID of the sender
     * @param receiverId     the ID of the receiver
     * @param amount         the amount to be transferred
     * @param senderWalletId the wallet ID of the sender
     * @param paymentTag    the payment tag for the transaction
     * @param reminderId    the reminder ID associated with the transfer
     * @return the updated main wallet response after the transfer
     */
    MainWalletResponse transferBetweenUsers(
             String senderId,
             String receiverId,
             Double amount,
             String paymentTag,
             String senderWalletId,
             String reminderId,
             Boolean isAutoPayment
            );
}
