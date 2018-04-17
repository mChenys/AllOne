package blog.csdn.net.mchenys.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 页面跳转工具类
 * Created by mChenys on 2017/12/27.
 */

public class JumpUtils {

    public static void toActivity(Activity activity, Intent intent) {
        if (activity == null) {
            return;
        }
        if (null != intent) {
            activity.startActivity(intent);
        }
    }

    public static void toActivityForResult(Activity activity, Intent intent, int requestCode) {
        if (activity == null) {
            return;
        }
        if (null != intent) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static void startActivity(Activity activity, Class<?> cls) {
        startActivity(activity, cls, null);
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> cls, int requestCode) {
        startActivityForResult(activity, cls, null, requestCode);
    }

    public static void startActivity(Activity activity, Class<?> cls, Bundle bundle) {
        if (null != activity && null != cls) {
            Intent intent = new Intent(activity, cls);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            activity.startActivity(intent);
        }
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> cls, Bundle bundle, int requestCode) {
        if (null != activity && null != cls) {
            Intent intent = new Intent(activity, cls);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            activity.startActivityForResult(intent, requestCode);
        }
    }


}
