package blog.csdn.net.mchenys.common.widget.recycleview.refresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.common.utils.LogUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.footer.ShoppingLoadingFooter;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.header.ShoppingRefreshHeader;


/**
 * Created by mChenys on 2017/3/2.
 */
public class RefreshRecyclerView extends RecyclerView {
    private static final String TAG = "log";
    private boolean isLoadingData; //是否正在加载数据
    private boolean isPullingUp; //是否是上拉
    private boolean isNoMore; //是否没有更多
    private boolean isApiScroll;//是否是调用api导致的滚动
    private WrapAdapter mWrapAdapter;
    private static final float DRAG_RATE = 3;
    private BaseRefreshHeader mRefreshHeader;
    private BaseLoaderFooter mLoaderFooter;

    private boolean pullRefreshEnabled = true;
    private boolean loadingMoreEnabled = true;

    private ArrayList<View> mHeaderViews = new ArrayList<>(); //位于下拉刷新头之下的headerview
    private ArrayList<View> mFooterViews = new ArrayList<>(); //位于加载更多头之下的footerview

    //下面的ItemViewType是保留值(ReservedItemViewType),如果用户的adapter与它们重复将会强制抛出异常。不过为了简化,我们检测到重复时对用户的提示是ItemViewType必须小于10000
    private static final int TYPE_REFRESH_HEADER = 10000;//设置一个很大的数字,尽可能避免和用户的adapter冲突
    private static final int HEADER_INIT_INDEX = TYPE_REFRESH_HEADER + 1;
    private static final int TYPE_LOADER_FOOTER = 20000;
    private static final int FOOTER_INIT_INDEX = TYPE_LOADER_FOOTER + 1;

    //每个header必须有不同的type,不然滚动的时候顺序会变化
    private static List<Integer> sHeaderTypes = new ArrayList<>();
    private static List<Integer> sFooterTypes = new ArrayList<>();
    //adapter没有数据的时候显示,类似于listView的emptyView
    private View mEmptyView;
    private AdapterDataObserver mDataObserver = new DataObserver();

    private List<OnPullScrollListener> mOnPullScrollListeners = new ArrayList<>();

    public void setOnPullScrollListener(OnPullScrollListener l) {
        if (null == l) return;
        mOnPullScrollListeners.add(l);

    }

    /**
     * 刷新/加载更多监听
     */
    public interface LoadingListener {

        void onRefresh(Bundle args);

        void onLoadMore();
    }

