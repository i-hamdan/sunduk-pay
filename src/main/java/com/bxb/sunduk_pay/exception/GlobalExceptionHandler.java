package com.bxb.sunduk_pay.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
@Log4j2
public class GlobalExceptionHandler {
    /**
     * Handles {@link InvalidMpinException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */

    @ExceptionHandler(value = InvalidMpinException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvalidMpinException(
            final InvalidMpinException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }
    /**
     * Handles {@link CannotCreateWalletException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(value = CannotCreateWalletException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleCannotCreateWalletException(
            final CannotCreateWalletException e,
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
    public ErrorResponse handleTransactionNotFoundException(
            final TransactionNotFoundException e,
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
    public ErrorResponse handleUserNotFoundException(
            final UserNotFoundException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
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
    public ErrorResponse handleWalletNotFoundException(
            final WalletNotFoundException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI());
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
    public ErrorResponse handleInsufficientBalanceException(
            final InsufficientBalanceException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
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
    public ErrorResponse handleResourceNotFoundException(
            final ResourceNotFoundException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI());
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
    public ErrorResponse handleInvalidSessionException(
            final InvalidSessionException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
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
    public ErrorResponse customExchangeRateException(
            final CustomExchangeRateException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
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
    public ErrorResponse invalidAmount(
            final NullAmountException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
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
    public ErrorResponse handleMaxSubWalletsExceededException(
            final MaxSubWalletsExceededException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
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
    public ErrorResponse handleNullValueException(
            final NullValueException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
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
    public ErrorResponse handleTransactionProcessingException(
            final TransactionProcessingException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }


    /**
     * Handles {@link SubWalletAlreadyExistsException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(value = SubWalletAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleSubWalletAlreadyExistsException(
            final SubWalletAlreadyExistsException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
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
    public ErrorResponse handleStripeSessionException(
            final StripeSessionException e,
            final HttpServletRequest request) {
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
    public ErrorResponse handleInvalidPayloadException(
            final InvalidPayloadException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
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
    public ErrorResponse handleCannotUpdateWalletException(
            final CannotUpdateWalletException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
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
    public ErrorResponse handleCannotDeleteWalletException(
            final CannotDeleteWalletException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }

    /**
     * Handles {@link InvalidPhotoException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */

    @ExceptionHandler(InvalidPhotoException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvalidPhotoException(
            final InvalidPhotoException e,
            final HttpServletRequest request) {

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI());
    }
    /**
     * Handles {@link MaxUploadSizeExceededException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handleMaxUploadSizeExceededException(
            final MaxUploadSizeExceededException e,
            final HttpServletRequest request) {

        // Log the actual error
        log.error("Multipart upload failed: {}", e.getMessage());

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(),
                "File size exceeds the limit! Individual files" +
                        " must be under 10MB and total request under 50MB.",
                request.getRequestURI()
        );

    }


    /**
     * Handles {@link CannotFetchMessagesException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */
    @ExceptionHandler(value = CannotFetchMessagesException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleCannotFetchMessagesException(
            final CannotFetchMessagesException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI());
    }
    /**
     * Handles {@link RedisOperationException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */


    @ExceptionHandler(value = RedisOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRedisOperationException(
            final RedisOperationException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Redis operation failed!",
                e.getMessage(),
                request.getRequestURI());
    }
    /**
     * Handles {@link ChatProcessingException}.
     *
     * @param e       the exception
     * @param request the HTTP request
     * @return structured error response
     */

    @ExceptionHandler(value = ChatProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleChatProcessingException(
            final ChatProcessingException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Chat processing failed!",
                e.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(value = InvestmentException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvestException(
            final InvestmentException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Investment processing failed!",
                e.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(value = MpinAlreadyExists.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse mpinAlreadyExists(
            final MpinAlreadyExists e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                e.getMessage(),
                e.getMessage(),
                request.getRequestURI());
    }


    /**
     * Handles {@link HttpMessageNotReadableException}
     * for invalid JSON payloads.
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

        if (
                cause instanceof com.fasterxml
                        .jackson.databind.exc
                        .InvalidFormatException
                        && cause.getCause() instanceof java
                        .time.format
                        .DateTimeParseException) {
            // Wrong date format
            message = "Invalid date format! "
                    + "Please use yyyy-MM-dd format.";
        } else if (
                cause instanceof com.fasterxml
                        .jackson.databind.exc
                        .InvalidFormatException) {
            // Wrong enum or wrong type
        message = "Invalid value provided for "
                + "one of the fields (e.g., ActionType).";
        }

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }
    @ExceptionHandler(value = GlobalPotNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleGlobalPotNotFoundException(
            final GlobalPotNotFoundException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(value = GoalAmountBelowThresholdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGoalAmountBelowThresholdException(
            final GoalAmountBelowThresholdException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(value = CannotCreateAnonymousUserException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleCannotCreateAnonymousUserException(
            final CannotCreateAnonymousUserException e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(value = UserIsBlocked.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUserIsBlockedException(
            final UserIsBlocked e,
            final HttpServletRequest request) {
        return new ErrorResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
    }



}
