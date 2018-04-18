package blog.csdn.net.mchenys.common.widget.listview;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;


/**
 * 用于设置ListView数据的holder
 * Created by Chenys on 2015/5/10.
 */
public class ListViewHolder {
    private int mPosition;
    private View mConvertView;
    private SparseArray<View> viewSparseArray;

    /**
     * 构造方法初始化
     *
     * @param context
     * @param resId
     * @param position
     */
    public ListViewHolder(Context context, int resId, int position) {
        mPosition = position;
        viewSparseArray = new SparseArray<>();
        mConvertView = View.inflate(context, resId, null);
        mConvertView.setTag(this);
    }

    /**
     * 定义获取ViewHolder的方法
     *
     * @param context
     * @param convertView
     * @param resId
     * @param position
     * @return
     */
    public static ListViewHolder getViewHolder(Context context, View convertView, int resId, int position) {
        if (null == convertView) {
            return new ListViewHolder(context, resId, position);
        } else {
            ListViewHolder holder = (ListViewHolder) convertView.getTag();
            holder.mPosition = position; //更新position的位置
            return holder;
        }
    }

    /**
     * 定义获取convertView的方法
     */
    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 获取item的控件的方法
     */
    public <T extends View> T getView(int viewId) {
        View view = viewSparseArray.get(viewId);
        if (null == view) {
            view = mConvertView.findViewById(viewId);
            viewSparseArray.put(viewId, view);
        }
        return (T) view;
    }

    public int getPosition() {
        return mPosition;
    }

    /**
     * 设置TextView的文本和点击监听
     */
    public ListViewHolder setTextView(int viewId, String text, View.OnClickListener onClickListener) {
        TextView textView = getView(viewId);
        textView.setText(text);
        if (null != onClickListener) {
            textView.setOnClickListener(onClickListener);
        }
        return this;
    }

    /**
     * 设置TextView的文本
     */
    public ListViewHolder setTextView(int viewId, String text) {
        return setTextView(viewId, text, null);
    }


    /**
     * 设置网络图片
     */
    public ListViewHolder setImageUrl(int viewId, String url) {
        ImageView imageView = getView(viewId);
        ImageLoadUtils.disPlay(url, imageView, null);
        return this;
    }

    /**
     * 设置网络图片
     */
    public ListViewHolder setImageUrl(ImageView imageView, String url) {
        ImageLoadUtils.disPlay(url, imageView, null);
        return this;
    }

    public ListViewHolder hide(int viewId) {
        getView(viewId).setVisibility(View.GONE);
        return this;
    }

    public ListViewHolder show(int viewId) {
        getView(viewId).setVisibility(View.VISIBLE);
        return this;
    }
}
