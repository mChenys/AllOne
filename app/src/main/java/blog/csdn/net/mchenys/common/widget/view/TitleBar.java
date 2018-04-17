package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;


/**
 * 顶部公用布局
 * Created by mChenys on 2012/12/27.
 */
public class TitleBar extends RelativeLayout {
    private RelativeLayout mTitleBar;
    private TextView mLeftTv, mCenterTv, mRightTv, mSubTitle;
    private ImageButton mLeftIcon, mRightIcon1, mRightIcon2;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTitleBar = (RelativeLayout) View.inflate(getContext(), R.layout.layout_title_bar, this);
        mLeftTv = findViewById(R.id.tv_left);
        mCenterTv = findViewById(R.id.tv_center);
        mRightTv = findViewById(R.id.tv_right);
        mLeftIcon = findViewById(R.id.ib_left);
        mRightIcon1 = findViewById(R.id.ib_right_icon1);
        mRightIcon2 = findViewById(R.id.ib_right_icon2);
        mSubTitle = findViewById(R.id.sub_title);
        mLeftIcon.setVisibility(View.GONE);
        mLeftTv.setVisibility(View.GONE);
        mRightTv.setVisibility(View.GONE);
        mRightIcon1.setVisibility(View.GONE);
        mRightIcon2.setVisibility(View.GONE);
    }

    public void setmSubTitleVisible(int visible) {
        mSubTitle.setVisibility(visible);
    }

    public void setCenterTv(String title) {
        mCenterTv.setText(title);
        mCenterTv.setVisibility(View.VISIBLE);
    }

    public void setLeftTv(String title) {
        mLeftTv.setText(title);
        mLeftTv.setVisibility(View.VISIBLE);
    }

    public void setRightTv(String title) {
        mRightTv.setText(title);
        mRightTv.setVisibility(View.VISIBLE);
    }

    public void setLeft(Integer resId, String leftTitle, OnClickListener onClickListener) {
        if (null != resId) {
            mLeftIcon.setImageResource(resId);
        }
        mLeftIcon.setOnClickListener(onClickListener);
        mLeftIcon.setVisibility(View.VISIBLE);
        setLeftTv(leftTitle);
    }

    public void setRightIcon1(Integer resId, OnClickListener onClickListener) {
        if (null != resId) {
            mRightIcon1.setImageResource(resId);
        }
        mRightIcon1.setOnClickListener(onClickListener);
        setRightIcon1Visiable(true);
    }

    public void setRightIcon1Clickable(boolean canClick) {
        mRightIcon1.setClickable(canClick);
    }

    public void setRightIcon2(Integer resId, OnClickListener onClickListener) {
        if (null != resId) {
            mRightIcon2.setImageResource(resId);
        }
        mRightIcon2.setOnClickListener(onClickListener);
        setRightIcon2Visiable(true);
    }

    public void setRightIcon1Visiable(boolean visiable) {
        mRightIcon1.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    public void setRightIcon2Visiable(boolean visiable) {
        mRightIcon2.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    public ImageButton getRightIcon1() {
        return mRightIcon1;
    }

    public ImageButton getRightIcon2() {
        return mRightIcon2;
    }

    public void setBgColor(int color) {
        findViewById(R.id.rl_title_bar).setBackgroundColor(color);
    }

    public void setCenterTvColor(int color) {
        mCenterTv.setTextColor(color);
    }
}
