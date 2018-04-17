package blog.csdn.net.mchenys.common.widget.recycleview.refresh;

/**
 * Created by mChenys on 2017/3/20.
 */
public abstract class SimpleRefreshAnimatorListener implements RefreshAnimatorListener {
    @Override
    public void onStayAnimEnd() {

    }

    @Override
    public void onCancelPullAnimEnd() {

    }

    @Override
    public void onAutoPullDownAnim(int deltaY) {

    }

    @Override
    public void onAutoPullBackAnim(int deltaValue, float currValue, float finalValue) {
    }

    @Override
    public void onAutoPullBackAnimEnd() {

    }
}
