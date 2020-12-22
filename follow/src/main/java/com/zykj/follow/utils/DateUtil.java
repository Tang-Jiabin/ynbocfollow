package com.zykj.follow.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具
 *
 * @author : J.Tang
 * @version : v1.0
 * @email : seven_tjb@163.com
 * @date : 2018-10-29
 **/

public class DateUtil {


    public static Date getSpecifiedDate(Date date, int days) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //增加天数，负数则为减少天数
        calendar.add(Calendar.DATE, days);
        date = calendar.getTime();
        return date;

    }

    public static Date getSpecifiedMinute(Date date, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //增加分钟数，负数则为减少分钟数
        calendar.add(Calendar.MINUTE, minute);
        date = calendar.getTime();
        return date;

    }


    public static String getTodayFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static String getFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date getTodayZeroHours() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }


    public static int longOfTwoDate(Date first, Date second) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(first);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(second);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        //同一年
        if (year1 != year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                //闰年
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    timeDistance += 366;
                } else {
                    //不是闰年
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }

    public static int difference(Date startDate,Date endDate){
        LocalDateTime ldtStartDate = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime ldtEndDate = LocalDateTime.ofInstant(startDate.toInstant(),ZoneId.systemDefault());
        long daysDiff = ChronoUnit.DAYS.between(ldtStartDate, ldtEndDate);
        return (int)daysDiff;
    }



}
