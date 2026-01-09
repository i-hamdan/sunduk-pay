package com.bxb.sunduk_pay.util;

/**
 * Represents the type of request that can be performed on a Global Pot.
 * <p>
 * This enum is used to route incoming requests to the appropriate
 * service or handler based on the requested operation.
 * </p>
 */
public enum GlobalPotRequestType {

    /* ---------- Pot Operations ---------- */

    /**
     * Creates a new global pot.
     */
    CREATE_POT,

    /**
     * Updates the details of an existing global pot.
     */
    UPDATE_POT,

    /**
     * Deletes an existing global pot.
     */
    DELETE_POT,

    /**
     * Fetches a list of global pots.
     */
    FETCH_POTS,

    /**
     * Fetches detailed information for a specific global pot.
     */
    FETCH_POT_DETAILS,

    /**
     * Verifies a global pot for authenticity or compliance.
     */
    VERIFY_POT,

    /* ---------- Wallet Operations ---------- */

    /**
     * Creates a wallet associated with a global pot.
     */
    CREATE_WALLET,

    /**
     * Updates an existing global pot wallet.
     */
    UPDATE_WALLET,

    /**
     * Deletes a wallet associated with a global pot.
     */
    DELETE_WALLET,

    /**
     * Fetches wallets associated with global pots.
     */
    FETCH_WALLETS,

    FETCH_ADMINS,

    /* ---------- Contributor & Engagement Operations ---------- */

    /**
     * Adds a contributor to a global pot.
     * <p>
     * Handled by {@code AddContributorService}.
     * </p>
     */
    ADD_CONTRIBUTOR,

    /**
     * Allows a user to follow a global pot.
     * <p>
     * Handled by {@code FollowPotService}.
     * </p>
     */
    FOLLOW_POT,

    /**
     * Adds a testimonial to a global pot.
     */
    ADD_TESTIMONIAL,

    /**
     * Transfers ownership of a global pot to another user.
     */
    TRANSFER_OWNERSHIP,


    /**
     * Fetches the chat history associated with a global pot.
     */
    FETCH_GROUP_CHAT_HISTORY,
    /**
     * Share ownership of a global pot to other users.
     */
    SHARE_OWNERSHIP,
    
    /**
     * Request type used for document verification.
     */
    VERIFY_DOCUMENT,
    
    /**
     * Request type for updating Global Pot details
     */
    Update_Global_Pot,


    /**
     * Fetches a specific global pot by identifier.
     */
    FETCH_GLOBAL_POT,

    /**
     * Admin can debit the amount from GlobalPot sub-wallet
     */
    DEBIT_GLOBAL_POT_WALLET,

    /**
     * for block Service
     */
    BLOCK_USER,

    /**
     * Admin can add member in global pot
     */
    ADD_MEMBER,

    /**
     *  Admin can remove member in global pot
     */
    REMOVE_MEMBER


    }
