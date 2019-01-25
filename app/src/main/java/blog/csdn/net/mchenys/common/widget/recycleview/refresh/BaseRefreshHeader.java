package blog.csdn.net.mchenys.common.widget.recycleview.refresh;

import android.view.View;

/**
 * Created by mChenys on 2017/3/2.
 */
public interface BaseRefreshHeader {



    int STATE_NORMAL = 0;
    int STATE_RELEASE_TO_REFRESH = 1;
    int STATE_REFRESHING = 2;
    int STATE_DONE = 3;

    void onMove(float deltaY);

    void releaseAction(RefreshAnimatorListener l);

    void autoRefresh(RefreshAnimatorListener l);

    void refreshComplete(RefreshAnimatorListener l);

    void setState(int state);

    void setRefreshImageView(int resId);

    int getVisibleHeight();

    int getState();

    View getView();

}