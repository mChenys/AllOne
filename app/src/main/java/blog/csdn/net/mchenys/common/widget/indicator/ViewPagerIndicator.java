package blog.csdn.net.mchenys.common.widget.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;

/**
 * 自定义属性
 * <?xml version="1.0" encoding="utf-8"?>
 * <resources>
 * <p/>
 * <attr name="item_count" format="integer"/>
 * <attr name="indicator_color" format="color"/>
 * <attr name="tab_select_color" format="color"/>
 * <attr name="tab_normal_color" format="color"/>
 * <p/>
 * <declare-styleable name="ViewPagerIndicator">
 * <attr name="item_count" />
 * <attr name="indicator_color"/>
 * <attr name="tab_select_color"/>
 * <attr name="tab_normal_color"/>
 * </declare-styleable>
 * <p/>
 * </resources>
 */
public class ViewPagerIndicator extends LinearLayout {
    //绘制指示器的画笔
    private Paint mPaint;
    //path构成一个三角形
    private Path mPath;
    //三角形的宽度
    private int mTriangleWidth;
    //三角形的高度
    private int mTriangleHeight;
    //三角形的宽度为单个Tab的1/6
    private static final float RADIO_TRIANGEL = 1.0f / 6;
    //三角形的最大宽度
    private final int DIMENSION_TRIANGEL_WIDTH = (int) (getScreenWidth() / 3 * RADIO_TRIANGEL);
    //初始时，三角形指示器的偏移量
    private int mInitTranslationX;
    //手指滑动时的偏移量
    private float mTranslationX;
    //默认的可见Tab数量
    private static final int COUNT_DEFAULT_TAB = 4;
    //tab数量
    private int mTabVisibleCount = COUNT_DEFAULT_TAB;
    //与之绑定的ViewPager
    public ViewPager mViewPager;
    //默认为直线的指示器
    private IStyle mIStyle = IStyle.linearIndicator;
    //tab的宽度
    private int mTabWidth;
    //指示器的颜色
    private int mIndicatorColor;
    //选中tab时tab文字的颜色
    private int mTabTextSelectColor;
    //未选中tab时tab文字的颜色
    private int mTabTextNormalColor;

    /**
     * 对外的ViewPager的回调接口
     */
    public interface PageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    private PageChangeListener onPageChangeListener;

    public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
        this.onPageChangeListener = pageChangeListener;
    }

    /**
     * 指示器的样式:直线或者三角形
     */
    enum IStyle {
        linearIndicator,
        triangleIndicator;
    }

    public void setIndicatorStyle(IStyle style) {
        this.mIStyle = style;
    }

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);


        // 获得自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_item_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0) mTabVisibleCount = COUNT_DEFAULT_TAB;
        mIndicatorColor = a.getColor(R.styleable.ViewPagerIndicator_indicator_color, Color.BLUE);
        mTabTextSelectColor = a.getColor(R.styleable.ViewPagerIndicator_indicator_color, Color.BLUE);
        mTabTextNormalColor = a.getColor(R.styleable.ViewPagerIndicator_indicator_color, Color.BLACK);
        a.recycle();

        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    /**
     * 绘制指示器
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        switch (mIStyle) {
            case linearIndicator:
                //画直线
                canvas.translate(mTranslationX, getHeight());
                mPaint.setColor(mIndicatorColor);
                mPaint.setStrokeWidth(9.0F);
                canvas.drawLine(0, 0, mTabWidth, 0, mPaint);
                break;
            case triangleIndicator:
                // 画笔平移到正确的位置
                canvas.translate(mInitTranslationX + mTranslationX, getHeight());
                canvas.drawPath(mPath, mPaint);
                break;
        }
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    /**
     * 初始化三角形的宽度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // tab width
        mTabWidth = w / mTabVisibleCount;
        //triangle width
        mTriangleWidth = (int) (mTabWidth * RADIO_TRIANGEL);// 1/6 of
        mTriangleWidth = Math.min(DIMENSION_TRIANGEL_WIDTH, mTriangleWidth);
        // 初始时triangle的偏移量
        mInitTranslationX = mTabWidth / 2 - mTriangleWidth / 2;
        // 初始化三角形
        initTriangle();
    }


    /**
     * 设置关联的ViewPager
     *
     * @param mViewPager 如何设置了getPageTitle,则布局添加的就无效
     * @param pos        默认选中的位置
     */
    public void setViewPager(ViewPager mViewPager, int pos) {
        this.mViewPager = mViewPager;

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 设置字体颜色高亮
                resetTextViewColor();
                highLightTextView(position);
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                // 滚动
                scroll(position, positionOffset);
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position,
                            positionOffset, positionOffsetPixels);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
        //初始tab
        initTabs();
        //设置当前页
        mViewPager.setCurrentItem(pos);
        //高亮
        highLightTextView(pos);
    }

    private void initTabs() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (null != adapter && null != adapter.getPageTitle(0)) {
            this.removeAllViews();
            for (int i = 0; i < adapter.getCount(); i++) {
                // 添加view
                addView(generateTextView(adapter.getPageTitle(i).toString()));
                // 设置item的click事件
                setItemClickEvent();
            }
        }
    }

    /**
     * 高亮文本
     *
     * @param position
     */
    protected void highLightTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(mTabTextSelectColor);
        }

    }

    /**
     * 重置文本颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(mTabTextNormalColor);
            }
        }
    }

    /**
     * 设置点击事件
     */
    public void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 根据标题生成我们的TextView
     *
     * @param text
     * @return
     */
    private TextView generateTextView(String text) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(mTabTextNormalColor);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 初始化三角形指示器
     */
    private void initTriangle() {
        mPath = new Path();
        mTriangleHeight = (int) (mTriangleWidth / 2 * Math.sqrt(3));
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }

    /**
     * 指示器跟随手指滚动，以及容器滚动
     *
     * @param position
     * @param offset   0~1的变化
     */
    public void scroll(int position, float offset) {
        //偏移量
        mTranslationX = mTabWidth * (position + offset);
        // 容器滚动，当移动到可见的倒数第2个，开始滚动,滚动到总数倒数第2个时停止滚动
        int startIndex = mTabVisibleCount - 2;
        int endIndex = getChildCount() - 2;
        if (offset > 0 && position >= startIndex
                && getChildCount() > mTabVisibleCount && position < endIndex) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - startIndex) * mTabWidth + (int) (mTabWidth * offset), 0);
            } else {
                // 为count为1时的特殊处理
                this.scrollTo((int) mTranslationX, 0);
            }
        }
        // 不断改变偏移量，invalidate
        invalidate();
    }

    /**
     * 设置布局中view的一些必要属性；如果设置了getPageTitle，布局中view则无效
     */
    @Override
    protected void onFinishInflate() {
        Log.e("TAG", "onFinishInflate");
        super.onFinishInflate();

        int cCount = getChildCount();
        if (cCount == 0)
            return;
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        // 设置点击事件
        setItemClickEvent();
    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}