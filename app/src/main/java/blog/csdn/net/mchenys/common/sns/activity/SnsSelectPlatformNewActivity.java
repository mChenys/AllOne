//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.sns.autho.SnsSSOLoginEngine;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.sns.callback.SnsShareListener;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.sns.config.SnsManager;
import blog.csdn.net.mchenys.common.sns.share.SnsShareEngine;
import blog.csdn.net.mchenys.common.sns.OauthUtils;
import blog.csdn.net.mchenys.common.sns.SnsImageShareUtil;

/**
 * 带界面分享
 */
public class SnsSelectPlatformNewActivity extends Activity implements OnClickListener {
    private static SnsShareListener shareListener;
    private SnsShareContent contentMessage;
    private LinearLayout topLayout;
    private SnsSSOLoginEngine ssoLogin;
    private SnsShareEngine mSnsShareEngine;


    public static void setShareListener(SnsShareListener shareListener) {
        SnsSelectPlatformNewActivity.shareListener = shareListener;
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        OauthUtils.doResultIntent(this, intent, shareListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.imofan_weibo_select_platform_activity);
        contentMessage = (SnsShareContent) getIntent().getSerializableExtra("content");
        ssoLogin = SnsManager.getSSOLogin();
        mSnsShareEngine = SnsManager.getSnsShare();
        SnsShareEngine.isHttpImage = false;
        SnsShareEngine.imageDownDone = false;
        initView();
    }

    private void initView() {
        topLayout = findViewById(R.id.imofan_top_layout);
        findViewById(R.id.imofan_bottom_layout).setOnClickListener(this);
        findViewById(R.id.imofan_share_qq_weibo).setOnClickListener(this);
        findViewById(R.id.imofan_share_webchat).setOnClickListener(this);
        findViewById(R.id.imofan_share_weixin).setOnClickListener(this);
        findViewById(R.id.imofan_share_sina).setOnClickListener(this);
        findViewById(R.id.imofan_share_qzone).setOnClickListener(this);
        findViewById(R.id.imofan_share_qqfriends).setOnClickListener(this);
        findViewById(R.id.imofan_share_copy).setOnClickListener(this);
        findViewById(R.id.imofan_share_cancle).setOnClickListener(this);
        topLayout.setOnClickListener(this);
        if (!contentMessage.getImage().isEmpty()) {
            SnsImageShareUtil.setImage(this, contentMessage.getImage());
        }
    }

    protected void onResume() {
        super.onResume();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                topLayout.setBackgroundColor(Color.parseColor("#b0222222"));
            }
        }, 500L);
        if (shareListener != null) {
            shareListener.onSelectedResume(this);
        }

    }

    protected void onPause() {
        super.onPause();
        if (shareListener != null) {
            shareListener.onSelectedPause(this);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != ssoLogin) {
            ssoLogin.onActivityResult(requestCode, resultCode, data);
        }

        mSnsShareEngine.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        back();
    }

    private void back() {
        topLayout.setBackgroundColor(Color.parseColor("#00222222"));
        finish();
        overridePendingTransition(0, R.anim.imofan_center_out);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imofan_top_layout:
            case R.id.imofan_share_cancle:
                back();
                break;
            case R.id.imofan_share_qq_weibo:
                mSnsShareEngine.share(SnsSelectPlatformNewActivity.this, SnsConfig.SHARE_TENCENT_BLOG, contentMessage, shareListener);
                break;
            case R.id.imofan_share_webchat:
                mSnsShareEngine.share(SnsSelectPlatformNewActivity.this, SnsConfig.SHARE_WECHAT_FRIEND, contentMessage, shareListener);
                break;
            case R.id.imofan_share_weixin:
                mSnsShareEngine.share(SnsSelectPlatformNewActivity.this, SnsConfig.SHARE_WECHAT, contentMessage, shareListener);
                break;
            case R.id.imofan_share_sina:
                mSnsShareEngine.share(SnsSelectPlatformNewActivity.this, SnsConfig.SHARE_SINA, contentMessage, shareListener);
                break;
            case R.id.imofan_share_qzone:
                mSnsShareEngine.share(SnsSelectPlatformNewActivity.this, SnsConfig.SHARE_TENCENT_ZONE, contentMessage, shareListener);
                break;
            case R.id.imofan_share_qqfriends:
                mSnsShareEngine.share(SnsSelectPlatformNewActivity.this, SnsConfig.SHARE_TENCENT, contentMessage, shareListener);
                break;
            case R.id.imofan_share_copy:
                shareListener.onTextSharedCopy(SnsSelectPlatformNewActivity.this);
                break;
        }
    }
}
