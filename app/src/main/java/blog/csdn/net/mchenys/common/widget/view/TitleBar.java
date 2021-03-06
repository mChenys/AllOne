package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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
    private RelativeLayout mCustContainerRl;
    private RelativeLayout mNormalBarRl;

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
        mCustContainerRl = findViewById(R.id.rl_cust_container);
        mNormalBarRl = findViewById(R.id.rl_normal_bar);
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


    public void setmTitleBarHeight(int height) {
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) this.getLayoutParams();
        layoutParams.height = height;
        this.setLayoutParams(layoutParams);
    }

    public void setLeftTvSize(int size) {
        mLeftTv.setTextSize(size);
    }

    public void setLeftTvStyleBold(boolean isBold) {
        mLeftTv.getPaint().setFakeBoldText(isBold);
    }

    public void setLeftTvMarginLeft(int leftMagin) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLeftTv.getLayoutParams();
        layoutParams.leftMargin = leftMagin;
        mLeftTv.setLayoutParams(layoutParams);
    }

    public void setTvMargin(int leftMagin, int topMargin, int rightMargin, int bottomMargin) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLeftTv.getLayoutParams();
        layoutParams.leftMargin = leftMagin;
        layoutParams.topMargin = topMargin;
        layoutParams.rightMargin = rightMargin;
        layoutParams.bottomMargin = bottomMargin;
        mLeftTv.setLayoutParams(layoutParams);
    }

    public void setRightTv(String title, OnClickListener onClickListener) {
        mRightTv.setText(title);
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setOnClickListener(onClickListener);
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

    public void setRightIcon1Margin(int leftMagin, int topMargin, int rightMargin, int bottomMargin) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRightIcon1.getLayoutParams();
        layoutParams.leftMargin = leftMagin;
        layoutParams.topMargin = topMargin;
        layoutParams.rightMargin = rightMargin;
        layoutParams.bottomMargin = bottomMargin;
        mRightIcon1.setLayoutParams(layoutParams);
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


    public void setCenterTvColor(int color) {
        mCenterTv.setTextColor(color);
    }

    public void addCustView(View view) {
        if (null != view) {
            mCustContainerRl.addView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mNormalBarRl.setVisibility(View.GONE);
            mCustContainerRl.setVisibility(View.VISIBLE);
        }
    }
}
