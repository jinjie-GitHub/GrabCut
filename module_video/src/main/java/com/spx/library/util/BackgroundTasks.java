package com.spx.library.util;

import android.os.Handler;
import android.os.Looper;

public class BackgroundTasks {

    private BackgroundTasks() {

    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    public boolean postDelayed(Runnable r, long delayMillis) {
        return mHandler.postDelayed(r, delayMillis);
    }

    public Handler getHandler() {
        return mHandler;
    }

    private static BackgroundTasks instance;

    public static BackgroundTasks getInstance() {
        if (instance == null) {
            synchronized (BackgroundTasks.class) {
                if (instance == null) {
                    instance = new BackgroundTasks();
                }
            }
        }
        return instance;
    }
}