package eu.innowise.exceptions;

/**
 * Custom exception that is thrown when there is an error during checksum calculation.
 * This exception is a subclass of {@link RuntimeException}.
 */
public class ChecksumCalculationException extends RuntimeException {

    public ChecksumCalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChecksumCalculationException(String message) {
        super(message);
    }
}
