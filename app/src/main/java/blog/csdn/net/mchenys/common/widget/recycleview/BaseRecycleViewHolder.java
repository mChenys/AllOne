package blog.csdn.net.mchenys.common.widget.recycleview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;


/**
 * 通用RecycleView ViewHolder类
 * Created by mChenys on 2017/2/16.
 */
public class BaseRecycleViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> viewSparseArray;

    public BaseRecycleViewHolder(View itemView) {
        super(itemView);
        viewSparseArray = new SparseArray<>();
    }

    /**
     * 获取item的控件的方法
     */
    public <T extends View> T getView(int viewId) {
        View view = viewSparseArray.get(viewId);
        if (null == view) {
            view = itemView.findViewById(viewId);
            viewSparseArray.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置TextView的文本和点击监听
     */
    public BaseRecycleViewHolder setTextView(int viewId, String text, View.OnClickListener onClickListener) {
        TextView textView = getView(viewId);
        textView.setText(text);
        if (null != onClickListener) {
            textView.setOnClickListener(onClickListener);
        }
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecycleViewHolder setImageRes(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    /**
     * 设置TextView的文本
     */
    public BaseRecycleViewHolder setTextView(int viewId, String text) {
        return setTextView(viewId, text, null);
    }


    /**
     * 设置图片
     */
    public BaseRecycleViewHolder setImageUrl(int viewId, String url) {
        if (!StringUtils.isEmpty(url)) {
            ImageView imageView = getView(viewId);
            ImageLoadUtils.disPlay(url, imageView);
        }
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecycleViewHolder setImageUrl(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        ImageLoadUtils.disPlay(resId, imageView);
        return this;
    }

    /**
     * 设置圆形图片
     */
    public BaseRecycleViewHolder setCircleImageUrl(int viewId, String url) {
        if (!StringUtils.isEmpty(url)) {
            ImageView imageView = getView(viewId);
            ImageLoadUtils.disPlayWithCircle(url, imageView);
        }
        return this;
    }


    /**
     * 设置圆角图片
     */
    public BaseRecycleViewHolder setCornerImageUrl(int viewId, int resId, int radiusDip) {
        ImageView imageView = getView(viewId);
        ImageLoadUtils.disPlayWithCorner(resId, imageView, radiusDip);
        return this;
    }

    /**
     * 设置圆角图片
     */
    public BaseRecycleViewHolder setCornerImageUrl(int viewId, int resId, int radiusDip, int h, int w) {
        ImageView imageView = getView(viewId);
        ImageLoadUtils.disPlayWithCorner(resId, imageView, radiusDip, w, h);
        return this;
    }

    public BaseRecycleViewHolder setImageUrl(int viewId, String url, int w, int h) {
        if (!StringUtils.isEmpty(url)) {
            ImageView imageView = getView(viewId);
            ImageLoadUtils.disPlay(url, imageView, w, h);
        }
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecycleViewHolder setImageUrl(ImageView imageView, String url) {
        ImageLoadUtils.disPlay(url, imageView);
        return this;
    }


    public BaseRecycleViewHolder hideView(int resId) {
        View view = getView(resId);
        if (null != view) view.setVisibility(View.GONE);
        return this;
    }

    public BaseRecycleViewHolder showView(int resId) {
        View view = getView(resId);
        if (null != view) view.setVisibility(View.VISIBLE);
        return this;
    }

    public ViewGroup.LayoutParams getLayoutParams(int resId) {
        return getView(resId).getLayoutParams();
    }

    public BaseRecycleViewHolder setLayoutParams(int resId, ViewGroup.LayoutParams params) {
        getView(resId).setLayoutParams(params);
        return this;
    }
    public BaseRecycleViewHolder setHeaderImageView(int resId, String url) {
        ImageView view = getView(resId);
        if (null != view) {
            ImageLoadUtils.disPlayWithCircle(url, view, R.drawable.bg_bbs_default_avatar);
        }
        return this;
    }


    public BaseRecycleViewHolder displayWithRound(int resId, String url, int radiusDip) {
        ImageView view = getView(resId);
        if (null != view) {
            ImageLoadUtils.disPlayWithCorner(url, view, radiusDip);

        }
        return this;
    }

    public BaseRecycleViewHolder displayWithRoundBySize(int resId, String url,int w,int h, int radiusDip) {
        ImageView view = getView(resId);
        if (null != view) {
            ImageLoadUtils.displayWithRound(url, view, w,h,radiusDip);

        }
        return this;
    }

    public void hide(int resId) {
        View v = getView(resId);
        if (null != v)
            v.setVisibility(View.GONE);
    }

    public void show(int resId) {
        View v = getView(resId);
        if (null != v)
            v.setVisibility(View.VISIBLE);
    }
}