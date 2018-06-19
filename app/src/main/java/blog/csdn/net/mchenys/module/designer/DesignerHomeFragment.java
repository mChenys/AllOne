package blog.csdn.net.mchenys.module.designer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseRecyclerViewListFragment;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.utils.BdLbsUtils;
import blog.csdn.net.mchenys.common.utils.DisplayUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewAdapter;
import blog.csdn.net.mchenys.common.widget.recycleview.BaseRecycleViewHolder;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.DesignPrice;
import blog.csdn.net.mchenys.model.DesignProvince;
import blog.csdn.net.mchenys.model.DesignerPojo;

/**
 * 设计师首页
 */
public class DesignerHomeFragment extends BaseRecyclerViewListFragment<DesignerPojo> {

    private RecyclerView mConditionFilterRv;
    private View mBgView;
    private RadioGroup mConditionRg;
    private RadioButton mPriceRb, mProvinceRb, mSameCityRb;
    private List<DesignPrice> mPriceList = new ArrayList<>();
    private List<DesignProvince> mProvinceList = new ArrayList<>();
    private int priceType;
    private int provinceId;
    private String sameCityName;
    private boolean isPressCity2Rest; //是否是点击所在地区来重置只看同城
    private DesignerPriceAdapter mPriceAdapter;
    private DesingerProvinceAdapter mProvinceAdapter;

    @Override
    protected void initParams() {
        super.initParams();
        mPriceList.addAll(DesignPrice.getAllPrice());
        DesignProvince.getList(new DesignProvince.ResultCallback() {

            @Override
            public void onResult(List<DesignProvince> list) {
                mProvinceList.addAll(list);
                mProvinceList.add(0, new DesignProvince(0, "全部地区", true));
            }
        });
        initLocation();
    }

    @Override
    protected void initView() {
        super.initView();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView. setAdapter(new DesignerHomeAdapter(mContext, mData));
        addCoverTopLayout(initHeaderView(), new FrameLayout.LayoutParams(-1, -1));
    }

    private View initHeaderView() {
        View header = View.inflate(getActivity(), R.layout.layout_header_designer_home, null);
        mConditionFilterRv = (RecyclerView) header.findViewById(R.id.rv_conditional_filter);
        mConditionFilterRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mConditionRg = (RadioGroup) header.findViewById(R.id.rg_condition);
        mRecyclerView.setPadding(0, DisplayUtils.dip2px(mContext, 38), 0, 0);
        mPriceRb = (RadioButton) header.findViewById(R.id.rb_price);
        mProvinceRb = (RadioButton) header.findViewById(R.id.rb_province);
        mSameCityRb = (RadioButton) header.findViewById(R.id.rb_same_city);
        mBgView = header.findViewById(R.id.view_bg);
        showChooseList(false);

        mPriceAdapter = new DesignerPriceAdapter(getActivity(), mPriceList, R.layout.item_filter_list);
        mProvinceAdapter = new DesingerProvinceAdapter(getActivity(), mProvinceList, R.layout.item_filter_list);
        return header;
    }

