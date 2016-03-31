package model;

import java.text.*;
import java.util.*;

/**
 * Created by alutman on 19/02/14.
 *
 * Basis of all calculations. Input gets processed into output here.
 *
 */
public class Converter {

    //changing the date format positions requires changes in the _LOC constants

    //Format adheres to ISO8601 e.g. 2014-07-23 14:52:30,043
    public static final String DATE_DELIM = "-";
    public static final String TIME_DELIM = ":";
    public static final String MS_DELIM = ",";
    public static final String SEPARATOR_DELIM = " ";
    public static final String DATE_FORMAT = "yyyy"+DATE_DELIM+"MM"+DATE_DELIM+"dd"+SEPARATOR_DELIM+"HH"+TIME_DELIM+"mm"+TIME_DELIM+"ss"+MS_DELIM+"SSS";

    //Ordered largest to smallest time segments
    private static final int YEAR_LOC = 0;
    private static final int MONTH_LOC = 1;
    private static final int DAY_LOC = 2;
    private static final int HOUR_LOC = 3;
    private static final int MINUTE_LOC = 4;
    private static final int SECOND_LOC = 5;
    private static final int MILSEC_LOC = 6;

    private static final int NUM_DATE_FORMAT = 3;
    private static final int NUM_TIME_FORMAT = 4;
    private static final int NUM_TOTAL_FORMAT = 7;

    /**
     * Current epoch value in milliseconds
     * @return current time in epoch milliseconds
     */
    public static long getEpoch() {
        return System.currentTimeMillis();
    }


    /**
     * Convert milliseconds to a human readable time span
     * @param ms milliseconds to convert
     * @return human readable time span string in format Xy Xd HH:mm:ss,SSS
     */
    public static String msToHumanSpan(long ms) {
        boolean neg = false;

        //Check if negative and remove the sign
        if (ms < 0) {
            neg = true;
            ms = Math.abs(ms);
        }

        //Time measures in milliseconds
        long yearL = 31536000000L;
        long dayL = 86400000L;
        long hourL = 3600000L;
        long minuteL = 60000L;
        long secondL = 1000L;

        long years = Math.abs(ms / yearL);
        ms = ms % yearL;
        long days = Math.abs(ms / dayL);
        ms = ms % dayL;
        long hours = Math.abs(ms / hourL);
        ms = ms % hourL;
        long minutes = Math.abs(ms / minuteL);
        ms = ms % minuteL;
        long seconds = Math.abs(ms / secondL);
        ms = ms % secondL;
        long milliseconds = ms;

        String span = String.format("%dy %dd %02d:%02d:%02d,%03d",years, days, hours, minutes, seconds, milliseconds);

        //Represent negative ms as negative span
        if(neg) {
            span = "-"+span;
        }
        return span;
    }


    /* Used for Era calculations */
    private static Calendar gCal = new GregorianCalendar();

    /* Overloaded with default CE era (1)*/
    public static long dateStringToEpoch(String dateString) throws ConvertParseException, ConvertRangeException {
        return dateStringToEpoch(dateString, 1);
    }
    /**
     * Convert a formatted date string to epoch milliseconds
     * @param dateString dateString in format yyyy-MM-dd HH:mm:ss,SSS
     * @param era time era (BC, AD) as represented by int in
     * @return millisecond epoch representation of that date. Returns negative values on error
     * @throws ConvertParseException
     */
    public static long dateStringToEpoch(String dateString, int era) throws ConvertParseException, ConvertRangeException {
        long epoch;
        try {
            //pull out the numbers. This will force default values for missing elements
            long[] a = dateStringToArray(dateString);
            //Put it back into a string. Now with default values if nothing was there before
            dateString = dateArrayToString(a);
            if(!isDateStringWithinEpoch(dateString, era)) throw new ConvertRangeException("Date is outside epoch time range");

            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            //Adhere to num days in month, max hour time etc rules
            df.setLenient(false);
            Date sdf = df.parse(dateString);
            gCal.setTime(sdf);
            gCal.set(Calendar.ERA, era);
            epoch = gCal.getTimeInMillis();
        }
        catch(ParseException pe) {
            //Date must be in '01/12/1970 01:00:00' format
            //or invalid month/day number. eg 21/52/2010, 30/02/2010
            throw new ConvertParseException("Date must be in "+Converter.DATE_FORMAT+" format", pe);
        }
        catch(NumberFormatException nfe) {
            //something else wrong with the format
            throw new ConvertParseException(nfe.getMessage(), nfe);
        }
        return epoch;
    }

