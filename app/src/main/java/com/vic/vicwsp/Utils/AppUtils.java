package com.vic.vicwsp.Utils;


import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppUtils {


    /****************************************
     * Get "String" to check is it null or not
     ****************************************/
    public static boolean isSet(String string) {
        if (string != null && string.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Calendar convertStrDateToCalendar(String strDate) {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            cal.setTime(sdf.parse(strDate));
            return cal;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String formatDateFromDate(String dt) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d", Locale.ENGLISH);
            Date date = simpleDateFormat.parse(dt);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String msgDate = sdf.format(date);
            return msgDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public static String formatDateFromDateTime(String dt) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            Date date = simpleDateFormat.parse(dt);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            String msgDate = sdf.format(date);
            return msgDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public static String removeCommaFromString(String dt) {
        if (isSet(dt))
            if (dt.contains(","))
                return dt.replace(',', '.');
        return dt;
    }

    public static float convertStringToFloat(String dt) {
        removeCommaFromString(dt);
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        Number parsedNumber = null;
        try {
            parsedNumber = nf.parse(dt);
            return parsedNumber.floatValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double convertStringToDouble(String dt) {
        removeCommaFromString(dt);
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        Number parsedNumber = null;
        try {
            parsedNumber = nf.parse(dt);
            return parsedNumber.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}
