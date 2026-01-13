package com.bxb.sunduk_pay.factories.adminFactory;

import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
import com.bxb.sunduk_pay.util.AdminRequestType;

/**
 * Contract for handling admin-specific operations in Sunduk Pay.
 * Each implementation represents a single admin request type
 * and contains the logic to perform that operation.
 */
public interface SundukPayAdminOperation {

    /**
     * Returns the admin request type supported by this operation.
     *
     * @return the supported admin request type
     */
    AdminRequestType getAdminRequestType();

    /**
     * Performs the admin operation using the provided request data.
     *
     * @param request the admin request containing required inputs
     * @return response containing the result of the admin operation
     */
    SundukPayAdminResponse perform(SundukPayAdminRequest request);
}
