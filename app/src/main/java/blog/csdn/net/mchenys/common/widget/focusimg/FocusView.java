package blog.csdn.net.mchenys.common.widget.focusimg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.DisplayUtils;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.model.Focus;

/**
 * Created by mChenys on 2016/6/2.
 */
public class FocusView extends RelativeLayout {
    boolean isFocusInit;
    Handler mHandler;
    List<Focus> mFocusList;//数据
    FocusViewPager mViewPager;
    ViewPagerAdapter mPagerAdapter;
    int mAutoPlayTime; //自动切换图片的时间
    boolean isAutoPlay = true; //是否自动切换
    boolean isDescribe; //是否需要描述信息
    int mWidth; //焦点图宽
    int mHeight;//焦点图高
    int mIndicatorPadding;//指示器的内边距
    int mDotMagin;
    int mIndicatorBottomMargin;//指示器的底部外边距
    int mDescHeight; //描述信息的高度
    int mDescPadding;//描述文字的左内边距
    int mIndicatorSelectorResId;
    int id_view_pager;
    int id_desc_tv;
    int id_indicator;
    OnPagerLoadListener mPagerLoadListener;
    OnFocusClickListener mClickListener;

    //指示器和描述文本的位置,左,中,右
    private XGravity mIndicatorGravity, mDescTextGravity;
    private static final XGravity[] mGravities = {
            XGravity.LEFT,
            XGravity.CENTER,
            XGravity.RIGHT,
    };

    public enum XGravity {
        LEFT(0),
        CENTER(1),
        RIGHT(2);
        final int gravity;

        XGravity(int g) {
            gravity = g;
        }
    }

    public static final int AD_RIGHT_BOOTOM = 111;   //右下
    public static final int AD_RIGHT_TOP = 222;      //右上

    private int adPositive = AD_RIGHT_BOOTOM;

    /**
     * 设置adTag的位置
     *
     * @param adPositive
     */
    public void setAdPositive(int adPositive) {
        this.adPositive = adPositive;
    }

    public FocusView(Context context) {
        this(context, null);
    }

