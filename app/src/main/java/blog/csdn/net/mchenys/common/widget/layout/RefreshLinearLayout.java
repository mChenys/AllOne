package blog.csdn.net.mchenys.common.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import blog.csdn.net.mchenys.common.widget.recycleview.refresh.BaseRefreshHeader;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.SimpleRefreshAnimatorListener;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.header.ShoppingRefreshHeader;


/**
 * 下拉刷新LinearLayout
 * Created by mChenys on 2018/1/5.
 */

public class RefreshLinearLayout extends LinearLayout {
    private float mLastY = -1; // 保存y坐标
    private BaseRefreshHeader mRefreshHeader;
    private OnScrollListener mOnScrollListener;
    private OnRefreshListener mOnRefreshListener;
    private final static float OFFSET_RADIO = 1.8f;

    /**
     * 刷新监听
     */
    public interface OnRefreshListener {
        void onRefresh();
    }

    /**
     * 滚动监听
     */
    public interface OnScrollListener {
        void onScrollChanged(int x, int y, int oldx, int oldy);
    }

    public RefreshLinearLayout(Context context) {
        this(context, null);
    }

    public RefreshLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
    }


    /**
     * 设置自定义刷新头
     *
     * @param refreshHeader
     */
    public void setRefreshHeader(BaseRefreshHeader refreshHeader) {
        mRefreshHeader = refreshHeader;
        if (null != mRefreshHeader) {
            LayoutParams layoutParams = new
                    LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            addView(mRefreshHeader.getView(), layoutParams);
        }
    }

    /**
     * 添加位于刷新头之上的view
     *
     * @param view
     */
    public void addSubHeader(View view) {
        LayoutParams layoutParams = new
                LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        addView(view, 0, layoutParams);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (null == mRefreshHeader) {
            setRefreshHeader(new ShoppingRefreshHeader(getContext()));
        }
        super.addView(child, params);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getScrollY() == 0
                        && mRefreshHeader != null && (mRefreshHeader.getVisibleHeight() > 0 || deltaY > 0)) {
                    mRefreshHeader.onMove(deltaY / OFFSET_RADIO);
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    ev.setLocation(-1, -1);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getScrollY() < 5 && mRefreshHeader != null) {
                    mRefreshHeader.releaseAction(new SimpleRefreshAnimatorListener() {
                        @Override
                        public void onStayAnimEnd() {
                            if (null != mOnRefreshListener) {
                                mOnRefreshListener.onRefresh();
                            }
                        }
                    });
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setRefreshImageView(int resId) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setRefreshImageView(resId);
        }
    }

    /**
     * 刷新完成
     */
    public void refreshComplete() {
        if (null != mRefreshHeader) {
            mRefreshHeader.refreshComplete(null);
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    public void setOnScrollListener(OnScrollListener scrollViewListener) {
        mOnScrollListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollChanged(l, t, oldl, oldt);
        }

    }
}
