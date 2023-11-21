package com.github.nodeepshit.model;

import android.util.Log;

public class Logdog {

    private static Listener listener;
    private static final String TAG = "Logdog";

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

    public static void tip(String message) {
        Log.d(TAG, message);
        listener.onTraceRead('T' + message);
    }

    public static void result(String message) {
        Log.d(TAG, message);
        listener.onTraceRead('S' + message);
    }

    public static void response(String message) {
        Log.d(TAG, message);
        listener.onTraceRead('R' + message);
    }

    public static void update(String message) {
        Log.d(TAG, message);
        listener.onTraceRead('U' + message);
    }

    public static void log(TraceLevel level, String message) {
        switch(level) {
            case RESULT:
                result(message);
                break;
            case RESPONSE:
                response(message);
                break;
            case TIP:
                tip(message);
                break;
            case DEBUG:
                debug(message);
                break;
            case WARNING:
                warning(message);
                break;
            case ERROR:
                error(message);
                break;

            default:
                info(message);
        }
    }

    interface Listener {
        void onTraceRead(String logcatTrace);
    }
}
