package com.bxb.sunduk_pay.util;

/**
 * Represents the current verification status of a document.
 * <p>
 * This enum is used in document verification workflows to track
 * the lifecycle of a document from submission to final decision.
 * </p>
 */
public enum DocumentStatus {

    /**
     * The document has been successfully verified and approved.
     */
    VERIFIED,

    /**
     * The document is pending action or processing.
     */
    PENDING,

    /**
     * The document has been submitted and is awaiting review.
     */
    SUBMITTED,

    /**
     * The document is currently under review by the verification authority.
     */
    UNDER_REVIEW,

    /**
     * The document has been reviewed and rejected.
     */
    REJECTED
}
