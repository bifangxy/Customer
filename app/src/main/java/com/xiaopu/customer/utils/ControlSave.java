package com.xiaopu.customer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by Xieying on 2016/6/18 0014.
 * 功能：SharePreference 保存，删除，取出
 */
public class ControlSave {
    /**
     * 类标签
     */
    public static final String LOG_TAG = "ControlSave";

    public static final String SharedPreferencesName = "ControlSharedPreferences";

    /*
    保存
     */
    public static void save(@NonNull Context context, @NonNull String key, String value) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }
    /*
       取出
        */
    public static String read(@NonNull Context context, @NonNull String key, String defValue) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(key, defValue);
    }
    /*
       删除
        */
    public static void delete(@NonNull Context context, @NonNull String key) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.commit();
    }


}
