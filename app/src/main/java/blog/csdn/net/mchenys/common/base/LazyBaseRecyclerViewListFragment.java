package blog.csdn.net.mchenys.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * 懒加载fragment列表基类
 * Created by mChenys on 2017/12/27.
 */
public abstract class LazyBaseRecyclerViewListFragment<T> extends BaseRecyclerViewListFragment<T> {
    private boolean isPrepared;
    private boolean isVisible;
    private boolean isFirstInLoad = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLazyLoad(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    @Override
    public void onFragmentResume() {
        isVisible = true;
        lazyLoad();

    }

    @Override
    public void onFragmentPause() {
        isVisible = false;
    }

    protected void lazyLoad() {
        if (!isVisible || !isPrepared || !isFirstInLoad) {
            return;
        }
        isFirstInLoad = false;
        loadData(isforceRefresh);
    }


}
