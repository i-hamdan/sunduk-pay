package com.bxb.sunduk_pay.util;

/**
 * Enum representing all supported currency pairs.
 */
public enum CurrencyPair {
    /** Saudi Riyal to UAE Dirham. */
//    SARAED,
//    /** Omani Rial to US Dollar. */
//    OMRUSD,
//    /** Chinese Yuan to Kuwaiti Dinar. */
//    CNYKWD,
//    /** Kuwaiti Dinar to Saudi Riyal. */
//    KWDSAR,
//    /** Saudi Riyal to Turkish Lira. */
//    SARTRY,
//    /** British Pound to Kuwaiti Dinar. */
//    GBPKWD,
//    /** Chinese Yuan to Japanese Yen. */
//    CNYJPY,
//    /** Chinese Yuan to British Pound. */
//    CNYGBP,
//    /** US Dollar to Kuwaiti Dinar. */
//    USDKWD,
//    /** US Dollar to Japanese Yen. */
//    USDJPY,
//    /** Kuwaiti Dinar to Indian Rupee. */
//    KWDINR,
//    /** UAE Dirham to US Dollar. */
//    AEDUSD,
//    /** Chinese Yuan to US Dollar. */
//    CNYUSD,
//    /** US Dollar to British Pound. */
//    USDGBP,
//    /** Kuwaiti Dinar to Chinese Yuan. */
//    KWDCNY,
//    /** Omani Rial to British Pound. */
//    OMRGBP,
//    /** US Dollar to Chinese Yuan. */
//    USDCNY,
//    /** Omani Rial to Kuwaiti Dinar. */
//    OMRKWD,
//    /** Kuwaiti Dinar to British Pound. */
//    KWDGBP,
//    /** US Dollar to Indian Rupee. */
//    USDINR,
//    /** British Pound to US Dollar. */
//    GBPUSD,
//    /** Kuwaiti Dinar to Japanese Yen. */
//    KWDJPY,
//    /** British Pound to Saudi Riyal. */
//    GBPSAR,
//    /** Turkish Lira to UAE Dirham. */
//    TRYAED,
//    /** Omani Rial to Saudi Riyal. */
//    OMRSAR,
//    /** US Dollar to Saudi Riyal. */
//    USDSAR,
//    /** Kuwaiti Dinar to Turkish Lira. */
//    KWDTRY,
//    /** Omani Rial to Chinese Yuan. */
//    OMRCNY,
//    /** Omani Rial to Indian Rupee. */
//    OMRINR,
//    /** UAE Dirham to Turkish Lira. */
//    AEDTRY,
//    /** Saudi Riyal to Omani Rial. */
//    SAROMR,
//    /** Indian Rupee to Turkish Lira. */
//    INRTRY,
//    /** Kuwaiti Dinar to UAE Dirham. */
//    KWDAED,
//    /** Omani Rial to Japanese Yen. */
//    OMRJPY,
//    /** Indian Rupee to UAE Dirham. */
//    INRAED,
//    /** British Pound to Japanese Yen. */
//    GBPJPY,
//    /** US Dollar to Omani Rial. */
//    USDOMR,
//    /** British Pound to Chinese Yuan. */
//    GBPCNY,
//    /** British Pound to Indian Rupee. */
//    GBPINR,
//    /** Kuwaiti Dinar to Omani Rial. */
//    KWDOMR,
//    /** Japanese Yen to UAE Dirham. */
//    JPYAED,
//    /** Turkish Lira to Omani Rial. */
//    TRYOMR,
//    /** Japanese Yen to Turkish Lira. */
//    JPYTRY,
//    /** UAE Dirham to Omani Rial. */
//    AEDOMR,
//    /** Indian Rupee to Omani Rial. */
//    INROMR,
//    /** British Pound to UAE Dirham. */
//    GBPAED,
//    /** Chinese Yuan to Turkish Lira. */
//    CNYTRY,
//    /** Japanese Yen to Saudi Riyal. */
//    JPYSAR,
//    /** US Dollar to UAE Dirham. */
//    USDAED,
//    /** US Dollar to Turkish Lira. */
//    USDTRY,
//    /** Saudi Riyal to Kuwaiti Dinar. */
//    SARKWD,
//    /** British Pound to Turkish Lira. */
//    GBPTRY,
//    /** Japanese Yen to Chinese Yuan. */
//    JPYCNY,
//    /** Turkish Lira to Saudi Riyal. */
//    TRYSAR,
//    /** Saudi Riyal to British Pound. */
//    SARGBP,
//    /** Japanese Yen to Indian Rupee. */
//    JPYINR,
//    /** British Pound to Omani Rial. */
//    GBPOMR,
//    /** Saudi Riyal to Japanese Yen. */
//    SARJPY,
//    /** Indian Rupee to Saudi Riyal. */
//    INRSAR,
//    /** Saudi Riyal to Indian Rupee. */
//    SARINR,
//    /** Omani Rial to Turkish Lira. */
//    OMRTRY,
//    /** Turkish Lira to Indian Rupee. */
//    TRYINR,
//    /** Saudi Riyal to Chinese Yuan. */
//    SARCNY,
//    /** Japanese Yen to Omani Rial. */
//    JPYOMR,
//    /** Turkish Lira to Chinese Yuan. */
//    TRYCNY,
//    /** Turkish Lira to Kuwaiti Dinar. */
//    TRYKWD,
//    /** Chinese Yuan to UAE Dirham. */
//    CNYAED,
//    /** Turkish Lira to Japanese Yen. */
//    TRYJPY,
//    /** Turkish Lira to British Pound. */
//    TRYGBP,
//    /** Omani Rial to UAE Dirham. */
//    OMRAED,
//    /** Indian Rupee to Chinese Yuan. */
//    INRCNY,
//    /** UAE Dirham to British Pound. */
//    AEDGBP,
//    /** UAE Dirham to Japanese Yen. */
//    AEDJPY,
//    /** Japanese Yen to US Dollar. */
//    JPYUSD,
//    /** Indian Rupee to Japanese Yen. */
//    INRJPY,
//    /** Indian Rupee to Kuwaiti Dinar. */
//    INRKWD,
//    /** Indian Rupee to British Pound. */
//    INRGBP,
//    /** Kuwaiti Dinar to US Dollar. */
//    KWDUSD,
//    /** Turkish Lira to US Dollar. */
//    TRYUSD,
//    /** UAE Dirham to Kuwaiti Dinar. */
//    AEDKWD,
//    /** Chinese Yuan to Omani Rial. */
//    CNYOMR,
//    /** Japanese Yen to Kuwaiti Dinar. */
//    JPYKWD,
//    /** UAE Dirham to Saudi Riyal. */
//    AEDSAR,
//    /** Japanese Yen to British Pound. */
//    JPYGBP,
//    /** Chinese Yuan to Indian Rupee. */
//    CNYINR,
//    /** UAE Dirham to Chinese Yuan. */
//    AEDCNY,
//    /** Indian Rupee to US Dollar. */
//    INRUSD,
//    /** UAE Dirham to Indian Rupee. */
//    AEDINR,
//    /** Saudi Riyal to US Dollar. */
//    SARUSD,
//    /** Chinese Yuan to Saudi Riyal. */
//    CNYSAR;
}
