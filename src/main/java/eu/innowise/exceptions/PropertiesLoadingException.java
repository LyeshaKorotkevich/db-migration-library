package eu.innowise.exceptions;

public class PropertiesLoadingException extends RuntimeException {
    public PropertiesLoadingException(String message) {
        super(message);
    }

    public PropertiesLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
