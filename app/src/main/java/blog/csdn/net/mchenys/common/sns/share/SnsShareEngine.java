//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX.Req;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX.Resp;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage.IMediaObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.util.ArrayList;

import blog.csdn.net.mchenys.common.sns.SnsUtils;
import blog.csdn.net.mchenys.common.sns.autho.SnsSSOLoginEngine;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.sns.bean.SnsUser;
import blog.csdn.net.mchenys.common.sns.callback.SnsAuthListener;
import blog.csdn.net.mchenys.common.sns.callback.SnsShareListener;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.sns.config.SnsManager;
import blog.csdn.net.mchenys.common.sns.SnsImageShareUtil;


/**
 * 分享主类
 */
public class SnsShareEngine {

    public int sharePlatform = -1;
    protected Context context;
    protected SnsShareContent contentMessage;
    protected SnsShareListener shareListener;
    protected int wechatType;
    protected SnsSSOLoginEngine ssoLogin;
    protected Tencent mTencent;
    protected IWXAPI api = null;
    public static boolean isHttpImage = false;
    public static boolean imageDownDone = false;
    Handler doResultHandler = new Handler();

    //qq分享相关的回调
    private IUiListener iUiListener = new IUiListener() {
        public void onCancel() {
            if (SnsShareEngine.this.shareListener != null) {
                SnsShareEngine.this.shareListener.onFailed(SnsShareEngine.this.context, "mf_share_cancel");
            }

        }

        public void onError(UiError e) {
            if (SnsShareEngine.this.shareListener != null) {
                SnsShareEngine.this.shareListener.onFailed(SnsShareEngine.this.context, "mf_share_failed");
            }

        }

        public void onComplete(Object response) {
            if (SnsShareEngine.this.shareListener != null) {
                SnsShareEngine.this.shareListener.onTencentQQSucceeded(SnsShareEngine.this.context, response);
                SnsShareEngine.this.shareListener.onSucceeded(SnsShareEngine.this.context);
            }

        }
    };

    public SnsShareEngine() {
    }

    /**
     * 无界面的分享入口
     *
     * @param context
     * @param platform
     * @param contentMessage
     * @param shareListener
     */
    public void share(final Context context, int platform, final SnsShareContent contentMessage, SnsShareListener shareListener) {
        this.context = context;
        this.contentMessage = contentMessage;
        this.sharePlatform = platform;
        this.shareListener = shareListener;
        switch (platform) {
            case SnsConfig.SHARE_WECHAT:
                this.WeiXinClick(Req.WXSceneSession);
                break;
            case SnsConfig.SHARE_WECHAT_FRIEND:
                this.WeiXinClick(Req.WXSceneTimeline);
                break;
            case SnsConfig.SHARE_TENCENT:
                this.qqFriendsClick();
                break;
            case SnsConfig.SHARE_TENCENT_ZONE:
                this.shareToQzone();
                break;
            case SnsConfig.SHARE_SINA:
                if (contentMessage != null) {
                    (new Thread(new Runnable() {
                        public void run() {
                            SnsImageShareUtil.saveImage(context, contentMessage.getImage());
                            SnsShareEngine.this.doResultHandler.post(new Runnable() {
                                public void run() {
                                    SnsShareEngine.this.shareToWeibo(SnsConfig.PLATFORM_SINA_WEIBO);
                                }
                            });
                        }
                    })).start();
                }
                break;
            case SnsConfig.SHARE_TENCENT_BLOG:
                this.shareToWeibo(SnsConfig.PLATFORM_QQ_WEIBO);
        }

    }

    /**
     * 分享到新浪微博/qq微博(已取消)
     * @param platform
     */
    protected void shareToWeibo(int platform) {
        String shareEnd = null;
        if (platform == SnsConfig.PLATFORM_QQ_WEIBO) {
            shareEnd = "appShare=qqweibo";
        } else if (platform == SnsConfig.PLATFORM_SINA_WEIBO) {
            shareEnd = "appShare=sina";
        }

        SnsShareContent weiboShareContent = this.contentMessage;
        if (!TextUtils.isEmpty(weiboShareContent.getUrl())) {
            if (weiboShareContent.getUrl().contains("?")) {
                weiboShareContent.setUrl(weiboShareContent.getUrl() + "&" + shareEnd);
            } else {
                weiboShareContent.setUrl(weiboShareContent.getUrl() + "?" + shareEnd);
            }
        }

        if (isHttpImage && !TextUtils.isEmpty(weiboShareContent.getImage())) {
            if (weiboShareContent.getImage().contains("?")) {
                weiboShareContent.setImage(weiboShareContent.getImage() + "&" + shareEnd);
            } else {
                weiboShareContent.setImage(weiboShareContent.getImage() + "?" + shareEnd);
            }
        }

        if (!TextUtils.isEmpty(weiboShareContent.getWapUrl())) {
            if (weiboShareContent.getWapUrl().contains("?")) {
                weiboShareContent.setWapUrl(weiboShareContent.getWapUrl() + "&" + shareEnd);
            } else {
                weiboShareContent.setWapUrl(weiboShareContent.getWapUrl() + "?" + shareEnd);
            }
        }

        if (!TextUtils.isEmpty(weiboShareContent.getHideContent())) {
            if (weiboShareContent.getHideContent().contains("?")) {
                weiboShareContent.setHideContent(weiboShareContent.getHideContent() + "&" + shareEnd);
            } else {
                weiboShareContent.setHideContent(weiboShareContent.getHideContent() + "?" + shareEnd);
            }
        }

        if (SnsUtils.isAuthorized(this.context, platform)) {
            SnsUtils.uploadToSinaAllInOne(this.context, this.contentMessage);
        } else {
            SnsAuthListener authListener = new SnsAuthListener() {
                public void onSucceeded(Context context, SnsUser openUser) {
                    SnsUtils.uploadToSinaAllInOne(context, SnsShareEngine.this.contentMessage);
                }

                public void onFail(Context context, String errMessage) {
                }
            };
            this.ssoLogin = SnsManager.getSSOLogin();
            this.ssoLogin.SSOLogin(this.context, platform, authListener);
        }

    }

