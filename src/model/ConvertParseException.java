package model;

/**
 * Created by alutman on 10-Feb-16.
 */
public class ConvertParseException extends Exception {
    public ConvertParseException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConvertParseException(String message) {
        super(message);
    }
}
