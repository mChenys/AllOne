package blog.csdn.net.mchenys.common.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import blog.csdn.net.mchenys.AllOneApplication;

/**
 * 软件盘监听
 */

public class KeyboardMonitor implements ViewTreeObserver.OnGlobalLayoutListener {

    private View mContentView;
    private static final int KEYBORAD_OPEN = 0;
    private static final int KEYBORAD_CLOSE = 1;
    private int keyboardState = KEYBORAD_CLOSE;
    private int screentHight;
    private KeyboardMonitor.KeyboardStateChangeListener listener;
    public int keyboradHeight;

    public KeyboardMonitor(Activity mActivity, KeyboardMonitor.KeyboardStateChangeListener listener) {
        this.listener = listener;
        mContentView = mActivity.findViewById(android.R.id.content);
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        //获取View可见区域的bottom
        screentHight = ScreenUtils.getScreenHeight(AllOneApplication.mAppContext);
        Rect rect = new Rect();
        mContentView.getWindowVisibleDisplayFrame(rect);
        int diff = screentHight - rect.bottom;
        if (diff > 400) {
            keyboradHeight = diff;
            keyboardState = KEYBORAD_OPEN;
        } else {
            keyboardState = KEYBORAD_CLOSE;
        }
        if (listener != null) {
            listener.onKeyboardStateChange(keyboardState == KEYBORAD_OPEN);
        }
    }

    public interface KeyboardStateChangeListener {
        void onKeyboardStateChange(boolean isOpened);
    }
}
