package eu.innowise.exceptions;

public class SchemaLockException extends RuntimeException {
    public SchemaLockException(String message) {
        super(message);
    }

  public SchemaLockException(String message, Throwable cause) {
    super(message, cause);
  }
}
