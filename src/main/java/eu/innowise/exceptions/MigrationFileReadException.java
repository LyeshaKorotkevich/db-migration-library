package eu.innowise.exceptions;

public class MigrationFileReadException extends RuntimeException {
    public MigrationFileReadException(String message) {
        super(message);
    }

    public MigrationFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
