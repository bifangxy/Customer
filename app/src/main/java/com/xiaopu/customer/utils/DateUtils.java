package com.xiaopu.customer.utils;

import android.text.TextUtils;
import android.util.Log;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Administrator on 2016/3/22 0022.
 */
public class DateUtils {
    /**
     * 拿到当前年份
     *
     * @return int
     */
    public static int getYearInt() {
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 拿到当前年份
     *
     * @return String
     */
    public static String getYearString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        return year;
    }

    /**
     * 拿到年月日
     *
     * @return String
     */
    public static String getYMDString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(date);
        return s;
    }

    /**
     * 拿到月日
     */
    public static String getMDString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String s = sdf.format(date);
        return s;
    }

    /**
     * 拿到年月日（包括小时分钟）
     *
     * @return String
     */
    public static String getYMDHMS(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = sdf.format(date);
        return s;
    }


    /**
     * 拿到年月日(yyyy/MM/dd)
     *
     * @return String
     */
    public static String getYMD1(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String s = sdf.format(date);
        return s;
    }

    /**
     * 拿到年月日(yyyy/MM/dd HH:mm)
     *
     * @return String
     */
    public static String getYMDHm1(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String s = sdf.format(date);
        return s;
    }

    /**
     * 拿到年月日(yyyy年MM月dd日HH点mm分)
     *
     * @return String
     */
    public static String getYMDHm(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分");
        String s = sdf.format(date);
        return s;
    }

    /**
     * 拿到时分秒
     *
     * @return String
     */
    public static String getHMS(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String s = sdf.format(date);
        return s;
    }

    /**
     * 获得星期几
     *
     * @
     */
    public static String getWeek(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 获取时间戳（两时间之差）
     * str字符串的时间不能大于当前时间
     */
    public static long getTimeStamp(String str) {
        Timestamp ts1 = new Timestamp(System.currentTimeMillis());
        Timestamp ts2;
        try {
            ts2 = Timestamp.valueOf(str);
            long timelenth = ts1.getTime() - ts2.getTime();
            return timelenth;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取时间戳
     */
    public static long getTimeStamp1(String str) {
        Timestamp ts1 = Timestamp.valueOf(str);
        long timelenth = ts1.getTime();
        return timelenth;
    }

    /**
     * 两个时间比较大小,如果第一个参数大，则返回true
     * yyyy-MM_dd
     */
    public static boolean compareDate(String start_time, String end_time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dateStart = dateFormat.parse(start_time);
            Date dateEnd = dateFormat.parse(end_time);
            if (dateStart.getTime() > dateEnd.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {

        }
        return true;
    }


    /**
     * 根据年月获取这个月多少天
     */
    public static int getlenthofyearmonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if (isleapyear(year)) {
                    return 29;
                } else {
                    return 28;
                }
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
        }
        return 0;
    }

    private static boolean isleapyear(int year) {
        if (year % 100 == 0) {
            if (year % 400 == 0)
                return true;
        } else {
            if (year % 4 == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 今天几岁了
     *
     * @param date
     * @return
     */
    public static int getYears(String date) {
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        String stryear, strmonth, strday;
        if (isEmpty(date)) {
            return 0;
        } else {
            String[] darr = date.split("-");
            stryear = darr[0];
            strmonth = darr[1];
            strday = darr[2];
            if (year - Integer.parseInt(stryear) <= 0) {
                return 0;
            } else if (Integer.parseInt(strmonth) < month) {
                return year - Integer.parseInt(stryear);
            } else if (Integer.parseInt(strmonth) == month) {
                //月也相同，就判断日期
                if (Integer.parseInt(strday) < day) {
                    return year - Integer.parseInt(stryear);
                } else {
                    return year - Integer.parseInt(stryear) - 1;
                }
            }
            //最后剩下当前月比生日月份小
            else {
                return year - Integer.parseInt(stryear) - 1;
            }
        }
    }

    /**
     * 把年月日的string转成date
     *
     * @param YMD
     * @return
     */
    public static Date YMDToDate(String YMD) {
        DateFormat fmt = new SimpleDateFormat("yyyy年-MM月-dd日");
        try {
            Date date = fmt.parse(YMD);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date YMDToDateTwo(String YMD) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = fmt.parse(YMD);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 是否在i天内，如果是，就可以续费
     *
     * @param start
     * @param end
     * @return
     */
    public static boolean isInsideIDays(int i, Date start, Date end) {
        long time1, time2;
        time1 = start.getTime();
        time2 = end.getTime();
        if (time2 - time1 < i * 86400000) {
            return true;
        }
        return false;
    }

    public static int getDays(Date start, Date end) {
        long time1, time2;
        int days = 0;
        time1 = start.getTime();
        time2 = end.getTime();
        days = Math.round((time2 - time1) / 86400000);
        return days;
    }


    public static long getTimeStampP(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return date.getTime();
        } else {
            return 0;
        }
    }

    /**
     * 获取从2016-01-01到现在的每个日期
     *
     * @return
     */
    public static List<String> getDaydate() {
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        SimpleDateFormat sim = new java.text.SimpleDateFormat("MM.dd");
        dates.add("今天");
        c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
        dates.add("昨天");

        int day = daysBetween(c.getTime(), YMDToDateTwo("2016-01-01"));
        for (int i = 0; i < day; i++) {
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
            String date = sim.format(c.getTime());
            dates.add(date);
        }
        Collections.reverse(dates); // 倒序排列
        return dates;
    }

    public static List<String> getDaydateDetail() {
        List<String> dates = new ArrayList<String>();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        dates.add(TimeUtils.DATE_FORMAT_DATE.format(c.getTime()));
        int day = daysBetween(c.getTime(), YMDToDateTwo("2016-01-01"));
        for (int i = 0; i < day; i++) {
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
            dates.add(TimeUtils.DATE_FORMAT_DATE.format(c.getTime()));
        }
        Collections.reverse(dates); // 倒序排列
        return dates;
    }

    /**
     * 获取2016-01-01到现在每个周
     *
     * @return
     */
    public static List<String> getWeekdate() {
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        SimpleDateFormat sim = new SimpleDateFormat("MM.dd");
        int dayOfweek = c.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < dayOfweek + 7; i++) {
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
        }
        dates.add("本周");
        dates.add("上周");
        int day = daysBetween(c.getTime(), YMDToDateTwo("2016-01-01"));
        String date = "";
        for (int i = 0; i < day; i++) {
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                date = sim.format(c.getTime());
            } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                if (TextUtils.isEmpty(date)) {
                    date = sim.format(c.getTime()) + "-" + sim.format(c.getTime());
                } else {
                    date = sim.format(c.getTime()) + "-" + date;
                }
                dates.add(date);
            }
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
        }
        Collections.reverse(dates); // 倒序排列
        return dates;
    }

    public static List<String> getWeekdateDatail() {
        List<String> dates = new ArrayList<String>();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int day = daysBetween(c.getTime(), YMDToDateTwo("2016-01-01"));
        String date = "";
        for (int i = 0; i < day; i++) {
            if (i == 0 && c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                date = TimeUtils.DATE_FORMAT_DATE.format(c.getTime());
            } else {
                if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    date = TimeUtils.DATE_FORMAT_DATE.format(c.getTime());
                } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    if (TextUtils.isEmpty(date)) {
                        date = TimeUtils.DATE_FORMAT_DATE.format(c.getTime()) + "/" + TimeUtils.DATE_FORMAT_DATE.format(c.getTime());
                    } else {
                        date = TimeUtils.DATE_FORMAT_DATE.format(c.getTime()) + "/" + date;
                    }
                    dates.add(date);
                }
            }
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
        }
        Collections.reverse(dates); // 倒序排列
        return dates;
    }

    public static List<String> getMonthdate() {
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        SimpleDateFormat sim = new SimpleDateFormat("yyyy.MM");
        String date = sim.format(c.getTime());
        dates.add(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int month_count = (year - 2016) * 12 + month;
        for (int i = 0; i < month_count - 1; i++) {
            c.add(Calendar.MONTH, -1);
            date = sim.format(c.getTime());
            dates.add(date);
        }
        Collections.reverse(dates); // 倒序排列
        return dates;
    }


    public static List<String> getSevendate() {
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        for (int i = 0; i < 7; i++) {
            String mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
            String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
            String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH) - i);// 获取当前日份的日期号码
            String date = mMonth + "." + mDay;
            dates.add(date);
        }
        return dates;
    }


    private static int daysBetween(Date now, Date returnDate) {
        Calendar cNow = Calendar.getInstance();
        Calendar cReturnDate = Calendar.getInstance();
        cNow.setTime(now);
        cReturnDate.setTime(returnDate);
        setTimeToMidnight(cNow);
        setTimeToMidnight(cReturnDate);
        long todayMs = cNow.getTimeInMillis();
        long returnMs = cReturnDate.getTimeInMillis();
        long intervalMs = todayMs - returnMs;
        return millisecondsToDays(intervalMs);
    }

    private static int millisecondsToDays(long intervalMs) {
        return (int) (intervalMs / (1000 * 86400));
    }

    private static void setTimeToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

}