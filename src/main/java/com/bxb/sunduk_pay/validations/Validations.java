package com.bxb.sunduk_pay.validations;


import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines validation operations for wallets, users, and transactions.
 */
public interface Validations {
    /**
     * Validates that the balance is sufficient for a transaction.
     *
     * @param balance current balance
     * @param amount  amount to be transacted
     */
    void validateBalance(Double balance, Double amount);
    /**
     * Retrieves user information by email.
     *
     * @param email user email
     * @return {@link User}
     */
    User getUserInfo(String email);

    /**
     * Retrieves a main wallet by its ID.
     *
     * @param walletId main wallet ID
     * @return {@link MainWallet}
     */

    MainWallet getMainWalletByWalletId(String walletId);
    /**
     * Retrieves the main wallet associated with a user.
     *
     * @param uuid user UUID
     * @return {@link MainWallet}
     */
    MainWallet getMainWalletInfo(String uuid);

    /**
     * Validates the allowed number of sub-wallets.
     *
     * @param size current number of sub-wallets
     */
    void validateNumberOfSubWallets(int size);

    /**
     * Validates and retrieves transactions based on various filters.
     *
     * @param uuid            user UUID
     * @param walletId        main wallet ID
     * @param paymentMethod   payment method filter (optional)
     * @param transactionType transaction type filter (optional)
     * @param pageable        pagination information
     * @return paginated list of {@link Transaction}
     */
    Page<Transaction> getTransactions(
            String uuid,
            String walletId,
            PaymentMethod paymentMethod,
            TransactionType transactionType,
            Pageable pageable);

    /**
     * Finds a sub-wallet by its ID within a main wallet.
     *
     * @param mainWalletId
     * @param subWalletId  sub-wallet ID
     * @return {@link SubWallet} if found, otherwise null
     */
    SubWallet findSubWalletIfExists(String mainWalletId,String subWalletId);

    /**
     * Retrieves the icon for the source wallet in a transaction.
     *
     * @param mainWalletId main wallet ID
     * @param fromWalletId source wallet ID
     * @return icon string
     */
    String getFromIconOfTxn(String mainWalletId, String fromWalletId);

    /**
     * Retrieves the icon for the destination wallet in a transaction.
     *
     * @param mainWalletId main wallet ID
     * @param toWalletId   destination wallet ID
     * @return icon string
     */
    String getToIconOfTxn(String mainWalletId, String toWalletId);
    /**
     * Removes a sub-wallet from a main wallet.
     *
     * @param wallet main wallet
     * @param subWalletId sub-wallet ID
     * @return true if removed successfully
     */
    Boolean removeSubwallet(MainWallet wallet, String subWalletId);
    /**
     * Retrieves the master wallet associated with a user.
     *
     * @param uuid user UUID
     * @return {@link MasterWallet}
     */

    MasterWallet getMasterWalletInfo(String uuid);

    /**
     * Finds a sub-wallet by its name within a main wallet.
     *
     * @param subWalletName sub-wallet name
     *                      if found,
     *                      otherwise empty
     * @param mainWalletId
     */
    void findSubWalletByName(
            String subWalletName, String mainWalletId);

    /**
     * Validates that the target balance is
     * sufficient for a transaction.
     * @param balance current target balance
     * @param amount  amount to be transacted
     */
    void validateTargetBalance(Double balance, Double amount);

/**
     * Retrieves user email information by UUID.
     *
     * @param uuid user UUID
     * @return {@link User}
     */
    User getUserEmailInfo(String uuid);
}
