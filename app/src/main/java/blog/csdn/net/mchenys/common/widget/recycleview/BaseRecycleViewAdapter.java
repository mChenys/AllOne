package blog.csdn.net.mchenys.common.widget.recycleview;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import blog.csdn.net.mchenys.common.utils.DisplayUtils;


/**
 * RecycleView adapter的基类
 * Created by mChenys on 2017/2/16.
 */
public abstract class BaseRecycleViewAdapter<T> extends RecyclerView.Adapter<BaseRecycleViewHolder> {
    protected List<T> mData;
    protected Context mContext;
    private int[] layoutIds;
    private BaseRecycleViewHolder mHolder;

    public BaseRecycleViewAdapter(Context ctx, List<T> data, int layoutId) {
        this(ctx, data, new int[]{layoutId});
    }

    public BaseRecycleViewAdapter(Context ctx, List<T> data, int[] layoutIds) {
        this.mContext = ctx;
        this.mData = data;
        this.layoutIds = layoutIds;
    }

    @Override
    public BaseRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecycleViewHolder[] holders = new BaseRecycleViewHolder[layoutIds.length];
        holders[viewType] = new BaseRecycleViewHolder(LayoutInflater.from(mContext).inflate(layoutIds[viewType], parent, false));
        mHolder = holders[viewType];
        return mHolder;
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }


    @Override
    public void onBindViewHolder(BaseRecycleViewHolder holder, final int position) {
        if (position >= 0 && null != mData) {
            T t = position < mData.size() ? mData.get(position) : null;
            holder.itemView.setTag(t);
            bindView(holder, position, t);
        }

    }


    protected abstract void bindView(BaseRecycleViewHolder holder, int position, T data);


    protected void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    protected void insertItem(T data) {
        mData.add(data);
        notifyItemInserted(mData.size() - 1);
    }

    protected void changeItem(T data, int position) {
        mData.set(position, data);
        notifyItemChanged(position);
    }

    protected void changeItem(T data, int position, int change) {
        mData.set(position, data);
        notifyItemChanged(position, change);
    }

    protected int getColor(int id) {
        return mContext.getResources().getColor(id);
    }

    protected int getPx(float dip) {
        return DisplayUtils.convertDIP2PX(mContext, dip);
    }

    public int getAdapterPosition() {
        return null == mHolder ? 0 : mHolder.getAdapterPosition();
    }

    public void notifyDataSetChangedDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 120);
    }

    public List<T> getData() {
        return mData;
    }
}

