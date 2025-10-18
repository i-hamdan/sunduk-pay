package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

/**
 * Entity representing currency exchange
 * rates for various currency pairs on a specific date.
 */
@Entity
@Table(name = "currency_rates")
@Data
public class CurrencyRates {

    /**
     * The date for which the currency rates are recorded.
     */
    @Id
    @Column(name = "Date", nullable = false)
    private LocalDate date;

    /** INR to TRY rate. */
    @Column(name = "INRTRY") private Double inrtry;
    /** INR to AED rate. */
    @Column(name = "INRAED") private Double inraed;
    /** INR to USD rate. */
    @Column(name = "INRUSD") private Double inrusd;
    /** INR to GBP rate. */
    @Column(name = "INRGBP") private Double inrgbp;
    /** INR to CNY rate. */
    @Column(name = "INRCNY") private Double inrcny;
    /** INR to SAR rate. */
    @Column(name = "INRSAR") private Double inrsar;
    /** INR to KWD rate. */
    @Column(name = "INRKWD") private Double inrkwd;
    /** INR to OMR rate. */
    @Column(name = "INROMR") private Double inromr;
    /** INR to JPY rate. */
    @Column(name = "INRJPY") private Double inrjpy;

    /** TRY to INR rate. */
    @Column(name = "TRYINR") private Double tryinr;
    /** TRY to AED rate. */
    @Column(name = "TRYAED") private Double tryaed;
    /** TRY to USD rate. */
    @Column(name = "TRYUSD") private Double tryusd;
    /** TRY to GBP rate. */
    @Column(name = "TRYGBP") private Double trygbp;
    /** TRY to CNY rate. */
    @Column(name = "TRYCNY") private Double trycny;
    /** TRY to SAR rate. */
    @Column(name = "TRYSAR") private Double trysar;
    /** TRY to KWD rate. */
    @Column(name = "TRYKWD") private Double trykwd;
    /** TRY to OMR rate. */
    @Column(name = "TRYOMR") private Double tryomr;
    /** TRY to JPY rate. */
    @Column(name = "TRYJPY") private Double tryjpy;

    /** AED to INR rate. */
    @Column(name = "AEDINR") private Double aedinr;
    /** AED to TRY rate. */
    @Column(name = "AEDTRY") private Double aedtry;
    /** AED to USD rate. */
    @Column(name = "AEDUSD") private Double aedusd;
    /** AED to GBP rate. */
    @Column(name = "AEDGBP") private Double aedgbp;
    /** AED to CNY rate. */
    @Column(name = "AEDCNY") private Double aedcny;
    /** AED to SAR rate. */
    @Column(name = "AEDSAR") private Double aedsar;
    /** AED to KWD rate. */
    @Column(name = "AEDKWD") private Double aedkwd;
    /** AED to OMR rate. */
    @Column(name = "AEDOMR") private Double aedomr;
    /** AED to JPY rate. */
    @Column(name = "AEDJPY") private Double aedjpy;

    /** USD to INR rate. */
    @Column(name = "USDINR") private Double usdinr;
    /** USD to TRY rate. */
    @Column(name = "USDTRY") private Double usdtry;
    /** USD to AED rate. */
    @Column(name = "USDAED") private Double usdaed;
    /** USD to GBP rate. */
    @Column(name = "USDGBP") private Double usdgbp;
    /** USD to CNY rate. */
    @Column(name = "USDCNY") private Double usdcny;
    /** USD to SAR rate. */
    @Column(name = "USDSAR") private Double usdsar;
    /** USD to KWD rate. */
    @Column(name = "USDKWD") private Double usdkws;
    /** USD to OMR rate. */
    @Column(name = "USDOMR") private Double usdomr;
    /** USD to JPY rate. */
    @Column(name = "USDJPY") private Double usdjpy;

    /** GBP to INR rate. */
    @Column(name = "GBPINR") private Double gbpinr;
    /** GBP to TRY rate. */
    @Column(name = "GBPTRY") private Double gbptry;
    /** GBP to AED rate. */
    @Column(name = "GBPAED") private Double gbpaed;
    /** GBP to USD rate. */
    @Column(name = "GBPUSD") private Double gbpusd;
    /** GBP to CNY rate. */
    @Column(name = "GBPCNY") private Double gbpcny;
    /** GBP to SAR rate. */
    @Column(name = "GBPSAR") private Double gbpsar;
    /** GBP to KWD rate. */
    @Column(name = "GBPKWD") private Double gbpkwd;
    /** GBP to OMR rate. */
    @Column(name = "GBPOMR") private Double gbpomr;
    /** GBP to JPY rate. */
    @Column(name = "GBPJPY") private Double gbpjpy;

