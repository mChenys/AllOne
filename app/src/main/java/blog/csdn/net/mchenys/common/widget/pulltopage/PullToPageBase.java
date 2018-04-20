package blog.csdn.net.mchenys.common.widget.pulltopage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import blog.csdn.net.mchenys.R;

/**
 * 公用分页组件,支持各种view
 *
 * @param <T>
 */
public abstract class PullToPageBase<T extends View> extends LinearLayout {
    // ===========================================================
    // Constants
    // ===========================================================

    static final float FRICTION = 2.0f;
    // 下拉刷新
    static final int PULL_TO_REFRESH = 0x0;
    // 释放刷新
    static final int RELEASE_TO_REFRESH = 0x1;
    // 正在刷新
    static final int REFRESHING = 0x2;
    // 手动刷新中
    static final int MANUAL_REFRESHING = 0x3;

    //模式
    public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
    public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
    public static final int MODE_BOTH = 0x3;

    // 下拉类型
    public static final String PULL_UP = "up"; // 上拉
    public static final String PULL_DOWN = "down";// 下拉
    private static String PULL_TYPE = "other"; // 下拉类型

    // ===========================================================
    // Fields
    // ===========================================================

    private int touchSlop;

    private float initialMotionY;
    private float lastMotionX;
    private float lastMotionY;
    private boolean isBeingDragged = false;

    private int state = PULL_TO_REFRESH;
    private int mode = MODE_PULL_DOWN_TO_REFRESH;
    private int currentMode;

    private boolean disableScrollingWhileRefreshing = true;

    public T refreshableView;
    private boolean isPullToPageEnabled = true;

    private ILoadingLayout headerLayout;
    private ILoadingLayout footerLayout;
    private int headerHeight;

    private final Handler handler = new Handler();

    private OnPageListener onPageListener;

    private SmoothScrollRunnable currentSmoothScrollRunnable;

    final class SmoothScrollRunnable implements Runnable {

        // 动画持续时间
        static final int ANIMATION_DURATION_MS = 260;
        // 动画帧速
        static final int ANIMATION_FPS = 1000 / 60;
        // 动画插补器，可使动画加速、减缓、重复
        private final Interpolator interpolator;
        private final int scrollToY;
        private final int scrollFromY;
        private final Handler handler;

        private boolean continueRunning = true;
        private long startTime = -1;
        private int currentY = -1;

        public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
            this.handler = handler;
            this.scrollFromY = fromY;
            this.scrollToY = toY;
            this.interpolator = new AccelerateDecelerateInterpolator();
        }

        @Override
        public void run() {
            /**
             * Only set startTime if this is the first time we're starting, else
             * actually calculate the Y delta 如果是第一次操作下拉刷新则设置开始时间，否则计算Y方向的变动值
             */
            if (startTime == -1) {
                startTime = System.currentTimeMillis();
            } else {

                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                long normalizedTime = (1000 * (System.currentTimeMillis() - startTime)) / ANIMATION_DURATION_MS;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

                final int deltaY = Math.round((scrollFromY - scrollToY)
                        * interpolator.getInterpolation(normalizedTime / 1000f));
                this.currentY = scrollFromY - deltaY;
                setHeaderScroll(currentY);
            }

            // If we're not at the target Y, keep going...
            if (continueRunning && scrollToY != currentY) {
                // 每ANIMATION_FPS毫秒执行一次run方法
                handler.postDelayed(this, ANIMATION_FPS);
            }
        }

