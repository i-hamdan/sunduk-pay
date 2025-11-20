package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.InvestmentRequesType;
import com.bxb.sunduk_pay.util.RiskLevel;
import lombok.Getter;
import lombok.Setter;
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
* Type of investment request (e.g., CREATE, UPDATE, DELETE).
*/
private InvestmentRequesType requestType;
}
