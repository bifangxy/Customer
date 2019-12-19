package com.xiaopu.customer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/5/13 0013.
 */
public class StringUtils {

    public static double toDouble(String str) {
        double d = Double.valueOf(str).doubleValue();
        return d;
    }

    public static int toInteger(String str) {
        int i = Integer.valueOf(str).intValue();
        return i;
    }

    /**
     * 获取字符串中的数字 如“2016 年” ->  "2016"
     */
    public static String getNumInString(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


}
