package blog.csdn.net.mchenys.module.personal;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.okhttp2.x.OkHttpEngine;
import blog.csdn.net.mchenys.common.okhttp2.x.listener.RequestCallBackHandler;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.photo.PhotoPreviewActivity;
import blog.csdn.net.mchenys.common.photo.PhotoPreviewActivity2;
import blog.csdn.net.mchenys.common.photo.crop.CropActivity;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.utils.AccountUtils;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.utils.PreferencesUtils;
import blog.csdn.net.mchenys.common.utils.ShareUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.model.Account;
import blog.csdn.net.mchenys.model.Province;
import blog.csdn.net.mchenys.module.account.LoginActivity;
import blog.csdn.net.mchenys.module.terminal.PageTerminalActivity;


/**
 * 个人中心
 * Created by mChenys on 2017/12/28.
 */

public class PersonalFragment extends BaseFragment implements View.OnClickListener {

    private Button mLoginOutBtn;
    private Button mCityBtn, mHouseBtn,mAreaBtn;
    private TextView nickNameTv;
    private ImageView headerIv;
    private TextView phoneTv;
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
    protected Integer getLayoutResID() {
        return R.layout.fragment_personal;
    }

    @Override
    protected boolean isSavePagerStatus() {
        return false;
    }

    @Override
    protected void initParams() {
        super.initParams();
        parseJsonData();
        for (int i = 0; i < 11; i++) {
            tingList.add(i + "厅");
            shiList.add(i + "室");
            weiList.add(i + "卫");
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mLoginOutBtn = findViewById(R.id.btn_login_out);
        nickNameTv = findViewById(R.id.tv_nickName);
        phoneTv = findViewById(R.id.tv_phone);
        headerIv = findViewById(R.id.iv_header);
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
        mHousePinkView.setNPicker(tingList,shiList,weiList);

        //省市区
        OptionsPickerView.Builder builder =new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
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
        mAreaPinkView.setPicker(provinceNameList, cityNameList,lastNameList);
    }



    @Override
    protected void loadData() {
        super.loadData();
        Account account = AccountUtils.getLoginAccount();
        if (null != account) {
            nickNameTv.setText("昵称:" + account.getUserName());
            phoneTv.setText("手机:" + account.getPhoneNum());
            ImageLoadUtils.disPlayWitchCircleForceNetwork(account.getAvatarUrl(), headerIv);
            mLoginOutBtn.setText("注销");

        } else {
            nickNameTv.setText("未登录");
            mLoginOutBtn.setText("登录");
            headerIv.setImageResource(R.mipmap.ic_launcher);
        }

    }

    @Override
    protected void initListener() {
        findViewById(R.id.iv_wechat).setOnClickListener(this);
        findViewById(R.id.iv_qq).setOnClickListener(this);
        findViewById(R.id.iv_sina).setOnClickListener(this);
        findViewById(R.id.iv_friend).setOnClickListener(this);
        findViewById(R.id.btn_login_out).setOnClickListener(this);
        findViewById(R.id.iv_header).setOnClickListener(this);
        findViewById(R.id.btn_to_page_ternimal).setOnClickListener(this);
        findViewById(R.id.btn_current_city).setOnClickListener(this);
        findViewById(R.id.btn_house_type).setOnClickListener(this);
        findViewById(R.id.btn_area).setOnClickListener(this);
        findViewById(R.id.btn_show_pic).setOnClickListener(this);
        findViewById(R.id.btn_show_pic2).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_wechat: //分享至微信好友
                ShareUtils.shareWithoutSurface(mContext, getShareContent(), mCallback, SnsConfig.SHARE_WECHAT);
                break;
            case R.id.iv_qq: //分享至qq
                ShareUtils.shareWithoutSurface(mContext, getShareContent(), mCallback, SnsConfig.SHARE_TENCENT);
                break;
            case R.id.iv_sina://分享至新浪
                ShareUtils.shareWithoutSurface(mContext, getShareContent(), mCallback, SnsConfig.SHARE_SINA);
                break;
            case R.id.iv_friend:  //分享至微信朋友圈
                ShareUtils.shareWithoutSurface(mContext, getShareContent(), mCallback, SnsConfig.SHARE_WECHAT_FRIEND);
                break;
            case R.id.btn_login_out:
                if (mLoginOutBtn.getText().equals("注销")) {
                    AccountUtils.logout();
                    loadData();
                    ToastUtils.showShort("注销成功");
                } else {
                    JumpUtils.startActivityForResult(mContext, LoginActivity.class, Constant.REQ_LOGIN);
                }

                break;
            case R.id.iv_header:
                if (AccountUtils.isLogin()) {
                    JumpUtils.startActivityForResult(mContext, SettingActivity.class, Constant.REQ_UPDATE_HEADER);
                }
                break;

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
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.REQ_LOGIN:
                    loadData();
                    break;
                case Constant.REQ_UPDATE_HEADER:
                    String filePath = data.getStringExtra(CropActivity.CROP_IMAGE_PATH);
                    File outputFile = new File(filePath);
                    doUploadHeaderImage(outputFile);
                    break;
            }


        }
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private void doUploadHeaderImage(final File uploadFile) {
        String fileName = dateFormat.format(new Date()) + ".jpg"; //头像文件的名称
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Cookie", Urls.COMMON_SESSION_ID + AccountUtils.getSessionId());
        String url = "http://upc.pcbaby.com.cn/upload_head.jsp";
        OkHttpEngine.getInstance().asyncPostImage(fileName, fileName, url, new RequestCallBackHandler() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort("头像上传失败");
            }

            @Override
            public Object doInBackground(OkResponse pcResponse) {
                return null;
            }

            @Override
            public void onResponse(Object o, OkResponse pcResponse) {
                if (null != pcResponse) {
                    try {
                        JSONObject jsonObject = new JSONObject(pcResponse.getResult());
                        int retCode = jsonObject.optInt("retCode");
                        if (0 == retCode) {
                            ToastUtils.showShort("头像上传成功");
                            ImageLoadUtils.disPlayWitchCircleForceNetwork(uploadFile, headerIv);
                            //更新数据
                            AccountUtils.getUserInfo(true, AccountUtils.getLoginAccount(), null);

                        } else {
                            ToastUtils.showShort("头像上传失败");
                        }
                    } catch (JSONException e) {
                        ToastUtils.showShort("头像上传失败");
                    }
                }
            }
        }, uploadFile, "", headersMap, null);
    }

    private SnsShareContent getShareContent() {
        SnsShareContent shareContent = ShareUtils.wrapShareContent("测试分享", "我是描述",
                "我是内容", "我是隐藏的", "https://www.baidu.com",
                "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo_top_ca79a146.png");
        return shareContent;
    }

    ShareUtils.Callback mCallback = new ShareUtils.Callback() {
        @Override
        public void onSucceed() {

        }

        @Override
        public void onFailed() {

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
        return tingList.get(options1)+","+shiList.get(options2)+","+weiList.get(options3);
    }

    private String parseSelectedResult2(int options1, int options2, int options3) {
        return provinceList.get(options1).getName()+","+cityNameList.get(options1).get(options2)+","+
                lastNameList.get(options1).get(options2).get(options3);
    }
}
