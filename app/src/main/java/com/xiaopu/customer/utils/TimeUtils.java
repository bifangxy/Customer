package com.xiaopu.customer.utils;

/**
 * Created by Administrator on 2016/6/6 0006.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TimeUtils
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_Point = new SimpleDateFormat("yy.MM.dd HH:mm");
    public static final SimpleDateFormat DATE_FORMAT_Point2 = new SimpleDateFormat("yy,MM,dd,HH,mm,ss");

    public static final SimpleDateFormat DATA_FORMAT_NO_YEAR = new SimpleDateFormat("MM-dd HH:mm");
    public static final SimpleDateFormat DATA_FORMAT_HOUR = new SimpleDateFormat("yyyy-MM-dd HH");

    public static final SimpleDateFormat DATE_FORMAT_NO_DAY = new SimpleDateFormat("yyyy.MM");

    public static final SimpleDateFormat DATE_FORMAT_HOUR_MINUTE_SECOND = new SimpleDateFormat("HH小时mm分钟ss秒");


    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_DATE);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static String DateToString(Date date) {
        return DATE_FORMAT_DATE.format(date);
    }

    public static String DateToStringDatail(Date date) {
        return DEFAULT_DATE_FORMAT.format(date);
    }

    public static String DateToStringSimpleDateFormat(Date date, SimpleDateFormat formate) {
        return formate.format(date);
    }

    public static Date StringToDateSimpeDateFormat(String date) {
        try {
            return DEFAULT_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据用户生日计算年龄
     */
    public static int getAgeByBirthday(Date birthday) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthday)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }
        return age;
    }

    public static String getEndTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = TimeUtils.DateToStringSimpleDateFormat(date, format);
        String currentTime = TimeUtils.DateToStringSimpleDateFormat(new Date(), format);
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dt1 = df.parse(time);
            Date dt2 = df.parse(currentTime);
            long time1 = dt1.getTime();
            long time2 = dt2.getTime();
            long diff;
            diff = time1 - time2;
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);

        } catch (Exception e) {

        }
        return day + "天" + hour + "小时" + min + "分钟";
    }


    public static String getBandTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        //获取整点数据需要传入的时间
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String time = year + "," + month + "," + day + "," + hour + "," + minute + "," + second;
        return time;
    }


    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }

}
