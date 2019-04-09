package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;


/**
 * 原生下拉刷新
 * Created by mChenys on 2018/12/19.
 */
public class AdvanceSwipeRefreshLayout extends SwipeRefreshLayout {
    private OnPreInterceptTouchEventDelegate mOnPreInterceptTouchEventDelegate;
    private int mTouchSlop;
    private Handler mHandler = new Handler();

    public AdvanceSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public AdvanceSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        //避免重复下拉刷新导致动画异常
        return !isRefreshing() && super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    private int lastY, lastX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean disallowIntercept = false; //是否禁止拦截
        boolean isHorizontal = false; //是否水平滚动
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) ev.getY();
                lastX = (int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) Math.abs(ev.getX() - lastX);
                int deltaY = (int) Math.abs(ev.getY() - lastY);
                isHorizontal = deltaX > mTouchSlop && deltaX > deltaY;
                break;
        }
        if (mOnPreInterceptTouchEventDelegate != null) {
            disallowIntercept = mOnPreInterceptTouchEventDelegate.shouldDisallowInterceptTouchEvent(ev);
        }


        boolean intercept = super.onInterceptTouchEvent(ev);
        //   LogUtils.e("cys", "AdvanceSwipeRefreshLayout:intercept=" + intercept + " disallowIntercept:" + disallowIntercept + " isHorizontal:" + isHorizontal);

        if (disallowIntercept || isHorizontal) {
            return false;
        }

        return intercept;
    }

    public void setOnPreInterceptTouchEventDelegate(OnPreInterceptTouchEventDelegate listener) {
        mOnPreInterceptTouchEventDelegate = listener;
    }

    public interface OnPreInterceptTouchEventDelegate {
        /**
         * true:禁止拦截,false:拦截
         *
         * @param ev
         * @return
         */
        boolean shouldDisallowInterceptTouchEvent(MotionEvent ev);
    }

    /**
     * 结束刷新,隐藏刷新头
     */
    public void finisRefresh() {
        finisRefresh(0);
    }

    public void finisRefresh(long delay) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AdvanceSwipeRefreshLayout.super.setRefreshing(false);
            }
        }, delay);
    }

    /**
     * 开始刷新,显示刷新头
     */
    public void startRefresh() {
        if(!isRefreshing())
        super.setRefreshing(true);
    }




}