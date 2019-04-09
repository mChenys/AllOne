package blog.csdn.net.mchenys.module.demo.pagersnaphelper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;

/**
 * Created by mChenys on 2019/4/1.
 */

public class PagerSnapHelperActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private List<String> mData = new ArrayList<>();

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.setCenterTv("PagerSnapHelper");
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_pagersnaphelper;
    }

    @Override
    protected void initData() {
        super.initData();
        for (int i = 0; i < 20; i++) {
            mData.add("数据====" + i);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setNestedScrollingEnabled(false);
        PagerSnapHelper snapHelper = new PagerSnapHelper() {
            // 在 Adapter的 onBindViewHolder 之后执行
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                // TODO 找到对应的Index
                Log.e("cys: ", "---findTargetSnapPosition---");
                int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                Log.e("cys: ", "targetPos: " + targetPos);

                Toast.makeText(PagerSnapHelperActivity.this, "滑到到 " + targetPos + "位置", Toast.LENGTH_SHORT).show();

                return targetPos;
            }

            // 在 Adapter的 onBindViewHolder 之后执行
            @Nullable
            @Override
            public View findSnapView(RecyclerView.LayoutManager layoutManager) {
                // TODO 找到对应的View
                Log.e("cys: ", "---findSnapView---");
                View view = super.findSnapView(layoutManager);
                Log.e("cys: ", "tag: " + view.getTag());

                return view;
            }
        };

        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new PagerSnapHelperAdapter(this,mData));

       /* LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new PagerSnapHelperAdapter(this,mData));*/

    }

    public class PagerSnapHelperAdapter extends BaseRecycleViewAdapter<String> {

        public PagerSnapHelperAdapter(Context ctx, List<String> data) {
            super(ctx, data, R.layout.item_bg_list);

        }

        @Override
        protected void bindView(BaseRecycleViewHolder holder, int position, String data) {
            holder.itemView.setTag(position);
            holder.setTextView(R.id.tv_info, mData.get(position));
        }

    }
}
