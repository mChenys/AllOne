package blog.csdn.net.mchenys.common.widget.layout;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class AdvanceSwipeRefreshLayout extends SwipeRefreshLayout {
    private OnPreInterceptTouchEventDelegate mOnPreInterceptTouchEventDelegate;

    public AdvanceSwipeRefreshLayout(Context context) {
        super(context);
    }

    public AdvanceSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean disallowIntercept = false;
        if (mOnPreInterceptTouchEventDelegate != null)
            disallowIntercept = mOnPreInterceptTouchEventDelegate.shouldDisallowInterceptTouchEvent(ev);

        if (disallowIntercept) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setOnPreInterceptTouchEventDelegate(OnPreInterceptTouchEventDelegate listener) {
        mOnPreInterceptTouchEventDelegate = listener;
    }

    public interface OnPreInterceptTouchEventDelegate {
        //true:不拦截,false:拦截
        boolean shouldDisallowInterceptTouchEvent(MotionEvent ev);
    }

    public void setRefreshing(long delay) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                AdvanceSwipeRefreshLayout.super.setRefreshing(false);
            }
        }, delay);
    }
}