    @Override
    protected void initListener() {
        super.initListener();

        mConditionRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_price:
                        Log.e("cys", "价格:" + mPriceRb.isChecked());
                        if (mPriceRb.isChecked()) {
                            showChooseList(true);
                            mConditionFilterRv.setAdapter(mPriceAdapter);
                        } else {
                            showChooseList(false);
                        }

                        break;
                    case R.id.rb_province:
                        Log.e("cys", "省份:" + mProvinceRb.isChecked());
                        if (mProvinceRb.isChecked()) {
                            showChooseList(true);
                            mConditionFilterRv.setAdapter(mProvinceAdapter);
                        } else {
                            showChooseList(false);
                        }
                        break;
                }
            }
        });


        mBgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseList(false);
            }
        });

        //同城 与 选择所在地区筛选是互斥的
        mSameCityRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isPressCity2Rest||isChecked) { //避免点击所在地区导致连锁反应
                    resetCityChoose(0);
                    mConditionRg.clearCheck();
                    loadData(false);
                }
                isPressCity2Rest = false;
            }
        });


    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {
        titleBar.setCenterTv("设计师");
    }

    @Override
    protected Req onCreateReq() {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("provinceId", String.valueOf(provinceId));
        bodyMap.put("priceType", String.valueOf(priceType));
        bodyMap.put("v", "4.4.0");
        if (mSameCityRb.isChecked()) {
            bodyMap.put("provinceId", "-1");
            if (!StringUtils.isEmpty(sameCityName))
                bodyMap.put("provinceName", sameCityName);
        }
        return new Req(Urls.DESIGNER_INDEX, null, bodyMap);
    }

    @Override
    protected List<DesignerPojo> parseList(JSONObject jsonObject) throws Exception {
        return DesignerPojo.parseList(jsonObject.optJSONArray("data"));
    }

    @Override
    protected void beforeDataSet(JSONObject jsonObject, boolean isLoadMore) {

    }

    @Override
    protected void afterDataSet(List<DesignerPojo> data, boolean isLoadMore) {

    }


    private boolean isViewCreated;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
    }

    public void showChooseList(boolean isShow) {
        if (!isViewCreated) return;
        if (isShow) {
            mConditionFilterRv.setVisibility(View.VISIBLE);
            mBgView.setVisibility(View.VISIBLE);
        } else {
            mConditionFilterRv.setVisibility(View.GONE);
            mBgView.setVisibility(View.GONE);
            mConditionRg.clearCheck();


        }
    }

    private void resetCityChoose(int excludePosition) {
        for (DesignProvince dp : mProvinceList) {
            dp.isSelected = false;
        }
        DesignProvince data = mProvinceList.get(excludePosition);
        data.isSelected = true;
        provinceId = data.provinceId;
    }

    private void resetPriceChoose(int excludePosition) {
        for (DesignPrice dp : mPriceList) {
            dp.isSelected = false;
        }
        DesignPrice data = mPriceList.get(excludePosition);
        data.isSelected = true;
    }

    //所在地区
    public class DesingerProvinceAdapter extends BaseRecycleViewAdapter<DesignProvince> {
        public DesingerProvinceAdapter(Context mContext, List<DesignProvince> mDatas, int layoutId) {
            super(mContext, mDatas, layoutId);
        }

        @Override
        protected void bindView(BaseRecycleViewHolder holder, final int position, DesignProvince data) {
            TextView tvInfo = holder.getView(R.id.tv_info);
            tvInfo.setText(data.provinceName);
            if (data.isSelected) {
                holder.show(R.id.iv_check);
                tvInfo.setTextColor(getResources().getColor(R.color.color_222222));
            } else {
                tvInfo.setTextColor(getResources().getColor(R.color.color_999999));
                holder.hide(R.id.iv_check);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPressCity2Rest = true;
                    resetCityChoose(position);
                    showChooseList(false);
                    mSameCityRb.setChecked(false);
                    loadData(false);
                }
            });
        }
    }

    //设计师报价
    public class DesignerPriceAdapter extends BaseRecycleViewAdapter<DesignPrice> {
        public DesignerPriceAdapter(Context mContext, List<DesignPrice> mDatas, int layoutId) {
            super(mContext, mDatas, layoutId);
        }

        @Override
        protected void bindView(BaseRecycleViewHolder holder, final int position, DesignPrice data) {
            TextView tvInfo = holder.getView(R.id.tv_info);
            tvInfo.setText(data.price);
            if (data.isSelected) {
                holder.show(R.id.iv_check);
                tvInfo.setTextColor(getResources().getColor(R.color.color_222222));
            } else {
                tvInfo.setTextColor(getResources().getColor(R.color.color_999999));
                holder.hide(R.id.iv_check);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetPriceChoose(position);
                    DesignPrice data = mPriceList.get(position);
                    priceType = data.priceType;
                    showChooseList(false);
                    loadData(false);
                }
            });
        }
    }

    private void initLocation() {
        BdLbsUtils bdLbsUtils = BdLbsUtils.getInstance();
        bdLbsUtils.setLocationListener(new BdLbsUtils.PCLocationListener() {
            @Override
            public void success(BdLbsUtils.PCLocationModel pcLocationModel) {
                Log.e("cys", "定位成功!" + pcLocationModel.toString());
                sameCityName = pcLocationModel.province;
            }

            @Override
            public void failure(int errType, String errMsg) {
                Log.e("cys", "erroType=" + errType + " erroMsg=" + errMsg);
            }
        });
        bdLbsUtils.start();
    }
}
