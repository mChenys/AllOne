package blog.csdn.net.mchenys.module.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.LazyBaseRecyclerViewListFragment;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.SubColumnData;
import blog.csdn.net.mchenys.module.adapter.HomeLiveAdapter;

/**
 * Created by mChenys on 2018/6/7.
 */

public class LiveInstituteFragment extends LazyBaseRecyclerViewListFragment<SubColumnData> {

    private String columnId;

    @Override
    protected void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            columnId = bundle.getString("columnId");
        }
    }

    /**
     * 通过给定传参创建实例
     *
     * @param columnId
     * @return
     */
    public static LiveInstituteFragment newInstance(String columnId) {
        Bundle bundle = new Bundle();
        bundle.putString("columnId", columnId);
        LiveInstituteFragment mLiveInstituteFragment = new LiveInstituteFragment();
        mLiveInstituteFragment.setArguments(bundle);
        return mLiveInstituteFragment;
    }

    @Override
    protected void initView() {
        super.initView();
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(new HomeLiveAdapter(mContext, mData, R.layout.item_layout_base_sub_column));
        Log.e("cys", "initView  columnId:" + columnId);
    }

    @Override
    protected Req onCreateReq() {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("columnType", "livingColumn");
        bodyMap.put("columnId", columnId);
        Req req = new Req(Urls.HOME_COLUMN_LIST, null, bodyMap);
        return req;
    }

    @Override
    protected List<SubColumnData> parseList(JSONObject jsonObject) throws Exception {
        return SubColumnData.parseList(jsonObject.optJSONArray("data"));
    }

    @Override
    protected void beforeDataSet(JSONObject jsonObject, boolean isLoadMore) {
        HomeFragment fragment = (HomeFragment) getParentFragment();
        if (null != fragment) {
            fragment.parseFocusList(jsonObject);
        }
    }

    @Override
    protected void afterDataSet(List<SubColumnData> data, boolean isLoadMore) {

    }

    @Override
    public void onReqComplete(boolean isLoadMore) {
        super.onReqComplete(isLoadMore);
        HomeFragment fragment = (HomeFragment) getParentFragment();
        if (null != fragment) {
            fragment.finishRefresh(isLoadMore);
        }
    }
}
