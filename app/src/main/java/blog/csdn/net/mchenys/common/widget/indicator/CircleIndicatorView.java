package blog.csdn.net.mchenys.common.widget.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.DisplayUtils;


/**
 * Created by mChenys on 2017/7/17.
 */
public class CircleIndicatorView extends View implements ViewPager.OnPageChangeListener {
    private int mSelectColor = Color.parseColor("#FFFFFF");
    private Paint mCirclePaint;
    private Paint mTextPaint;
    private int mCount; // indicator 的数量
    private int mRadius;//半径
    private int mStrokeWidth;//border
    private int mTextColor;// 小圆点中文字的颜色
    private int mDotNormalColor;// 小圆点默认颜色
    private int mSpace = 0;// 圆点之间的间距
    private List<Indicator> mIndicators;
    private int mSelectPosition = 0; // 选中的位置
    private ViewPager mViewPager;
    private OnIndicatorClickListener mOnIndicatorClickListener;
    /**
     * 是否允许点击Indicator切换ViewPager
     */
    private boolean mIsEnableClickSwitch = false;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public CircleIndicatorView(Context context) {
        super(context);
        init();
    }

    public CircleIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttr(context, attrs);
        init();
    }

    public CircleIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttr(context, attrs);
        init();
    }

    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setDither(true);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mTextPaint = new Paint();
        mTextPaint.setDither(true);
        mTextPaint.setAntiAlias(true);
        // 默认值
        mIndicators = new ArrayList<>();

        initValue();

    }

    private void initValue() {
        mCirclePaint.setColor(mDotNormalColor);
        mCirclePaint.setStrokeWidth(mStrokeWidth);

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mRadius);
    }

    /**
     * 获取自定义属性
     *
     * @param context
     * @param attrs
     */
    private void getAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicatorView);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.CircleIndicatorView_indicatorRadius, DisplayUtils.dip2px(context, 6));
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.CircleIndicatorView_indicatorBorderWidth, DisplayUtils.dip2px(context, 2));
        mSpace = typedArray.getDimensionPixelSize(R.styleable.CircleIndicatorView_indicatorSpace, DisplayUtils.dip2px(context, 5));
        // color
        mSelectColor = typedArray.getColor(R.styleable.CircleIndicatorView_indicatorSelectColor, Color.WHITE);
        mDotNormalColor = typedArray.getColor(R.styleable.CircleIndicatorView_indicatorColor, Color.GRAY);

        mIsEnableClickSwitch = typedArray.getBoolean(R.styleable.CircleIndicatorView_enableIndicatorSwitch, false);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (mRadius + mStrokeWidth) * 2 * mCount + mSpace * (mCount - 1);
        int height = mRadius * 2 + mSpace * 2;
        setMeasuredDimension(width, height);
        measureIndicator();
    }

    /**
     * 测量每个圆点的位置
     */
    private void measureIndicator() {
        mIndicators.clear();
        float cx = 0;
        for (int i = 0; i < mCount; i++) {
            Indicator indicator = new Indicator();
            if (i == 0) {
                cx = mRadius + mStrokeWidth;
            } else {
                cx += (mRadius + mStrokeWidth) * 2 + mSpace;
            }
            indicator.cx = cx;
            indicator.cy = getMeasuredHeight() / 2;
            mIndicators.add(indicator);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mIndicators.size(); i++) {
            Indicator indicator = mIndicators.get(i);
            float x = indicator.cx;
            float y = indicator.cy;
            if (mSelectPosition == i) {
                mCirclePaint.setStyle(Paint.Style.FILL);
                mCirclePaint.setColor(mSelectColor);
                canvas.drawCircle(x, y, mRadius, mCirclePaint);
            } else {
                mCirclePaint.setColor(mDotNormalColor);
                mCirclePaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(x, y, mRadius - mStrokeWidth/2.0f, mCirclePaint);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xPoint = 0;
        float yPoint = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xPoint = event.getX();
                yPoint = event.getY();
                handleActionDown(xPoint, yPoint);
                break;

        }

        return super.onTouchEvent(event);
    }

    private void handleActionDown(float xDis, float yDis) {
        for (int i = 0; i < mIndicators.size(); i++) {
            Indicator indicator = mIndicators.get(i);
            if (xDis < (indicator.cx + mRadius + mStrokeWidth)
                    && xDis >= (indicator.cx - (mRadius + mStrokeWidth))
                    && yDis >= (yDis - (indicator.cy + mStrokeWidth))
                    && yDis < (indicator.cy + mRadius + mStrokeWidth)) {
                // 找到了点击的Indicator
                // 是否允许切换ViewPager
                if (mIsEnableClickSwitch) {
                    mViewPager.setCurrentItem(i, false);
                }

                // 回调
                if (mOnIndicatorClickListener != null) {
                    mOnIndicatorClickListener.onSelected(i);
                }
                break;
            }
        }
    }

    public void setOnIndicatorClickListener(OnIndicatorClickListener onIndicatorClickListener) {
        mOnIndicatorClickListener = onIndicatorClickListener;
    }

    public void setPageCount(int count) {
        mCount = count;
        requestLayout();
        invalidate();
    }

    /**
     * 设置 border
     *
     * @param borderWidth
     */
    public void setBorderWidth(int borderWidth) {
        mStrokeWidth = borderWidth;
        initValue();
    }

    /**
     * 设置文字的颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        mTextColor = textColor;
        initValue();
    }

    /**
     * 设置选中指示器的颜色
     *
     * @param selectColor
     */
    public void setSelectColor(int selectColor) {
        mSelectColor = selectColor;
    }

    /**
     * 设置指示器默认颜色
     *
     * @param dotNormalColor
     */
    public void setDotNormalColor(int dotNormalColor) {
        mDotNormalColor = dotNormalColor;
        initValue();
    }

    /**
     * 设置选中的位置
     *
     * @param selectPosition
     */
    public void setSelectPosition(int selectPosition) {
        mSelectPosition = selectPosition;
    }


    /**
     * 设置Indicator 半径
     *
     * @param radius
     */
    public void setRadius(int radius) {
        mRadius = radius;
        initValue();
    }

    public void setSpace(int space) {
        mSpace = space;
    }

    public void setEnableClickSwitch(boolean enableClickSwitch) {
        mIsEnableClickSwitch = enableClickSwitch;
    }

    /**
     * 与ViewPager 关联
     *
     * @param viewPager
     */
    public void setUpWithViewPager(ViewPager viewPager) {
        releaseViewPager();
        if (viewPager == null) {
            return;
        }
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
        int count = mViewPager.getAdapter().getCount();
        if (count != Integer.MAX_VALUE) {
            setPageCount(count);
        }
    }

    /**
     * 重置ViewPager
     */
    private void releaseViewPager() {
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
            mViewPager = null;
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (null != mOnPageChangeListener)
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        mSelectPosition = position % mCount;
        invalidate();
        if (null != mOnPageChangeListener) mOnPageChangeListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (null != mOnPageChangeListener) mOnPageChangeListener.onPageScrollStateChanged(state);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }


    /**
     * Indicator 点击回调
     */
    public interface OnIndicatorClickListener {
        void onSelected(int position);
    }


    public static class Indicator {
        public float cx; // 圆心x坐标
        public float cy; // 圆心y 坐标
    }

}