    public FocusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributeValue(context, attrs);
        init();
    }

    private void getAttributeValue(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FocusView);
        this.isDescribe = a.getBoolean(R.styleable.FocusView_describe, false);
        this.mAutoPlayTime = a.getInt(R.styleable.FocusView_auto_play_time, 3000);

        this.mIndicatorPadding = (int) a.getDimension(R.styleable.FocusView_indicator_padding,
               DisplayUtils.convertDIP2PX(context,5));
        this.mDotMagin = (int) a.getDimension(R.styleable.FocusView_indicator_child_margin, (int)
                DisplayUtils.convertDIP2PX(context,5));
        this.mDescHeight = (int) a.getDimension(R.styleable.FocusView_desc_height,
                DisplayUtils.convertDIP2PX(context,40));
        this.mIndicatorGravity = mGravities[a.getInt(R.styleable.FocusView_indicator_gravity, 1)];
        this.mDescTextGravity = mGravities[a.getInt(R.styleable.FocusView_desc_gravity, 0)];
        this.mIndicatorBottomMargin =   DisplayUtils.convertDIP2PX(context,5);
        this.mDescPadding =   DisplayUtils.convertDIP2PX(context,10);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mWidth = getMeasuredWidth();
        this.mHeight = getMeasuredHeight();
    }

    private void init() {
        this.id_view_pager = R.id.FocusView_id_view_pager;
        this.id_desc_tv = R.id.FocusView_id_desc_tv;
        this.id_indicator = R.id.FocusView_id_indicator;
        this.mHandler = new Handler();
        this.mFocusList = new ArrayList<>();
        this.mPagerAdapter = new ViewPagerAdapter();

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        //添加ViewPager
        this.mViewPager = new FocusViewPager(getContext());
        mViewPager.setId(id_view_pager);
        this.mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        addView(mViewPager, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        //添加描述信息
        TextView desc = new TextView(getContext());
        desc.setId(id_desc_tv);
        desc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        desc.setTextColor(Color.WHITE);
        desc.setSingleLine(true);
        desc.setEllipsize(TextUtils.TruncateAt.END);
        desc.setShadowLayer(2,2,2,getResources().getColor(R.color.color_64000000));
        desc.getPaint().setFakeBoldText(true);
        desc.setBackgroundColor(getResources().getColor(R.color.half_transparent));
        addView(desc, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mDescHeight));//添加描述信息
        desc.setVisibility(View.GONE);
        setDescTextGravity(mDescTextGravity);

        //添加指示器
        LinearLayout indicatorLayout = new LinearLayout(getContext());
        indicatorLayout.setId(id_indicator);
        indicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        indicatorLayout.setPadding(mIndicatorPadding, mIndicatorPadding, mIndicatorPadding, mIndicatorPadding);
        indicatorLayout.setGravity(Gravity.CENTER);
        addView(indicatorLayout, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setIndicatorGravity(mIndicatorGravity);
    }

    /**
     * 外部必须调用
     *
     * @param list
     */
    public final void initFocus(List<Focus> list) {
        if (null == list || (null != list && list.isEmpty())) {
            setFocusImageParams(0, 0);
            return;
        } else if (null != list && list.size() == 1) {
            mViewPager.setNoScroll(true);
        }
        this.mFocusList = list;
        initDot();
        if (!isFocusInit) {
            isFocusInit = !isFocusInit;
            int size = list.size();
            mViewPager.setCurrentItem(size * 1000);//多次调用,会出现卡顿情况
        }
        mPagerAdapter.notifyDataSetChanged();

    }

    /**
     * 由调用者调用,当数据发生变化时,通知adapter刷新ViewPager
     */
    public void notifyDataSetChanged() {
        if (null != mPagerAdapter) {
//            initDot();
            changeIndicator(mViewPager.getCurrentItem());
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    //初始化指示点
    private void initDot() {
        LinearLayout indicator = (LinearLayout) findViewById(id_indicator);
        indicator.removeAllViews();
        if (null != mFocusList && mFocusList.size() < 2) {
            return;
        }
        int size = mFocusList.size();
        if (size > 0) {
            LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            dotLp.leftMargin = mDotMagin;
            for (int i = 0; i < size; i++) {
                ImageView dot = new ImageView(getContext());
                dot.setBackgroundResource(mIndicatorSelectorResId);
                dot.setClickable(false);
                dot.setId(i);
                if (i == 0) {
                    indicator.addView(dot, i);
                } else {
                    indicator.addView(dot, i, dotLp);
                }
            }
        }
    }

    /**
     * 如果在代码中创建FocusView,则必须调用
     *
     * @param w FocusView的宽
     * @param h FocusView的高
     */
    public final void setFocusImageParams(int w, int h) {
        if (w > -1 && h > -1) {
            ViewParent vp = getParent();
            if (vp instanceof FrameLayout) {
                setLayoutParams(new FrameLayout.LayoutParams(w, h));
            } else if (vp instanceof RelativeLayout) {
                setLayoutParams(new LayoutParams(w, h));
            } else if (vp instanceof LinearLayout) {
                setLayoutParams(new LinearLayout.LayoutParams(w, h));
            } else {
                setLayoutParams(new ViewGroup.LayoutParams(w, h));
            }
            this.mWidth = w;
            this.mHeight = h;
            this.mDescHeight = this.mHeight / 4;

            TextView desc = (TextView) findViewById(id_desc_tv);
            LayoutParams tLP = (LayoutParams) desc.getLayoutParams();
            tLP.height = mDescHeight;
            tLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            desc.setLayoutParams(tLP);

        }
    }


    /**
     * ViewPager的切换监听
     */
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            changeIndicator(i);
            if (null != mPagerLoadListener) {
                mPagerLoadListener.onSuccess(i%mFocusList.size(), mFocusList.get(i%mFocusList.size()));
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private void changeIndicator(int position) {
        LinearLayout indicator = findViewById(id_indicator);
        if (null != indicator && indicator.getChildCount() > 0) {
            int count = indicator.getChildCount();
            position = position % count;
            //在这里先初始化点后选择，避免样式变形
            initDot();
            for (int i = 0; i < count; i++) {
                indicator.getChildAt(i).setSelected(false);
            }
            indicator.getChildAt(position).setSelected(true);
        }
    }

    //adapter
    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getItemView(position);
            if (null != view) {
                container.addView(view);
            }
            return view;
        }

        private View getItemView(int position) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            //推广标签
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));
            final TextView adtag = new TextView(getContext());
            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(DisplayUtils.convertDIP2PX(getContext(),32),
                    DisplayUtils.convertDIP2PX(getContext(),20));
            if (adPositive == AD_RIGHT_BOOTOM) {
                tlp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                tlp.bottomMargin = DisplayUtils.convertDIP2PX(getContext(), 5);
                tlp.rightMargin = DisplayUtils.convertDIP2PX(getContext(), 5);
            } else {
                tlp.gravity = Gravity.RIGHT | Gravity.TOP;
                tlp.topMargin = DisplayUtils.convertDIP2PX(getContext(), 5);
                tlp.rightMargin = DisplayUtils.convertDIP2PX(getContext(), 5);
            }

            adtag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            adtag.setGravity(Gravity.CENTER);
            adtag.setText("广告");
            adtag.setBackgroundResource(R.drawable.shape_border_4_colorb5b5b5_corner4);
            adtag.setTextColor(getResources().getColor(R.color.color_b5b5b5));
            adtag.setVisibility(View.GONE);
            Focus focus = getFocus(position);
            if (null != focus && !StringUtils.isEmpty(focus.type)) {
                if (getFocus(position).type.equals("ad")) {
                    adtag.setVisibility(View.VISIBLE);
                }
            }

            //焦点图
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            frameLayout.addView(imageView);
            frameLayout.addView(adtag, tlp);


            ImageLoadUtils.disPlay(getUrl(position), imageView, new ImageLoadUtils.ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String url, View view) {

                }

                @Override
                public void onLoadingFailed(String url, View view) {
                    if (null != mPagerLoadListener) {
                        mPagerLoadListener.onFailure();
                    }
                }

                @Override
                public void onLoadingComplete(String url, View view) {
                    if (null != mFocusList && !mFocusList.isEmpty()) {
                        int position = mViewPager.getCurrentItem() % mFocusList.size();
                        //图片加载出来后再显示描述
                        Focus focus = mFocusList.get(position);
                        if (isDescribe) {
                            TextView desc = (TextView) findViewById(id_desc_tv);
                            desc.setText(focus.title);
                            desc.setVisibility(TextUtils.isEmpty(focus.title) ? GONE : View.VISIBLE);
                        }

                    }
                }
            });
            frameLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mClickListener && null != mFocusList && !mFocusList.isEmpty()) {
                        int position = mViewPager.getCurrentItem() % mFocusList.size();
                        mClickListener.onClickItem(position, mFocusList.get(position));
                    }
                }
            });

            return frameLayout;
        }


        private String getUrl(int position) {
            if (null == mFocusList) {
                return null;
            }
            if (mFocusList.size() > 0) {
                position = position % mFocusList.size();
                if (position > -1 && position < mFocusList.size()) {
                    return mFocusList.get(position).image;
                }
            }
            return null;
        }

        private Focus getFocus(int position) {
            if (mFocusList.size() > 0) {
                position = position % mFocusList.size();
                if (position > -1 && position < mFocusList.size()) {
                    return mFocusList.get(position);
                }
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //页面切换监听
    public interface OnPagerLoadListener {
        void onSuccess(int position, Focus focus);

        void onFailure();
    }

    //页面点击监听
    public interface OnFocusClickListener {
        void onClickItem(int position, Focus focus);
    }

    public void setOnPagerLoadListener(OnPagerLoadListener pagerLoadListener) {
        this.mPagerLoadListener = pagerLoadListener;
    }

    public void setOnClickItemListener(OnFocusClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    /**
     * 事件分发处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoPlay(); //手指按下时,停止切换ViewPager
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoPlay(); //手指弹起或者取消时开始切换ViewPager
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 启动自动切换，在Activity或者Fragment页面的onResume中调用
     */
    public void startAutoPlay() {
        stopAutoPlay();
        if (null != mFocusList && mFocusList.size() < 2) {
            return;
        }
        mHandler.postDelayed(autoPlayRunnable, mAutoPlayTime);
    }

    /**
     * 停止自动切换，在使用页面的onPause中调用
     */
    public void stopAutoPlay() {
        mHandler.removeCallbacks(autoPlayRunnable);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            startAutoPlay();
        } else {
            stopAutoPlay();
        }
    }

    /**
     * 自动切换图片的Runnnable
     */
    private Runnable autoPlayRunnable = new Runnable() {
        @Override
        public void run() {

            if (isAutoPlay && null != mViewPager) {
                int currentItem = mViewPager.getCurrentItem();
                currentItem++;
                if (currentItem >= Integer.MAX_VALUE) {
                    currentItem = 0;
                } else if (currentItem <= 0) {
                    currentItem = Integer.MAX_VALUE;
                }
                mViewPager.setCurrentItem(currentItem);
                startAutoPlay(); //开启循环执行该run方法
            }
        }
    };

    public void setIndicatorPadding(int indicatorPadding) {
        mIndicatorPadding = indicatorPadding;
    }

    public void setDotMagin(int dotMagin) {
        mDotMagin = dotMagin;
    }

    public void setIndicatorBottomMargin(int indicatorBottomMargin) {
        mIndicatorBottomMargin = indicatorBottomMargin;
    }

    public void setDescHeight(int descHeight) {
        mDescHeight = descHeight;
    }

    public void setDescPadding(int descPadding) {
        mDescPadding = descPadding;
    }

    public void setIndicatorSelectorResId(int indicatorSelectorResId) {
        mIndicatorSelectorResId = indicatorSelectorResId;
    }

    public void setIsDescribe(boolean isDescribe) {
        this.isDescribe = isDescribe;
    }

    public void setIsAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
    }

    public void setAutoPlayTime(int autoPlayTime) {
        mAutoPlayTime = autoPlayTime;
    }

    /***
     * 设置描述文本的背景色
     * @param background 述文本的背景色
     */
    public void setDescBackgroundColor(int background) {
        TextView desc = findViewById(id_desc_tv);
        desc.setBackgroundColor(background);
    }

    /***
     * 设置描述文本的背景图
     * @param drawable 述文本的背景图
     */
    public void setDescBackground(Drawable drawable) {
        TextView desc = findViewById(id_desc_tv);
        desc.setBackground(drawable);
    }

    /**
     * 设置描述文本的位置
     *
     * @param descTextGravity
     */
    public void setDescTextGravity(XGravity descTextGravity) {
        this.mDescTextGravity = descTextGravity;
        TextView desc = (TextView) findViewById(id_desc_tv);
        LayoutParams tLP = (LayoutParams) desc.getLayoutParams();
        if (null == tLP) {
            tLP = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mDescHeight);
        }
        tLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (mDescTextGravity == XGravity.LEFT) {
            desc.setPadding(mDescPadding, 0, 0, 0);
            desc.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        } else if (mDescTextGravity == XGravity.RIGHT) {
            desc.setPadding(0, 0, 0, mDescPadding);
            desc.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        } else {
            desc.setGravity(Gravity.CENTER);
        }
        desc.setLayoutParams(tLP);
    }

    /**
     * 设置指示器的位置
     *
     * @param indicatorGravity
     */
    public void setIndicatorGravity(XGravity indicatorGravity) {
        this.mIndicatorGravity = indicatorGravity;
        LinearLayout indicatorLayout = (LinearLayout) findViewById(id_indicator);
        int rule = RelativeLayout.CENTER_HORIZONTAL;
        if (mIndicatorGravity == XGravity.LEFT) {
            rule = RelativeLayout.ALIGN_PARENT_LEFT;
        } else if (mIndicatorGravity == XGravity.RIGHT) {
            rule = RelativeLayout.ALIGN_PARENT_RIGHT;
        }
        LayoutParams rlp = (LayoutParams) indicatorLayout.getLayoutParams();
        if (null == rlp) {
            rlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlp.bottomMargin = mIndicatorBottomMargin;
        rlp.addRule(rule);
        indicatorLayout.setLayoutParams(rlp);
    }
}
