/*
 * Copyright (c) 2016.
 * 公历转农历
 */

package com.xiaopu.customer.utils;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/29 0029.
 */
public class GregorianToLunarUtils {
    /**
     * 未来10年 农历每月天数打表，包括闰月
     * 1-12列 : -1表示小月 29 天， 1表示大月 30 天
     * 13列 ：0表示空， + 表示大月， - 表示小月
     */
    private static final Integer[][] lunarMap = {
            //闰月 1  2   3   4   5   6   7   8   9  10   11  12
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},//2016
            {6, -1, 1, -1, 1, -1, -1, -1, 1, -1, 1, 1, 1},//2017
            {0, -1, 1, -1, 1, -1, -1, 1, -1, 1, -1, 1, 1},//2018
            {0, 1, -1, 1, -1, 1, -1, -1, 1, -1, -1, 1, 1},//2019
            {-4, -1, 1, 1, 1, 1, -1, -1, 1, -1, 1, -1, 1},//2020
            {0, -1, 1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1},//2021
            {0, 1, -1, 1, -1, 1, 1, -1, 1, -1, 1, -1, 1},//2022
            {-2, -1, 1, -1, 1, 1, -1, 1, 1, -1, 1, -1, 1},//2023
            {0, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, 1, -1},//2024
            {-6, 1, -1, 1, -1, -1, 1, 1, -1, 1, 1, 1, -1},//2025
    };
    private static final String standardDate = "2016-11-29";
    private static final String testNowDate = "2017-01-28";

    public GregorianToLunarUtils() {

    }

    /**
     * 计算间隔天数
     */
    private int intervalDay() {
        Date date = new Date();
        long nowTime = date.getTime();
        /*long nowTime = 0;*/
        long standardTime = 0;
        try {
            /*nowTime = TimeUtils.DATE_FORMAT_DATE.parse(testNowDate).getTime();*/
            standardTime = TimeUtils.DATE_FORMAT_DATE.parse(standardDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int intervalDay = (int) ((nowTime - standardTime) / (1000 * 60 * 60 * 24)) + 1;  //1000毫秒*60分钟*60秒*24小时 = 天
        return intervalDay;
    }

    private int dayOfMonth(int big_smoll) {
        int dayOfMonth;
        switch (big_smoll) {
            case 1:
                dayOfMonth = 30;
                break;
            case -1:
                dayOfMonth = 29;
                break;
            default:
                dayOfMonth = 0;
        }
        return dayOfMonth;
    }

    /**
     * 计算相隔多天后，得出日期
     */
    public String getLunar() {
        int intervalDay = intervalDay();
        String strLunarDate;
        int toolDay = 0;
        int lunarYear = 2016;
        int lunarMonth = 11;
        int lunarDay = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 1; j < 13; j++) {

                if (lunarMap[i][j] != 0) {

                    if (lunarMap[i][0] > 0 && j == lunarMap[i][0] + 1) {
                        toolDay += 30;
                        if (intervalDay - toolDay <= 0) {
                            lunarDay = 30 + (intervalDay - toolDay);
                            lunarMonth--;
                            break;
                        } else {
                            toolDay += dayOfMonth(lunarMap[i][j]);
                            if (intervalDay - toolDay <= 0) {
                                lunarDay = dayOfMonth(lunarMap[i][j]) + (intervalDay - toolDay);
                                break;
                            } else {
                                lunarMonth++;
                                if (lunarMonth == 13) {
                                    lunarMonth = 1;
                                    lunarYear++;
                                }
                            }
                        }
                    } else if (lunarMap[i][0] < 0 && j == Math.abs(lunarMap[i][0]) + 1) {
                        toolDay += 29;
                        if (intervalDay - toolDay <= 0) {
                            lunarDay = 29 + (intervalDay - toolDay);
                            lunarMonth--;
                            break;
                        } else {
                            toolDay += dayOfMonth(lunarMap[i][j]);
                            if (intervalDay - toolDay <= 0) {
                                lunarDay = dayOfMonth(lunarMap[i][j]) + (intervalDay - toolDay);
                                break;
                            } else {
                                lunarMonth++;
                                if (lunarMonth == 13) {
                                    lunarMonth = 1;
                                    lunarYear++;
                                }
                            }
                        }
                    } else {
                        toolDay += dayOfMonth(lunarMap[i][j]);
                        if (intervalDay - toolDay <= 0) {
                            lunarDay = dayOfMonth(lunarMap[i][j]) + (intervalDay - toolDay);
                            break;
                        } else {
                            lunarMonth++;
                            if (lunarMonth == 13) {
                                lunarMonth = 1;
                                lunarYear++;
                            }
                        }
                    }
                }
            }
            if (intervalDay - toolDay <= 0) {
                break;
            }
        }
        String strLunarYear = String.format("%d", lunarYear);
        String strLunarMonth = String.format("%02d", lunarMonth);
        String strLunarDay = String.format("%02d", lunarDay);
        strLunarDate = "-" + strLunarMonth + "-" + strLunarDay + "L";
        return strLunarDate;
    }

}
