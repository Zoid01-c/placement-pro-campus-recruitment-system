package exceptions;

/**
 * Thrown when trying to register a user with an email or ID that already exists.
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
