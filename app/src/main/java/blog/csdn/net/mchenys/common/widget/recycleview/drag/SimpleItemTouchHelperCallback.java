package blog.csdn.net.mchenys.common.widget.recycleview.drag;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import blog.csdn.net.mchenys.common.utils.LogUtils;


/**
 * 处理RecycleView的选中,拖拽移动,拖拽删除的实现类
 * Created by mChenys on 2017/2/16.
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    public static final String TAG = "SimpleItemTouchHelperCallback";
    private ItemTouchHelperAdapter mAdapter;
    private boolean enableDrag;
    private boolean enableSwipe;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, boolean enableDrag,
                                         boolean enableSwipe) {
        this.mAdapter = adapter;
        this.enableDrag = enableDrag;
        this.enableSwipe = enableSwipe;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return this.enableDrag;//长按启用拖拽
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return this.enableSwipe; //是否启用拖拽删除
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = enableSwipe ? ItemTouchHelper.LEFT : 0; //只允许从右向左侧滑
        int dragFlags = enableDrag ? ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT : 0; //允许上下左右的拖动
        return makeMovementFlags(dragFlags, swipeFlags);
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        //通过接口传递拖拽交换数据的起始位置和目标位置的ViewHolder
        LogUtils.e(TAG, "#onMove");
        mAdapter.onItemMove(source, target);
        return true;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //移动删除回调,如果不用可以不用理
        LogUtils.e(TAG, "#onSwiped");
        mAdapter.onItemDissmiss(viewHolder);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        LogUtils.e(TAG, "#onSelectedChanged");
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            //当滑动或者拖拽view的时候通过接口返回该ViewHolder
            mAdapter.onItemSelect(viewHolder);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        LogUtils.e(TAG, "#clearView");
        if (!recyclerView.isComputingLayout()) {
            //当需要清除之前在onSelectedChanged或者onChildDraw,onChildDrawOver设置的状态或者动画时通过接口返回该ViewHolder
            mAdapter.onItemClear(viewHolder);        }

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        LogUtils.e(TAG, " dx: " + dX + " actionState:" + actionState + " isCurrentlyActive:" + isCurrentlyActive);
        //仅对侧滑状态下的效果做出改变
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            mAdapter.onItemSwipe(viewHolder, dX, dY);
        } else {
            //拖拽状态下不做改变，需要调用父类的方法
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }


}