package com.bxb.sunduk_pay.util;

/**
 * Enumeration of wallet types in SundukPay.
 *
 * <p>This enum distinguishes between the main wallet and other
 * (sub) wallets associated with a user account.</p>
 */
public enum SubWalletType {

    /** Represents the user's main wallet (primary fund holder). */
    MAIN,

    /** Represents any other (sub) wallet linked to the main wallet. */
    OTHER
}
