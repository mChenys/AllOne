package blog.csdn.net.mchenys.common.widget.layout;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by mChenys on 2018/6/5.
 */

public class RefreshLayout extends LinearLayout {

    private int lastX, lastY;
    private ImageView mRefreshView;
    private int refreshHeight;
    private Scrollable mScrollableView; //可滚动的View
    private Scroller mScroller;
    private AnimationDrawable mAnimationDrawable;
    private Handler mHandler = new Handler();
    private boolean isRefreshing;

    public RefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRefreshView = (ImageView) getChildAt(0);
        mScrollableView = (Scrollable) getChildAt(1);
        mRefreshView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (refreshHeight <= 0) {
                    refreshHeight = mRefreshView.getHeight();

                }
            }
        });
        mAnimationDrawable = (AnimationDrawable) mRefreshView.getDrawable();

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldIntercept = false;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = (int) ev.getRawX();
            lastY = (int) ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            int y = (int) ev.getRawY();
            int x = (int) ev.getRawX();
            int dx = x - lastX;
            int dy = y - lastY;
            lastX = x;
            lastY = y;
            boolean isVerticalScroll = Math.abs(dy) > Math.abs(dx);
            boolean isPullingDown = dy > 0;
            //向下拦截
            shouldIntercept = (mScrollableView.canIntercept(dy) && isPullingDown && isVerticalScroll) || (getScrollY() < 0);

        }
        if (shouldIntercept) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("cys", "RefreshLayout->onTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int y = (int) (event.getRawY());
                int dy = y - lastY;
                lastY = y;
                int ratioDy = (int) (dy * 0.5f + 0.5f);
                scrollBy(0, -ratioDy);
                if (Math.abs(getScrollY()) >= refreshHeight) {
                    mAnimationDrawable.start();
                }
                break;
            case MotionEvent.ACTION_UP:
                //完全划出后松手
                if (Math.abs(getScrollY()) >= refreshHeight) {
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY() - refreshHeight, 1000);
                    if (null != mOnRefreshListener && !isRefreshing) {
                        //startScroll未完成时scrollTo调用是无效的
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!mScroller.computeScrollOffset()) {
                                    Log.e("cys", "开始刷新");
                                    mOnRefreshListener.onRefresh();
                                    isRefreshing = true;
                                }

                            }
                        }, 1010);

                    }
                } else {
                    //未完全划出松手
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 1000);
                    mAnimationDrawable.stop();
                }
                invalidate();
                break;
        }

        return true;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void onComplete() {
        scrollTo(0, 0);
        Log.e("cys", "刷新完毕");
        isRefreshing = false;
        mAnimationDrawable.stop();
    }
}
