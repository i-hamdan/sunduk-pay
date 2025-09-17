package com.bxb.sunduk_pay.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Global exception handler for the application.
 * <p>
 * Catches various exceptions thrown by controllers and services
 * and returns structured {@link ErrorResponse} objects with appropriate
 * HTTP status codes and messages.
 * </p>
 */
@RestControllerAdvice
public class globalExceptionHandler {

    /**
     * Handles {@link CannotCreateWalletException}.
     */
    @ExceptionHandler(CannotCreateWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleCannotCreateWalletException(CannotCreateWalletException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link TransactionNotFoundException}.
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTransactionNotFoundException(TransactionNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link UserNotFoundException}.
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link WalletNotFoundException}.
     */
    @ExceptionHandler(WalletNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWalletNotFoundException(WalletNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link InsufficientBalanceException}.
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientBalanceException(InsufficientBalanceException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles generic payload parsing errors, including invalid dates and enums.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();
        String message = "Invalid request payload.";

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException &&
                cause.getCause() instanceof java.time.format.DateTimeParseException) {
            message = "Invalid date format! Please use yyyy-MM-dd format.";
        } else if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            message = "Invalid value provided for one of the fields (e.g., ActionType).";
        }

        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), message, request.getRequestURI());
    }

    // Additional exception handlers follow the same pattern...
}
