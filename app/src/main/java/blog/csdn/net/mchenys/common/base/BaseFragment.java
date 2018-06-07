package blog.csdn.net.mchenys.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment基类
 * Created by mChenys on 2017/12/27.
 */

public abstract class BaseFragment extends Fragment {
    protected View mRootView;
    protected Activity mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null || !isSavePagerStatus() && null != getLayoutResID()) {
            mRootView = inflater.inflate(getLayoutResID(), container, false);
            initParams();
            initView();
            initListener();
            loadData();
        } else {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        }
        return mRootView;
    }



    public <T extends View> T findViewById(@IdRes int id) {
        return mRootView.findViewById(id);
    }

    public BaseFragment show(@IdRes int id) {
        findViewById(id).setVisibility(View.VISIBLE);
        return this;
    }

    public BaseFragment hide(@IdRes int id) {
        findViewById(id).setVisibility(View.GONE);
        return this;
    }

    public BaseFragment hideAll(int... ids) {
        for (int id : ids) {
            findViewById(id).setVisibility(View.GONE);
        }
        return this;
    }

    public BaseFragment showAll(int... ids) {
        for (int id : ids) {
            findViewById(id).setVisibility(View.VISIBLE);
        }
        return this;
    }


    protected abstract Integer getLayoutResID();

    protected void initView() {
        // LogUtils.d(TAG, this.getClass().getSimpleName() + "#initView");

    }

    protected void initListener() {
        //LogUtils.d(TAG, this.getClass().getSimpleName() + "#initListener");
    }

    /**
     * 加载数据
     */
    protected void loadData() {
        //LogUtils.d(TAG, this.getClass().getSimpleName() + "#loadData");
    }


    /**
     * 用在ViewPager切换时是否需要保存状态功能,默认true
     *
     * @return
     */
    protected boolean isSavePagerStatus() {
        return true;
    }

    /**
     * Fragment当前状态是否可见,是在onCreateView之前调用,
     * 仅仅工作在有FragmentPagerAdapter的情况，单独用在activity中不会回调。
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onFragmentResume();
        } else {
            onFragmentPause();
        }
    }


    /**
     * Fragment可见仅仅工作在有FragmentPagerAdapter的情况,其他情况用Activity的onResume
     */
    public void onFragmentResume() {
    }

    /**
     * Fragment不可见仅仅工作在有FragmentPagerAdapter的情况,其他情况用Activity的onPause
     */
    public void onFragmentPause() {
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        resetUI();
        super.onViewStateRestored(savedInstanceState);
    }

    public void resetUI() {
        //empty 当界面需要单独刷新,可以在改方法执行刷新操作
    }

    public void autoRefresh(Bundle bundle) {

    }

    protected void initParams() {

    }
}
