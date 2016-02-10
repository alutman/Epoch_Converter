package model;

/**
 * Created by alutman on 10-Feb-16.
 */
public class ConvertRangeException extends Exception {
    public ConvertRangeException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConvertRangeException(String message) {
        super(message);
    }
}
