package blog.csdn.net.mchenys.module.personal;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.config.Constant;
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
            ImageLoadUtils.disPlayWithCircle(account.getAvatarUrl(), headerIv);
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
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQ_LOGIN && resultCode == Activity.RESULT_OK) {
            loadData();
        }
    }
}
