package com.zeroq6.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public class MyDateUtils {

    public final static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

    public final static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static String yyyyMMdd(Date date) {
        return yyyyMMdd.format(date);
    }

    public static String yyyyMMddHHmmss(Date date) {
        return yyyyMMddHHmmss.format(date);
    }

    public static String format(Date date) {
        if (null == date) {
            return null;
        }
        return yyyyMMddHHmmss(date);
    }

    public static String format(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        return new SimpleDateFormat(pattern, Locale.US).format(date);
    }

    public static Date parse(String dateString, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Date toDate(Date date) {
        try {
            return yyyyMMdd.parse(yyyyMMdd(date));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar toCalendarCopy(Calendar calendar) {
        Calendar re = Calendar.getInstance();
        re.setTime(calendar.getTime());
        return re;
    }


    public static int daysBetween(Calendar start, Calendar end) {
        return daysBetween(start.getTime(), end.getTime());
    }

    public static int daysBetween(Date startDate, Date endDate) {
        long end = toDate(endDate).getTime();
        long start = toDate(startDate).getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }


}
