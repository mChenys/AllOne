package blog.csdn.net.mchenys.common.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.util.Timer;
import java.util.TimerTask;

public class SoftInputUtils {
    public SoftInputUtils() {
    }

    public static void closedSoftInput(Activity acitivity) {
        if(null != acitivity && acitivity.getCurrentFocus() != null && null != acitivity.getWindow()) {
            acitivity.getWindow().getDecorView().clearFocus();
            InputMethodManager im = (InputMethodManager)acitivity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(null != im) {
                im.hideSoftInputFromWindow(acitivity.getWindow().getDecorView().getWindowToken(), 2);
            }
        }

    }

    public static void openSoftInput(final Activity acitivity) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)acitivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, 2);
            }
        }, 300L);
    }

    public static void openSoftInputImmediately(Activity acitivity) {
        InputMethodManager imm = (InputMethodManager)acitivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 2);
    }
}