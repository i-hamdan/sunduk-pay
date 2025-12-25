package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.CurrencyMapper;
import com.bxb.sunduk_pay.exception.CustomExchangeRateException;
import com.bxb.sunduk_pay.exception.InvalidCurrencyType;
import com.bxb.sunduk_pay.exception.NullAmountException;
import com.bxb.sunduk_pay.repository.CurrencyRepository;
import com.bxb.sunduk_pay.request.CurrencyRequest;
import com.bxb.sunduk_pay.response.CurrencyResponse;
import com.bxb.sunduk_pay.util.TimeSeries;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CurrencyMapper mapper;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyServiceImpl currencyService;


    @Test
    void convertCurrency_week_success() {

        CurrencyRequest request = CurrencyRequest.builder()
                .fromCurrency("USD")
                .toCurrency("INR")
                .amount(100d)
                .timeSeries(TimeSeries.WEEK)
                .build();

        mockValidApiResponse();

        when(currencyRepository.findSpecificRate(any(), any(), anyString()))
                .thenReturn(List.of());

        when(mapper.toCurrencyRatesResponses(anyList(), anyString(), eq(TimeSeries.WEEK)))
                .thenReturn(List.of());

        when(mapper.currencyResponse(
                anyDouble(), anyDouble(), anyDouble(), anyDouble(),
                isNull(), isNull(), anyList()
        )).thenReturn(CurrencyResponse.builder().exchangeRate(80).build());

        CurrencyResponse response = currencyService.convertCurrency(request);

        assertNotNull(response);
        verify(restTemplate, times(1))
                .getForEntity(anyString(), eq(Map.class));
    }

    @Test
    void convertCurrency_month_success() {

        CurrencyRequest request = CurrencyRequest.builder()
                .fromCurrency("USD")
                .toCurrency("INR")
                .amount(100d)
                .timeSeries(TimeSeries.MONTH)
                .build();

        mockValidApiResponse();

        when(currencyRepository.findSpecificRate(any(), any(), anyString()))
                .thenReturn(List.of());

        when(mapper.toCurrencyRatesResponses(anyList(), anyString(), eq(TimeSeries.MONTH)))
                .thenReturn(List.of());

        when(mapper.currencyResponse(
                anyDouble(), anyDouble(), anyDouble(), anyDouble(),
                isNull(), anyList(), isNull()
        )).thenReturn(CurrencyResponse.builder().build());

        assertNotNull(currencyService.convertCurrency(request));
    }

    @Test
    void convertCurrency_year_success() {

        CurrencyRequest request = CurrencyRequest.builder()
                .fromCurrency("USD")
                .toCurrency("INR")
                .amount(100d)
                .timeSeries(TimeSeries.YEAR)
                .build();

        mockValidApiResponse();

        when(currencyRepository.findSpecificRate(any(), any(), anyString()))
                .thenReturn(List.of());

        when(mapper.toMonthlyAverageResponses(anyList(), anyString()))
                .thenReturn(List.of());

        when(mapper.currencyResponse(
                anyDouble(), anyDouble(), anyDouble(), anyDouble(),
                anyList(), isNull(), isNull()
        )).thenReturn(CurrencyResponse.builder().build());

        assertNotNull(currencyService.convertCurrency(request));
    }


    @Test
    void convertCurrency_amountNull_shouldThrowException() {

        CurrencyRequest request = CurrencyRequest.builder()
                .fromCurrency("USD")
                .toCurrency("INR")
                .amount(null)
                .timeSeries(TimeSeries.WEEK)
                .build();

        assertThrows(
                NullAmountException.class,
                () -> currencyService.convertCurrency(request)
        );

        verifyNoInteractions(restTemplate);
    }

    @Test
    void convertCurrency_timeSeriesNull_shouldThrowException() {

        CurrencyRequest request = CurrencyRequest.builder()
                .fromCurrency("USD")
                .toCurrency("INR")
                .amount(100d)
                .timeSeries(null)
                .build();

        mockValidApiResponse();

        assertThrows(
                ResourceNotFoundException.class,
                () -> currencyService.convertCurrency(request)
        );
    }


    @Test
    void fetchExchangeRate_apiFailure_shouldThrowInvalidCurrency() {

        CurrencyRequest request = CurrencyRequest.builder()
                .fromCurrency("XXX")
                .toCurrency("INR")
                .amount(100d)
                .timeSeries(TimeSeries.WEEK)
                .build();

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("API DOWN"));

        assertThrows(
                InvalidCurrencyType.class,
                () -> currencyService.convertCurrency(request)
        );
    }

    @Test
    void fetchExchangeRate_nullBody_shouldThrowException() {

        CurrencyRequest request = CurrencyRequest.builder()
                .fromCurrency("USD")
                .toCurrency("INR")
                .amount(100d)
                .timeSeries(TimeSeries.WEEK)
                .build();

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(
                CustomExchangeRateException.class,
                () -> currencyService.convertCurrency(request)
        );
    }

    @Test
    void fetchExchangeRate_missingConversionRates_shouldThrowException() {

        CurrencyRequest request = CurrencyRequest.builder()
                .fromCurrency("USD")
                .toCurrency("INR")
                .amount(100d)
                .timeSeries(TimeSeries.WEEK)
                .build();

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        assertThrows(
                CustomExchangeRateException.class,
                () -> currencyService.convertCurrency(request)
        );
    }

    @Test
    void fetchExchangeRate_targetCurrencyMissing_shouldThrowException() {

        CurrencyRequest request = CurrencyRequest.builder()
                .fromCurrency("USD")
                .toCurrency("EUR")
                .amount(100d)
                .timeSeries(TimeSeries.WEEK)
                .build();

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(
                        Map.of("conversion_rates", Map.of("INR", 80))
                ));

        assertThrows(
                InvalidCurrencyType.class,
                () -> currencyService.convertCurrency(request)
        );
    }


    private void mockValidApiResponse() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(
                        Map.of("conversion_rates", Map.of("INR", 80.0))
                ));
    }
}
