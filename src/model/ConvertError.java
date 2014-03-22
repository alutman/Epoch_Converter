package model;

/**
 * Created by alutman on 24/02/14.
 *
 * enum to assign long values to error codes. Better readability than just putting -1
 *
 */
public enum ConvertError {
    PARSE_ERROR(-1),
    RANGE_ERROR(-2);

    private long value;
    private ConvertError(long value) {
        this.value = value;
    }
    public long getValue() {
        return value;
    }
}
