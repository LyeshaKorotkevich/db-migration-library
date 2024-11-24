package eu.innowise.exceptions;

/**
 * Custom exception that is thrown when a migration operation fails.
 * This exception is a subclass of {@link RuntimeException}.
 */
public class MigrationException extends RuntimeException {

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

