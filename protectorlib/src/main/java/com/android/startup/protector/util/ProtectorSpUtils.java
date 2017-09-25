package com.android.startup.protector.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.startup.protector.Protector;

import static java.security.AccessController.getContext;

/**
 * Created by liuzhao on 2017/9/22.
 */
public class ProtectorSpUtils {

    private static String NAME = "startup_protector";

    public static SharedPreferences getSharedPreference() {
        return Protector.getInstance().getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static boolean putString(String key, String value) {
        return getSharedPreference().edit().putString(key, value).commit();
    }

    public static boolean putInt(String key, int value) {
        return getSharedPreference().edit().putInt(key, value).commit();
    }


    public static boolean putFloat(String key, float value) {
        return getSharedPreference().edit().putFloat(key, value).commit();
    }


    public static boolean putLong(String key, long value) {
        return getSharedPreference().edit().putLong(key, value).commit();
    }

    public static boolean putBoolean(String key, Boolean value) {
        return getSharedPreference().edit().putBoolean(key, value).commit();
    }

    public static String getString(String key) {
        return getSharedPreference().getString(key, "");
    }

    public static String getString(String key, String defValue) {
        return getSharedPreference().getString(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return getSharedPreference().getInt(key, defValue);
    }

    public static float getFloat(String key, Float defValue) {
        return getSharedPreference().getFloat(key, defValue);
    }

    public static boolean getBoolean(String key, Boolean defValue) {
        return getSharedPreference().getBoolean(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return getSharedPreference().getLong(key, defValue);
    }

}
