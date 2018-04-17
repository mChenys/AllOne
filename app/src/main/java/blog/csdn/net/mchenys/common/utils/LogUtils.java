package blog.csdn.net.mchenys.common.utils;

import android.util.Log;

public class LogUtils {
    public static boolean DEBUG = false;

    public LogUtils() {
    }

    public static void setLogsDebug(boolean isDebug) {
        DEBUG = isDebug;
    }

    public static void v(String tag, String msg) {
        if(DEBUG) {
            Log.v(tag, msg == null?"null":msg);
        }

    }

    public static void e(String tag, String msg) {
        if(DEBUG) {
            Log.e(tag, msg == null?"null":msg);
        }

    }

    public static void i(String tag, String msg) {
        if(DEBUG) {
            Log.i(tag, msg == null?"null":msg);
        }

    }

    public static void d(String tag, String msg) {
        if(DEBUG) {
            Log.d(tag, msg == null?"null":msg);
        }

    }

    public static void w(String tag, String msg) {
        if(DEBUG) {
            Log.w(tag, msg == null?"null":msg);
        }

    }
}