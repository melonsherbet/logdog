package com.accrux.console.model;

import android.util.Log;

public class Console {

    private static Listener listener;
    private static final String TAG = "Console";

    /**
     * Configures a listener to be notified with new traces read from the application logcat.
     *
     * @param listener the Logcat listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static void debug(String message) {
        Log.d(TAG, message);
        listener.onTraceRead('D' + message);
    }

    public static void info(String message) {
        Log.i(TAG, message);
        listener.onTraceRead('I' + message);
    }

    public static void warning(String message) {
        Log.w(TAG, message);
        listener.onTraceRead('W' + message);
    }

    public static void error(String message) {
        Log.e(TAG, message);
        listener.onTraceRead('E' + message);
    }

    public static void write(String message) {
        Log.d(TAG, message);
        listener.onTraceRead('D' + message);
    }

    interface Listener {
        void onTraceRead(String logcatTrace);
    }
}