    private LoadingListener mLoadingListener;

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }


    /**
     * 没有更多监听
     */
    public interface NoMoreListener {
        void onNoMore(boolean first);
    }

    private NoMoreListener mNoMoreListener;

    public void setNoMoreListener(NoMoreListener noMoreListener) {
        mNoMoreListener = noMoreListener;
    }

    public WrapAdapter getWrapAdapter() {
        return mWrapAdapter;
    }

    /**
     * 当脚不可见监听
     */
    public interface OnFooterInvisiableListener {
        void invisiable();
    }

    private OnFooterInvisiableListener mOnFooterInvisibleListener;

    public void setOnFooterInvisibleListener(OnFooterInvisiableListener onFooterInvisibleListener) {
        mOnFooterInvisibleListener = onFooterInvisibleListener;
    }

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    private void init() {
        if (pullRefreshEnabled) {
            if (null == mRefreshHeader) {
                setRefreshHeader(new ShoppingRefreshHeader(getContext()));
            }
        }
        if (loadingMoreEnabled) {
            if (null == mLoaderFooter) {
                setLoaderFooter(new ShoppingLoadingFooter(getContext()));
            }
        }

    }


    /**
     * 设置底部加载中,没有更多的文本
     *
     * @param loading
     * @param noMore
     */
    public void setLoadViewText(String loading, String noMore) {
        if (null == mLoaderFooter) {
            init();
        }
        if (loadingMoreEnabled) {
            mLoaderFooter.setLoadingHint(loading);
            mLoaderFooter.setNoMoreHint(noMore);
        }
    }


    public void setLoaderFooter(final BaseLoaderFooter view) {
        mLoaderFooter = view;
        hideLoaderFooterView();
    }

    public void setRefreshHeader(BaseRefreshHeader refreshHeader) {
        mRefreshHeader = refreshHeader;
    }

    public View getLoaderFooterView() {
        return null == mLoaderFooter ? null : mLoaderFooter.getView();
    }

    public View getRefreshHeaderView() {
        return null == mRefreshHeader ? null : mRefreshHeader.getView();
    }


    /**
     * 没有更多
     *
     * @param noMore
     */
    public void setNoMore(boolean noMore) {
        isLoadingData = false;
        isNoMore = noMore;

        mLoaderFooter.setState(isNoMore ? BaseLoaderFooter.STATE_NOMORE : BaseLoaderFooter.STATE_COMPLETE);

        if (noMore && pullRefreshEnabled && mRefreshHeader.getState() == BaseRefreshHeader.STATE_REFRESHING) {
            mRefreshHeader.refreshComplete(null);
        }
        notifyDataSetChange();

        if (noMore) {
            if (null != mNoMoreListener) {
                mNoMoreListener.onNoMore(true);
            }
            for (OnPullScrollListener l : mOnPullScrollListeners) {
                l.onLoaderNoMore();
            }
        }

    }

    public void setNoMore(boolean noMore, boolean autoScrollLast) {
        setNoMore(noMore);
        if (autoScrollLast) {
            smoothScrollToPosition(mWrapAdapter.getItemCount() - 1);
        }
    }

    public boolean isNoMore() {
        return isNoMore;
    }

    public void notifyDataSetChange() {
        if (null != getAdapter()) {
            getAdapter().notifyDataSetChanged();
        }
        if (null != mWrapAdapter) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 自动刷新
     */
    public void autoRefresh(final Bundle args) {
        scrollToPosition(0);
        if (pullRefreshEnabled && mLoadingListener != null) {
            if (null == mRefreshHeader) {
                init();
            }
            if (mRefreshHeader.getState() != BaseRefreshHeader.STATE_REFRESHING) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshHeader.autoRefresh(new SimpleRefreshAnimatorListener() {
                            @Override
                            public void onStayAnimEnd() {
                                mLoadingListener.onRefresh(args);
                            }

                            @Override
                            public void onAutoPullDownAnim(int deltaY) {
                                for (OnPullScrollListener l : mOnPullScrollListeners) {
                                    l.onPulling(deltaY);
                                }
                            }

                            @Override
                            public void onAutoPullBackAnim(int deltaValue, float currValue, float finalValue) {
                                for (OnPullScrollListener l : mOnPullScrollListeners) {
                                    l.onAutoPullBackAnimating(deltaValue, currValue, finalValue);
                                }
                            }
                        });
                    }
                }, 100);
            }


        }
    }

    /**
     * 自动加载更多
     */
    public void autoLoadMore() {
        if (isNoMore || isLoadingData) return;
        isLoadingData = true;
        mLoaderFooter.setState(BaseLoaderFooter.STATE_LOADING);
        mLoadingListener.onLoadMore();
        smoothScrollToPosition(mWrapAdapter.getItemCount() - 1);
    }

    /**
     * 隐藏加载更多
     */
    public void hideLoaderFooterView() {
        isLoadingData = false;
        if (null != mLoaderFooter)
            mLoaderFooter.setState(BaseLoaderFooter.STATE_COMPLETE);
        notifyDataSetChange();
        for (OnPullScrollListener l : mOnPullScrollListeners) {
            l.onLoaderHidden();
        }
    }

    /**
     * 重置
     */
    public void reset() {
        setNoMore(false);
        loadMoreComplete();
        refreshComplete();
    }

    /**
     * 刷新完成
     */
    public void refreshComplete() {
        if (null != mRefreshHeader) {
            mRefreshHeader.refreshComplete(new SimpleRefreshAnimatorListener() {
                @Override
                public void onAutoPullBackAnimEnd() {
                    setNoMore(false);
                    for (OnPullScrollListener l : mOnPullScrollListeners) {
                        l.onFinishRefresh();
                    }
                }
            });
        } else {
            setNoMore(false);
            for (OnPullScrollListener l : mOnPullScrollListeners) {
                l.onFinishRefresh();
            }
        }
    }

    /**
     * 加载更多完成
     */
    public void loadMoreComplete() {
        isApiScroll = false;
        hideLoaderFooterView();
        for (OnPullScrollListener l : mOnPullScrollListeners) {
            l.onFinishLoader();
        }

    }


    @Override
    public void scrollBy(int x, int y) {
        isApiScroll = true;
        super.scrollBy(x, y);

    }

    @Override
    public void scrollTo(int x, int y) {
        isApiScroll = true;
        super.scrollTo(x, y);
    }

    @Override
    public void scrollToPosition(int position) {
        isApiScroll = true;
        super.scrollToPosition(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        isApiScroll = true;
        super.smoothScrollToPosition(position);
    }

    public void smoothScrollToTop() {
        smoothScrollToPosition(0, true, 0);
    }

    /**
     * 平滑滚动
     *
     * @param position
     * @param stickTop 是否支持置顶功能
     * @param dy       距离recycleview顶部的距离
     */
    public void smoothScrollToPosition(int position, boolean stickTop, int dy) {
        isApiScroll = true;
        if (stickTop && getLayoutManager() instanceof LinearLayoutManager) {
            int offsetY = -dy;
            if (mWrapAdapter.hasLoaderFooter()) {
                if (position == mWrapAdapter.getItemCount() - 1 && isNoMore) {
                    offsetY += mLoaderFooter.getOriginHeight();//如果置顶的是脚,不需要看到没有更多提示,因此要计算偏移量,减去"没有更多"提示的高度

                }
            }
            MySmoothTopScroller smoothTopScroller = new MySmoothTopScroller(getContext(), offsetY);
            smoothTopScroller.setTargetPosition(position);
            getLayoutManager().startSmoothScroll(smoothTopScroller);
        } else {
            super.smoothScrollToPosition(position);
        }

    }


    //实现置顶的功能
    private class MySmoothTopScroller extends LinearSmoothScroller {
        int offsetY;


        public MySmoothTopScroller(Context context, int offsetY) {
            super(context);
            this.offsetY = offsetY;
        }

        /**
         * @param viewStart      RecyclerView的top位置
         * @param viewEnd        RecyclerView的bottom位置
         * @param boxStart       Item的top位置
         * @param boxEnd         Item的bottom位置
         * @param snapPreference 判断滑动方向的标识（The edge which the view should snap to when entering the visible
         *                       area. One of {@link #SNAP_TO_START}, {@link #SNAP_TO_END} or
         *                       {@link #SNAP_TO_END}.）
         * @return 移动偏移量, 返回负数则item会从顶部移除, 正数就是距离顶部的距离
         */
        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return boxStart - viewStart - offsetY;// 这里是关键，得到的就是置顶的偏移量
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            // 此函数计算滚动dx的距离需要多久，当要滚动的距离很大时，比如说52000，
            // 经测试，系统会多次调用此函数，每10000距离调一次，所以总的滚动时间
            // 是多次调用此函数返回的时间的和，所以修改每次调用该函数时返回的时间的
            // 大小就可以影响滚动需要的总时间，可以直接修改些函数的返回值，也可以修改
            // dx的值，这里暂定使用后者.
            // (See LinearSmoothScroller.TARGET_SEEK_SCROLL_DISTANCE_PX)
            if (dx > 3000) {
                dx = 3000;
            }
            return super.calculateTimeForScrolling(dx);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return ((LinearLayoutManager) getLayoutManager()).computeScrollVectorForPosition(targetPosition);
        }
    }


    //控制飞滑的速度
    private double flingScale = 0.7;

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= flingScale;
        return super.fling(velocityX, velocityY);
    }

    public void setPullRefreshEnabled(boolean enabled) {
        pullRefreshEnabled = enabled;
        if (!enabled) {
            mRefreshHeader = null;
        }
    }

    public void setLoadingMoreEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
        if (!enabled && null != mLoaderFooter) {
            hideLoaderFooterView();
        }
    }

    /**
     * 强制停止RecyclerView滑动方法
     */
    public void forceStopRecyclerViewScroll() {
        dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_CANCEL, 0, 0, 0));
    }

    public void setRefreshImageView(int resId) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setRefreshImageView(resId);
        }
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        mDataObserver.onChanged();
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public interface Callback {
        void finish();
    }

    public void simulateClick(float x, float y, final Callback callback) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 300;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
        onTouchEvent(downEvent);
        onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != callback) callback.finish();
            }
        }, 300);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        init();
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
        super.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                for (OnPullScrollListener l : mOnPullScrollListeners) {
                    l.onScrollStateChanged(recyclerView, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //  LogUtils.e("cww","recyclerView"+recyclerView.getScrollY());
                isPullingUp = dy > 0;
                for (OnPullScrollListener l : mOnPullScrollListeners) {
                    int loaderFooterViewTop = mWrapAdapter.hasLoaderFooter() ? mLoaderFooter.getView().getTop() : 0;
                    int firstHeaderViewTop = mHeaderViews.isEmpty() ? 0 : mHeaderViews.get(0).getTop();
                    l.onScrolled(recyclerView, dx, dy, firstHeaderViewTop, loaderFooterViewTop);
                }
                if (shouldFooterInvisible()) {
                    if (null != mOnFooterInvisibleListener)
                        mOnFooterInvisibleListener.invisiable();
                }
                int firstVisibleItemPosition = getFirstVisiablePosition();
                int lastVisibleItemPosition = getLastVisibleItemPosition();
                int lastDataCount = getLastDataPosition();
                LogUtils.e(TAG, "firstVisibleItemPosition:" + firstVisibleItemPosition + " lastVisibleItemPosition:" + lastVisibleItemPosition +
                        " lastDataCount:" + lastDataCount + " isApiScroll:" + isApiScroll);
                for (OnPullScrollListener l : mOnPullScrollListeners) {
                    l.onScrolled(firstVisibleItemPosition, lastVisibleItemPosition, lastDataCount, isApiScroll, isPullingUp, isLoadingData);
                }

            }
        });
    }


    //避免用户自己调用getAdapter() 引起的ClassCastException
    @Override
    public Adapter getAdapter() {
        if (mWrapAdapter != null)
            return mWrapAdapter.getOriginalAdapter();
        else
            return null;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (mWrapAdapter != null) {
            if (layout instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) layout);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (mWrapAdapter.isHeader(position) || mWrapAdapter.isFooter(position) ||
                                mWrapAdapter.isLoaderFooter(position) || mWrapAdapter.isRefreshHeader(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (null != mLoaderFooter) {
            mLoaderFooter.setState(BaseLoaderFooter.STATE_SCROLL);
            if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled) {
                if (shouldFooterInvisible()) {
                    if (null != mOnFooterInvisibleListener)
                        mOnFooterInvisibleListener.invisiable();
                }
                if (isPullingUp) {
                    if (isNoMore) {
                        mLoaderFooter.setState(BaseLoaderFooter.STATE_NOMORE);
                        if (null != mNoMoreListener) mNoMoreListener.onNoMore(false);
                    } else if (mWrapAdapter.getItemCount() > 0) {
                        if (null != mRefreshHeader && mRefreshHeader.getState() >= BaseRefreshHeader.STATE_REFRESHING) {
                            return;
                        }
                        if (getLastVisibleItemPosition() == getLastDataPosition() && !isApiScroll) { //等于最后一条数据是显示加载更多
                            isLoadingData = true;
                            mLoaderFooter.setState(BaseLoaderFooter.STATE_LOADING);
                            smoothScrollToPosition(getLastDataPosition() + 1);
                            LogUtils.e(TAG, "1: onScrollStateChanged: isApiScroll " + isApiScroll);
                            mLoadingListener.onLoadMore();
                            LogUtils.e(TAG, "2: onScrollStateChanged: isApiScroll " + isApiScroll);

                        }

                    }
                }
                LogUtils.e(TAG, "3: onScrollStateChanged: isApiScroll " + isApiScroll);
            }
        }

    }

    private float mLastY = -1;
    private float mLastX = -1;
    private float deltaY;
    private float deltaX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mLastY = ev.getY();
            mLastX = ev.getX();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (mLastY == -1) {
                mLastY = ev.getY();  //针对ACTION_DOWN事件没有接受到的情况
                mLastX = ev.getX();
            }
            deltaY = ev.getY() - mLastY;
            deltaX = ev.getX() - mLastX;
            isPullingUp = deltaY < 0;
            mLastY = ev.getY();
            mLastX = ev.getX();
        }
        return super.dispatchTouchEvent(ev);
    }

