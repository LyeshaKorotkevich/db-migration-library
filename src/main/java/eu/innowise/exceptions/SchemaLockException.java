package eu.innowise.exceptions;

/**
 * Custom exception that is thrown when there is an error acquiring or releasing a lock on schema_history table.
 * This exception is a subclass of {@link RuntimeException}.
 */
public class SchemaLockException extends RuntimeException {
    public SchemaLockException(String message) {
        super(message);
    }

  public SchemaLockException(String message, Throwable cause) {
    super(message, cause);
  }
}
