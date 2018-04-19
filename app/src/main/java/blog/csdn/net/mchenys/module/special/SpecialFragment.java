package blog.csdn.net.mchenys.module.special;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseRecyclerViewListFragment;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.Recommend;
import blog.csdn.net.mchenys.module.adapter.RecommendAdapter;

/**
 * 推荐
 * Created by mChenys on 2017/12/28.
 */

public class SpecialFragment extends BaseRecyclerViewListFragment<Recommend> {

    private TextView mHeaderInfo;

    @Override
    protected void setTitleBar(TitleBar titleBar) {
        titleBar.setCenterTv("专家");
    }

    @Override
    protected void initView() {
        super.initView();
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.layout_test_header, null);
        mHeaderInfo = headerView.findViewById(R.id.tv_header_info);
        mRecyclerView.addHeaderView(headerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(mRecyclerView.new DividerItemDecoration(new ColorDrawable(Color.GRAY)));
        mRecyclerView.setAdapter(new RecommendAdapter(mContext, mData, R.layout.item_recommend));
    }

    @Override
    protected void beforeDataSet(JSONObject jsonObject, boolean isLoadMore) {
        if (!isLoadMore) {
            mHeaderInfo.setText(jsonObject.optString("total"));
        }
    }

    @Override
    protected void afterDataSet(List<Recommend> data, boolean isLoadMore) {
        Recommend recommend = data.get(0);
        recommend.techDesc = "测试描述";
    }

    @Override
    protected List<Recommend> parseList(JSONObject jsonObject) throws Exception {
        return Recommend.parseList(jsonObject.optJSONArray("data"));
    }

    @Override
    protected Req onCreateReq() {

        return new Req(Urls.TEST, null, null);
    }




}
