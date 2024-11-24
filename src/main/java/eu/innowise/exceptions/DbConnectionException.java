package eu.innowise.exceptions;

/**
 * Custom exception that is thrown when there is a database connection error.
 * This exception is a subclass of {@link RuntimeException}.
 */
public class DbConnectionException extends RuntimeException {
    public DbConnectionException(String message) {
        super(message);
    }

  public DbConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
