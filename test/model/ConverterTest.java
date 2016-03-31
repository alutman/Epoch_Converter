package model;

import java.util.GregorianCalendar;

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

        assert Converter.dateStringToEpoch("292269054-12-03 02:47:04,100", 0) == -9223372005318775900L;
        hadException = false;
        try {
            Converter.dateStringToEpoch("292269056-12-03 02:47:04,200", 0);
        }
        catch (ConvertRangeException cre) {
            hadException = true;
        }
        assertTrue(hadException);

    }

    @org.junit.Test
    public void pre_epoch_strings() throws Exception {
        assert Converter.dateStringToEpoch("1800-12-25", 1) == -5333767200000L;
    }

    @org.junit.Test
    public void zero_epoch_dates() throws Exception {
        assert Converter.dateStringToEpoch("1970-01-01 10:00:00,000", 1) == 0L;
        assert Converter.dateStringToEpoch("1970-01-01 10:00:00,000", 0) == -124304284800000L;
    }

    @org.junit.Test
    public void era_mirrored_dates() throws Exception {
        assert Converter.epochToDateString(1444395600000L).equals("2015-10-10 00:00:00,000");
        assert Converter.getEraFromEpoch(1444395600000L) == GregorianCalendar.AD;
        assert Converter.epochToDateString(-125700026400000L).equals("2015-10-10 00:00:00,000");
        assert Converter.getEraFromEpoch(-125700026400000L) == GregorianCalendar.BC;
    }

    @org.junit.Test
    public void bc_date_strings() throws Exception {
        assert Converter.dateStringToEpoch("500-10-05", 0) == -77890672800000L;
    }

    @org.junit.Test
    public void era_boundary() throws Exception {
        assert Converter.epochToDateString(-62135805600002L).equals("0001-12-31 23:59:59,998");
        assert Converter.getEraFromEpoch(-62135805600002L) == GregorianCalendar.BC;
        assert Converter.epochToDateString(-62135805600001L).equals("0001-12-31 23:59:59,999");
        assert Converter.getEraFromEpoch(-62135805600001L) == GregorianCalendar.BC;
        assert Converter.epochToDateString(-62135805600000L).equals("0001-01-01 00:00:00,000");
        assert Converter.getEraFromEpoch(-62135805600000L) == GregorianCalendar.AD;
        assert Converter.epochToDateString(-62135805599999L).equals("0001-01-01 00:00:00,001");
        assert Converter.getEraFromEpoch(-62135805599999L) == GregorianCalendar.AD;
    }

}