/*      @Override
       public boolean onInterceptTouchEvent(MotionEvent e) {
           if (e.getAction() == MotionEvent.ACTION_DOWN) {
               return false;
           } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
               return Math.abs(deltaY) > Math.abs(deltaX);
           }
           return false;
       }*/

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                isApiScroll = false;
                if (pullRefreshEnabled)
                    LogUtils.d(TAG, ">>>>>>非刷新状态====" + mRefreshHeader.getState());
                LogUtils.d(TAG, ">>>>>>isOnTop() ====" + isOnTop());
                if (isOnTop() /*&& appbarState == AppBarStateChangeListener.State.EXPANDED*/
                        && mRefreshHeader.getState() != BaseRefreshHeader.STATE_REFRESHING) {
                    //LogUtils.d(TAG, ">>>>>>非刷新状态");
                    mRefreshHeader.onMove(deltaY / DRAG_RATE);
                    //通知滑动距离
                    for (OnPullScrollListener l : mOnPullScrollListeners) {
                        l.onPulling((int) (deltaY / DRAG_RATE));
                    }
                    if (mRefreshHeader.getVisibleHeight() > 0 && mRefreshHeader.getState() < BaseRefreshHeader.STATE_REFRESHING) {
                        return true;
                    }
                } else {
                    LogUtils.d(TAG, ">>>>>>新状态");  //手动推上去
                    if (pullRefreshEnabled) {
                        mRefreshHeader.manuallyPushUp(deltaY / DRAG_RATE);
                    }
                    if (pullRefreshEnabled) {
                        for (OnPullScrollListener l : mOnPullScrollListeners) {
                            l.onRefreshStayMove(mRefreshHeader.getVisibleHeight(), deltaY > 0);
                        }
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() /*&& appbarState == AppBarStateChangeListener.State.EXPANDED*/) {
                    mRefreshHeader.releaseAction(new SimpleRefreshAnimatorListener() {
                        @Override
                        public void onStayAnimEnd() {
                            if (null != mLoadingListener) {
                                mLoadingListener.onRefresh(null);
                            }
                        }

                        @Override
                        public void onCancelPullAnimEnd() {
                            for (OnPullScrollListener l : mOnPullScrollListeners) {
                                l.onPullCancel();
                            }
                        }

                        @Override
                        public void onAutoPullBackAnim(int deltaValue, float currValue, float finalValue) {
                            for (OnPullScrollListener l : mOnPullScrollListeners) {
                                l.onAutoPullBackAnimating(deltaValue, currValue, finalValue);
                            }
                        }
                    });
                } else if (pullRefreshEnabled && null != mRefreshHeader && mRefreshHeader.getState() == BaseRefreshHeader.STATE_REFRESHING) {
                    if (null != mLoadingListener) {
                        mLoadingListener.onRefresh(null);
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 获取第一个可见的item位置
     *
     * @return
     */
    public int getFirstVisiablePosition() {
        LayoutManager layoutManager = getLayoutManager();
        int firstVisibleItemPosition;
        if (layoutManager instanceof GridLayoutManager) {
            firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(into);
            firstVisibleItemPosition = findMin(into);
        } else {
            firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
        return firstVisibleItemPosition;
    }

    public int getFooterFistVisiblePosition() {
        return mWrapAdapter.getItemCount() - mFooterViews.size();
    }

    public int getExceptFooterCount() {
        return mWrapAdapter.getItemCount() - mWrapAdapter.getFootersCount();
    }

    private boolean shouldFooterInvisible() {
        return mFooterViews.size() > 0 && getLastVisibleItemPosition() < getFooterFistVisiblePosition();
    }

    /**
     * 最后一条数据的位置,包括头,不包括脚
     *
     * @return
     */
    public int getLastDataPosition() {
        return mWrapAdapter.getItemCount() - mWrapAdapter.getFootersCount() - 1;
    }

    /**
     * 获取可见列表内最后一个item的位置
     *
     * @return
     */
    public int getLastVisibleItemPosition() {
        int lastVisibleItemPosition;
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
            lastVisibleItemPosition = findMax(into);
        } else {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        return lastVisibleItemPosition;
    }


    public boolean isBottom() {
        return !canScrollVertically(1);
    }


    private int findMin(int[] firstPositions) {
        int min = firstPositions[0];
        for (int value : firstPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }


    public boolean isFooterVisible() {
        return getLastVisibleItemPosition() >= getFooterFistVisiblePosition();
    }

    public boolean isLoaderFooterVisible() {
        return getLastVisibleItemPosition() >= getExceptFooterCount();
    }

    public void smoothScrollToBottom() {
        smoothScrollToPosition(mWrapAdapter.getItemCount() - 1);
    }

    public void smoothScrollToFooter() {
        smoothScrollToPosition(getFooterFistVisiblePosition());
    }

    private boolean isOnTop() {
        //头部不在屏幕时是没有attach到RecycleView上,所以拿不到parent
        if (pullRefreshEnabled && null != mRefreshHeader && mRefreshHeader.getView().getParent() != null) {
            return true;
        } else {
            return false;
        }
    }

    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
            if (mWrapAdapter != null && mEmptyView != null) {
                int emptyCount = mWrapAdapter.getHeadersCount() + mWrapAdapter.getFootersCount();
                if (mWrapAdapter.getItemCount() == emptyCount) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    //判断一个type是否为HeaderType
    private boolean isHeaderType(int itemViewType) {
        return mHeaderViews.size() > 0 && sHeaderTypes.contains(itemViewType);
    }

    private boolean isFooterType(int itemViewType) {
        return mFooterViews.size() > 0 && sFooterTypes.contains(itemViewType);
    }

    //判断是否是RecyclerView保留的itemViewType
    private boolean isReservedItemViewType(int itemViewType) {
        if (itemViewType == TYPE_REFRESH_HEADER ||
                itemViewType == TYPE_LOADER_FOOTER ||
                sHeaderTypes.contains(itemViewType) ||
                sFooterTypes.contains(itemViewType)) {
            return true;
        } else {
            return false;
        }
    }

    //根据header的ViewType判断是哪个header
    private View getHeaderViewByType(int itemType) {
        if (!isHeaderType(itemType)) {
            return null;
        }
        return mHeaderViews.get(itemType - HEADER_INIT_INDEX);
    }

    private View getFooterViewByType(int itemType) {
        if (!isFooterType(itemType)) {
            return null;
        }
        return mFooterViews.get(itemType - FOOTER_INIT_INDEX);
    }

    /**
     * 添加头部
     *
     * @param view 如果遇到头部View的宽度不能全屏的问题,需要更换根View的布局为RelativeLayout
     */
    public void addHeaderView(View view) {
        view.setTag(mHeaderViews.size());
        sHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size());
        mHeaderViews.add(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    //添加额外脚
    public void addFooterView(View view) {
        view.setTag(mFooterViews.size());
        sFooterTypes.add(FOOTER_INIT_INDEX + mFooterViews.size());
        mFooterViews.add(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void removeFooterView(View view) {
        sFooterTypes.remove((int) view.getTag());
        mFooterViews.remove(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public View getHeaderView(int index) {
        return index < mHeaderViews.size() ? mHeaderViews.get(index) : null;
    }

    public int getHeadersCount() {
        return null == mWrapAdapter ? 0 : mWrapAdapter.getHeadersCount();
    }

    private boolean hasFooterViewVisiable() {
        boolean visiable = false;
        for (View view : mFooterViews) {
            if (view.isShown() || view.getLayoutParams().height > 0) {
                visiable = true;
                break;
            }
        }
        return visiable;
    }

    public final class WrapAdapter extends Adapter<ViewHolder> {

        private Adapter adapter;

        public WrapAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        public Adapter getOriginalAdapter() {
            return this.adapter;
        }

        public boolean isRefreshHeader(int position) {
            return hasRefreshHeader() && position == 0;
        }

        public boolean hasRefreshHeader() {
            return pullRefreshEnabled && null != mRefreshHeader;
        }

        /**
         * 是否是除了刷新头的其他头部
         *
         * @param position
         * @return
         */
        public boolean isHeader(int position) {
            int startPosition = hasRefreshHeader() ? 1 : 0;
            int endPosition = hasRefreshHeader() ? mHeaderViews.size() : mHeaderViews.size() - 1;
            return position >= startPosition && position <= endPosition;
        }

        public boolean isLoaderFooter(int position) {
            return hasLoaderFooter() && position == getItemCount() - mFooterViews.size() - 1;
        }


        public boolean hasLoaderFooter() {
            return loadingMoreEnabled && null != mLoaderFooter;
        }

        /**
         * 是否是除了加载脚的其他脚
         *
         * @param position
         * @return
         */
        public boolean isFooter(int position) {
            int startPosition = getItemCount() - mFooterViews.size();
            int endPosition = getItemCount() - 1;
            return position >= startPosition && position <= endPosition;
        }


        public int getHeadersCount() {
            if (pullRefreshEnabled) {
                return mHeaderViews.size() + 1;
            }
            return mHeaderViews.size();
        }

        public int getFootersCount() {
            if (loadingMoreEnabled) {
                return mFooterViews.size() + 1;
            }
            return mFooterViews.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(mRefreshHeader.getView());
            } else if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (viewType == TYPE_LOADER_FOOTER) {
                return new SimpleViewHolder(mLoaderFooter.getView());
            } else if (isFooterType(viewType)) {
                return new SimpleViewHolder(getFooterViewByType(viewType));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position) || isRefreshHeader(position)) {
                return;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        // some times we need to override this
        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else {
                if (isHeader(position) || isRefreshHeader(position)) {
                    return;
                }
                int adjPosition = position - getHeadersCount();
                int adapterCount;
                if (adapter != null) {
                    adapterCount = adapter.getItemCount();
                    if (adjPosition < adapterCount) {
                        adapter.onBindViewHolder(holder, adjPosition, payloads);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                return getHeadersCount() + adapter.getItemCount() + getFootersCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }


        @Override
        public int getItemViewType(int position) {
            int adjPosition = position - getHeadersCount();
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER; //刷新头的类型
            }
            if (isHeader(position)) {
                position = hasRefreshHeader() ? position - 1 : position;
                return sHeaderTypes.get(position); //自定义头的类型
            }
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    int type = adapter.getItemViewType(adjPosition); //数据源的类型,10000以下的数字
                    if (isReservedItemViewType(type)) {
                        throw new IllegalStateException("RefreshRecyclerView require itemViewType in adapter should be less than " + TYPE_REFRESH_HEADER);
                    }
                    return type;
                }

                if (isLoaderFooter(position)) {
                    return TYPE_LOADER_FOOTER; //加载更多脚的类型
                }

                if (isFooter(position)) {
                    position = hasLoaderFooter() ? adjPosition - adapterCount - 1 : adjPosition - adapterCount;
                    return sFooterTypes.get(position); //自定义脚的类型
                }
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount()) {
                int adjPosition = position - getHeadersCount();
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position) || isLoaderFooter(position) || isRefreshHeader(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            int position = holder.getLayoutPosition();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(position) || isFooter(position) || isRefreshHeader(position) || isLoaderFooter(position))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


    /**
     * 水平/垂直分割线
     */
    public class DividerItemDecoration extends ItemDecoration {

        private Drawable mDivider;
        private int mOrientation;

        public DividerItemDecoration(Drawable divider) {
            mDivider = divider;
        }

        //绘制分割线
        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, State state) {
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                drawHorizontalDividers(canvas, parent);
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                drawVerticalDividers(canvas, parent);
            }
        }

        //设置item间距留出分割线位置
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) <= mWrapAdapter.getHeadersCount()) {
                return;
            }
            mOrientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                outRect.left = mDivider.getIntrinsicWidth();
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                outRect.top = mDivider.getIntrinsicHeight();
            }
        }

        private void drawHorizontalDividers(Canvas canvas, RecyclerView parent) {
            int parentTop = parent.getPaddingTop();
            int parentBottom = parent.getHeight() - parent.getPaddingBottom();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                LayoutParams params = (LayoutParams) child.getLayoutParams();

                int parentLeft = child.getRight() + params.rightMargin;
                int parentRight = parentLeft + mDivider.getIntrinsicWidth();

                mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
                mDivider.draw(canvas);
            }
        }

        private void drawVerticalDividers(Canvas canvas, RecyclerView parent) {
            int parentLeft = parent.getPaddingLeft();
            int parentRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                LayoutParams params = (LayoutParams) child.getLayoutParams();

                int parentTop = child.getBottom() + params.bottomMargin;
                int parentBottom = parentTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
                mDivider.draw(canvas);
            }
        }
    }

    /**
     * 设置item的间隙
     */
    public class SpacesItemDecoration extends ItemDecoration {

        private int leftSpace;
        private int topSpace;
        private int rightSpace;
        private int bottomSpace;

        /**
         * 所有间距传dip值
         *
         * @param leftSpace
         * @param topSpace
         * @param rightSpace
         * @param bottomSpace
         */
        public SpacesItemDecoration(int leftSpace, int topSpace, int rightSpace, int bottomSpace) {
            float density = getContext().getResources().getDisplayMetrics().density;
            this.leftSpace = (int) (density * leftSpace);
            this.topSpace = (int) (density * topSpace);
            this.rightSpace = (int) (density * rightSpace);
            this.bottomSpace = (int) (density * bottomSpace);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int childPosition = parent.getChildAdapterPosition(view);
            int itemStartPosition = mWrapAdapter.getHeadersCount();
            int itemEndPosition = mWrapAdapter.getItemCount() - mWrapAdapter.getFootersCount() - 1;
            if (childPosition < itemStartPosition || childPosition > itemEndPosition) {
                //头部和脚部区域
                outRect.left = 0;
                outRect.top = 0;
                outRect.right = 0;
                outRect.bottom = 0;
                return;
            } else {
                //item区域
                LayoutManager lp = getLayoutManager();
                if (lp instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) lp;
                    if (itemStartPosition >= 2) {

                    } else {
                        if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                            outRect.left = 0;
                            outRect.right = 0;
                            outRect.bottom = 0;
                            outRect.top = topSpace;
                            setPadding(leftSpace, 0, rightSpace, bottomSpace);
                        } else {
                            outRect.top = 0;
                            outRect.bottom = 0;
                            outRect.right = 0;
                            outRect.left = leftSpace;
                            setPadding(0, topSpace, rightSpace, bottomSpace);
                        }
                    }

                } else if (lp instanceof StaggeredGridLayoutManager) { //网格布局
                    if (itemStartPosition >= 2) {

                    } else {
                        outRect.bottom = 0;
                        outRect.right = 0;
                        outRect.top = topSpace;
                        outRect.left = leftSpace;
                        setPadding(0, 0, rightSpace, bottomSpace);
                    }

                }
            }

        }
    }

    /**        有用到可以解开,引入design包
     private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;

     @Override protected void onAttachedToWindow() {
     super.onAttachedToWindow();
     //解决和CollapsingToolbarLayout冲突的问题
     AppBarLayout appBarLayout = null;
     ViewParent p = getParent();
     while (p != null) {
     if (p instanceof CoordinatorLayout) {
     break;
     }
     p = p.getParent();
     }
     if (p instanceof CoordinatorLayout) {
     CoordinatorLayout coordinatorLayout = (CoordinatorLayout) p;
     final int childCount = coordinatorLayout.getChildCount();
     for (int i = childCount - 1; i >= 0; i--) {
     final View child = coordinatorLayout.getChildAt(i);
     if (child instanceof AppBarLayout) {
     appBarLayout = (AppBarLayout) child;
     break;
     }
     }
     if (appBarLayout != null) {
     appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
     @Override public void onStateChanged(AppBarLayout appBarLayout, State state) {
     appbarState = state;
     }
     });
     }
     }
     }

     public abstract static class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

     public enum State {
     EXPANDED,
     COLLAPSED,
     IDLE
     }

     private State mCurrentState = State.IDLE;

     @Override public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
     if (i == 0) {
     if (mCurrentState != State.EXPANDED) {
     onStateChanged(appBarLayout, State.EXPANDED);
     }
     mCurrentState = State.EXPANDED;
     } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
     if (mCurrentState != State.COLLAPSED) {
     onStateChanged(appBarLayout, State.COLLAPSED);
     }
     mCurrentState = State.COLLAPSED;
     } else {
     if (mCurrentState != State.IDLE) {
     onStateChanged(appBarLayout, State.IDLE);
     }
     mCurrentState = State.IDLE;
     }
     }

     public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
     }
     **/
}