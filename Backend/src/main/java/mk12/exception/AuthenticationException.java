package mk12.exception;

/**
 * Exception thrown when there is an authentication error.
 */
public class AuthenticationException extends RuntimeException {
    /**
     * Constructs a new AuthenticationException with the specified detail message.
     *
     * @param message the detail message
     */
    public AuthenticationException(String message) {
        super(message);
    }
}