package blog.csdn.net.mchenys.common.widget.layout;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import blog.csdn.net.mchenys.common.widget.recycleview.refresh.RefreshRecyclerView;


/**
 * 嵌套滑动置顶功能的父View
 * Created by mChenys on 2018/12/17.
 */
public class PinkNestedScrollView3 extends LinearLayout implements NestedScrollingParent, Scrollable {
    private static final String TAG = "PinkNestedScrollParent";
    private View mTopView;
    private View mPinkView;
    private View mContentView;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private int topViewHeight;
    private int pinkViewHeight;
    private OverScroller mScroller;
    private View mTarget;
    private OnScrollChangeCallback mScrollChangeCallback; //滚动监听

    public void setOnScrollChangeCallback(OnScrollChangeCallback c) {
        mScrollChangeCallback = c;
    }

    public PinkNestedScrollView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mScroller = new OverScroller(context);
    }

    //获取子view
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTopView = getChildAt(0);
        mPinkView = getChildAt(1);
        mContentView = getChildAt(2);
        mTopView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (topViewHeight <= 0) {
                    topViewHeight = mTopView.getMeasuredHeight();
                }
                LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
                lp.height = getHeight() - mPinkView.getHeight();
                mContentView.setLayoutParams(lp);

                if (pinkViewHeight <= 0) {
                    pinkViewHeight = mPinkView.getMeasuredHeight();

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mTopView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

    }


    //在此可以判断参数target是哪一个子view以及滚动的方向，然后决定是否要配合其进行嵌套滚动
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL) {
            if (target instanceof RefreshRecyclerView || target instanceof AdvanceSwipeRefreshLayout
                    ||target instanceof NestedScrollView) {
                mTarget = target;
               // Log.e("cys", "onStartNestedScroll-> target:" + target);
                return true;
            }

        }
        return false;

    }


    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        if (nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && target instanceof RefreshRecyclerView) {
            mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        }
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
      /*  if (!shouldIntercept) {
            mTarget = null; //已拦截事件就没必要清空了
            Log.e("cys", "onStopNestedScroll-> 清空target");
        }*/
    }

    //先于child滚动
    //前3个为输入参数，最后一个是输出参数

    /**
     * @param target   子View
     * @param dx       子View需要在x轴滑动的距离
     * @param dy       子View需要在y轴滑动的距离
     * @param consumed 子View传给父View的数组,用于保存消费的x和y方向的距离
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
      //  Log.e("cys", "onNestedPreScroll->target.canScrollVertically(1):" + target.canScrollVertically(1) + " target.canScrollVertically(-1)："+ target.canScrollVertically(-1));
        //recycleView返回的dy>0表示上拉,dy<0表示下拉
        if (showTopView(-dy, target) || hideTopView(-dy)) {//如果需要显示或隐藏图片，即需要自己(parent)滚动

            scrollBy(0, dy);//滚动
            consumed[1] = dy;//告诉child我消费了多少
        }
        if (getScrollY() == 0) {
            //向下滑动mTarget带动parent完全划出,继续向下滑需要划出parent的parent
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        Log.e("cys", "onNestedPreScroll-> dy:" + dy + " 父View consumed:" + consumed[1] + " getScrollY:" + getScrollY());
    }

    //后于child滚动

    /**
     * @param target       子View
     * @param dxConsumed   x轴被子View消耗的距离
     * @param dyConsumed   y轴被子View消耗的距离
     * @param dxUnconsumed x轴未被子View消耗的距离
     * @param dyUnconsumed y轴未被子View消耗的距离
     */
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
      //  Log.e("cys", "onNestedScroll-> dyUnconsumed:" + dyUnconsumed + " dyConsumed:" + dyConsumed);
        if (dyUnconsumed > 0 && mTarget.canScrollVertically(1)) {
            // 如果子View还有未消费的,可以继续消费
            scrollBy(0, dyUnconsumed);//滚动
        }
    }

    //返回值：是否消费了fling 先于child fling
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
      //  Log.e("cys", "onNestedPreFling-> velocityY:" + velocityY + " getScrollY:" + getScrollY() + " topViewHeight:" + topViewHeight);
        if (getScrollY() > 0 && getScrollY() < topViewHeight) {
            fling((int) velocityY);
            return true; //飞滑一旦消费就是全部消费,没有部分消费.
        }
        return false;

    }

    //返回值：是否消费了fling,后于child fling
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
      //  Log.e("cys", "onNestedFling-> 子View是否consumed:" + consumed);
        if (!consumed) {
            fling((int) velocityY);
            return true;
        }
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    //下拉的时候是否要向下滚动以显示顶部
    public boolean showTopView(int dy, View target) {
        if (dy > 0 && mTopView.isShown()) {
            if (target instanceof NestedScrollingChild) {
                if (getScrollY() > 0 &&/* target.getScrollY() == 0*/ !target.canScrollVertically(-1)) {
                    return true; //显示顶部
                }
            }
        }
        return false;
    }

    //上拉的时候，是否要向上滚动，隐藏图片
    public boolean hideTopView(int dy) {
        if (dy < 0 && mTopView.isShown()) {
            if (getScrollY() < topViewHeight) {
                return true;
            }
        }
        return false;
    }

    //scrollBy内部会调用scrollTo
    //限制滚动范围
    @Override
    public void scrollTo(int x, int y) {

        if (y < 0) {
            y = 0;
        }
        if (y > topViewHeight) {
            y = topViewHeight;
        }

        super.scrollTo(x, y);
        if (null != mScrollChangeCallback) {
            mScrollChangeCallback.onChange(this, x, y);
        }

    }


    public void fling(int velocityY) {
        if (mTopView.isShown()) {
            mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, topViewHeight);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }

    //处理自身的滚动逻辑
    private int lastY;
    private int lastX;
    private VelocityTracker mVelocityTracker;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            //按下
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) { //fling
                    mScroller.abortAnimation();
                }
                break;
            //移动
            case MotionEvent.ACTION_MOVE:
                //首次点击或者滑动的不是NestedScrollChild的时候是拿不到target的
                if (null != mTarget) {
                    if (!mTarget.canScrollVertically(-1)) {
                        scrollBy(0, -deltaY); //mTarget处于顶部不能继续下滑的时候才能滑该View自己
                  //      Log.e("cys", "ACTION_MOVE getScrollY:" + getScrollY());
                    } else {
                 //       Log.e("cys", "scrollBy 需要传给mTarget");
                        mTarget.onTouchEvent(event);
                        return true;
                    }
                    //解决当该View已经拦截了事件,而mPinkView已经固定,仍然继续上滑时需要划出mTarget
                    if (shouldIntercept && getScrollY() == topViewHeight) {
                        // Log.e("cys", "ACTION_MOVE 需要传给mTarget");
                        mTarget.onTouchEvent(event);
                    }
                } else {
               //     Log.e("cys", "mTarget不存在,ACTION_MOVE getScrollY:" + getScrollY());
                    if (isVerticalScroll)
                        scrollBy(0, -deltaY);
                }


                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                int vy = (int) mVelocityTracker.getYVelocity();
                if (null != mTarget) {
                    //解决当该View已经拦截了事件,飞划mTarget有效果
                    if (shouldIntercept && getScrollY() == topViewHeight) {
                  //      Log.e("cys", "ACTION_UP 需要传给mTarget");
                        mTarget.onTouchEvent(event);
                        return true;
                    }
                }

                fling(-vy);

                break;
        }

        return true;
    }

    private boolean isVerticalScroll;
    private boolean isPullUp;
    private boolean shouldIntercept;
    private int deltaY;

   /* @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                shouldIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isVerticalScroll && mTopView.isShown()) {
                    if (isPullUp) { //上拉
                        if (getScrollY() >= 0 && getScrollY() < topViewHeight) {
                            //target到顶不能往下滑动时,让父View拦截
                            shouldIntercept = null != mTarget ? !mTarget.canScrollVertically(-1) : true;
                        }

                    } else { //下拉
                        if (getScrollY() > 0 && getScrollY() <= topViewHeight) {
                            //target到顶不能往下滑动时,让父View拦截
                            shouldIntercept = null != mTarget ? !mTarget.canScrollVertically(-1) : true;
                            if (null != mTarget) {
                                Log.e("cys", "onInterceptTouchEvent-> target.canScrollVertically(-1)："
                                        + mTarget.canScrollVertically(-1));
                            }
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                shouldIntercept = false;
                break;
        }


        Log.e("cys", "onInterceptTouchEvent->" + shouldIntercept);
        return shouldIntercept;
    }
*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) ev.getRawX();
                lastY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (ev.getRawX() - lastX);
                int dy = deltaY = (int) (ev.getRawY() - lastY);
                isVerticalScroll = Math.abs(dy) > Math.abs(dx);
                isPullUp = dy < 0;
                lastX = (int) ev.getRawX();
                lastY = (int) ev.getRawY();

                break;
        }
        return super.dispatchTouchEvent(ev);

    }

    /**
     * @param dy 整数表示向下拉动
     * @return
     */
    @Override
    public boolean canIntercept(int dy) {
        if (null != mTarget) {
            return dy > 0 && !mTarget.canScrollVertically(-1) && getScrollY() == 0;
        } else {
            return dy > 0 && getScrollY() == 0;
        }
    }

    /**
     * 是否已经置顶
     *
     * @return
     */
    public boolean isPink() {
        return getScrollY() == topViewHeight;
    }

    public int getTopViewHeight() {
        return topViewHeight;
    }

    public View getTarget() {
        return mTarget;
    }

    public boolean canTargetScrollVertically(int direction) {
        if (null == mTarget) {
            return false;
        } else {
            return mTarget.canScrollVertically(direction);
        }

    }
}