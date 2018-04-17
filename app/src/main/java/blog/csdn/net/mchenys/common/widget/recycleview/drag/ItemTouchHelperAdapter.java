package blog.csdn.net.mchenys.common.widget.recycleview.drag;

import android.support.v7.widget.RecyclerView;

/**
 * 定义RecycleView的Adapter和SimpleItemTouchHelperCallback直接的交互接口方法
 * Created by mChenys on 2017/2/16.
 */
public interface ItemTouchHelperAdapter {
    //数据交换
    void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target);

    //数据删除
    void onItemDissmiss(RecyclerView.ViewHolder source);

    //drag或者swipe选中
    void onItemSelect(RecyclerView.ViewHolder source);

    //状态清除
    void onItemClear(RecyclerView.ViewHolder source);

    //滑动过程
    void onItemSwipe(RecyclerView.ViewHolder source, float dX, float dY);
}