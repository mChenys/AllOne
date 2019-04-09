package blog.csdn.net.mchenys.module.demo;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.photo.PhotoPreviewActivity;
import blog.csdn.net.mchenys.common.photo.PhotoPreviewActivity2;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.utils.PreferencesUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.Province;
import blog.csdn.net.mchenys.module.demo.douyin.DouYinVideoActivity;
import blog.csdn.net.mchenys.module.demo.pagersnaphelper.PagerSnapHelperActivity;
import blog.csdn.net.mchenys.module.demo.pagersnaphelper.TextureListActivity;
import blog.csdn.net.mchenys.module.demo.pagersnaphelper.VideoListActivity;
import blog.csdn.net.mchenys.module.demo.pindheader.TestPinnedHeaderActivity;
import blog.csdn.net.mchenys.module.demo.test.TestChartActivity;
import blog.csdn.net.mchenys.module.demo.test.TestNestScrollRefreshActivity;
import blog.csdn.net.mchenys.module.demo.test.TestSlideViewActivity;
import blog.csdn.net.mchenys.module.demo.test.TestSmartRefreshActivity;
import blog.csdn.net.mchenys.module.terminal.PageTerminalActivity;

/**
 * Created by mChenys on 2019/4/1.
 */

public class DemoActivity extends BaseActivity implements View.OnClickListener {
    private Button mCityBtn, mHouseBtn, mAreaBtn;
    private OptionsPickerView<String> mCityPinkView; //https://blog.csdn.net/qq_22393017/article/details/58099486
    private OptionsPickerView<String> mHousePinkView;
    private OptionsPickerView<String> mAreaPinkView;

    private List<String> provinceNameList = new ArrayList<>(); //省份列表
    private List<Province> provinceList = new ArrayList<>();
    private List<List<String>> cityNameList = new ArrayList<>(); //城市列表
    private List<List<List<String>>> lastNameList = new ArrayList<>(); //最后一个城市列表(测试用数据)
    private List<String> tingList = new ArrayList<>();//厅
    private List<String> shiList = new ArrayList<>();//室
    private List<String> weiList = new ArrayList<>();//卫

    @Override
    protected void initData() {
        super.initData();
        parseJsonData();
        for (int i = 0; i < 11; i++) {
            tingList.add(i + "厅");
            shiList.add(i + "室");
            weiList.add(i + "卫");
        }
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.setCenterTv("Demo");
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_demo;
    }

