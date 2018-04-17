package blog.csdn.net.mchenys.common.widget.recycleview.refresh;

/**
 * Created by mChenys on 2017/3/2.
 */
public interface RefreshAnimatorListener {

    void onCancelPullAnimEnd();//取消下拉动画结束

    void onAutoPullDownAnim(int deltaY);//自动下拉动画

    void onAutoPullBackAnim(int deltaValue, float currValue, float finalValue);//自动回滚动画,finalValue为0时隐藏头部

    void onStayAnimEnd();//停留动画结束,开始回调加载数据逻辑

    void onAutoPullBackAnimEnd();//刷新完后,自动回滚结束,下拉刷新的最后步骤

}
