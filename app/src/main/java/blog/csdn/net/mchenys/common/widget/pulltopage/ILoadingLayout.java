package blog.csdn.net.mchenys.common.widget.pulltopage;

import android.view.View;

/**
 * Created by mChenys on 2018/4/20.
 */

interface ILoadingLayout {

    //重置
    void reset();

    //释放加载
    void releaseToRefresh();

    //开始下拉时执行
    void startPull();

    //正在加载
    void loading();

    //下拉刷新
    void pullToRefresh();

    View getView();

    void setSecondTip(String downTitle);

}
