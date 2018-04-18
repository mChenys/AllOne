package blog.csdn.net.mchenys.common.widget.webview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.webkit.JavascriptInterface;

import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.utils.AccountUtils;
import blog.csdn.net.mchenys.common.utils.ShareUtils;


/**
 * Created by mChenys on 2017/12/27.
 */

public class MyWebViewJavaScriptSInterface  {
    public static final int CODE_SHARE_FAIL = 0;
    public static final int CODE_SHARE_SUCCESS = 1;
    public static final int CODE_GET_HTML = 2;
    private Handler mHandler;
    private Context mContext;

    public MyWebViewJavaScriptSInterface(@NonNull Context context, @NonNull Handler mHandler) {
        super();
        this.mHandler = mHandler;
        this.mContext = context;
    }

    @JavascriptInterface
    public String appVer() {
        return String.valueOf(Env.versionCode);
    }

    @JavascriptInterface
    public boolean login() {
        return AccountUtils.isLogin();
    }

    @JavascriptInterface
    public String commonSessionId() {
        return AccountUtils.getSessionId();
    }

    @JavascriptInterface
    public String appId() {
        return Env.packageName;
    }

    @JavascriptInterface
    public void getHtml(String html) {
        this.mHandler.obtainMessage(CODE_GET_HTML, html).sendToTarget();
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
    @JavascriptInterface
    public void share(String title, String content, final String wapurl, String icon, final String callBack) {
        SnsShareContent shareEntity = ShareUtils.wrapShareContent(title, content, null, null, wapurl, icon);
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
    @JavascriptInterface
    public void shareWithoutSurface(String title, String content, final String wapurl, String icon, final String callBack, int type) {
        SnsShareContent shareEntity = ShareUtils.wrapShareContent(title, content, null, null, wapurl, icon);
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

    public int getSType(int type) {
        int sType = SnsConfig.SHARE_SINA;
        switch (type) {
            case 1:
                sType = SnsConfig.SHARE_WECHAT;
                break;
            case 2:
                sType = SnsConfig.SHARE_WECHAT_FRIEND;
                break;
            case 3:
                sType = SnsConfig.SHARE_SINA;
                break;
            case 4:
                sType = SnsConfig.SHARE_TENCENT;
                break;
        }
        return sType;
    }
}
