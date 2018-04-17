package blog.csdn.net.mchenys.common.widget.recycleview.refresh.footer;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.LogUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.BaseLoaderFooter;


/**
 * 加载更多视图
 * Created by mChenys on 2018/1/2.
 */
public class ShoppingLoadingFooter extends LinearLayout implements BaseLoaderFooter {
    private static final String TAG = "ShoppingLoadingFooter";
    private RelativeLayout mNoMoreRl;
    private RelativeLayout mLoadingRl;
    private TextView mNoMoreTv, mLoadingTv;
    private ProgressBar mLoadingPb;

    private String noMoreText = "没有更多了";
    private String loadingText = "正在加载中...";
    private int left, top, right, bottom;
    public int mMeasuredHeight;
    private RelativeLayout mContainer;
    private LinearLayout mOtherLl; //其他view,在脚底下,没有更多的时候显示
    private View otherView;
    private int mState = STATE_COMPLETE;
    private boolean isShowNoMoreTip = true; //没有更多时是否显示提示

    public void setShowNoMoreTip(boolean showNoMoreTip) {
        isShowNoMoreTip = showNoMoreTip;
    }

    public void setNoMoreText(String noMoreText) {
        this.noMoreText = noMoreText;
        mNoMoreTv.setText(Html.fromHtml(noMoreText));
    }

    public ShoppingLoadingFooter(Context context) {
        this(context, null);
    }

    public ShoppingLoadingFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShoppingLoadingFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        setLayoutParams(lp);
        setPadding(0, 0, 0, 0);
        mContainer = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_refresh_footer, null);
        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mNoMoreRl = findViewById(R.id.rl_noMore);
        mLoadingRl = findViewById(R.id.rl_loading);
        mNoMoreTv = findViewById(R.id.tv_no_more);
        mLoadingTv = findViewById(R.id.tv_loading);
        mLoadingPb = findViewById(R.id.pb_loading);
        mOtherLl = findViewById(R.id.ll_other);
        mNoMoreTv.setText(Html.fromHtml(noMoreText));
        mLoadingTv.setText(Html.fromHtml(loadingText));

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setState(int state) {
        this.mState = state;
        LogUtils.e(TAG, "STATE: " + state);
        switch (state) {
            case STATE_SCROLL:
                this.setPadding(left, top, right, bottom);
                break;
            case STATE_LOADING:
                mLoadingRl.setVisibility(View.VISIBLE);
                mNoMoreRl.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                this.setVisibleHeight(mMeasuredHeight);
                this.setPadding(left, top, right, bottom);
                LogUtils.e(TAG, "正在加载...");
                break;
            case STATE_COMPLETE:
                mLoadingRl.setVisibility(View.VISIBLE);
                mNoMoreRl.setVisibility(View.GONE);
                this.setVisibility(View.GONE);
                this.setPadding(0, 0, 0, 0);
                this.setVisibleHeight(0);
                break;
            case STATE_NOMORE:
                mLoadingRl.setVisibility(View.GONE);
                if (isShowNoMoreTip) {
                    mNoMoreRl.setVisibility(View.VISIBLE);
                    this.setVisibility(View.VISIBLE);
                    this.setPadding(left, top, right, bottom);
                    this.setVisibleHeight(mMeasuredHeight);
                } else {
                    this.setVisibility(View.GONE);
                    this.setPadding(0, 0, 0, 0);
                    this.setVisibleHeight(0);
                }
                // LogUtils.e(TAG, "没有更多 top:" + getTop());
                break;

        }
        if (null != otherView) {
            if (state == STATE_NOMORE || state == STATE_ONLY_OTHER) {
                showOtherView();
            } else if (state != STATE_SCROLL) {
                mOtherLl.setVisibility(View.GONE);
                mOtherLl.removeAllViews();
                LogUtils.e(TAG, "移除OtherView");
            }

        }
    }


    public void setLoadingPadding(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    @Override
    public void setLoadingHint(String loading) {

    }

    @Override
    public void setNoMoreHint(String noMore) {

    }

    @Override
    public void setLoadImageView(int resId) {

    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public int getOriginHeight() {
        return isShowNoMoreTip ? mMeasuredHeight : 0;
    }

    public void addOtherView(View view) {
        this.otherView = view;
    }

    /**
     * 显示其他View
     */
    private void showOtherView() {
        if (mState != STATE_NOMORE) mLoadingRl.setVisibility(View.GONE); //初始高度=0要把"正在加载..."隐藏
        mOtherLl.setVisibility(View.VISIBLE);
        // otherView.measure(0, 0);
        if (mOtherLl.getChildCount() == 0) {
            mOtherLl.addView(otherView);
            LogUtils.e(TAG, "添加OtherView");
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mContainer.setLayoutParams(lp);
        this.setVisibility(View.VISIBLE);
    }

    public boolean isOtherViewShow() {
        return this.isShown() && mOtherLl.isShown() && mOtherLl.getChildCount() > 0;
    }
}
