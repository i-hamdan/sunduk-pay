package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.factories.adminFactory.SundukPayAdminOperation;
import com.bxb.sunduk_pay.factories.adminFactory.SundukPayAdminOperationFactory;
import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
import com.bxb.sunduk_pay.service.SundukPayAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link SundukPayAdminService}.
 * This service acts as a dispatcher that routes admin requests
 * to the appropriate admin operation based on the request type.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class SundukPayAdminServiceImpl implements SundukPayAdminService {



    /**
     * Factory responsible for resolving the correct
     * admin operation based on admin request type.
     */
    private final SundukPayAdminOperationFactory sundukPayAdminOperationFactory;

    /**
     * Processes the admin API request by resolving the corresponding
     * admin operation and executing it.
     *
     * @param request the admin request containing the operation type and data
     * @return response containing the result of the admin operation
     */
    @Override
    public SundukPayAdminResponse adminApi(
            final SundukPayAdminRequest request) {

        log.info("Processing admin request of type: {}",
                request.getAdminRequestType());

        long startTime = System.currentTimeMillis();
        log.info("Admin request processing started at: 0 ms");
        
        try {
            SundukPayAdminOperation adminOperation =
                    sundukPayAdminOperationFactory
                            .getSundukPayAdminOperation(
                                    request.getAdminRequestType());
            long endTime = System.currentTimeMillis();
            log.info("Resolved admin operation for request type: {} ms"
                    ,endTime - startTime);

            if (adminOperation == null) {
                log.error(
                        "No admin operation found for request type: {}",
                        request.getAdminRequestType());
                throw new IllegalArgumentException(
                        "Unsupported admin request type");
            }

            SundukPayAdminResponse response =
                    adminOperation.perform(request);

            log.info(
                    "Admin request processed successfully for type: {}",
                    request.getAdminRequestType());
            
             endTime = System.currentTimeMillis();
            log.info("Admin request processed successfully for type: {} ms"
                    ,endTime - startTime);

            return response;

        } catch (Exception ex) {
log.error(
        "Error occurred while processing admin request of type: {}",
                    request.getAdminRequestType(), ex);
            throw ex;
        }
    }
}
