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

    //Ordered largest to smallest time segments for DateArray conversion
    private static final int DA_YEAR_LOC = 0;
    private static final int DA_MONTH_LOC = 1;
    private static final int DA_DAY_LOC = 2;
    private static final int DA_HOUR_LOC = 3;
    private static final int DA_MINUTE_LOC = 4;
    private static final int DA_SECOND_LOC = 5;
    private static final int DA_MILSEC_LOC = 6;

    private static final int DA_DATE_SEG_LEN = 3;
    private static final int DA_TIME_SEG_LEN = 4;
    private static final int DA_TOTAL_LEN = 7;

    private static final int DELIMITER_OFFSET = 2;
    private static final int DATE_STRING_TOKENS = 5;
    private static final int TIME_STRING_TOKENS = 7;


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
        long milliseconds = Math.abs(ms);

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
            long[] dateArray = dateStringToArray(dateString);
            //Put it back into a string. Now with default values if nothing was there before
            dateString = dateArrayToString(dateArray);

            //Dates outside the max/min value for epoch LONG values will still be parsed successfully but will overflow
            //giving weird values. Check the value here and fail early instead
            if(!isDateArrayWithinEpoch(dateArray, era)) throw new ConvertRangeException("Date is outside epoch time range");

            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            //Don't be lenient. Adhere to rules specifying the number of days in each month, months in year, etc
            df.setLenient(false);
            Date sdf = df.parse(dateString);

            //Set the era before interpreting the millsecond value
            gCal.setTime(sdf);
            gCal.set(Calendar.ERA, era);
            epoch = gCal.getTimeInMillis();
        }
        catch(ParseException pe) {
            //Date must be in '01/12/1970 01:00:00' format
            //or invalid month/day number. eg 21/52/2010, 30/02/2010
            throw new ConvertParseException("Date must be in "+Converter.DATE_FORMAT+" format", pe);
        }
        catch(NumberFormatException | DateFormatException e) {
            //something else wrong with the format
            throw new ConvertParseException(e.getMessage(), e);
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
        sb.append(values[DA_YEAR_LOC]);
        sb.append(DATE_DELIM);
        sb.append(values[DA_MONTH_LOC]);
        sb.append(DATE_DELIM);
        sb.append(values[DA_DAY_LOC]);
        sb.append(SEPARATOR_DELIM);
        //Time values
        sb.append(values[DA_HOUR_LOC]);
        sb.append(TIME_DELIM);
        sb.append(values[DA_MINUTE_LOC]);
        sb.append(TIME_DELIM);
        sb.append(values[DA_SECOND_LOC]);
        sb.append(MS_DELIM);
        sb.append(values[DA_MILSEC_LOC]);
        return sb.toString();
    }

    /**
     * Converts a formatted string to an array of date values, setting defaults if the smaller values are missing
     * @param date string formatted date
     * @return array representation of that date
     * @throws NumberFormatException DateFormatException
     */
    private static long[] dateStringToArray(String date) throws NumberFormatException, DateFormatException {
        //order from biggest to smallest
        long[] ia = new long[DA_TOTAL_LEN];

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

        //TODO Simplify the split sections

        /* SPLIT DATE STRING VALUES INTO ARRAY */

        //Split date array on -
        st = new StringTokenizer(dateString, DATE_DELIM, true);
        int i = 0;

        //Shouldn't ever have more than the expected tokens
        if(st.countTokens() > DATE_STRING_TOKENS) throw new DateFormatException("Invalid date format");

        //For each delimiter and value in the date portion of the string
        for( ; i < DATE_STRING_TOKENS; i++) {
            switch(i) {
                case 0: //year
                    //The first element should be the year, if there are no tokens for it, fail.
                    if(!st.hasMoreTokens()) {
                        throw new DateFormatException("Year must be set");
                    }
                case 2: // month location in the tokenized string
                case 4: // day location in the tokenized string
                    if(st.hasMoreTokens()) {
                        //Yes there is a value for the year/month/day, convert to number and put into array

                        // DELIMITER OFFSET converts the data position in the token array to the real position in the date array
                        ia[i/DELIMITER_OFFSET] = Long.parseLong(st.nextToken());
                        if(ia[i/DELIMITER_OFFSET] < 0 ) throw new NumberFormatException("Date values cannot be negative");
                    }
                    else {
                        //No there isn't a value present, set it to the default of 1
                        ia[i/DELIMITER_OFFSET] = 1;
                    }
                    break;
                default: //Delimiters will end up here
                    //Just consume and move on
                    if(st.hasMoreTokens()) {
                        st.nextToken();
                    }
            }
        }

        /* SPLIT TIME STRING VALUES INTO ARRAY */

        //Split the time array on : and ,
        st = new StringTokenizer(timeString,TIME_DELIM.concat(MS_DELIM), true);

        //Shouldn't ever have more than the expected tokens
        if(st.countTokens() > TIME_STRING_TOKENS) throw new DateFormatException("Invalid time format");
        for( i = 0 ; i < TIME_STRING_TOKENS; i++) {
            switch(i) {
                case 1:// ':' delimiter
                case 3:// second ':' delimiter
                    if(st.hasMoreTokens()) {
                        //assert that the first and second delimiters are ':'
                        if (!st.nextToken().equals(TIME_DELIM)) throw new DateFormatException("Invalid time delimiter");
                    }
                    break;
                case 5: // ',' delimiter
                    if(st.hasMoreTokens()) {
                        //asser that the final delimiter is ','
                        if (!st.nextToken().equals(MS_DELIM)) throw new DateFormatException("Invalid MS delimiter");
                    }
                    break;
                case 0: //hour location in the tokenized string
                case 2: //minute location in the tokenized string
                case 4: //second location in the tokenized string
                case 6: //ms location in the tokenized string
                    if(st.hasMoreTokens()) {
                        //Yes there is a value for one of the time elements, convert and add to array

                        //DELIMITER_OFFSET factors in the amount of delimiters in the data array and DA_DATE_SEG_LEN
                        //factors in the date elements already entered. This ensures the time data gets placed in the correct part of the date array
                        ia[i / DELIMITER_OFFSET + DA_DATE_SEG_LEN] = Long.parseLong(st.nextToken());
                        if (ia[i / DELIMITER_OFFSET + DA_DATE_SEG_LEN] < 0) throw new NumberFormatException("Date values cannot be negative");
                    }
                    else {
                        //No data present, set the default value of 0
                        ia [i / DELIMITER_OFFSET  + DA_DATE_SEG_LEN] = 0;
                    }
                    break;
                default:
                    //For safety, shouldn't ever fire
                    if(st.hasMoreTokens()) {
                        st.nextToken();
                    }
            }
        }

        return ia;
    }


    /**
     * Check if a date array  value is within the maximum and minimum epoch value
     * @param dateArray date array
     * @param era which era the date corresponds to (BC 0, AD 1)
     * @return true if date string is less than the max epoch, otherwise false
     * @throws NumberFormatException
     */
    private static boolean isDateArrayWithinEpoch(long[] dateArray, int era) throws NumberFormatException, DateFormatException {
        long[] maxA = new long[DA_TOTAL_LEN];
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
            if(dateArray[0] == maxA[0]) {
                return compare(maxA, dateArray, 1, true);
            }
            else return dateArray[0] < maxA[0];
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
            return compare(maxA, dateArray, 0, false);
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
            if(place == DA_MILSEC_LOC) {
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
