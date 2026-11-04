package exceptions;

/**
 * Thrown when a specified placement drive is not found or inactive.
 */
public class DriveNotFoundException extends Exception {
    public DriveNotFoundException(String message) {
        super(message);
    }
}
