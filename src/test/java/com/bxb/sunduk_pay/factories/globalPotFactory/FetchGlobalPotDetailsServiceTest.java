package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.FetchGlobalPotDetailsService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.DocumentStatus;
import java.time.Duration;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.List;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchGlobalPotDetailsServiceTest {

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private GlobalPotMapper globalPotMapper;

    @Mock
    private RedisTemplate<String, GlobalPotResponse> redisTemplate;

    @Mock
    private ValueOperations<String, GlobalPotResponse> valueOperations;

    @Mock
    private GenerateKeyUtil generateKeyUtil;

    @Mock
    private GlobalPotDocumentRepository globalPotDocumentRepository;

    @InjectMocks
    private FetchGlobalPotDetailsService fetchGlobalPotDetailsService;

    @Test
    void shouldReturnFetchPotDetailsRequestType() {
        GlobalPotRequestType type = fetchGlobalPotDetailsService.getGlobalPotRequestType();
        Assertions.assertEquals(GlobalPotRequestType.FETCH_POT_DETAILS, type);
    }

    @Test
    void shouldReturnDataFromRedisCache() {
        GlobalPotRequest request = new GlobalPotRequest();
        request.setGlobalPotId("GP1");

        String redisKey = "REDIS_GP1";

        GlobalPotResponse cachedResponse = new GlobalPotResponse();
        cachedResponse.setGlobalPotId("GP1");
        cachedResponse.setGlobalPotId("GP1");

        when(generateKeyUtil.getGlobalPotKey("GP1"))
                .thenReturn(redisKey);

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(redisKey))
                .thenReturn(cachedResponse);

        GlobalPotResponse response =
                fetchGlobalPotDetailsService.perform(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("GP1", response.getGlobalPotId());

        verify(valueOperations).get(redisKey);
        verify(globalPotValidations, never())
                .getGlobalPot(any());

    }

    @Test
    void shouldFetchFromDatabaseAndCacheInRedis() {

        GlobalPotRequest request = new GlobalPotRequest();
        request.setGlobalPotId("GP2");

        String redisKey = "REDIS_GP2";

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("GP2");

        GlobalPotResponse responseFromMapper = new GlobalPotResponse();
        responseFromMapper.setGlobalPotId("GP2");

        GlobalPotDocument document = new GlobalPotDocument();
        document.setDocumentHeading("KYC");
        document.setDocumentTitle("PAN");
        document.setDocumentStatus(DocumentStatus.PENDING);
        document.setDocument(new byte[]{1, 2});

        when(generateKeyUtil.getGlobalPotKey("GP2"))
                .thenReturn(redisKey);

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(redisKey))
                .thenReturn(null);

        when(globalPotValidations.getGlobalPot("GP2"))
                .thenReturn(globalPot);

        when(globalPotValidations.getContributorsCount("GP2"))
                .thenReturn(3);

        when(globalPotValidations.getFollowersCount("GP2"))
                .thenReturn(7);

        when(globalPotMapper.toGlobalPotResponse(globalPot))
                .thenReturn(responseFromMapper);

        when(globalPotDocumentRepository.findByGlobalPotGlobalPotId("GP2"))
                .thenReturn(List.of(document));

        GlobalPotResponse response =
                fetchGlobalPotDetailsService.perform(request);

        Assertions.assertEquals(3, response.getContributorCount());
        Assertions.assertEquals(7, response.getFollowerCount());
        Assertions.assertEquals(1, response.getGlobalPotDocumentResponses().size());

        verify(valueOperations).set(
                eq(redisKey),
                any(GlobalPotResponse.class),
                eq(Duration.ofMinutes(5))
        );
    }

    @Test
    void shouldThrowExceptionWhenErrorOccurs() {

        GlobalPotRequest request = new GlobalPotRequest();
        request.setGlobalPotId("GP_ERR");

        when(generateKeyUtil.getGlobalPotKey("GP_ERR"))
                .thenThrow(new RuntimeException("Redis error"));

        Assertions.assertThrows(RuntimeException.class, () ->
                fetchGlobalPotDetailsService.perform(request)
        );
    }
}