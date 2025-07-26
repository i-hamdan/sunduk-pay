package com.bxb.sunduk_pay.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class globalExceptionHandler {

    @ExceptionHandler(value = CannotDeleteWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleCannotDeleteWalletException(CannotDeleteWalletException e, HttpServletRequest request) {
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
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e,HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),HttpStatus.CONFLICT.value(),HttpStatus.CONFLICT.getReasonPhrase(),e.getMessage(),request.getRequestURI());
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














}