    /** CNY to INR rate. */
    @Column(name = "CNYINR") private Double cnyinr;
    /** CNY to TRY rate. */
    @Column(name = "CNYTRY") private Double cnytry;
    /** CNY to AED rate. */
    @Column(name = "CNYAED") private Double cnyaed;
    /** CNY to USD rate. */
    @Column(name = "CNYUSD") private Double cnyusd;
    /** CNY to GBP rate. */
    @Column(name = "CNYGBP") private Double cnygbp;
    /** CNY to SAR rate. */
    @Column(name = "CNYSAR") private Double cnysar;
    /** CNY to KWD rate. */
    @Column(name = "CNYKWD") private Double cnykwd;
    /** CNY to OMR rate. */
    @Column(name = "CNYOMR") private Double cnyomr;
    /** CNY to JPY rate. */
    @Column(name = "CNYJPY") private Double cnyjpy;

    /** SAR to INR rate. */
    @Column(name = "SARINR") private Double sarinr;
    /** SAR to TRY rate. */
    @Column(name = "SARTRY") private Double sartry;
    /** SAR to AED rate. */
    @Column(name = "SARAED") private Double saraed;
    /** SAR to USD rate. */
    @Column(name = "SARUSD") private Double sarusd;
    /** SAR to GBP rate. */
    @Column(name = "SARGBP") private Double sargbp;
    /** SAR to CNY rate. */
    @Column(name = "SARCNY") private Double sarcny;
    /** SAR to KWD rate. */
    @Column(name = "SARKWD") private Double sarkwd;
    /** SAR to OMR rate. */
    @Column(name = "SAROMR") private Double saromr;
    /** SAR to JPY rate. */
    @Column(name = "SARJPY") private Double sarjpy;

    /** KWD to INR rate. */
    @Column(name = "KWDINR") private Double kwdinr;
    /** KWD to TRY rate. */
    @Column(name = "KWDTRY") private Double kwdtry;
    /** KWD to AED rate. */
    @Column(name = "KWDAED") private Double kwdaed;
    /** KWD to USD rate. */
    @Column(name = "KWDUSD") private Double kwdusd;
    /** KWD to GBP rate. */
    @Column(name = "KWDGBP") private Double kwdgbp;
    /** KWD to CNY rate. */
    @Column(name = "KWDCNY") private Double kwdcny;
    /** KWD to SAR rate. */
    @Column(name = "KWDSAR") private Double kwdsar;
    /** KWD to OMR rate. */
    @Column(name = "KWDOMR") private Double kwdomr;
    /** KWD to JPY rate. */
    @Column(name = "KWDJPY") private Double kwdjpy;

    /** OMR to INR rate. */
    @Column(name = "OMRINR") private Double omrinr;
    /** OMR to TRY rate. */
    @Column(name = "OMRTRY") private Double omrtry;
    /** OMR to AED rate. */
    @Column(name = "OMRAED") private Double omraed;
    /** OMR to USD rate. */
    @Column(name = "OMRUSD") private Double omrusd;
    /** OMR to GBP rate. */
    @Column(name = "OMRGBP") private Double omrgbp;
    /** OMR to CNY rate. */
    @Column(name = "OMRCNY") private Double omrcny;
    /** OMR to SAR rate. */
    @Column(name = "OMRSAR") private Double omrsar;
    /** OMR to KWD rate. */
    @Column(name = "OMRKWD") private Double omrkwd;
    /** OMR to JPY rate. */
    @Column(name = "OMRJPY") private Double omrjpy;

    /** JPY to INR rate. */
    @Column(name = "JPYINR") private Double jpyinr;
    /** JPY to TRY rate. */
    @Column(name = "JPYTRY") private Double jpytry;
    /** JPY to AED rate. */
    @Column(name = "JPYAED") private Double jpyaed;
    /** JPY to USD rate. */
    @Column(name = "JPYUSD") private Double jpyusd;
    /** JPY to GBP rate. */
    @Column(name = "JPYGBP") private Double jpygbp;
    /** JPY to CNY rate. */
    @Column(name = "JPYCNY") private Double jpycny;
    /** JPY to SAR rate. */
    @Column(name = "JPYSAR") private Double jpysar;
    /** JPY to KWD rate. */
    @Column(name = "JPYKWD") private Double jpykwd;
    /** JPY to OMR rate. */
    @Column(name = "JPYOMR") private Double jpyomr;

    /**
     * Map for dynamic access to currency rates (not persisted in DB).
     */
    @Transient
    private Map<String, Double> rates;
}
