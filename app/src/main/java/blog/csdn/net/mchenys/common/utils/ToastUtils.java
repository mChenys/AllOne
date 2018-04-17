package blog.csdn.net.mchenys.common.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import blog.csdn.net.mchenys.ShoppingApplication;

/**
 * Created by mChenys on 2018/4/17.
 */

public class ToastUtils {
    private static Toast toast;

    public synchronized static void showShort(Context context, String msg) {
        if (!StringUtils.isEmpty(msg)) show(context, msg, Toast.LENGTH_SHORT);
    }
    public synchronized static void longShort(Context context, String msg) {
        if (!StringUtils.isEmpty(msg)) show(context, msg, Toast.LENGTH_SHORT);
    }
    public synchronized static void showShort(String msg) {
        if (!StringUtils.isEmpty(msg)) show(ShoppingApplication.mAppContext, msg, Toast.LENGTH_SHORT);
    }
    public synchronized static void show(Context context, String msg, int duration) {
        try {
            if (context == null) return;
            if (toast == null) {
                toast = Toast.makeText(context, msg, duration);
            } else {
                toast.setText(msg);
                toast.setDuration(duration);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void showCenter(Context context, String msg, int duration) {
        try {
            if (context == null) return;
            if (toast == null) {
                toast = Toast.makeText(context, msg, duration);
            } else {
                toast.setText(msg);
                toast.setDuration(duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * onPause()页面被遮挡的时候调用这个方法取消toast
     */
    public static void onActivityPaused() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
