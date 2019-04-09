package blog.csdn.net.mchenys.module.demo.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.widget.layout.PinkNestedScrollView3;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.RefreshRecyclerView;
import blog.csdn.net.mchenys.common.widget.recycleview.refresh.SimplePullScrollListener;
import blog.csdn.net.mchenys.common.widget.view.AdvanceSwipeRefreshLayout;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;

/**
 * Created by mChenys on 2018/12/13.
 */

public class TestNestScrollRefreshActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private AdvanceSwipeRefreshLayout mSwipeRefreshLayout;//官方下拉刷新组件
    private List<String> mData = new ArrayList<>();
    private RefreshRecyclerView recyclerView;
    private PinkNestedScrollView3 mPinkNsv;

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_test_nest_scroll_refresh;
    }

    @Override
    protected void initData() {
        super.initData();
        for (int i = 0; i < 20; i++) {
            mData.add("数据:" + i);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mSwipeRefreshLayout = findViewById(R.id.swl_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mPinkNsv=  findViewById(R.id.pnsv_content);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new MyAdapter(this, mData));
    }

    @Override
    protected void initListener() {
        super.initListener();
        //只有当内容列表完全处于顶部才能下拉刷新
        mSwipeRefreshLayout.setOnPreInterceptTouchEventDelegate(new AdvanceSwipeRefreshLayout.OnPreInterceptTouchEventDelegate() {
            @Override
            public boolean shouldDisallowInterceptTouchEvent(MotionEvent ev) {
                /*if (mUEView.getState() != UEView.STATE_HIDDEN) {
                    return true;
                }*/
                return mPinkNsv.getScrollY() > 0 || mPinkNsv.canTargetScrollVertically(-1);
            }
        });


        recyclerView.setLoadingListener(new RefreshRecyclerView.LoadingListener() {
            @Override
            public void onRefresh(Bundle args) {

            }

            @Override
            public void onLoadMore() {
                for (int i = 0; i < 20; i++) {
                    mData.add("new数据:" + i);
                }
                recyclerView.stopRefresh(true);
            }
        });

        recyclerView.setOnPullScrollListener(new SimplePullScrollListener() {
            @Override
            public void onFinishRefresh() {
                super.onFinishRefresh();
                mSwipeRefreshLayout.finisRefresh(1500);
            }

            @Override
            public void onFinishLoader() {
                super.onFinishLoader();
                mSwipeRefreshLayout.finisRefresh(1500);
            }
        });
    }

    @Override
    public void onRefresh() {
        mData.clear();
        for (int i = 0; i < 20; i++) {
            mData.add("refresh数据:" + i);
        }
        recyclerView.stopRefresh(true);
    }

    public class MyAdapter extends BaseRecycleViewAdapter<String> {

        public MyAdapter(Context ctx, List<String> data) {
            super(ctx, data, R.layout.item_text_list);
        }

        @Override
        protected void bindView(BaseRecycleViewHolder holder, int position, String data) {
            holder.setTextView(R.id.tv_info, mData.get(position));
        }

    }
}
