package com.bxb.sunduk_pay.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles WebSocket-specific exceptions during STOMP messaging.
 * Automatically sends structured error frames back to the user.
 */
@Log4j2
@ControllerAdvice
public class GlobalWebSocketExceptionHandler {

    /**
     * Handles cases when the receiver user is not found in the system.
     *
     * @param e the WebSocketUserNotFoundException
     * @return structured WebSocket error response
     */
    @MessageExceptionHandler(WebSocketUserNotFoundException.class)
    @SendToUser("/queue/error")
    public WebSocketErrorResponse handleWebSocketUserNotFound(
            WebSocketUserNotFoundException e) {
        log.warn("WebSocket user not found: {}", e.getMessage());
        return new WebSocketErrorResponse(
                "USER_NOT_FOUND",
                e.getMessage(),
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern(
                                "dd MMM yyyy hh:mm:ss a"))
        );
    }

    /**
     * Handles any other unhandled WebSocket exceptions gracefully.
     *
     * @param e generic Exception
     * @return fallback error response for WebSocket clients
     */
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/error")
    public WebSocketErrorResponse handleGenericWebSocketError(Exception e) {
        log.error("WebSocket error: {}", e.getMessage(), e);
        return new WebSocketErrorResponse(
                "ERROR",
  "Something went wrong while sending your message."
          + " Please try again later.",
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern(
                                "dd MMM yyyy hh:mm:ss a"))
        );
    }
}
