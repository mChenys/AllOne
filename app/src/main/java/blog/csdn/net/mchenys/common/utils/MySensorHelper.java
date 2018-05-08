package blog.csdn.net.mchenys.common.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.OrientationEventListener;

/**
 * 重力传感监听
 * Created by mChenys on 2018/4/23.
 */
public class MySensorHelper {
    private static final String TAG = MySensorHelper.class.getSimpleName();
    private OrientationEventListener mLandOrientationListener;
    private OrientationEventListener mPortOrientationListener;

    public interface Callback {
        void onOrientationChange(int orientation);
    }

    public MySensorHelper(final Activity activity, final Callback callback) {
        this.mLandOrientationListener = new OrientationEventListener(activity, 3) {
            public void onOrientationChanged(int orientation) {
                Log.d(MySensorHelper.TAG, "mLandOrientationListener");
                if (orientation < 100 && orientation > 80 || orientation < 280 && orientation > 260) {
                    Log.e(MySensorHelper.TAG, "转到了横屏");
                    if (callback != null) {
                        Log.e(MySensorHelper.TAG, "转到了横屏##################");
                        callback.onOrientationChange(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }

            }
        };
        this.mPortOrientationListener = new OrientationEventListener(activity, 3) {
            public void onOrientationChanged(int orientation) {
                Log.w(MySensorHelper.TAG, "mPortOrientationListener");
                if (orientation < 10 || orientation > 350 || orientation < 190 && orientation > 170) {
                    Log.e(MySensorHelper.TAG, "转到了竖屏");
                    if (callback != null) {
                        Log.e(MySensorHelper.TAG, "转到了竖屏!!!!!!!!!!!!!!!!!!!!!!");
                        callback.onOrientationChange(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }

            }
        };
        this.enable();
    }

    //禁用切换屏幕的开关
    public void disable() {
        Log.e(TAG, "disable");
        this.mPortOrientationListener.disable();
        this.mLandOrientationListener.disable();
    }

    //开启横竖屏切换的开关
    public void enable() {
        this.mPortOrientationListener.enable();
        this.mLandOrientationListener.enable();
    }
}