package blog.csdn.net.mchenys.common.widget.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;

/**
 * 通用的设置数据的BaseHolder,可用于所有View
 * Usage:
 * class MyHolderTest extends Activity {
 *
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * <p/>
 * FrameLayout mainLayout = new FrameLayout(this);
 * TitleHolder titleHolder = new TitleHolder(this);
 * try {
 * titleHolder.setData(new JSONObject("{'title':'this is a test'}"));
 * } catch (JSONException e) {
 * e.printStackTrace();
 * }
 * mainLayout.addView(titleHolder.getContentView());
 * setContentView(mainLayout);
 * }
 * <p/>
 * class TitleHolder extends BaseViewHolder<JSONObject> {
 * TextView mTextView;
 * <p/>
 * public TitleHolder(Context context) {
 * super(context);
 * }
 * @Override public View onCreateView() {
 * View layout = View.inflate(mContext, R.layout.activity_test, null);
 * mTextView = getView(R.id.tv_info);
 * return layout;
 * }
 * @Override public void onBindView(JSONObject jsonObject) {
 * mTextView.setText(jsonObject.optString("title"));
 * }
 * }
 * }
 * Created by mChenys on 2016/5/23.
 */
public abstract class BaseViewHolder<D> {

    protected View mContentView;
    private D mData;
    protected Context mContext;
    public ViewGroup parent;


    public BaseViewHolder(Context context) {
        this.mContext = context;
        long start = System.currentTimeMillis();
        initCus();
        mContentView = onCreateView();
       
    }


    protected abstract void initCus();
    /**
     * 必须调用该方法,否则onBindView不会回调
     *
     * @param d
     */
    public final void setData(D d) {
        this.mData = d;
        long start = System.currentTimeMillis();
        onBindView(mData);
    }

    /**
     * 获取根View,在setData之前调用返回的则是没有填充数据之前的
     *
     * @return 如果getTestFileName()有值, 则返回的是测试结果, 否则返回的是正式结果
     */
    public final View getContentView() {
        return mContentView;
    }

    /**
     * 返回已设置的数据
     *
     * @return
     */
    public final D getData() {
        return mData;
    }

    public final <V extends View> V getView(int viewId) {
        return (V) getView(mContentView, viewId);
    }

    public final <V extends View> V getView(View group, int viewId) {
        return (V) group.findViewById(viewId);
    }

    /**
     * 由具体的子类去实现布局的初始化
     *
     * @return 返回初始化后的View
     */
    public abstract View onCreateView();

    /**
     * 由子类去实现刷新界面数据的方法,在setData方法中回调
     *
     * @param d 具体的数据
     */
    public abstract void onBindView(D d);

    /**
     * 单元自测
     *
     * @param jsonObject 模拟数据,getTestFileName方法返回
     */
    public abstract void onBindViewTest(JSONObject jsonObject);

    /**
     * 返回模拟数据所在文件的名名称,例如test.json
     *
     * @return
     */
    public abstract String getTestFileName();

    /**
     * 隐藏根View
     */
    public void hide() {
        mContentView.setVisibility(View.GONE);
    }

    /**
     * 显示根View
     */
    public void show() {
        mContentView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置根View
     *
     * @param contentView
     */
    public final void setContentView(View contentView) {
        this.mContentView = contentView;
    }

    /**
     * 设置根View
     *
     * @param contentView
     */
    public final void setContentView(int contentView) {
        this.mContentView = View.inflate(mContext, contentView, null);
    }

    /**
     * 由子类去实现,在调用方和Holder直接需要交互的时候使用
     *
     * @param arg
     */
    public Object setCallback(Object... arg) {
        return null;
    }

    /**
     * 绑定对应holder的contentView到对应 模块中
     *
     * @param parent
     */
    public final void bind(ViewGroup parent) {
        
        ViewGroup viewGroup = (ViewGroup) mContentView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(mContentView);
        }
        parent.addView(mContentView, 0);
        this.parent = parent;
    }

    public final BaseViewHolder setTextView(int tvId, String msg) {
        return setTextView(mContentView, tvId, msg, -1);
    }

    public final BaseViewHolder setTextView(int tvId, String msg, int maxLength) {
        return setTextView(mContentView, tvId, msg, maxLength);
    }

    public final BaseViewHolder setTextView(View group, int tvId, String msg) {
        return setTextView(group, tvId, msg, -1);
    }


    public final BaseViewHolder setTextView(View group, int tvId, String msg, int maxLength) {
        if (StringUtils.isEmpty(msg)) {
            hide(group, tvId);
        } else {
            show(group, tvId);
            if (maxLength != -1 && msg.length() > maxLength) {
                msg = msg.substring(maxLength);
            }
            ((TextView) group.findViewById(tvId)).setText(msg);
        }
        return this;
    }

    public final BaseViewHolder setViewBackground(int resId, Drawable drawable) {
        return setViewBackground(mContentView, resId, drawable);
    }

