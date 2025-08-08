package com.bxb.sunduk_pay.request;

import lombok.Data;

@Data
public class TransactionsRequest {
    private String uuid;
    private String subWalletId;
    private int page = 0;
    private int size = 10;
    private String sortBy = "dateTime";
    private String sortDirection = "DESC";
}
