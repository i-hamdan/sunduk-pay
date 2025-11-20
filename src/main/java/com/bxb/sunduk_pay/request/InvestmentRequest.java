package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.RiskLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request class for investment operations.
 */
@Getter
@Setter
public class InvestmentRequest {
 /*** UUID of the user. */
private String uuid;
/** ID of the sub-wallet for the investment. */
private String subWalletId;
/** Amount to be invested.*/
private RiskLevel riskLevel;
    /**
     * Amount to be invested.
     */
    private LocalDate investedAt;
    /**
* Type of investment request (e.g., CREATE, UPDATE, DELETE).
*/
private InvestmentRequestType requestType;
}
