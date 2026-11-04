package exceptions;

/**
 * Thrown when user credentials fail authentication.
 */
public class InvalidLoginException extends Exception {
    public InvalidLoginException(String message) {
        super(message);
    }
}
