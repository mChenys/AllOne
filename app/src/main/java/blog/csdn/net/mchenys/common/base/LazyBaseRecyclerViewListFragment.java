package blog.csdn.net.mchenys.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

/**
 * 懒加载fragment列表基类
 * Created by mChenys on 2017/12/27.
 */
public abstract class LazyBaseRecyclerViewListFragment<T> extends BaseRecyclerViewListFragment<T> {
    private boolean hasLoad;//是否已经加载数据
    private boolean isInit;//是否已经初始化

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLazyLoad(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        lazyLoad();
        if (isVisibleToUser) {
            Log.e("cys", "setUserVisibleHint->isInit=" + isInit + " hasLoad=" + hasLoad);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isInit = true;
        lazyLoad();
        Log.e("cys", "onViewCreated->isInit=" + isInit + " hasLoad=" + hasLoad);
    }


    protected void lazyLoad() {
        if (mData.isEmpty()) hasLoad = false;//避免首次加载不出数据后,切换回来还是加载不出来
        if (!isInit || hasLoad) return;
        if (getUserVisibleHint()) {
            loadData(isforceRefresh);
            hasLoad = true;
        }
    }

}
