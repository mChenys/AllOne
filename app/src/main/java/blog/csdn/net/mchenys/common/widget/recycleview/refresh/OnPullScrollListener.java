package blog.csdn.net.mchenys.common.widget.recycleview.refresh;

import android.support.v7.widget.RecyclerView;

/**
 * 滚动状态监听
 * Created by mChenys on 2017/3/3.
 */
public interface OnPullScrollListener {

    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    void onScrolled(RecyclerView recyclerView, int dx, int dy, int firstHeaderViewTop, int loaderFooterViewTop);


    void onFinishRefresh();

    void onFinishLoader();
    //下拉距离监听
    void onPulling(int deltaY);

    void onPullCancel();

    void onLoaderHidden();

    void onLoaderNoMore();

    void onAutoPullBackAnimating(int deltaValue, float currValue, float finalValue);
    //未触发刷新的滚动
    void onRefreshStayMove(int headerVisibleHeight, boolean pullDown);

    void onScrolled(int firstVisibleItemPosition, int lastVisibleItemPosition, int lastDataCount, boolean isApiScroll, boolean isPullingUp, boolean isLoadingData);
}
