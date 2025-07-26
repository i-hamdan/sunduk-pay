package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.response.CurrencyResponse;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapperImpl implements CurrencyMapper {
    public CurrencyResponse currencyResponse(double exchangeRate, double converted, double fee, double finalAmount){
        CurrencyResponse response = new CurrencyResponse();
        response.setExchangeRate(exchangeRate);
        response.setConvertedAmount(converted);
        response.setConversionFee(fee);
        response.setFinalAmount(finalAmount);
        return response;
    }
}
