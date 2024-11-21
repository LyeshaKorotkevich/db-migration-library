package eu.innowise.exceptions;

public class ChecksumCalculationException extends RuntimeException {

    public ChecksumCalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChecksumCalculationException(String message) {
        super(message);
    }
}
