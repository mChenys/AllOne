package blog.csdn.net.mchenys.common.widget.recycleview;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.util.DisplayMetrics;

/**
 *  控制LinearLayoutManager的smoothToPosition效果
 *  Created by mChenys on 2019/1/4.
 */
public class MySmoothTopScroller extends LinearSmoothScroller {
    private int offsetPx;//置顶偏移量

    public MySmoothTopScroller(Context context, int offsetPx) {
        super(context);
        this.offsetPx = offsetPx;
    }

    /**
     * @param viewStart      Item的top或left位置
     * @param viewEnd        Item的bottom或right位置
     * @param boxStart       RecyclerView的开始位置
     * @param boxEnd         RecyclerView的结束位置
     * @param snapPreference 判断滑动方向的标识（The edge which the view should snap to when entering the visible
     *                       area. One of {@link #SNAP_TO_START}, {@link #SNAP_TO_END} or
     *                       {@link #SNAP_TO_END}.）
     * @return 移动偏移量, 返回负数则item会从顶部移除, 正数就是距离顶部的距离
     */
    @Override
    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
        return boxStart - viewStart - offsetPx;// 这里是关键，得到的就是置顶的偏移量
    }


    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return ((LinearLayoutManager) getLayoutManager()).computeScrollVectorForPosition(targetPosition);
    }

    //This returns the milliseconds it takes to scroll one pixel.
    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        //返回滑动一个pixel需要多少毫秒
        return 0.3f;
    }
}