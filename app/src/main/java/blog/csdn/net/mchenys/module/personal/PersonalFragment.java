package blog.csdn.net.mchenys.module.personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.utils.ShareUtils;


/**
 * 个人中心
 * Created by mChenys on 2017/12/28.
 */

public class PersonalFragment extends BaseFragment implements View.OnClickListener {
    private String title;
    private int position;

    @Override
    protected Integer getLayoutResID() {
        return R.layout.fragment_personal;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.title = getArguments().getString(Constant.KEY_TITLE);
        this.position = getArguments().getInt(Constant.KEY_POSITION);
    }


    @Override
    protected void initView() {
        super.initView();
        TextView infoTv = findViewById(R.id.tv_info);
        infoTv.setText(position + ":" + title);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.iv_wechat).setOnClickListener(this);
        findViewById(R.id.iv_qq).setOnClickListener(this);
        findViewById(R.id.iv_sina).setOnClickListener(this);
        findViewById(R.id.iv_friend).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_wechat: //分享至微信好友
                ShareUtils.shareWithoutSurface(mContext, getShareContent() , mCallback, SnsConfig.SHARE_WECHAT);
                break;
            case R.id.iv_qq: //分享至qq
                ShareUtils.shareWithoutSurface(mContext, getShareContent() , mCallback, SnsConfig.SHARE_TENCENT);
                break;
            case R.id.iv_sina://分享至新浪
                ShareUtils.shareWithoutSurface(mContext, getShareContent() , mCallback, SnsConfig.SHARE_SINA);
                break;
            case R.id.iv_friend:  //分享至微信朋友圈
                ShareUtils.shareWithoutSurface(mContext, getShareContent() , mCallback, SnsConfig.SHARE_WECHAT_FRIEND);
                break;
        }
    }

    private SnsShareContent getShareContent() {
        SnsShareContent shareContent = ShareUtils.wrapShareContent("测试分享","我是描述",
                "我是内容","我是隐藏的","https://www.baidu.com",
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
