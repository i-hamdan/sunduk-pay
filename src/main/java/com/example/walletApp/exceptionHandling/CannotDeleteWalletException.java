package com.example.walletApp.exceptionHandling;

public class CannotDeleteWalletException extends RuntimeException{
    public CannotDeleteWalletException(String message) {
        super(message);
    }
}
