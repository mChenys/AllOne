package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.pconline.shopping.R;


/**
 * 用户界面交互 组合控件(Loading,Exception,NoData)
 * Created by mChenys on 2016/10/24.
 */
public class UEView extends FrameLayout {

    private LoadingView mLoadingView;
    private NetworkErrorView mErrorView;
    private LinearLayout mNoDataView;
    private TextView mNoDataTv;
    private FrameLayout mOtherFl;
    public static final int STATE_LOADING = 0;
    public static final int STATE_NODATA = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_HIDDEN = 3;
    public static final int STATE_OTHER = 4;
    private OnPageChangeListener mOnPageChangeListener;

    public interface OnPageChangeListener {
        void onChange(int state);
    }

    public void setOnPageChangeListener(OnPageChangeListener l) {
        this.mOnPageChangeListener = l;
    }

    public UEView(Context context) {
        this(context, null);
    }

    public UEView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UEView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_ue, this, true);
        setBackgroundColor(getResources().getColor(R.color.white));
        initView();
    }

    private void initView() {
        mLoadingView = findViewById(R.id.loadView);
        mErrorView = findViewById(R.id.error);
        mNoDataView = findViewById(R.id.ll_no_data);
        mNoDataTv = findViewById(R.id.tv_no_data);
        mOtherFl = findViewById(R.id.fl_other);
    }


    /**
     * 设置没有数据显示的文字
     *
     * @param text
     * @return
     */
    public UEView setNoDataText(String text) {
        mNoDataTv.setText(text);
        mNoDataTv.setVisibility(View.VISIBLE);
        return this;
    }


    /**
     * 显示加载
     *
     * @return
     */
    public UEView showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        if (null != mOnPageChangeListener) mOnPageChangeListener.onChange(STATE_LOADING);
        return this;
    }

    /**
     * 显示异常
     *
     * @return
     */
    public UEView showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mNoDataView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        if (null != mOnPageChangeListener) mOnPageChangeListener.onChange(STATE_ERROR);
        return this;
    }

    /**
     * 显示无数据
     *
     * @return
     */
    public UEView showNoData() {
        mNoDataView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        if (null != mOnPageChangeListener) mOnPageChangeListener.onChange(STATE_NODATA);
        return this;
    }

    /**
     * 重新加载监听
     *
     * @param clickListener
     * @return
     */
    public UEView setOnReloadListener(OnClickListener clickListener) {
        mErrorView.setOnClickListener(clickListener);
        return this;
    }

    public void hideAll() {
        mNoDataView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        setVisibility(View.GONE);
        if (null != mOnPageChangeListener) mOnPageChangeListener.onChange(STATE_HIDDEN);
    }

    public void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
        setVisibility(View.GONE);
    }

    public void hideError() {
        mErrorView.setVisibility(View.GONE);
        setVisibility(View.GONE);
    }

    public void hideNoData() {
        mNoDataView.setVisibility(View.GONE);
        setVisibility(View.GONE);
    }

    /**
     * 显示没有数据的布局,外部传入
     *
     * @param view
     */
    public void setNoDataView(View view) {
        mNoDataView.removeAllViews();
        mNoDataView.addView(view);
    }

    /**
     * 添加其他控件
     *
     * @param view
     */
    public void addOtherView(View view) {
        mNoDataView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mOtherFl.setVisibility(View.VISIBLE);
        setVisibility(View.VISIBLE);
        mOtherFl.addView(view);
        if (null != mOnPageChangeListener) mOnPageChangeListener.onChange(STATE_OTHER);
    }

    public View getOtherView() {
        return mOtherFl.getChildAt(0);
    }

    public boolean isNoDataShow() {
        return mNoDataView.isShown();
    }

}
