package exceptions;

/**
 * Thrown when an input does not conform to validation criteria.
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
