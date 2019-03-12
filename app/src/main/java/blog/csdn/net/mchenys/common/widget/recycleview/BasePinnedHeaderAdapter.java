package blog.csdn.net.mchenys.common.widget.recycleview;


import android.content.Context;

import java.util.List;

public abstract class BasePinnedHeaderAdapter<T> extends BaseRecycleViewAdapter<T> {

    public BasePinnedHeaderAdapter(Context ctx, List<T> data, int layoutId) {
        super(ctx, data, layoutId);
    }

    public BasePinnedHeaderAdapter(Context ctx, List<T> data, int[] layoutIds) {
        super(ctx, data, layoutIds);
    }

    /**
     * 判断该position对应的位置是要固定
     *
     * @param position adapter position
     * @return true or false
     */
    public abstract boolean isPinnedPosition(int position);


}
