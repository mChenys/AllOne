package blog.csdn.net.mchenys.common.widget.recycleview.refresh;

import android.support.v7.widget.RecyclerView;

import cn.com.pconline.shopping.common.utils.DisplayUtils;


/**
 * Created by mChenys on 2017/3/3.
 */
public abstract class SimplePullScrollListener implements OnPullScrollListener {
    private int dx, dy;
    private int newState;
    private int totalX, totalY;
    private boolean isScrolling;
    private int oldScreemCount = -1;

    public void resetTotalXY(int x, int y) {
        totalX = x;
        totalY = y;
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //IDE状态onScrolled没有调用,所以在这里补调
        this.newState = newState;
        this.isScrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
        onScroll(recyclerView, dx, dy, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy, int firstHeaderViewTop, int loaderFooterViewTop) {
        this.dx = dx;
        this.dy = dy;
        this.totalX += dx;
        this.totalY += dy;
        if(firstHeaderViewTop==0){
            totalY = 0;
        }
        onScroll(recyclerView, dx, dy, newState);
        onScroll(recyclerView, dx, dy, totalX, totalY, firstHeaderViewTop, loaderFooterViewTop);
    }

    public void onScroll(RecyclerView recyclerView, int dx, int dy, int newState) {
        //综合了上面2个方法的参数回调
    }

    /**
     * @param recyclerView
     * @param dx
     * @param dy
     * @param totalX       总距离
     * @param totalY       总距离
     */
    public void onScroll(RecyclerView recyclerView, int dx, int dy, int totalX, int totalY, int firstHeaderViewTop, int loaderFooterViewTop) {
        int count = totalY / DisplayUtils.getScreenHeigth(recyclerView.getContext());
        if (oldScreemCount == count) return;
        onScreenPageCountChange(count);
        oldScreemCount = count;
    }

    /**
     * 滚屏监听
     *
     * @param count 第几屏幕
     */
    public void onScreenPageCountChange(int count) {

    }

    @Override
    public void onFinishRefresh() {
        resetTotalXY(0, 0);
    }


    @Override
    public void onPulling(int deltaY) {
        resetTotalXY(0, 0);
    }

    @Override
    public void onPullCancel() {
        resetTotalXY(0, 0);
    }

    public float getTotalY() {
        return totalY;
    }

    public int getTotalX() {
        return totalX;
    }

    @Override
    public void onFinishLoader() {
    }

    @Override
    public void onLoaderHidden() {

    }

    @Override
    public void onLoaderNoMore() {

    }

    @Override
    public void onAutoPullBackAnimating(int deltaValue, float currValue, float finalValue) {

    }

    @Override
    public void onRefreshStayMove(int headerVisibleHeight, boolean pullDown) {

    }

    @Override
    public void onScrolled(int firstVisibleItemPosition, int lastVisibleItemPosition, int lastDataCount,boolean isApiScroll, boolean isPullingUp,boolean isLoadingData) {

    }
}