        public void stop() {
            this.continueRunning = false;
            this.handler.removeCallbacks(this);
        }
    }

    // 下拉刷新
    private String pullDownLabel = "下拉翻页";
    private String pullUpLabel = "上拉翻页";
    // 正在加载
    private String refreshingLabel = "正在加载";
    // 松手后刷新
    private String releaseLabel = "释放翻页";
    // ===========================================================
    // Constructors
    // ===========================================================

    public PullToPageBase(Context context) {
        super(context);
        init(context, null);
    }

    public PullToPageBase(Context context, int mode) {
        super(context);
        this.mode = mode;
        init(context, null);
    }

    public PullToPageBase(Context context, int mode, AttributeSet attrs) {
        super(context, attrs);
        this.mode = mode;
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PullToPageBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        touchSlop = ViewConfiguration.getTouchSlop();
        // Refreshable View
        // By passing the attrs, we can add ListView/GridView params via XML
        refreshableView = this.createRefreshableView(context, attrs);
        this.addRefreshableView(refreshableView);

        // Styleables from XML
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefresh);
        if (a.hasValue(R.styleable.PullToRefresh_model)) {
            mode = a.getInteger(R.styleable.PullToRefresh_model, MODE_PULL_DOWN_TO_REFRESH);
        }
        if (a.hasValue(R.styleable.PullToRefresh_pheaderBackground)) {
            this.setBackgroundResource(a.getResourceId(R.styleable.PullToRefresh_pheaderBackground, Color.WHITE));
        }
        if (a.hasValue(R.styleable.PullToRefresh_adapterViewBackground)) {
            refreshableView.setBackgroundResource(a.getResourceId(R.styleable.PullToRefresh_adapterViewBackground, Color.WHITE));
        }
        a.recycle();

        //根据模式初始化头和脚
        if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
            headerLayout = new LoadingLayout(getContext(), MODE_PULL_DOWN_TO_REFRESH, releaseLabel,
                    pullDownLabel, refreshingLabel);
            addHeaderView(headerLayout);
        }
        if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
            footerLayout = new LoadingLayout(context, MODE_PULL_UP_TO_REFRESH, releaseLabel, pullUpLabel,
                    refreshingLabel);
            addFooterView(footerLayout);
        }



        // Hide loading Views
        switch (mode) {
            case MODE_BOTH:
                setPadding(0, -headerHeight, 0, -headerHeight);
                break;
            case MODE_PULL_UP_TO_REFRESH:
                setPadding(0, 0, 0, -headerHeight);
                break;
            case MODE_PULL_DOWN_TO_REFRESH:
            default:
                setPadding(0, -headerHeight, 0, 0);
                break;
        }

        // If we're not using MODE_BOTH, then just set currentMode to current
        // mode
        if (mode != MODE_BOTH) {
            currentMode = mode;
        }
    }

    public void addHeaderView(ILoadingLayout headerLayout) {
        if (null != headerLayout) {
            this.headerLayout = headerLayout;
            View header = headerLayout.getView();
            // 以指定的参数添加下拉刷新view到当前LinearLayout中
            addView(header, 0, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            measureView(header);
            // 获取测量高度
            headerHeight = header.getMeasuredHeight();
        }

    }

    public void addFooterView(ILoadingLayout footerLayout) {
        if (null != footerLayout) {
            this.footerLayout = footerLayout;
            View footer =footerLayout.getView();
            addView(footer, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            measureView(footer);
            headerHeight = footer.getMeasuredHeight();
        }

    }
    // ===========================================================
    // Getter & Setter
    // ===========================================================

    /**
     * Get the Wrapped Loadable View. Anything returned here has already been
     * added to the content view.
     *
     * @return The View which is currently wrapped
     */
    public final T getLoadableView() {
        return refreshableView;
    }

    /**
     * Whether Pull-to-Page is enabled
     *
     * @return enabled
     */
    public final boolean isPullToPageEnabled() {
        return isPullToPageEnabled;
    }

    /**
     * Returns whether the widget has disabled scrolling on the Refreshable View
     * while refreshing.
     *
     * @return true if the widget has disabled scrolling while refreshing
     */
    public final boolean isDisableScrollingWhileRefreshing() {
        return disableScrollingWhileRefreshing;
    }

    /**
     * Returns whether the Widget is currently in the Refreshing state
     *
     * @return true if the Widget is currently refreshing
     */
    public final boolean isRefreshing() {
        return state == REFRESHING || state == MANUAL_REFRESHING;
    }

    /**
     * By default the Widget disabled scrolling on the Refreshable View while
     * refreshing. This method can change this behaviour.
     *
     * @param disableScrollingWhileRefreshing - true if you want to disable scrolling while refreshing
     */
    public final void setDisableScrollingWhileRefreshing(boolean disableScrollingWhileRefreshing) {
        this.disableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
    }

    /**
     * Mark the current Refresh as complete. Will Reset the UI and hide the
     * Refreshing View
     */
    public final void onRefreshComplete() {
        if (state != PULL_TO_REFRESH) {
            // 控制正在刷新显示时间为1000ms
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetHeader();
                }
            }, 500);
        }
    }

    /**
     * Set OnRefreshListener for the Widget
     *
     * @param listener - Listener to be used when the Widget is set to Refresh
     */
    public final void setOnRefreshListener(OnPageListener listener) {
        onPageListener = listener;
    }

    /**
     * A mutator to enable/disable Pull-to-Refresh for the current View
     *
     * @param enable Whether Pull-To-Refresh should be used
     */
    public final void setPullToRefreshEnabled(boolean enable) {
        this.isPullToPageEnabled = enable;
    }


    public final void autoRefresh() {
        this.autoRefresh(true);
    }

    /**
     * Sets the Widget to be in the refresh state. The UI will be updated to
     * show the 'Refreshing' view.
     *
     * @param doScroll - true if you want to force a scroll to the Refreshing view.
     */
    public final void autoRefresh(boolean doScroll) {
        if (!isRefreshing()) {
            setRefreshingInternal(doScroll);
            state = MANUAL_REFRESHING;
        }
    }

    public final boolean hasPullFromTop() {
        return currentMode != MODE_PULL_UP_TO_REFRESH;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        if (!isPullToPageEnabled) {
            return false;
        }

        if (isRefreshing() && disableScrollingWhileRefreshing) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE: {
                if (isBeingDragged) {
                    lastMotionY = event.getY();
                    this.pullEvent();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPull()) {
                    lastMotionY = initialMotionY = event.getY();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (isBeingDragged) {
                    isBeingDragged = false;

                    if (state == RELEASE_TO_REFRESH && null != onPageListener) {
                        setRefreshingInternal(true);
                        if (currentMode == MODE_PULL_DOWN_TO_REFRESH) {
                            PULL_TYPE = PULL_DOWN;
                        } else if (currentMode == MODE_PULL_UP_TO_REFRESH) {
                            PULL_TYPE = PULL_UP;
                        }
                        onPageListener.onPage(PULL_TYPE);
                    } else {
                        if (state == PULL_TO_REFRESH) {
                            smoothScrollTo(0);
                        } else {
                            smoothScrollTo(-headerHeight);
                        }
                    }
                    return true;
                }
                break;
            }
        }

        return false;
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        if (onPageListener == null) {
            return false;
        }

        if (!isPullToPageEnabled) {
            return false;
        }

        if (isRefreshing() && disableScrollingWhileRefreshing) {
            return true;
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            isBeingDragged = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && isBeingDragged) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                if (isReadyForPull()) {

                    final float y = event.getY();
                    final float dy = y - lastMotionY;
                    final float yDiff = Math.abs(dy);
                    final float xDiff = Math.abs(event.getX() - lastMotionX);

                    if (yDiff > touchSlop && yDiff > xDiff) {
                        if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) && dy >= 0.0001f
                                && isReadyForPullDown()) {
                            lastMotionY = y;
                            isBeingDragged = true;
                            if (mode == MODE_BOTH) {
                                if (onPageListener != null) {
                                    onPageListener.initTitle(PULL_DOWN);
                                }
                                headerLayout.setSecondTip(getDownTitle());
                                headerLayout.startPull();
                                currentMode = MODE_PULL_DOWN_TO_REFRESH;
                            }
                        } else if ((mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) && dy <= 0.0001f
                                && isReadyForPullUp()) {
                            lastMotionY = y;
                            isBeingDragged = true;
                            if (mode == MODE_BOTH) {
                                if (onPageListener != null) {
                                    onPageListener.initTitle(PULL_UP);
                                }
                                //   footerLayout.setRefreshTip(getUpLoading());
                                footerLayout.setSecondTip(getUpTitle());
                                //    footerLayout.setPullTip("上拉翻页");
                                footerLayout.startPull();
                                currentMode = MODE_PULL_UP_TO_REFRESH;
                            }
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPull()) {
                    lastMotionY = initialMotionY = event.getY();
                    lastMotionX = event.getX();
                    isBeingDragged = false;
                }
                break;
            }
        }

        return isBeingDragged;
    }

    // 二级标题
    private String upTitle = "初始化中...";
    private String downTitle = "初始化中...";

    public void setUpTitle(String upTitle) {
        this.upTitle = upTitle;
    }

    protected String getUpTitle() {
        return "下一页: " + upTitle;
    }

    public void setDownTitle(String downTitle) {
        this.downTitle = downTitle;
    }

    public String getDownTitle() {
        return "上一页：" + downTitle;
    }


    // 添加webview
    protected void addRefreshableView(T refreshableView) {
        addView(refreshableView,
                new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 0, 1.0f));
    }

    /**
     * 创建可刷新的view
     *
     * @return New instance of the Refreshable View
     */
    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

    protected final int getCurrentMode() {
        return currentMode;
    }

    protected final ILoadingLayout getFooterLayout() {
        return footerLayout;
    }

    protected final ILoadingLayout getHeaderLayout() {
        return headerLayout;
    }

    protected final int getHeaderHeight() {
        return headerHeight;
    }

    protected final int getMode() {
        return mode;
    }

    /**
     * Implemented by derived class to return whether the View is in a state
     * where the user can Pull to Refresh by scrolling down.
     *
     * @return true if the View is currently the correct state (for example, top
     * of a ListView)
     */
    protected abstract boolean isReadyForPullDown();

    /**
     * Implemented by derived class to return whether the View is in a state
     * where the user can Pull to Refresh by scrolling up.
     *
     * @return true if the View is currently in the correct state (for example,
     * bottom of a ListView)
     */
    protected abstract boolean isReadyForPullUp();

    // ===========================================================
    // Methods
    // ===========================================================

    protected void resetHeader() {
        state = PULL_TO_REFRESH;
        isBeingDragged = false;

        if (null != headerLayout) {
            headerLayout.reset();
        }
        if (null != footerLayout) {
            footerLayout.reset();
        }

        smoothScrollTo(0);
    }

    public void setRefreshingInternal(boolean doScroll) {
        state = REFRESHING;

        if (null != headerLayout) {
            headerLayout.loading();
        }
        if (null != footerLayout) {
            footerLayout.loading();
        }

        if (doScroll) {
            smoothScrollTo(currentMode == MODE_PULL_DOWN_TO_REFRESH ? -headerHeight : headerHeight);
        }
    }

    protected final void setHeaderScroll(int y) {
        scrollTo(0, y);
    }

    protected final void smoothScrollTo(int y) {
        if (null != currentSmoothScrollRunnable) {
            currentSmoothScrollRunnable.stop();
        }

        if (this.getScrollY() != y) {
            this.currentSmoothScrollRunnable = new SmoothScrollRunnable(handler, getScrollY(), y);
            handler.post(currentSmoothScrollRunnable);
        }
    }


    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * Actions a Pull Event
     *
     * @return true if the Event has been handled, false if there has been no
     * change
     */
    private boolean pullEvent() {

        final int newHeight;
        final int oldHeight = this.getScrollY();

        switch (currentMode) {
            case MODE_PULL_UP_TO_REFRESH:
                newHeight = Math.round(Math.max(initialMotionY - lastMotionY, 0) / FRICTION);
                // newHeight = Math.round((initialMotionY - lastMotionY) /
                // FRICTION);
                break;
            case MODE_PULL_DOWN_TO_REFRESH:
            default:
                newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0) / FRICTION);
                // newHeight = Math.round((initialMotionY - lastMotionY) /
                // FRICTION);
                break;
        }

        setHeaderScroll(newHeight);
        if (newHeight != 0) {
            if (state == PULL_TO_REFRESH && headerHeight < Math.abs(newHeight)) {
                state = RELEASE_TO_REFRESH;
                switch (currentMode) {
                    case MODE_PULL_UP_TO_REFRESH:
                        footerLayout.releaseToRefresh();
                        break;
                    case MODE_PULL_DOWN_TO_REFRESH:
                        headerLayout.releaseToRefresh();
                        break;
                }
                return true;

            } else if (state == RELEASE_TO_REFRESH && headerHeight >= Math.abs(newHeight)) {
                state = PULL_TO_REFRESH;
                switch (currentMode) {
                    case MODE_PULL_UP_TO_REFRESH:
                        footerLayout.pullToRefresh();
                        break;
                    case MODE_PULL_DOWN_TO_REFRESH:
                        headerLayout.pullToRefresh();
                        break;
                }
                return true;
            }
        }

        return oldHeight != newHeight;
    }

    private boolean isReadyForPull() {
        switch (mode) {
            case MODE_PULL_DOWN_TO_REFRESH:
                return isReadyForPullDown();
            case MODE_PULL_UP_TO_REFRESH:
                return isReadyForPullUp();
            case MODE_BOTH:
                return isReadyForPullUp() || isReadyForPullDown();
        }
        return false;
    }

    // 翻页监听器
    public interface OnPageListener {
        void initTitle(String type);

        void onPage(String pullType);
    }

    @Override
    public void setLongClickable(boolean longClickable) {
        getLoadableView().setLongClickable(longClickable);
    }
}
