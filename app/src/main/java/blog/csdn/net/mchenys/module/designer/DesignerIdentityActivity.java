package blog.csdn.net.mchenys.module.designer;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.utils.PreferencesUtils;
import blog.csdn.net.mchenys.common.utils.SoftInputUtils;
import blog.csdn.net.mchenys.common.widget.dialog.DesignerPositionDialog;
import blog.csdn.net.mchenys.common.widget.dialog.DesignerSpaceDialog;
import blog.csdn.net.mchenys.common.widget.dialog.DesignerStyleDialog;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.Province;

/**
 * 设计师认证
 * Created by mChenys on 2018/8/13.
 */

public class DesignerIdentityActivity extends BaseActivity implements View.OnClickListener {

    private ViewFlipper mViewFlipper;
    private int currPagePosition;

    private List<String> provinceNameList = new ArrayList<>(); //省份列表
    private List<Province> provinceList = new ArrayList<>();
    private List<List<String>> cityNameList = new ArrayList<>(); //城市列表
    private OptionsPickerView mCityPinkView;

    private DesignerPositionDialog mPositionDialog;//职位选择
    private DesignerSpaceDialog mSpaceDialog;//擅长类别
    private DesignerStyleDialog mStyleDialog;//擅长风格
    private TextView mCityTv;
    private TextView mPositionTv;
    private TextView mSpaceTv;
    private TextView mStyleTv;

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_designer_identity;
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        parseJsonData();
    }

    @Override
    protected void initView() {
        super.initView();
        mCityTv = findViewById(R.id.tv_city_value);
        mPositionTv = findViewById(R.id.tv_position_value);
        mSpaceTv = findViewById(R.id.tv_space_value);
        mStyleTv = findViewById(R.id.tv_style_value);

        mPositionDialog = new DesignerPositionDialog(this);
        mSpaceDialog = new DesignerSpaceDialog(this);
        mStyleDialog = new DesignerStyleDialog(this);

        mViewFlipper = findViewById(R.id.view_flipper);
        //省市
        OptionsPickerView.Builder builder = new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mCityTv.setText(cityNameList.get(options1).get(options2));
            }
        });
        //采用自定义的布局
        builder.setLayoutRes(R.layout.layout_city_pickerview_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                v.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCityPinkView.returnData();
                        mCityPinkView.dismiss();
                    }
                });


            }
        });
        builder.setLineSpacingMultiplier(2.0f);
        mCityPinkView = new OptionsPickerView<>(builder);
        mCityPinkView.setPicker(provinceNameList, cityNameList);
        if (provinceNameList.size() > 2 && cityNameList.size() > 3) {
            mCityPinkView.setSelectOptions(2, 3);   //默认选中广东省广州市
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        findViewById(R.id.btn_to_step2).setOnClickListener(this);
        findViewById(R.id.btn_to_step3).setOnClickListener(this);
        findViewById(R.id.btn_to_commit).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        mCityTv.setOnClickListener(this);
        mPositionTv.setOnClickListener(this);
        mSpaceTv.setOnClickListener(this);
        mStyleTv.setOnClickListener(this);
        mPositionDialog.setOnPositionSelectListener(mPositionSelectListener);
        mSpaceDialog.setOnSpaceSelectListener(mSpaceSelectListener);
        mStyleDialog.setOnStyleSelectListener(mStyleSelectListener);
    }

    @Override
    public void onClick(View v) {
        SoftInputUtils.closedSoftInput(this);
        switch (v.getId()) {
            case R.id.btn_to_step2:
                goNext(1);
                break;
            case R.id.btn_to_step3:
                goNext(2);
                break;
            case R.id.btn_to_commit:
                break;
            case R.id.iv_back:
                goPre();
                break;
            case R.id.tv_city_value:
                mCityPinkView.show();
                break;
            case R.id.tv_position_value:
                mPositionDialog.show();
                break;
            case R.id.tv_space_value:
                mSpaceDialog.show();
                break;
            case R.id.tv_style_value:
                mStyleDialog.show();
                break;
        }
    }

    private void goNext(int currPosition) {
        mViewFlipper.setInAnimation(this, R.anim.slide_right_in);
        mViewFlipper.setInAnimation(this, R.anim.slide_left_out);
        mViewFlipper.showNext();
        currPagePosition = currPosition;
    }

    private void goPre() {
        if (currPagePosition == 0) {
            finish();
        } else {
            mViewFlipper.setInAnimation(this, R.anim.slide_left_in);
            mViewFlipper.setInAnimation(this, R.anim.slide_right_out);
            mViewFlipper.showPrevious();
            currPagePosition--;
        }
    }

    private DesignerPositionDialog.OnPositionSelectListener mPositionSelectListener = new DesignerPositionDialog.OnPositionSelectListener() {
        @Override
        public void onSelected(DesignerPositionDialog.Position position) {
            mPositionTv.setText(position.name);
        }
    };
    private DesignerSpaceDialog.OnSpaceSelectListener mSpaceSelectListener = new DesignerSpaceDialog.OnSpaceSelectListener() {
        @Override
        public void onSelected(DesignerSpaceDialog.Space space) {
            mSpaceTv.setText(space.name);
        }
    };

    private DesignerStyleDialog.OnStyleSelectListener mStyleSelectListener = new DesignerStyleDialog.OnStyleSelectListener() {
        @Override
        public void onSelected(List<DesignerStyleDialog.Style> styleList) {
            if (styleList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (DesignerStyleDialog.Style style : styleList) {
                    sb.append(style.name + ",");
                }
                mStyleTv.setText(sb.substring(0, sb.length() - 1));
            }

        }
    };

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
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        goPre();
    }

}
