package exceptions;

/**
 * Thrown when a student attempts to apply again to a placement drive they have already applied for.
 */
public class AlreadyAppliedException extends Exception {
    public AlreadyAppliedException(String message) {
        super(message);
    }
}
