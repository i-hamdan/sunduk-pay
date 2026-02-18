package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotDocumentResponse;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to fetch details of a global pot.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class FetchGlobalPotDetailsService implements GlobalPotOperation {

    /**
     * Validations for global pot operations.
     */
    private final GlobalPotValidations globalPotValidations;

    /**
     * Mapper for global pot entities and requests/responses.
     */
    private final GlobalPotMapper globalPotMapper;

    /**
     * General validations utility.
     */
    private final Validations validations;

    /**
     * Redis template for caching global pot responses.
     */
    private final RedisTemplate<String, GlobalPotResponse> redisTemplate;

    /**
     * Utility for generating redis keys.
     */
    private final GenerateKeyUtil generateKeyUtil;

    /**
     * Repository for accessing global pot documents.
     */
    private final GlobalPotDocumentRepository globalPotDocumentRepository;

    /**
     * Specifies the type of request this service handles.
     *
     * @return GlobalPotRequestType.FETCH_POT_DETAILS
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FETCH_POT_DETAILS;
    }

    /**
     * Performs the operation to fetch global pot details.
     *
     * @param request The request containing details for fetching the pot.
     * @return A response containing the fetched pot details.
     */
    @Override
    public GlobalPotResponse perform(final GlobalPotRequest request) {

        log.info("Performing fetch global pot details operation : {} ",
                System.currentTimeMillis());

        try {

            log.info("Validating user with UUID: {}",
                    request.getUuid());

            User user = validations.getUserInfo(request.getUuid());

            log.info("User validated successfully: {}",
                    user.getFullName());


            log.info("Fetching details for Global Pot ID: {}",
                    request.getGlobalPotId());

//            String redisKey = generateKeyUtil
//                    .getGlobalPotKey(request.getGlobalPotId());
//
//            log.info("Generated Redis Key: {}", redisKey);
//
//            GlobalPotResponse redisGlobalPotResponse = redisTemplate
//                    .opsForValue().get(redisKey);
//
//            if (redisGlobalPotResponse != null) {
//                log.info(
//         "Global Pot details found in Redis cache for key: {}",
//                        redisKey);
//                return redisGlobalPotResponse;
//            } else {

                log.info(
"Global Pot details not found in Redis. Fetching from database for ID: {}",
                        request.getGlobalPotId());
                log.info("Validating Global Pot ID: {}",
                        System.currentTimeMillis());


                GlobalPot globalPot = globalPotValidations
                        .getGlobalPot(request.getGlobalPotId());

                log.info("Global Pot validated successfully. ID: {}"
                        ,System.currentTimeMillis());

                globalPotValidations.validatePotAccess(user, globalPot);


                int contributorsCount = globalPotValidations.
                        getContributorsCount(
                        globalPot.getGlobalPotId());

                log.info("Contributors count fetched: {}"
                        ,System.currentTimeMillis());


                int followersCount = globalPotValidations.getFollowersCount(
                        globalPot.getGlobalPotId());


                log.info("Followers count fetched: {}"
                ,System.currentTimeMillis());


                GlobalPotResponse globalPotResponse = globalPotMapper
                        .toGlobalPotResponse(globalPot);

                log.info("Mapped Global Pot entity to response: {}"
                ,System.currentTimeMillis());


                globalPotResponse.setContributorCount(contributorsCount);
                globalPotResponse.setFollowerCount(followersCount);

                List<GlobalPotDocument> globalPotDocuments =
                        globalPotDocumentRepository.findByGlobalPotGlobalPotId(
                                request.getGlobalPotId());
                log.info("Fetched documents for Global Pot ID: {}"
                        ,System.currentTimeMillis());

         List<GlobalPotDocumentResponse>
                 globalPotDocumentList = new ArrayList<>();


                globalPotDocuments.forEach(document -> {
                    GlobalPotDocumentResponse documentResponse =
                            new GlobalPotDocumentResponse();

                    documentResponse.setDocumentHeading(
                            document.getDocumentHeading());
                    documentResponse.setDocumentTitle(
                            document.getDocumentTitle());
                     documentResponse.setDocument(globalPotMapper.toBase64(
                             document.getDocument()));
                    documentResponse.setDocumentStatus(
                            document.getDocumentStatus().name());
                    globalPotDocumentList.add(documentResponse);

                });
                globalPotResponse.setGlobalPotDocumentResponses(
                        globalPotDocumentList);
                log.info("Mapped Global Pot documents to response DTOs: {}"
                ,System.currentTimeMillis());

//
//                redisTemplate.opsForValue()
//                    .set(redisKey, globalPotResponse, Duration.ofMinutes(5));
//                log.info(
//"Cached Global Pot details in Redis with key: {}", redisKey);

                return globalPotResponse;
//            }
        } catch (Exception e) {
            log.error("Error fetching global pot details: {}",
                    e.getMessage());
            throw e;
        }
    }
}
