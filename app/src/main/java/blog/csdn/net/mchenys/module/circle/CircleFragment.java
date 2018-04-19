package blog.csdn.net.mchenys.module.circle;

import android.support.v7.widget.StaggeredGridLayoutManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseRecyclerViewListFragment;
import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.widget.focusimg.FocusView;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.CircleTopics;
import blog.csdn.net.mchenys.model.Focus;
import blog.csdn.net.mchenys.module.adapter.CircleTopicsAdapter;


/**
 * 圈子
 * Created by mChenys on 2017/12/28.
 */

public class CircleFragment extends BaseRecyclerViewListFragment<CircleTopics> {

    private FocusView mFocusView;
    @Override
    protected void setTitleBar(TitleBar titleBar) {
        titleBar.setCenterTv("圈子");
    }

    @Override
    protected Req onCreateReq() {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("topicType", "hot");
        bodyMap.put("deviceId", "1fb16ae0d28233c3ad79ade0686e0bd8");
        return new Req(Urls.CIRCLE_HOME, null, bodyMap);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=mFocusView)mFocusView.startAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mFocusView)mFocusView.stopAutoPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mFocusView)mFocusView.stopAutoPlay();
    }

    @Override
    protected void initView() {
        super.initView();
        mFocusView = new FocusView(mContext);
        int focusHeight =  Env.screenWidth * 350 / 750;
        mFocusView.setFocusImageParams(Env.screenWidth, focusHeight);
        mFocusView.setIsDescribe(true);
        mFocusView.setIndicatorSelectorResId(R.drawable.index_focus_indicator_selector);
        mFocusView.setDescBackground(getResources().getDrawable(R.drawable.text_gradient_bg_black));
        mFocusView.setIndicatorGravity(FocusView.XGravity.RIGHT);
        mRecyclerView.addHeaderView(mFocusView);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(new CircleTopicsAdapter(getActivity(), 8,mData));

    }

    @Override
    protected List<CircleTopics> parseList(JSONObject jsonObject) throws Exception {
        return CircleTopics.parseList(jsonObject.optJSONArray("dataList"));
    }

    @Override
    protected void beforeDataSet(JSONObject jsonObject, boolean isLoadMore) {
        if (!isLoadMore) {
            List<Focus> focusList = Focus.parseList(jsonObject.optJSONArray("focus"));
            mFocusView.initFocus(focusList);
        }

    }

    @Override
    protected void afterDataSet(List<CircleTopics> data, boolean isLoadMore) {

    }
}
