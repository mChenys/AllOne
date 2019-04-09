package blog.csdn.net.mchenys.common.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.SimplePullScrollListener;
import blog.csdn.net.mchenys.common.widget.view.AdvanceSwipeRefreshLayout;


/**
 * Created by user on 2018/12/24.
 */

public abstract class BaseSwipeRefreshActivity<T> extends BaseRecyclerViewListActivity<T> {
    protected AdvanceSwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout mPinkTopFl;
    private FrameLayout mCenterFl;

    @Override
    protected Integer getLayoutResID() {
        return R.layout.fragment_base_swipe_refresh_layout;
    }

    @Override
    protected void initView() {
        super.initView();
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mSwipeRefreshLayout = findViewById(R.id.srl_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        mPinkTopFl = findViewById(R.id.fl_pink_top);
        mCenterFl = findViewById(R.id.fl_center);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onUserRefreshStart();
                startRefresh();
            }
        });
        mRecyclerView.setOnPullScrollListener(new SimplePullScrollListener() {
            @Override
            public void onFinishRefresh() {
                mSwipeRefreshLayout.finisRefresh(1500);
            }
        });
    }

    //添加固定的头
    public void addPinkTop(View view) {
        mPinkTopFl.addView(view, new FrameLayout.LayoutParams(-1, -2));
    }

    //添加悬浮头
    public void addFollowTop(View view) {
        ((LinearLayout) findViewById(R.id.ll_flow_top)).addView(view, new FrameLayout.LayoutParams(-1, -2));
    }

    //添加中间显示的view
    public void addCenterView(View view) {
        mCenterFl.addView(view);
    }

    @Override
    public void autoRefresh(Bundle bundle) {
        mSwipeRefreshLayout.startRefresh();
        mRecyclerView.scrollToPosition(0);
        startRefresh();
        onUserRefreshStart();

    }

    public void onUserRefreshStart() {

    }
}
