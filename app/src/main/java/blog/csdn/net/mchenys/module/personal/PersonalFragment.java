package blog.csdn.net.mchenys.module.personal;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.okhttp2.x.OkHttpEngine;
import blog.csdn.net.mchenys.common.okhttp2.x.listener.RequestCallBackHandler;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.photo.crop.CropActivity;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.utils.AccountUtils;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.utils.ShareUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.model.Account;
import blog.csdn.net.mchenys.module.account.LoginActivity;


/**
 * 个人中心
 * Created by mChenys on 2017/12/28.
 */

public class PersonalFragment extends BaseFragment implements View.OnClickListener {

    private Button mLoginOutBtn;
    private TextView nickNameTv;
    private ImageView headerIv;
    private TextView phoneTv;

    @Override
    protected Integer getLayoutResID() {
        return R.layout.fragment_personal;
    }

    @Override
    protected boolean isSavePagerStatus() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();
        mLoginOutBtn = findViewById(R.id.btn_login_out);
        nickNameTv = findViewById(R.id.tv_nickName);
        phoneTv = findViewById(R.id.tv_phone);
        headerIv = findViewById(R.id.iv_header);
    }

    @Override
    protected void loadData() {
        super.loadData();
        Account account = AccountUtils.getLoginAccount();
        if (null != account) {
            nickNameTv.setText(account.getUserName());
            phoneTv.setText(account.getPhoneNum());
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
}
