package com.bxb.sunduk_pay.util;

/**
 * Enumeration of supported payment methods in SundukPay.
 *
 * <p>This enum represents the different ways a user can make or receive a payment:
 * <ul>
 *   <li>{@link #UPI} – Unified Payments Interface (UPI)</li>
 *   <li>{@link #PHONE_NUMBER} – Using a registered phone number</li>
 *   <li>{@link #BANK} – Direct bank transfer</li>
 *   <li>{@link #QR} – Scanning a QR code</li>
 *   <li>{@link #CARD} – Using debit or credit card</li>
 * </ul>
 * </p>
 */
public enum PaymentMethod {
    /** Payment via Unified Payments Interface (UPI). */
    UPI,

    /** Payment using a registered phone number. */
    PHONE_NUMBER,

    /** Payment through a direct bank transfer. */
    BANK,

    /** Payment by scanning a QR code. */
    QR,

    /** Payment using a debit or credit card. */
    CARD
}
