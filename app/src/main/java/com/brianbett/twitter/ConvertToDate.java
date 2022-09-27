package com.brianbett.twitter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ConvertToDate {
    private static Date convertToDate(String mongodbTimeStamp){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsedDate = null;
        try {
            parsedDate=simpleDateFormat.parse(mongodbTimeStamp);
        }catch (ParseException e) {
            Log.e("Parse exception", e.getMessage());
        }
        return parsedDate;
    }
    static String getTime(String mongoDBTimestamp){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        Date date=convertToDate(mongoDBTimestamp);
        return dateFormat.format(date);
    }
    static String getDate(String mongoDBTimestamp){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
        Date date=convertToDate(mongoDBTimestamp);
        return dateFormat.format(date);
    }
    static String getTimeAgo( String mongoDBTimestamp){
        Date date=convertToDate(mongoDBTimestamp);

        return DateUtils.getRelativeTimeSpanString(date.getTime(),System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS).toString();
//        return DateUtils.getRelativeDateTimeString(context,date.getTime(),DateUtils.MINUTE_IN_MILLIS,DateUtils.WEEK_IN_MILLIS,1).toString();

    }

    static String getFullMonthFormat(String mongoDBTimestamp){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd,yyyy", Locale.ENGLISH);
        Date date=convertToDate(mongoDBTimestamp);
        return dateFormat.format(date);
    }



}
