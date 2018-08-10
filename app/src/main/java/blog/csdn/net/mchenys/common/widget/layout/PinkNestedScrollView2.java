package blog.csdn.net.mchenys.common.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Scroller;

import blog.csdn.net.mchenys.R;


public class PinkNestedScrollView2 extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "MyNestedScrollParent";
    private View mTopView;
    private View mPinkView;
    private View mContentView;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private int maxScrollY;
    private int pinkViewHeight;
    private Scroller mScroller;
    private OnTopScrollListener mOnTopScrollListener;
    private float pinkMarginTop;

    public interface OnTopScrollListener {
        void onScroll(float scrollY, float maxScrollY);
    }

    public void setOnTopScrollListener(OnTopScrollListener onTopScrollListener) {
        mOnTopScrollListener = onTopScrollListener;
    }

    public PinkNestedScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mScroller = new Scroller(context);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PinkNestedScrollView2);
        pinkMarginTop = array.getDimension(R.styleable.PinkNestedScrollView2_pinkMarginTop, 0f);
        array.recycle();
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
                if (maxScrollY <= 0) {
                    maxScrollY = (int) (mTopView.getMeasuredHeight() - pinkMarginTop);
                    LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
                    lp.height = (int) (getHeight() - mPinkView.getHeight() - pinkMarginTop);
                    mContentView.setLayoutParams(lp);
                }
                if (pinkViewHeight <= 0) {
                    pinkViewHeight = mPinkView.getMeasuredHeight();

                }
            }
        });
    }


    //在此可以判断参数target是哪一个子view以及滚动的方向，然后决定是否要配合其进行嵌套滚动
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.e("cys", "onStartNestedScroll-> target:" + target);
        return target instanceof NestedScrollingChild;
    }


    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
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
        //recycleView返回的dy>0表示上拉,dy<0表示下拉
        if (showTopView(-dy, target) || hideTopView(-dy)) {//如果需要显示或隐藏图片，即需要自己(parent)滚动

            scrollBy(0, dy);//滚动
            consumed[1] = dy;//告诉child我消费了多少


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
        Log.e("cys", "onNestedScroll-> dyUnconsumed:" + dyUnconsumed + " dyConsumed:" + dyConsumed);
        if (dyUnconsumed > 0 && target.canScrollVertically(1)) {
            // 如果子View还有未消费的,可以继续消费
            scrollBy(0, dyUnconsumed);//滚动
        }
    }

    //返回值：是否消费了fling 先于child fling
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.e("cys", "onNestedPreFling-> velocityY:" + velocityY + " getScrollY:" + getScrollY() + " maxScrollY:" + maxScrollY);
        if (!target.canScrollVertically(-1) && getScrollY() < maxScrollY) {
            Log.e("cys", "onNestedPreFling-> 父View消费Fling");
            fling((int) velocityY);
            return true; //飞滑一旦消费就是全部消费,没有部分消费.
        }
        return false;

    }

    //返回值：是否消费了fling,后于child fling
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.e("cys", "onNestedFling-> 子View是否consumed:" + consumed);
        if (!consumed) {
            fling((int) velocityY);
            return true;
        } else {

            if (null != mOnTopScrollListener) {
                mOnTopScrollListener.onScroll(maxScrollY, maxScrollY);
            }
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
            if (getScrollY() < maxScrollY) {
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
        if (y > maxScrollY) {
            y = maxScrollY;
        }
        if (null != mOnTopScrollListener) {
            mOnTopScrollListener.onScroll(getScrollY(), maxScrollY);
        }
        super.scrollTo(x, y);
    }


    public void fling(int velocityY) {
        if (mTopView.isShown()) {

            mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, maxScrollY);
            invalidate();
        }
        if (null != mOnTopScrollListener) {
            mOnTopScrollListener.onScroll(getScrollY(), maxScrollY);
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
                lastY = (int) event.getY();
                if (!mScroller.isFinished()) { //fling
                    mScroller.abortAnimation();
                }
                break;
            //移动
            case MotionEvent.ACTION_MOVE:
                int y = (int) (event.getY());
                int dy = y - lastY;
                lastY = y;
                scrollBy(0, -dy);

                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(300);
                int vy = (int) mVelocityTracker.getYVelocity();
                Log.e("cys", "up-> velocityY:" + vy);
                fling(-vy);

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                break;
        }

        return true;
    }

}
