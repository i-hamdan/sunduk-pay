package com.bxb.sunduk_pay.request;


import lombok.Data;

@Data
public class PaginationRequest {
    private Integer pageSize;
    private Integer pageNumber;
    private String sortBy;
}
