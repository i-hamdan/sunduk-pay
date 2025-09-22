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
    @ExceptionHandler(value = CannotCreateWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse
    handleCannotCreateWalletException(final
                                      CannotCreateWalletException e,
                                      final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link TransactionNotFoundException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(value = TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTransactionNotFoundException(final
                                        TransactionNotFoundException e,
                                        final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                                    HttpStatus.NOT_FOUND.value(),
                                     HttpStatus.NOT_FOUND.getReasonPhrase(),
                                        e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link UserNotFoundException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e,
                                                     final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link WalletNotFoundException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(value = WalletNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWalletNotFoundException(final
                                               WalletNotFoundException e,
                                               final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link InsufficientBalanceException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(value = InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientBalanceException(final
                                                 InsufficientBalanceException e,
                                                 final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link ResourceNotFoundException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(final
                                          ResourceNotFoundException e
                                        , final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),request.getRequestURI());
    }
    /**
     * Handles {@link InvalidSessionException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(value = InvalidSessionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvalidSessionException(final InvalidSessionException e,
                                        final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI());
    }

    /**
     * Handles {@link CustomExchangeRateException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(CustomExchangeRateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse CustomExchangeRateException(final
                                      CustomExchangeRateException e,
                                      final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(),request.getRequestURI());
    }
    /**
     * Handles {@link NullAmountException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(NullAmountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidAmount(final NullAmountException e,
                                       final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage(),
                                                request.getRequestURI());
    }

    /**
     * Handles {@link MaxSubWalletsExceededException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(MaxSubWalletsExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMaxSubWalletsExceededException(final
                                           MaxSubWalletsExceededException e,
                                           final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),request.getRequestURI());
    }

    /**
     * Handles {@link NullValueException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(NullValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNullValueException(final NullValueException e,
                                                 final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage(),
                request.getRequestURI());
    }

    /**
     * Handles {@link TransactionProcessingException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(TransactionProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleTransactionProcessingException(final
                                          TransactionProcessingException e,
                                          final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(),request.getRequestURI());
    }

    /**
     * Handles {@link StripeSessionException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(StripeSessionException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleStripeSessionException(final
                                         StripeSessionException e,
                                         final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link InvalidPayloadException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(InvalidPayloadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidPayloadException(final
                                                  InvalidPayloadException e,
                                                  final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),request.getRequestURI());
    }

    /**
     * Handles {@link CannotUpdateWalletException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(CannotUpdateWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleCannotUpdateWalletException(final
                                           CannotUpdateWalletException e,
                                           final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                e.getMessage(),request.getRequestURI());
    }


    /**
     * Handles {@link CannotDeleteWalletException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(CannotDeleteWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleCannotDeleteWalletException(final
                                          CannotDeleteWalletException e,
                                         final HttpServletRequest request){
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage(),
                                                        request.getRequestURI());
    }

    /**
     * Handles {@link HttpMessageNotReadableException} for invalid JSON payloads.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(final
                                      HttpMessageNotReadableException ex,
                                      final HttpServletRequest request) {
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
