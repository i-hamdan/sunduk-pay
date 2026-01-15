package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
import com.bxb.sunduk_pay.service.SundukPayAdminService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for handling
 * admin-related API requests in Sunduk Pay.
 * Acts as the entry point for all admin operations.
 */
@RestController
@RequiredArgsConstructor
public class SundukPayAdminController {

    /**
     * Logger instance for tracking admin API requests and responses.
     */
    private static final Logger LOGGER =
            LogManager.getLogger(SundukPayAdminController.class);

    /**
     * Service responsible for processing admin requests.
     */
    private final SundukPayAdminService service;

    /**
     * Handles admin API requests and delegates processing
     * to the admin service layer.
     *
     * @param request the admin request payload
     * @return response containing the result of the admin operation
     */
    @PostMapping("/sunduk-admin")
    public ResponseEntity<SundukPayAdminResponse> adminApi(
            @RequestBody final SundukPayAdminRequest request) {

        LOGGER.info("Received admin API request");

        try {
            SundukPayAdminResponse response = service.adminApi(request);
            LOGGER.info("Admin API request processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
LOGGER.error("Error occurred while processing admin API request", ex);
            throw ex;
        }
    }
}
