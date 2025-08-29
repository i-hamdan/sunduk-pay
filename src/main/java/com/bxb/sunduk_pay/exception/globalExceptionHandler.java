package com.bxb.sunduk_pay.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class globalExceptionHandler {

    @ExceptionHandler(value = CannotCreateWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleCannotCreateWalletException(CannotCreateWalletException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(value = TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTransactionNotFoundException(TransactionNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(value = WalletNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWalletNotFoundException(WalletNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(value = InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientBalanceException(InsufficientBalanceException e, HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e,HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),HttpStatus.NOT_FOUND.value(),HttpStatus.NOT_FOUND.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }

    @ExceptionHandler(value = InvalidSessionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvalidSessionException(InvalidSessionException e,HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),HttpStatus.FORBIDDEN.value(),HttpStatus.FORBIDDEN.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }
    @ExceptionHandler(CustomExchangeRateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse CustomExchangeRateException(CustomExchangeRateException e, HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }
    @ExceptionHandler(NullAmountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidAmount(NullAmountException e, HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }


    @ExceptionHandler(MaxSubWalletsExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMaxSubWalletsExceededException(MaxSubWalletsExceededException e, HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }

    @ExceptionHandler(NullValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNullValueException(NullValueException e, HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }

    @ExceptionHandler(TransactionProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleTransactionProcessingException(TransactionProcessingException e, HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }

    @ExceptionHandler(StripeSessionException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleStripeSessionException(StripeSessionException e, HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_GATEWAY.value(),HttpStatus.BAD_GATEWAY.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }

    @ExceptionHandler(InvalidPayloadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidPayloadException(InvalidPayloadException e, HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }



    @ExceptionHandler(CannotUpdateWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleCannotUpdateWalletException(CannotUpdateWalletException e, HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),HttpStatus.FORBIDDEN.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }



    @ExceptionHandler(CannotDeleteWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleCannotDeleteWalletException(CannotDeleteWalletException e, HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),HttpStatus.FORBIDDEN.getReasonPhrase(),e.getMessage(),request.getRequestURI());
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();

        String message = "Invalid request payload.";

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException &&
                cause.getCause() instanceof java.time.format.DateTimeParseException) {
            // Wrong date format
            message = "Invalid date format! Please use yyyy-MM-dd format.";
        }
        else if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            // Wrong enum or wrong type
            message = "Invalid value provided for one of the fields (e.g., ActionType).";
        }

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }



}
