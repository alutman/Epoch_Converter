package model;

import static org.junit.Assert.*;

/**
 * Created by alutman on 31-Mar-16.
 */
public class ConverterTest {

    @org.junit.Test
    public void max_epoch_date_string() throws Exception {
        assert Converter.dateStringToEpoch("292278994-08-17 17:12:55,807", 1) == 9223372036854775807L;
        boolean hadException = false;
        try {
            Converter.dateStringToEpoch("292278994-08-17 17:12:55,808", 1);
        }
        catch(ConvertRangeException cre) {
            hadException = true;
        }
        assertTrue(hadException);
        assert Converter.dateStringToEpoch("292278994-08-17 17:12:55,806", 1) == 9223372036854775806L;
    }

    @org.junit.Test
    public void min_epoch_date_string() throws Exception {
        boolean hadException = false;
        assert Converter.dateStringToEpoch("292269055-12-03 02:47:04,192", 0) == -9223372036854775808L;
        try {
            Converter.dateStringToEpoch("292269055-12-03 02:47:04,191", 0);
        }
        catch (ConvertRangeException cre) {
            hadException = true;
        }
        assertTrue(hadException);
        assert Converter.dateStringToEpoch("292269055-12-03 02:47:04,193", 0) == -9223372036854775807L;
    }

    @org.junit.Test
    public void pre_epoch_strings() throws Exception {
        assert Converter.dateStringToEpoch("1800-12-25", 1) == -5333767200000L;
    }


    @org.junit.Test
    public void bc_date_strings() throws Exception {
        assert Converter.dateStringToEpoch("500-10-05", 0) == -77890672800000L;
    }

}