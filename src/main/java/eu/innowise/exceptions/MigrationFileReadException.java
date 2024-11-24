package eu.innowise.exceptions;

/**
 * Custom exception that is thrown when an error occurs while reading a migration file.
 * This exception is a subclass of {@link RuntimeException}.
 */
public class MigrationFileReadException extends RuntimeException {
    public MigrationFileReadException(String message) {
        super(message);
    }

    public MigrationFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
