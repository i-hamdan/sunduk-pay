package com.bxb.sunduk_pay.exceptions;

import com.bxb.sunduk_pay.exception.*;
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
 * Catches exceptions thrown by controllers/services and returns
 * structured {@link ErrorResponse} with proper HTTP status and
 * message.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link CannotCreateWalletException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(CannotCreateWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleCannotCreateWalletException(
            final CannotCreateWalletException e,
            final HttpServletRequest request) {

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles {@link TransactionNotFoundException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTransactionNotFoundException(
            final TransactionNotFoundException e,
            final HttpServletRequest request) {

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles {@link UserNotFoundException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(
            final UserNotFoundException e,
            final HttpServletRequest request) {

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles {@link WalletNotFoundException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(WalletNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWalletNotFoundException(
            final WalletNotFoundException e,
            final HttpServletRequest request) {

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles {@link InsufficientBalanceException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientBalanceException(
            final InsufficientBalanceException e,
            final HttpServletRequest request) {

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles generic payload parsing errors, including invalid dates and enums.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpServletRequest request) {

        Throwable cause = ex.getCause();
        String message = "Invalid request payload.";

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException
                && cause.getCause() instanceof java.time.format.DateTimeParseException) {
            message = "Invalid date format! Please use yyyy-MM-dd format.";
        } else if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            message = "Invalid value provided for one of the fields "
                    + "(e.g., ActionType).";
        }

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }

    // Additional exception handlers follow the same pattern...
}
