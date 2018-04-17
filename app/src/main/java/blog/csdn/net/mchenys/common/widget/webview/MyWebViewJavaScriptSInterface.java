package blog.csdn.net.mchenys.common.widget.webview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.imofan.android.develop.sns.MFSnsShare;
import com.imofan.android.develop.sns.MFSnsShareContent;

import cn.com.pc.framwork.utils.app.WebViewJavaScriptSInterface;
import cn.com.pconline.shopping.common.config.Env;
import cn.com.pconline.shopping.common.utils.AccountUtils;
import cn.com.pconline.shopping.common.utils.ShareUtils;

/**
 * Created by mChenys on 2017/12/27.
 */

public class MyWebViewJavaScriptSInterface extends WebViewJavaScriptSInterface {

    private Handler mHandler;
    private Context mContext;
    public static final int HIDE_SHARE_BUTTON = 3;

    public MyWebViewJavaScriptSInterface(@NonNull Context context, @NonNull Handler mHandler) {
        super(context, mHandler);
        this.mHandler = mHandler;
        this.mContext = context;
    }

    @Override
    public String appVer() {
        return String.valueOf(Env.versionCode);
    }

    @Override
    public boolean login() {
        return AccountUtils.isLogin();
    }

    @Override
    public String commonSessionId() {
        return AccountUtils.getSessionId();
    }

    @Override
    public String appId() {
        return Env.packageName;
    }

    /**
     * 分享
     *
     * @param title    标题
     * @param content  描述
     * @param wapurl   分享wap连接
     * @param icon     分享图标
     * @param callBack 分享成功回调
     */
    public void share(String title, String content, final String wapurl, String icon, final String callBack) {
        MFSnsShareContent shareEntity = ShareUtils.wrapShareContent(title, content, null, null, wapurl, icon);
        ShareUtils.share(mContext, shareEntity, new ShareUtils.Callback() {
            @Override
            public void onSucceed() {
                mHandler.obtainMessage(CODE_SHARE_SUCCESS, callBack).sendToTarget();
            }

            @Override
            public void onFailed() {
                mHandler.obtainMessage(CODE_SHARE_FAIL, callBack).sendToTarget();
            }
        });
    }

    /**
     * 分享(无界面)
     *
     * @param title    标题
     * @param content  内容
     * @param wapurl   分享wap连接
     * @param icon     分享图标
     * @param callBack 分享成功回调
     * @param type     1:微信好友 2:微信朋友圈 3:微博 4:QQ好友
     */
    public void shareWithoutSurface(String title, String content, final String wapurl, String icon, final String callBack, int type) {
        MFSnsShareContent shareEntity = ShareUtils.wrapShareContent(title, content, null, null, wapurl, icon);
        ShareUtils.shareWithoutSurface(mContext, shareEntity, new ShareUtils.Callback() {
            @Override
            public void onSucceed() {
                mHandler.obtainMessage(CODE_SHARE_SUCCESS, callBack).sendToTarget();
            }

            @Override
            public void onFailed() {
                mHandler.obtainMessage(CODE_SHARE_FAIL, callBack).sendToTarget();
            }
        }, getSType(type));
    }


    @Override
    public void count(String s) {

    }

    @Override
    public void countWithId(String s, String s1, String s2) {

    }

    @Override
    public void events(String s) {

    }

    @Override
    public void eventKeyAndTag(String s, String s1) {

    }

    @Override
    public void hiddenShareButtonHidden(boolean b) {
        if (b) {
            this.mHandler.obtainMessage(HIDE_SHARE_BUTTON, null).sendToTarget();
        }

    }

    public int getSType(int type) {
        int sType = MFSnsShare.SHARE_SINA;
        switch (type) {
            case 1:
                sType = MFSnsShare.SHARE_WECHAT;
                break;
            case 2:
                sType = MFSnsShare.SHARE_WECHAT_FRIEND;
                break;
            case 3:
                sType = MFSnsShare.SHARE_SINA;
                break;
            case 4:
                sType = MFSnsShare.SHARE_TENCENT;
                break;
        }
        return sType;
    }
}
