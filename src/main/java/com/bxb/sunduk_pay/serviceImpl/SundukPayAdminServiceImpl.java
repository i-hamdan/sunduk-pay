package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.factories.adminFactory.SundukPayAdminOperation;
import com.bxb.sunduk_pay.factories.adminFactory.SundukPayAdminOperationFactory;
import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
import com.bxb.sunduk_pay.service.SundukPayAdminService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link SundukPayAdminService}.
 * This service acts as a dispatcher that routes admin requests
 * to the appropriate admin operation based on the request type.
 */
@Service
@RequiredArgsConstructor
public class SundukPayAdminServiceImpl implements SundukPayAdminService {

    /**
     * Logger instance for service-level logging.
     */
    private static final Logger logger =
            LogManager.getLogger(SundukPayAdminServiceImpl.class);

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
    public SundukPayAdminResponse adminApi(final SundukPayAdminRequest request) {

        logger.info("Processing admin request of type: {}",
                request.getAdminRequestType());

        try {
            SundukPayAdminOperation adminOperation =
                    sundukPayAdminOperationFactory
                            .getSundukPayAdminOperation(
                                    request.getAdminRequestType());

            if (adminOperation == null) {
                logger.error("No admin operation found for request type: {}",
                        request.getAdminRequestType());
                throw new IllegalArgumentException(
                        "Unsupported admin request type");
            }

            SundukPayAdminResponse response =
                    adminOperation.perform(request);

            logger.info("Admin request processed successfully for type: {}",
                    request.getAdminRequestType());

            return response;

        } catch (Exception ex) {
            logger.error("Error occurred while processing admin request of type: {}",
                    request.getAdminRequestType(), ex);
            throw ex;
        }
    }
}