    /**
     * 分享到qq空间
     */
    protected void shareToQzone() {
        this.mTencent = Tencent.createInstance(SnsConfig.CONSUMER_KEY_TECENT, this.context);
        String url = this.contentMessage.getUrl();
        String imgUrl = this.contentMessage.getImage();
        String wapUrl = this.contentMessage.getWapUrl();
        if ((imgUrl == null || imgUrl.length() <= 0) && (wapUrl == null || wapUrl.length() <= 0)) {
            this.showToast("qq空间暂不支持纯文本格式");
        } else {
            if (imgUrl != null && imgUrl.length() > 0) {
                if (!imgUrl.startsWith("http")) {
                    this.showToast("qq空间暂不支持本地图片分享");
                    return;
                }

                if (this.contentMessage.getImage().contains("?")) {
                    imgUrl = imgUrl + "&appShare=qqzone";
                } else {
                    imgUrl = imgUrl + "?appShare=qqzone";
                }
            }

            if (!TextUtils.isEmpty(wapUrl)) {
                if (wapUrl.contains("?")) {
                    wapUrl = wapUrl + "&appShare=qqzone";
                } else {
                    wapUrl = wapUrl + "?appShare=qqzone";
                }
            }

            if (!TextUtils.isEmpty(url)) {
                if (url.contains("?")) {
                    url = url + "&appShare=qqzone";
                } else {
                    url = url + "?appShare=qqzone";
                }
            }

            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, this.contentMessage.getTitle());
            if (null != this.contentMessage.getDescription() && !this.contentMessage.getDescription().equals("")) {
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, this.contentMessage.getDescription());
            }
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
            if (null != imgUrl && !imgUrl.equals("")) {
                ArrayList<String> list = new ArrayList();
                list.add(imgUrl);
                params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, list);
            } else {
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, 0);
            }

            this.mTencent.shareToQzone((Activity) this.context, params, this.iUiListener);
        }
    }

    /**
     * qq分享
     */
    protected void qqFriendsClick() {
        this.mTencent = Tencent.createInstance(SnsConfig.CONSUMER_KEY_TECENT, this.context);
        String url = this.contentMessage.getImage();
        String wapUrl = this.contentMessage.getWapUrl();
        if ((url == null || url.length() <= 0) && (wapUrl == null || wapUrl.length() <= 0)) {
            this.showToast("qq分享好友暂不支持纯文本格式");
        } else {
            if (url != null && url.length() > 0 && !url.startsWith("http")) {
                this.qqFriendsClickWhenSentLocalImg();
            } else {
                this.qqFriendsClickWhenSentHttpImg();
            }

        }
    }

    /**
     * 分享网络图片到qq好友
     */
    protected void qqFriendsClickWhenSentHttpImg() {
        Bundle params = new Bundle();
        //默认类型
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, this.contentMessage.getTitle());
        String imgUrl = this.contentMessage.getImage();
        if (null != imgUrl && !imgUrl.equals("")) {
            if (imgUrl.contains("http://") && imgUrl.contains("?")) {
                imgUrl = imgUrl + "&appShare=qq";
            } else if (imgUrl.contains("http://")) {
                imgUrl = imgUrl + "?appShare=qq";
            }

            ArrayList<String> list = new ArrayList();
            list.add(imgUrl);
            params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, list);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl); //分享的图片URL
        } else {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, 0);
        }

        String url = this.contentMessage.getUrl();
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("?")) {
                url = url + "&appShare=qq";
            } else {
                url = url + "?appShare=qq";
            }
        }
        //这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        if (null != this.contentMessage.getDescription() && !this.contentMessage.getDescription().equals("")) {
            //分享的消息摘要，最长50个字
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, this.contentMessage.getDescription());
        }
        this.mTencent.shareToQQ((Activity) this.context, params, this.iUiListener);
    }

    /**
     * 分享本地图片到qq好友
     */
    protected void qqFriendsClickWhenSentLocalImg() {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, this.contentMessage.getImage());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        this.mTencent.shareToQQ((Activity) this.context, params, this.iUiListener);
    }

    /**
     * 微信分享
     * @param type  SendMessageToWX.Req.WXSceneSession :好友
     *               SendMessageToWX.Req.WXSceneTimeline :朋友圈
     */
    protected void WeiXinClick(int type) {
        this.api = WXAPIFactory.createWXAPI(this.context, SnsConfig.CONSUMER_WEIXIN_APPID, true);
        this.api.registerApp(SnsConfig.CONSUMER_WEIXIN_APPID);
        String wapUrl = this.contentMessage.getWapUrl();
        if (!TextUtils.isEmpty(wapUrl)) {
            if (wapUrl.contains("?")) {
                if (type == Req.WXSceneSession) {
                    wapUrl = wapUrl + "&appShare=wx";
                } else {
                    wapUrl = wapUrl + "&appShare=wxtimeline";
                }
            } else if (type == Req.WXSceneSession) {
                wapUrl = wapUrl + "?appShare=wx";
            } else {
                wapUrl = wapUrl + "?appShare=wxtimeline";
            }
        }

        boolean isInstalledAndSupported = this.api.isWXAppInstalled();
        if (!isInstalledAndSupported) {
            this.shareListener.onWeiXinNoSupported(this.context, isInstalledAndSupported);
        } else if (isHttpImage && !imageDownDone) {
            this.showToast("图片尚未下载完成，请稍等");
        } else {
            File imageFile = null;
            Object appdata;
            String transaction;
            if (null != this.contentMessage.getImage()) {
                if (isHttpImage) {
                    imageFile = new File(SnsImageShareUtil.getCachUrl(this.context));
                    WXWebpageObject data = new WXWebpageObject();
                    data.webpageUrl = wapUrl;
                    appdata = data;
                    transaction = this.buildTransaction("webpage");
                } else {
                    imageFile = new File(this.contentMessage.getImage());
                    WXImageObject data = new WXImageObject();
                    data.imagePath = this.contentMessage.getImage();
                    appdata = data;
                    transaction = this.buildTransaction("imgshareappdata");
                }
            } else if (null == wapUrl) {
                WXTextObject data = new WXTextObject();
                data.text = this.contentMessage.getContent();
                appdata = data;
                transaction = this.buildTransaction("textshare");
            } else {
                WXWebpageObject data = new WXWebpageObject();
                data.webpageUrl = wapUrl;
                appdata = data;
                transaction = this.buildTransaction("webpage");
            }

            WXMediaMessage msg = new WXMediaMessage();
            if (null != imageFile) {
                Bitmap tmp = BitmapFactory.decodeFile(imageFile.getPath());
                msg.setThumbImage(ThumbnailUtils.extractThumbnail(tmp, 150, 150));
            }

            msg.title = this.contentMessage.getTitle();
            if (null != this.contentMessage.getDescription()) {
                msg.description = this.contentMessage.getDescription();
            }

            msg.mediaObject = (IMediaObject) appdata;
            Req req = new Req();
            req.transaction = transaction;
            req.message = msg;
            req.scene = type;
            this.wechatType = type;
            boolean isSuccess = this.api.sendReq(req);
            if (!isSuccess && null != this.shareListener) {
                this.shareListener.onFailed(this.context, "mf_share_failed");
            }

        }
    }

    /**
     * 微信分享结果回调,在WXEntryActivity中调用
     * @param resp
     */
    public void wxShareCallback(BaseResp resp) {
        Resp sendResp = (Resp) resp;
        if (sendResp.errCode == 0) {
            if (null != this.shareListener) {
                if (this.wechatType == Req.WXSceneTimeline) { //微信朋友圈
                    this.shareListener.onWeiXinFriendsSucceeded(this.context);
                } else if (this.wechatType ==  Req.WXSceneSession) { //微信好友
                    this.shareListener.onWeiXinSucceeded(this.context);
                }

                this.shareListener.onSucceeded(this.context);
            }
        } else if (sendResp.errCode == -2) {
            if (null != this.shareListener) {
                this.shareListener.onFailed(this.context, "mf_share_cancel");
            }
        } else if (null != this.shareListener) {
            this.shareListener.onFailed(this.context, "mf_share_failed");
        }

    }

    protected String buildTransaction(String type) {
        return type == null ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    protected void showToast(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.mTencent != null && (this.sharePlatform == SnsConfig.SHARE_TENCENT ||
                this.sharePlatform == SnsConfig.SHARE_TENCENT_BLOG ||
                this.sharePlatform == SnsConfig.SHARE_TENCENT_ZONE)) {
            Tencent.onActivityResultData(requestCode, resultCode, data, this.iUiListener);
        }

    }
}
