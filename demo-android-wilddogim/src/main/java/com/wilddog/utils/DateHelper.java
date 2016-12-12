package com.wilddog.utils;

import android.annotation.SuppressLint;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressLint("SimpleDateFormat")
public class DateHelper {
    public final static long DIS_INTERVAL = 300 * 1000;

    public static String GetStringFormat(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        java.util.Date dt = new Date(millis);
        return sdf.format(dt);
    }

    public String transMillis2DateString(String millis) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(millis));
        return formatter.format(calendar.getTime());
    }

    public static boolean LongInterval(long current, long last) {
        return (current - last) > DIS_INTERVAL ? true : false;
    }

}
