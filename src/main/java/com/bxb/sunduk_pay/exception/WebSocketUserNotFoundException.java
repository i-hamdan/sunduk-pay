package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a WebSocket message is sent to a user
 * who is not registered on the SundukPay platform.
 * <p>
 * This exception is typically raised during real-time message
 * processing in the chat module when the backend fails to find
 * a corresponding user for the provided phone number. It is used
 * to trigger a WebSocket error response that notifies the sender
 * that the recipient is not a SundukPay user.
 */
public class WebSocketUserNotFoundException extends RuntimeException {
 /**
 * Constructs a new {@code WebsocketUserNotFoundException}
 * with the specified detail message.
 *
 * @param message the detail message describing the reason
 *                for the exception
 */
    public WebSocketUserNotFoundException(String message) {
        super(message);
    }
}
