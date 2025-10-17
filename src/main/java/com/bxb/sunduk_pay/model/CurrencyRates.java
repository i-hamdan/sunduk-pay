package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "currency_rates")
@Data
public class CurrencyRates {

    @Id
    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "INRTRY") private Double inrtry;
    @Column(name = "INRAED") private Double inraed;
    @Column(name = "INRUSD") private Double inrusd;
    @Column(name = "INRGBP") private Double inrgbp;
    @Column(name = "INRCNY") private Double inrcny;
    @Column(name = "INRSAR") private Double inrsar;
    @Column(name = "INRKWD") private Double inrkwd;
    @Column(name = "INROMR") private Double inromr;
    @Column(name = "INRJPY") private Double inrjpy;

    @Column(name = "TRYINR") private Double tryinr;
    @Column(name = "TRYAED") private Double tryaed;
    @Column(name = "TRYUSD") private Double tryusd;
    @Column(name = "TRYGBP") private Double trygbp;
    @Column(name = "TRYCNY") private Double trycny;
    @Column(name = "TRYSAR") private Double trysar;
    @Column(name = "TRYKWD") private Double trykwd;
    @Column(name = "TRYOMR") private Double tryomr;
    @Column(name = "TRYJPY") private Double tryjpy;

    @Column(name = "AEDINR") private Double aedinr;
    @Column(name = "AEDTRY") private Double aedtry;
    @Column(name = "AEDUSD") private Double aedusd;
    @Column(name = "AEDGBP") private Double aedgbp;
    @Column(name = "AEDCNY") private Double aedcny;
    @Column(name = "AEDSAR") private Double aedsar;
    @Column(name = "AEDKWD") private Double aedkwd;
    @Column(name = "AEDOMR") private Double aedomr;
    @Column(name = "AEDJPY") private Double aedjpy;

    @Column(name = "USDINR") private Double usdinr;
    @Column(name = "USDTRY") private Double usdtry;
    @Column(name = "USDAED") private Double usdaed;
    @Column(name = "USDGBP") private Double usdgbp;
    @Column(name = "USDCNY") private Double usdcny;
    @Column(name = "USDSAR") private Double usdsar;
    @Column(name = "USDKWD") private Double usdkws;
    @Column(name = "USDOMR") private Double usdomr;
    @Column(name = "USDJPY") private Double usdjpy;

    @Column(name = "GBPINR") private Double gbpinr;
    @Column(name = "GBPTRY") private Double gbptry;
    @Column(name = "GBPAED") private Double gbpaed;
    @Column(name = "GBPUSD") private Double gbpusd;
    @Column(name = "GBPCNY") private Double gbpcny;
    @Column(name = "GBPSAR") private Double gbpsar;
    @Column(name = "GBPKWD") private Double gbpkwd;
    @Column(name = "GBPOMR") private Double gbpomr;
    @Column(name = "GBPJPY") private Double gbpjpy;

    @Column(name = "CNYINR") private Double cnyinr;
    @Column(name = "CNYTRY") private Double cnytry;
    @Column(name = "CNYAED") private Double cnyaed;
    @Column(name = "CNYUSD") private Double cnyusd;
    @Column(name = "CNYGBP") private Double cnygbp;
    @Column(name = "CNYSAR") private Double cnysar;
    @Column(name = "CNYKWD") private Double cnykwd;
    @Column(name = "CNYOMR") private Double cnyomr;
    @Column(name = "CNYJPY") private Double cnyjpy;

    @Column(name = "SARINR") private Double sarinr;
    @Column(name = "SARTRY") private Double sartry;
    @Column(name = "SARAED") private Double saraed;
    @Column(name = "SARUSD") private Double sarusd;
    @Column(name = "SARGBP") private Double sargbp;
    @Column(name = "SARCNY") private Double sarcny;
    @Column(name = "SARKWD") private Double sarkwd;
    @Column(name = "SAROMR") private Double saromr;
    @Column(name = "SARJPY") private Double sarjpy;

    @Column(name = "KWDINR") private Double kwdinr;
    @Column(name = "KWDTRY") private Double kwdtry;
    @Column(name = "KWDAED") private Double kwdaed;
    @Column(name = "KWDUSD") private Double kwdusd;
    @Column(name = "KWDGBP") private Double kwdgbp;
    @Column(name = "KWDCNY") private Double kwdcny;
    @Column(name = "KWDSAR") private Double kwdsar;
    @Column(name = "KWDOMR") private Double kwdomr;
    @Column(name = "KWDJPY") private Double kwdjpy;

    @Column(name = "OMRINR") private Double omrinr;
    @Column(name = "OMRTRY") private Double omrtry;
    @Column(name = "OMRAED") private Double omraed;
    @Column(name = "OMRUSD") private Double omrusd;
    @Column(name = "OMRGBP") private Double omrgbp;
    @Column(name = "OMRCNY") private Double omrcny;
    @Column(name = "OMRSAR") private Double omrsar;
    @Column(name = "OMRKWD") private Double omrkwd;
    @Column(name = "OMRJPY") private Double omrjpy;

    @Column(name = "JPYINR") private Double jpyinr;
    @Column(name = "JPYTRY") private Double jpytry;
    @Column(name = "JPYAED") private Double jpyaed;
    @Column(name = "JPYUSD") private Double jpyusd;
    @Column(name = "JPYGBP") private Double jpygbp;
    @Column(name = "JPYCNY") private Double jpycny;
    @Column(name = "JPYSAR") private Double jpysar;
    @Column(name = "JPYKWD") private Double jpykwd;
    @Column(name = "JPYOMR") private Double jpyomr;

    @Transient
    private Map<String, Double> rates;
}