    /**
     * Converts an millisecond epoch number to a formatted date string
     * @param epoch millisecond representation
     * @return formatted date string
     */
    public static String epochToDateString(long epoch) {
        return new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date(epoch));
    }
    public static int getEraFromEpoch(long epoch) {
        Date d = new java.util.Date(epoch);
        gCal.setTime(d);
        return gCal.get(Calendar.ERA);
    }

    /**
     * Converts an array of date values to a formatted String.
     * @param values array of date values
     * @return formatted date string from array
     */
    private static String dateArrayToString(long[] values) {
        StringBuilder sb = new StringBuilder();
        //Date values
        sb.append(values[YEAR_LOC]);
        sb.append(DATE_DELIM);
        sb.append(values[MONTH_LOC]);
        sb.append(DATE_DELIM);
        sb.append(values[DAY_LOC]);
        sb.append(SEPARATOR_DELIM);
        //Time values
        sb.append(values[HOUR_LOC]);
        sb.append(TIME_DELIM);
        sb.append(values[MINUTE_LOC]);
        sb.append(TIME_DELIM);
        sb.append(values[SECOND_LOC]);
        sb.append(MS_DELIM);
        sb.append(values[MILSEC_LOC]);
        return sb.toString();
    }

    /**
     * Converts a formatted string to an array of date values
     * @param date string formatted date
     * @return array representation of that date
     * @throws NumberFormatException
     */
    private static long[] dateStringToArray(String date) throws NumberFormatException{
        //order from biggest to smallest
        long[] ia = new long[NUM_TOTAL_FORMAT];

        //Split into time and date
        StringTokenizer st = new StringTokenizer(date,SEPARATOR_DELIM);
        String dateString;
        String timeString;
        try {
            dateString = st.nextToken();
        }
        catch(NoSuchElementException nsee) {
            //No string at all. Should cause a format exception later
            dateString = "0"+DATE_DELIM+"0"+DATE_DELIM+"0";
        }
        try {
            timeString = st.nextToken();
        }
        catch(NoSuchElementException nsee) {
            //If time is absent, set to 0 everything
            timeString = "0"+TIME_DELIM+"0"+TIME_DELIM+"0"+MS_DELIM+"000";
        }

        //Split the date values
        st = new StringTokenizer(dateString, DATE_DELIM);
        int i = 0;
        for( ; i < NUM_DATE_FORMAT; i++) {
            if(st.hasMoreTokens()) {
                ia[i] = Long.parseLong(st.nextToken());
            }
            else {
                if(i == 0) {
                    //default year to 1970
                    ia[i] = 1970;
                }
                else {
                    //If an element is missing, default it to 1 (Cannot be zero as 2014/0/0 makes no sense)
                    ia[i] = 1;
                }
            }
        }
        //Split the time values
        st = new StringTokenizer(timeString,TIME_DELIM.concat(MS_DELIM));
        for( ; i-NUM_DATE_FORMAT < NUM_TIME_FORMAT; i++) {
            if(st.hasMoreTokens()) {
                ia[i] = Long.parseLong(st.nextToken());
            }
            else {
                //Missing element default to 0. Time can be zero (midnight)
                ia[i] = 0;
            }
        }

        return ia;
    }

    /**
     * Check if a date string value is within the maximum and minimum epoch value
     * @param dateString formatted date string
     * @param era which era the date corresponds to (BC 0, AD 1)
     * @return true if date string is less than the max epoch, otherwise false
     * @throws NumberFormatException
     */
    private static boolean isDateStringWithinEpoch(String dateString, int era) throws NumberFormatException {
        long[] maxA = new long[NUM_TOTAL_FORMAT];
        long[] timeA = dateStringToArray(dateString);
        if (era == GregorianCalendar.BC) {
            //minimum values
            //292269055-12-03 02:47:04,192
            maxA[0] = 292269055; //year
            maxA[1] = 12; //month
            maxA[2] = 3; //day
            maxA[3] = 2; //hour
            maxA[4] = 47; //minute
            maxA[5] = 4; //second
            maxA[6] = 192; //ms
            //If the year is the max year, check that the times are GREATER THAN than the max
            //Else return that the year is LESS than the max
            if(timeA[0] == maxA[0]) {
                return compare(maxA, timeA, 1, true);
            }
            else return timeA[0] < maxA[0];
        } else {
            //maximum values
            //292278994-08-17 17:12:55,807
            maxA[0] = 292278994; //year
            maxA[1] = 8; //month
            maxA[2] = 17; //day
            maxA[3] = 17; //hour
            maxA[4] = 12; //minute
            maxA[5] = 55; //second
            maxA[6] = 807; //ms
            //Check if the time is less the the max
            return compare(maxA, timeA, 0, false);
        }
    }


    /**
     * Compares values in two arrays. Recursive method. Does comparisons in the array order.
     * Used to compare dates in array format ( [2015, 12, 10, 11, 30, 55, 232] == 2015-12-10 11:30:55,232)
     * If a value is greater, return false, no more subsequent values are checked
     * If a value is equal, the next will be checked. If its the last value, return true
     * If a value is less, return true, no more subsequent values are checked
     * @param maxA value array which to check against
     * @param timeA value array to check
     * @param place which element of the array to check (this should be zero for the first call
     * @param invert instead of checking if the values are less than the max, check they are greater than the max
     * @return true if timeA is less than maxA, otherwise false
     */
    private static boolean compare(long[] maxA, long[] timeA, int place, boolean invert) {
        if(invert) {
            if(timeA[place] < maxA[place]) {
                return false;
            }
        }
        else {
            //If bigger, return false
            if(timeA[place] > maxA[place]) {
                return false;
            }
        }

        //if equal, check the next date segment.
        if(timeA[place] == maxA[place]) {
            //Exit statement if last segment
            if(place == MILSEC_LOC) {
                return true;
            }
            return compare(maxA, timeA, ++place, invert);
        }
        //If less, return true
        else {
            return true;
        }
    }

}
