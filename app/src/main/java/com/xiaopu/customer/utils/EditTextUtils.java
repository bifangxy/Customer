package com.xiaopu.customer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Administrator on 2016/3/29 0029.
 */
public class EditTextUtils {
    public static boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(147)|(18[0-9])|(19[0-9])|(17[6-8]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

    String str = "^[1-9][0-9]{5}$";

    /**
     * 判断邮编
     */
    public static boolean isZipNO(String zipString) {
        String str = "^[1-9][0-9]{5}$";
        return Pattern.compile(str).matcher(zipString).matches();
    }

    /**
     * 判断邮箱是否合法
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判断是否是银联银行卡号（银联都是19位数字）
     */
    public static boolean isBankCard(String bankcard) {
        Pattern p = Pattern.compile("^\\d{19}$");
        Matcher m = p.matcher(bankcard);
        return m.matches();
    }

    /**
     * 判断是否是15或者18位身份证
     */
    public static boolean isIdCard(String id) {
        Pattern p = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|[X|x])$");
        Matcher m = p.matcher(id);
        return m.matches();
    }

    /**
     * 判断是否是中英文的姓名
     */
    public static boolean isName(String name) {
        Pattern p = Pattern.compile("^([\\u4e00-\\u9fa5]+|([a-zA-Z]+\\s?)+)$");
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * 判断给定的密码是否为空，如果不为空，再判断给定的密码是不是6到15位，如果正确，则返回true，如果不正确，则返回false
     *
     * @param password
     * @return
     */
    public static boolean isPassword(CharSequence password) {
        if (isEmpty(password)) {
            T.showShort("请输入密码");
            return false;
        }
        if (password.length() < 6 || password.length() > 15) {
            T.showShort("密码长度为6到15位");
            return false;
        } else {
            return true;
        }
    }


    /**
     * 判断用户名是否合法
     *
     * @param username
     * @return
     */
    public static boolean isUserName(CharSequence username) {
        if (isEmpty(username)) {
            ToastUtils.showErrorMsg("请输入用户名");
            return false;
        }
        if (username.length() <= 8 && username.length() >= 2) {
            return true;
        } else {
            ToastUtils.showErrorMsg("用户名长度为2-8");
            return false;
        }
    }

    /**
     * 产品建议是否合法
     *
     * @param str
     * @return
     */
    public static boolean isSuggest(String str) {
        Pattern p = Pattern.compile("[a-zA-Z0-9\\u4e00-\\u9fa5,，.。?？;；:：!！(（)）《<》>…\"“”‘’  、]+");

        Matcher m = p.matcher(str);

        return m.matches();
    }

    /**
     * 地址、简介之类是否合法
     *
     * @param str
     * @return
     */
    public static boolean isW(String str) {
        Pattern p = Pattern.compile("[a-zA-Z0-9\\u4e00-\\u9fa5]+");

        Matcher m = p.matcher(str);

        return m.matches();
    }
}
