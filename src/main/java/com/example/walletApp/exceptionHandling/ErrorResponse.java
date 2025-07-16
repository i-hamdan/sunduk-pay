package com.example.walletApp.exceptionHandling;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ErrorResponse {
    private LocalDateTime localDateTime;
    private Integer status;
    private String error;
    private String message;
    private String path;


    public ErrorResponse(LocalDateTime localDateTime, Integer status,String error, String message, String path) {
        this.error = error;
        this.localDateTime = localDateTime;
        this.message = message;
        this.path = path;
        this.status = status;
    }
}
