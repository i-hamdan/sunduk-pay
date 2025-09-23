package com.bxb.sunduk_pay.util;
/**
 * Enum representing different payment methods.
 */
public enum PaymentMethod {
    /** Payment via UPI (Unified Payments Interface). */
    UPI,

    /** Payment using a phone number. */
    PHONE_NUMBER,

    /** Payment through a bank account. */
    BANK,

    /** Payment using a QR code. */
    QR,

    /** Payment with a card (debit/credit). */
    CARD

}
