package com.ltzk.mbsf.utils;

import android.util.Log;
import com.ltzk.mbsf.BuildConfig;

/**
 * Created by JinJie on 2020/5/31
 */
public final class Logger {

    private static final String TAG = "--->";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private Logger() {

    }

    public static void d(String value) {
        if (DEBUG) {
            Log.d(TAG, value);
        }
    }

    public static void e(String value) {
        if (DEBUG) {
            Log.e(TAG, value);
        }
    }

    public static void e(Exception value) {
        if (DEBUG && value != null) {
            Log.e(TAG, value.getMessage());
        }
    }

    public static void i(String value) {
        if (DEBUG) {
            Log.i(TAG, value);
        }
    }

    public static void w(String value) {
        if (DEBUG) {
            Log.w(TAG, value);
        }
    }
}