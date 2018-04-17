package blog.csdn.net.mchenys.common.widget.recycleview.refresh;

import android.view.View;

/**
 * Created by mChenys on 2017/3/2.
 */
public interface BaseLoaderFooter {
    int STATE_LOADING = 0;
    int STATE_COMPLETE = 1;
    int STATE_NOMORE = 2;
    int STATE_SCROLL = 3;
    int STATE_ONLY_OTHER = 4;

    View getView();

    void setState(int state);

    void setLoadingHint(String loading);

    void setNoMoreHint(String noMore);

    void setLoadImageView(int resId);

    int getState();

    int getOriginHeight();

}
