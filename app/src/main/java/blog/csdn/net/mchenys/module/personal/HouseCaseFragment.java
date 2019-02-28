package blog.csdn.net.mchenys.module.personal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.LazyBaseRecyclerViewListFragment;
import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.model.HousingCase;
import blog.csdn.net.mchenys.model.UserInfo;


/**
 * 个人主页-案例列表
 * Created by mChenys on 2018/8/9.
 */

public class HouseCaseFragment extends LazyBaseRecyclerViewListFragment<HousingCase> {
    public static Fragment newInstance(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("userId",userId);
        HouseCaseFragment fragment = new HouseCaseFragment();
        fragment.setArguments(bundle);
        return fragment;


    }


    @Override
    protected void initView() {
        super.initView();
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new BaseRecycleViewAdapter<HousingCase>(getActivity(),mData, R.layout.item_personal_case_list) {

            @Override
            protected void bindView(BaseRecycleViewHolder holder, int position, HousingCase data) {
                holder.setImageUrl(R.id.iv_designer_header, data.userImage)
                        .displayWithRound(R.id.iv_cover,data.coverImage,4)
                        .setTextView(R.id.tv_designer_nickname, data.userName)
                        .setTextView(R.id.tv_house_pattern, data.housePattern)
                        .setTextView(R.id.tv_house_area, data.houseArea)
                        .setTextView(R.id.tv_title, data.title);

                if(data.editTag==1){
                    holder.show(R.id.iv_editTag);
                }else{
                    holder.hide(R.id.iv_editTag);
                }

                ImageView tagIv = holder.getView(R.id.iv_talent);
                if (data.isDesigner == 1) {
                    tagIv.setVisibility(View.VISIBLE);
                    tagIv.setImageResource(R.drawable.ic_personal_designer);
                } else if (data.isTalent == 1 && data.talents.size() > 0) {
                    tagIv.setVisibility(View.VISIBLE);
                    ImageLoadUtils.disPlay(data.talents.get(0).imgUrl, tagIv);
                } else {
                    tagIv.setVisibility(View.GONE);
                }
            }


        });
    }


    @Override
    protected Req onCreateReq() {
        String userId = getArguments().getString("userId");
        Map<String, String> bodyMap = new HashMap();
        Map<String, String> headerMap = new HashMap();
        bodyMap.put("v", Env.versionName);
        bodyMap.put("userId",userId);
        return new Req(Urls.PERSON_PAGE_TERMINAL,headerMap,bodyMap);
    }

    @Override
    protected List<HousingCase> parseList(JSONObject jsonObject) throws Exception {
        UserInfo userInfo = new UserInfo(jsonObject.optJSONObject("userInfo"));
        if (null != getActivity() && isAdded()) {
            PersonPageActivity activity = (PersonPageActivity) getActivity();
            activity.setUserInfo(userInfo);
            activity.setShareData(jsonObject.optJSONObject("share"));
        }
        return HousingCase.parseList(jsonObject.optJSONArray("case"));
    }


}
