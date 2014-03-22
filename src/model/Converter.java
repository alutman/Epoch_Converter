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

    //changing the date format requires changes in the _LOC constants
    public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

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

    //Current epoch value
    public long getEpoch() {
        return System.currentTimeMillis();
    }
    //Displays the epoch in an absolute time format.
    public String toHumanSpan(long epoch) {
        if(epoch < 0) {
            //don't bother if the epoch is invalid
            return null;
        }
        //Time measures in milliseconds
        long yearL = 31536000000L;
        long dayL = 86400000L;
        long hourL = 3600000L;
        long minuteL = 60000L;
        long secondL = 1000L;

        long years = Math.abs(epoch / yearL);
        epoch = epoch % yearL;
        long days = Math.abs(epoch / dayL);
        epoch = epoch % dayL;
        long hours = Math.abs(epoch / hourL);
        epoch = epoch % hourL;
        long minutes = Math.abs(epoch / minuteL);
        epoch = epoch % minuteL;
        long seconds = Math.abs(epoch / secondL);
        epoch = epoch % secondL;
        long milliseconds = epoch;

        return String.format("%dy %dd %02d:%02d:%02d.%03d",years, days, hours, minutes, seconds, milliseconds);

    }
    //converts a formatted date string to epoch
    public long toEpoch(String date) {
        long epoch;
        try {
            //pull out the numbers. This will force default values for missing elements
            long[] a = breakUpDateString(date);
            //Put it back into a string. Now with default values if nothing was there before
            date = makeDateString(a);
            if(!testDateWithinEpoch(date)) return ConvertError.RANGE_ERROR.getValue();

            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            //Adhere to num days in month, max hour time etc rules
            df.setLenient(false);
            Date sdf = df.parse(date);
            epoch = sdf.getTime();
            if(epoch < 0)  return ConvertError.RANGE_ERROR.getValue();
        }
        catch(ParseException pe) {
            //Date must be in '01/12/1970 01:00:00' format
            //or invalid month/day number. eg 21/52/2010, 30/02/2010
            return ConvertError.PARSE_ERROR.getValue();
        }
        catch(NumberFormatException nfe) {
            //something else wrong with the format
            return ConvertError.PARSE_ERROR.getValue();
        }
        return epoch;
    }

    //Converts an epoch number to a formatted date string
    public String toHuman(long epoch) {
        return new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date (epoch));
    }

    //Converts an array of date values to a formatted String.
    private String makeDateString(long[] values) {
        StringBuilder sb = new StringBuilder();
        //Date values
        sb.append(values[YEAR_LOC]);
        sb.append("/");
        sb.append(values[MONTH_LOC]);
        sb.append("/");
        sb.append(values[DAY_LOC]);
        sb.append(" ");
        //Time values
        sb.append(values[HOUR_LOC]);
        sb.append(":");
        sb.append(values[MINUTE_LOC]);
        sb.append(":");
        sb.append(values[SECOND_LOC]);
        sb.append(".");
        sb.append(values[MILSEC_LOC]);
        return sb.toString();
    }

    //Converts a formatted string to an array of date values
    private long[] breakUpDateString(String date) throws NumberFormatException{
        //order from biggest to smallest
        long[] ia = new long[NUM_TOTAL_FORMAT];

        //Split into time and date
        StringTokenizer st = new StringTokenizer(date," ");
        String dateString;
        String timeString;
        try {
            dateString = st.nextToken();
        }
        catch(NoSuchElementException nsee) {
            //No string at all. Should cause a format exception later
            dateString = "0/0/0";
        }
        try {
            timeString = st.nextToken();
        }
        catch(NoSuchElementException nsee) {
            //If time is absent, set to 0 everything
            timeString = "0:0:0.000";
        }

        //Split the date values
        st = new StringTokenizer(dateString,"/");
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
        st = new StringTokenizer(timeString,":.");
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

    //Compares individual date segments and checks to see if the corresponding date is within the epoch limits
    private boolean testDateWithinEpoch(String date) throws NumberFormatException{
        long[] maxA = new long[NUM_TOTAL_FORMAT];
        //maximum values
        maxA[0] = 292278994; //year
        maxA[1] = 8; //month
        maxA[2] = 17; //day
        maxA[3] = 17; //hour
        maxA[4] = 12; //minute
        maxA[5] = 55; //second
        maxA[6] = 807; //ms
        long[] timeA = breakUpDateString(date);

        //test max values with date values, returns true if less
        //starts with testing the year, if == then test month etc
        return compare(maxA, timeA, 0);
    }

    //Compares values in two arrays. Recursive method
    //Does comparisons in the array order. If a value is greater, return false, no more subsequent values are checked
    //If a value is equal, the next will be checked. If its the last value, return true
    //If a value is less, return true, no more subsequent values are checked
    private boolean compare(long[] maxA, long[] timeA, int place) {
        //If bigger, return false
        if(timeA[place] > maxA[place]) {
            return false;
        }
        //if equal, check the next date segment.
        else if(timeA[place] == maxA[place]) {
            //Exit statement if last segment
            if(place == MILSEC_LOC) {
                return true;
            }
            return compare(maxA, timeA, ++place);
        }
        //If less, return true
        else {
            return true;
        }
    }

}
