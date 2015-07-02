package com.rhodes.bsdiffdemo.utils;

import android.util.Log;

public class Logger {
    private static final String TAG = Logger.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static void log(String... s) {
        if (!DEBUG) return;//DEBUG

        String log = "";
        for (int i = 0; i < s.length; i++) {
            log += s[i];
        }

        Log.e(TAG, log);
    }
}
