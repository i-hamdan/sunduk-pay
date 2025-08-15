package com.bxb.sunduk_pay.request;

import lombok.Data;

@Data
public class SubWalletTransferRequest {
    private String uuid;
    private String subWalletId;
    private Double amount;
}
