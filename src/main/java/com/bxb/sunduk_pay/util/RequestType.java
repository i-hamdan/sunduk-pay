package com.bxb.sunduk_pay.util;

/**
 * Enum representing different types of requests that can be made in the system.
 */
public enum RequestType {
    /** Request to create a new resource. */
    CREATE,
    /** Request to update an existing resource. */
    UPDATE,
    /** Request to delete a resource. */
    DELETE,
    /** Request to fetch transaction details. */
    FETCH_TRANSACTIONS,
    /** Request to fetch wallet information. */
    FETCH_WALLET,
    /** Request to transfer money between accounts or wallets. */
    TRANSFER_MONEY,
    /** Request to search for contacts. */
    SEARCH_CONTACTS,
    /** Request to add a reminder. */
    ADD_REMINDER,
    /** Request to get reminders. */
    FETCH_REMINDERS,
    /** Request to update FCM token. */
    UPDATE_FCM_TOKEN,
    /** Request to delete a reminder. */
    DELETE_REMINDER,
    /** Request to confirm an autopay setup. */
    AUTOPAY_CONFIRMATION,
    ;

}
