package com.bxb.sunduk_pay.response;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

@Data
public class SubWalletResponse {
    private String subWalletId;
    private String subWalletName;
    private Double amount;
}
