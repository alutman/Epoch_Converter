package model;

/**
 * Created by alutman on 10-Feb-16.
 */
public class DateFormatException extends Exception {
    public DateFormatException(String message, Throwable cause) {
        super(message, cause);
    }
    public DateFormatException(String message) {
        super(message);
    }
}
