package blog.csdn.net.mchenys.common.widget.recycleview.refresh.header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.BaseRefreshHeader;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.RefreshAnimatorListener;


/**
 * 下拉刷新视图
 * Created by mChenys on 2018/1/2.
 */
public class ShoppingRefreshHeader extends LinearLayout implements BaseRefreshHeader {
    private static final String TAG = "ModernRefreshHeader";
    private ImageView mRefreshImageView;
    private LinearLayout mContainer;
    private int mState = STATE_NORMAL;
    public int mMeasuredHeight;

    public ShoppingRefreshHeader(Context context) {
        this(context, null);
    }

    public ShoppingRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShoppingRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_refresh_header, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        setLayoutParams(lp);
        setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);
        mRefreshImageView = (ImageView) findViewById(R.id.img_choice);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }

    @Override
    public void onMove(float delta) {
        if (getVisibleHeight() > 0 || delta > 0) {
            int currHeight = (int) delta + getVisibleHeight();
            setVisibleHeight(currHeight);
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
                float rotate = currHeight / (float) mMeasuredHeight;
                // 不断下拉的过程中不断的旋转图片
                mRefreshImageView.setRotation(rotate * 360);
            }
        }
    }

    @Override
    public void releaseAction(final RefreshAnimatorListener l) {
        if (getVisibleHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
        }

        if (mState != STATE_REFRESHING) {
            smoothVisibleHeightTo(0, new MyAnimatorListener(l) {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (null != l) l.onCancelPullAnimEnd();
                }
            });
        }

        if (mState == STATE_REFRESHING) {
            smoothVisibleHeightTo(mMeasuredHeight, new MyAnimatorListener(l) {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startRotateAnim(l);
                }
            });
        }

    }

    @Override
    public void refreshComplete(final RefreshAnimatorListener l) {
        setState(STATE_DONE);
        smoothVisibleHeightTo(0, new MyAnimatorListener(l) {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setState(STATE_NORMAL);
                if (null != l) l.onAutoPullBackAnimEnd();
            }
        });

    }

    @Override
    public void setState(int state) {
        mState = state;
    }


    @Override
    public void autoRefresh(final RefreshAnimatorListener l) {
        setState(STATE_REFRESHING);
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), mMeasuredHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setVisibleHeight(value);
                l.onAutoPullDownAnim(value);
            }
        });
        if (null != l) animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (null != l) startRotateAnim(l);
            }
        });
        animator.start();
    }

    public void startRotateAnim(final RefreshAnimatorListener l) {
        RotateAnimation animation = new RotateAnimation(720, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (null != l) l.onStayAnimEnd();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRefreshImageView.startAnimation(animation);
    }

    @Override
    public void setRefreshImageView(int resId) {
        mRefreshImageView.setImageResource(resId);
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    @Override
    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void manuallyPushUp(float deltaY) {

    }

    private class MyAnimatorListener implements Animator.AnimatorListener {
        private RefreshAnimatorListener refreshAnimatorListener;

        public MyAnimatorListener(RefreshAnimatorListener refreshAnimatorListener) {
            this.refreshAnimatorListener = refreshAnimatorListener;
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        public void onAnimationUpdate(int deltaValue, float currValue, float finalValue) {
            refreshAnimatorListener.onAutoPullBackAnim(deltaValue, currValue, finalValue);
        }
    }

    private int lastValue;

    private void smoothVisibleHeightTo(final int destHeight, final MyAnimatorListener l) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setVisibleHeight(value);
                int deltaValue = value - lastValue;
                if (null != l) l.onAnimationUpdate(deltaValue, value, destHeight);
                lastValue = value;
            }
        });
        if (null != l) animator.addListener(l);
        animator.start();
    }


}
