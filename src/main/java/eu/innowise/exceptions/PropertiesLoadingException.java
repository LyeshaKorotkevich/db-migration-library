package eu.innowise.exceptions;

/**
 * Custom exception that is thrown when there is an error loading properties from application.properties.
 * This exception is a subclass of {@link RuntimeException}.
 */
public class PropertiesLoadingException extends RuntimeException {
    public PropertiesLoadingException(String message) {
        super(message);
    }

    public PropertiesLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