    @Override
    protected void initView() {
        super.initView();
        mCityBtn = findViewById(R.id.btn_current_city);
        mHouseBtn = findViewById(R.id.btn_house_type);
        mAreaBtn = findViewById(R.id.btn_area);

        //城市滚轮
        mCityPinkView = new OptionsPickerView<>(new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mCityBtn.setText(parseSelectedResult(options1, options2));
            }
        }));
        mCityPinkView.setPicker(provinceNameList, cityNameList);
        if (provinceNameList.size() > 2 && cityNameList.size() > 3) {
            mCityPinkView.setSelectOptions(2, 3);   //默认选中广东省广州市
        }

        //户型滚轮

        mHousePinkView = new OptionsPickerView<>(new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mHouseBtn.setText(parseSelectedResult(options1, options2, options3));
            }
        }));
        mHousePinkView.setNPicker(tingList, shiList, weiList);

        //省市区
        OptionsPickerView.Builder builder = new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mAreaBtn.setText(parseSelectedResult2(options1, options2, options3));
            }
        });
        //采用自定义的布局
        builder.setLayoutRes(R.layout.layout_area_pickerview_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                v.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAreaPinkView.returnData();
                        mAreaPinkView.dismiss();
                    }
                });
                v.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAreaPinkView.dismiss();
                    }
                });

            }
        });
        mAreaPinkView = new OptionsPickerView<>(builder);
        mAreaPinkView.setPicker(provinceNameList, cityNameList, lastNameList);
    }

    @Override
    protected void initListener() {
        super.initListener();
        findViewById(R.id.btn_to_page_ternimal).setOnClickListener(this);
        findViewById(R.id.btn_current_city).setOnClickListener(this);
        findViewById(R.id.btn_house_type).setOnClickListener(this);
        findViewById(R.id.btn_area).setOnClickListener(this);
        findViewById(R.id.btn_show_pic).setOnClickListener(this);
        findViewById(R.id.btn_show_pic2).setOnClickListener(this);
        findViewById(R.id.chart1demo).setOnClickListener(this);
        findViewById(R.id.smartRefresh1demo).setOnClickListener(this);
        findViewById(R.id.testNestscrollRefresh).setOnClickListener(this);
        findViewById(R.id.btn_slide_view).setOnClickListener(this);
        findViewById(R.id.pinnedHeader).setOnClickListener(this);
        findViewById(R.id.btn_pagersnaphelper).setOnClickListener(this);
        findViewById(R.id.btn_video_list).setOnClickListener(this);
        findViewById(R.id.btn_texture_list).setOnClickListener(this);
        findViewById(R.id.btn_douyinvideo_list).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_page_ternimal:
                JumpUtils.startActivity(mContext, PageTerminalActivity.class);

                break;

            case R.id.btn_current_city:
                mCityPinkView.show();
                break;

            case R.id.btn_house_type:
                mHousePinkView.show();
                break;
            case R.id.btn_area:
                mAreaPinkView.show();
                break;
            case R.id.btn_show_pic:
                startActivity(new Intent(mContext, PhotoPreviewActivity.class));
                break;
            case R.id.btn_show_pic2:
                startActivity(new Intent(mContext, PhotoPreviewActivity2.class));
                break;
            case R.id.chart1demo:
                startActivity(new Intent(mContext, TestChartActivity.class));
                break;
            case R.id.smartRefresh1demo:
                startActivity(new Intent(mContext, TestSmartRefreshActivity.class));
                break;
            case R.id.testNestscrollRefresh:
                startActivity(new Intent(mContext, TestNestScrollRefreshActivity.class));
                break;

            case R.id.btn_slide_view:
                startActivity(new Intent(mContext, TestSlideViewActivity.class));
                break;
            case R.id.pinnedHeader:
                startActivity(new Intent(mContext, TestPinnedHeaderActivity.class));
                break;
            case R.id.btn_pagersnaphelper:
                startActivity(new Intent(mContext, PagerSnapHelperActivity.class));
                break;
            case R.id.btn_video_list:
                startActivity(new Intent(mContext, VideoListActivity.class));
                break;
            case R.id.btn_texture_list:
                startActivity(new Intent(mContext, TextureListActivity.class));
                break;
            case R.id.btn_douyinvideo_list:
                startActivity(new Intent(mContext, DouYinVideoActivity.class));
                break;
        }
    }

    /**
     * 从json数据中解析出省市列表数据
     */
    private void parseJsonData() {
        String jsonStr = PreferencesUtils.getPreference(mContext, "pre_province_cities", "key_province_cities", "");
        if (TextUtils.isEmpty(jsonStr)) return;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            provinceList = Province.parseProvinceList(jsonArray);
            if (provinceList != null) {
                for (int i = 0; i < provinceList.size(); i++) {
                    Province province = provinceList.get(i);
                    provinceNameList.add(province.getName());
                    List<Province.City> cityList = province.getCityList();
                    List<String> temp = new ArrayList<>();
                    for (int j = 0; j < cityList.size(); j++) {
                        temp.add(cityList.get(j).getCityName());
                    }
                    cityNameList.add(temp);
                    lastNameList.add(cityNameList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据option返回对应城市
     *
     * @param opt1 opt1
     * @param opt2 opt2
     * @return 城市
     */
    private String parseSelectedResult(int opt1, int opt2) {
        return provinceList.get(opt1).getCityList().get(opt2).getCityName();
    }

    private String parseSelectedResult(int options1, int options2, int options3) {
        return tingList.get(options1) + "," + shiList.get(options2) + "," + weiList.get(options3);
    }

    private String parseSelectedResult2(int options1, int options2, int options3) {
        return provinceList.get(options1).getName() + "," + cityNameList.get(options1).get(options2) + "," +
                lastNameList.get(options1).get(options2).get(options3);
    }
}
