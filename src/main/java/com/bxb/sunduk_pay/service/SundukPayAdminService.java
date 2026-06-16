package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;

/**
 * Service interface for handling admin-related requests
 * in the Sunduk Pay system.
 * Acts as an entry point for processing different
 * admin operations based on the request data.
 */
public interface SundukPayAdminService {

    /**
     * Processes an admin request and returns the corresponding response.
     *
     * @param request the admin request containing operation details
     * @return response representing the outcome of the admin request
     */
    SundukPayAdminResponse adminApi(SundukPayAdminRequest request);
}