    public final BaseViewHolder setViewBackground(View group, int resId, Drawable drawable) {
        group.findViewById(resId).setBackground(drawable);
        return this;
    }

    public final BaseViewHolder setImageView(int ivId, String url) {
        return setImageView(mContentView, ivId, url);
    }

    public final BaseViewHolder setImageView(View group, int ivId, String url) {
        ImageLoadUtils.disPlay(url, ((ImageView) group.findViewById(ivId)), null);
        return this;
    }

    public final BaseViewHolder hide(int resId) {
        return hide(mContentView, resId);
    }

    public final BaseViewHolder hide(View group, int resId) {
        group.findViewById(resId).setVisibility(View.GONE);
        return this;
    }

    public final BaseViewHolder show(int resId) {
        return show(mContentView, resId);
    }

    public final BaseViewHolder show(View group, int resId) {
        group.findViewById(resId).setVisibility(View.VISIBLE);
        return this;
    }

    public final BaseViewHolder visiable(View group, int resId, boolean show) {
        if (show) {
            show(group, resId);
        } else {
            hide(group, resId);
        }
        return this;
    }

    public final BaseViewHolder setLinearLayoutParams(int resId, int height, int width) {
        return setLinearLayoutParams(mContentView, resId, height, width);
    }

    public final BaseViewHolder setLinearLayoutParams(View group, int resId, int height, int width) {
        View view = group.findViewById(resId);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (null == params) {
            params = new LinearLayout.LayoutParams(width, height);
        } else {
            params.width = width;
            params.height = height;
        }
        view.setLayoutParams(params);
        return this;
    }

    public final BaseViewHolder setRelativeLayoutParams(int resId, int height, int width) {
        return setRelativeLayoutParams(mContentView, resId, height, width);
    }

    public final BaseViewHolder setRelativeLayoutParams(View group, int resId, int height, int width) {
        View view = group.findViewById(resId);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (null == params) {
            params = new RelativeLayout.LayoutParams(width, height);
        } else {
            params.width = width;
            params.height = height;
        }
        view.setLayoutParams(params);
        return this;
    }

    public final BaseViewHolder onClick(int resId, View.OnClickListener onClickListener) {
        return onClick(mContentView, resId, onClickListener);
    }

    public final BaseViewHolder onClick(View group, int resId, View.OnClickListener onClickListener) {
        group.findViewById(resId).setOnClickListener(onClickListener);
        return this;
    }

    public final boolean isVisiable() {
        return mContentView.getVisibility() == View.VISIBLE;
    }

    public final int getViewHeight() {
        int height = mContentView.getMeasuredHeight();
        if (0 == height) {
            mContentView.measure(0, 0);
            height = mContentView.getMeasuredHeight();
        }
        return height;
    }

    public final int getViewWeight() {
        int width = mContentView.getMeasuredWidth();
        if (0 == width) {
            mContentView.measure(0, 0);
            width = mContentView.getMeasuredWidth();
        }
        return width;
    }

    public final BaseViewHolder setScaleX(View target, long duration, float... scale) {
        getScale(target, "scaleX", scale).setDuration(duration).start();
        return this;
    }

    public final BaseViewHolder setScaleY(View target, long duration, float... scale) {
        getScale(target, "scaleY", scale).setDuration(duration).start();
        return this;
    }

    public final BaseViewHolder setScaleXY(View target, long duration, float... scale) {
        ObjectAnimator animatorX = getScale(target, "scaleX", scale);
        ObjectAnimator animatorY = getScale(target, "scaleY", scale);
        return withAnimator(duration, animatorX, animatorY);
    }

    public final ObjectAnimator getScale(View target, String type, float... scale) {
        return ObjectAnimator.ofFloat(target, type, scale);
    }

    public final BaseViewHolder setAlpha(View target, long duration, float... alpha) {
        getAlpha(target, alpha).setDuration(duration).start();
        return this;
    }

    public final ObjectAnimator getAlpha(View target, float... alpha) {
        return ObjectAnimator.ofFloat(target, "alpha", alpha);
    }

    public final void setTranslationY(View target, long duration, float... y) {
        getTranslation(target, "translationY", y).setDuration(duration).start();
    }

    public final void setTranslationX(View target, long duration, float... x) {
        getTranslation(target, "translationX", x).setDuration(duration).start();
    }

    public final ObjectAnimator getTranslation(View target, String type, float... v) {
        return ObjectAnimator.ofFloat(target, type, v);
    }

    /**
     * 多个动画同时执行
     *
     * @param duration
     * @param animators
     * @return
     */
    public final BaseViewHolder withAnimator(long duration, ObjectAnimator... animators) {
        if (null != animators && animators.length > 0) {
            AnimatorSet set = new AnimatorSet();
            AnimatorSet.Builder builder = set.play(animators[0]);
            for (int i = 1; i < animators.length; i++) {
                builder.with(animators[i]);
            }
            set.setDuration(duration);
            set.start();
        }
        return this;
    }

    public void onRefresh(boolean isRefresh) {
        //empty
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //empty
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
