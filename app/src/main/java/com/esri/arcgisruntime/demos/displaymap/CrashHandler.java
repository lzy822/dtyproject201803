package com.esri.arcgisruntime.demos.displaymap;

import android.util.Log;


public class CrashHandler implements Thread.UncaughtExceptionHandler{
    private static final String TAG = "MainActivity";
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.w(TAG, "uncaughtException: " + "出大问题了！");
    }
}
