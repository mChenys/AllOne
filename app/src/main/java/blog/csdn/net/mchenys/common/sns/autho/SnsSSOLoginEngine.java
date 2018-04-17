//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns.autho;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth.Req;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import blog.csdn.net.mchenys.common.sns.callback.SnsAuthListener;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.sns.OauthUtils;
import blog.csdn.net.mchenys.common.sns.SnsHttpUtils;


//登录相关
public class SnsSSOLoginEngine {
    private Context context;
    private Context appContext;
    private int platform;
    private SnsAuthListener authListener;
    private Tencent mTencent;
    private static final String SCOPE = "get_user_info,get_simple_userinfo,get_user_profile,get_app_friends,add_share,add_topic,list_album,upload_pic,add_album,set_user_face,get_vip_info,get_vip_rich_info,get_intimate_friends_weibo,match_nick_tips_weibo";
    private AuthInfo mAuthInfo;
    public SsoHandler mSsoHandler;
    private IWXAPI api = null;
    private static SnsSSOLoginEngine instance;

    private IUiListener tencentLoginListener = new IUiListener() {
        public void onError(UiError arg0) {
            if(null != SnsSSOLoginEngine.this.authListener && null != arg0) {
                SnsSSOLoginEngine.this.authListener.onFail(SnsSSOLoginEngine.this.context, arg0.errorMessage);
            }

        }

        public void onCancel() {
            if(null != SnsSSOLoginEngine.this.authListener) {
                SnsSSOLoginEngine.this.authListener.onFail(SnsSSOLoginEngine.this.context, "cancel_login");
            }

        }

        public void onComplete(Object arg0) {
            try {
                JSONObject arg1 = new JSONObject(arg0.toString());
                (new Thread(SnsSSOLoginEngine.this.new GetUserInforTask(arg1.optString("access_token"),
                        arg1.optString("expires_in"), arg1.optString("openid")))).start();
            } catch (JSONException var4) {
                var4.printStackTrace();
            }

        }
    };
    Handler doResultHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(null != SnsSSOLoginEngine.this.authListener && msg.obj != null && !msg.obj.equals("failed")) {
                SnsSSOLoginEngine.this.authListener.onSucceeded(SnsSSOLoginEngine.this.context, OauthUtils.getOpenUser(SnsSSOLoginEngine.this.context, SnsSSOLoginEngine.this.platform));
            } else if(null != SnsSSOLoginEngine.this.authListener) {
                SnsSSOLoginEngine.this.authListener.onFail(SnsSSOLoginEngine.this.context, "账号或网络发生异常，登陆失败");
            }

            super.handleMessage(msg);
        }
    };

    public SnsSSOLoginEngine() {
    }

    public static SnsSSOLoginEngine getInstance() {
        if(instance == null) {
            synchronized(SnsSSOLoginEngine.class) {
                if(instance == null) {
                    instance = new SnsSSOLoginEngine();
                }
            }
        }

        return instance;
    }

    public void SSOLogin(Context context, int platform, SnsAuthListener authListener) {
        this.context = context;
        this.appContext = context.getApplicationContext();
        this.platform = platform;
        this.authListener = authListener;
        if(platform != SnsConfig.PLATFORM_QQ_WEIBO && platform != SnsConfig.PLATFORM_QQ_QZONE) {
            if(platform == SnsConfig.PLATFORM_SINA_WEIBO) {
                this.sinaSSOLogin();
            } else if(platform == SnsConfig.PLATFORM_WEIXIN) {
                this.weChatSSOLogin();
            }
        } else {
            this.tencentSSOLogin();
        }

    }

    private void weChatSSOLogin() {
        this.api = WXAPIFactory.createWXAPI(this.context, SnsConfig.CONSUMER_WEIXIN_APPID, true);
        this.api.registerApp(SnsConfig.CONSUMER_WEIXIN_APPID);
        boolean isInstalledAndSupported = this.api.isWXAppInstalled();
        if(isInstalledAndSupported) {
            Req req = new Req();
            req.scope = "snsapi_userinfo";
            req.state = "pcgroup" + System.currentTimeMillis();
            this.api.sendReq(req);
        }
    }

    public void weChatCallBack(BaseResp resp) {
        this.platform = SnsConfig.PLATFORM_WEIXIN;
        Resp sendResp = (Resp)resp;
        if(sendResp.errCode == -2) {
            if(null != this.authListener) {
                this.authListener.onFail(this.context, "cancel_login");
            }

        } else {
            String code = sendResp.code;
            final String applyToken = SnsConfig.AUTHORIZE_WEIXIN_Token + "?appid=" + SnsConfig.CONSUMER_WEIXIN_APPID + "&secret=" + SnsConfig.CONSUMER_WEIXIN_SECRET + "&code=" + code + "&grant_type=authorization_code";
            (new Thread(new Runnable() {
                public void run() {
                    try {
                        String content = SnsHttpUtils.httpGet(applyToken, (String)null);
                        JSONObject jsonObject = new JSONObject(content);
                        (new Thread(SnsSSOLoginEngine.this.new GetUserInforTask(jsonObject.optString("access_token"), jsonObject.optString("expires_in"), jsonObject.optString("openid")))).start();
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }

                }
            })).start();
        }
    }

    private void tencentSSOLogin() {
        this.mTencent = Tencent.createInstance(SnsConfig.CONSUMER_KEY_TECENT, this.context);
        this.mTencent.logout(this.context);
        this.mTencent.login((Activity)this.context, "get_user_info,get_simple_userinfo,get_user_profile,get_app_friends,add_share,add_topic,list_album,upload_pic,add_album,set_user_face,get_vip_info,get_vip_rich_info,get_intimate_friends_weibo,match_nick_tips_weibo", this.tencentLoginListener);
    }

    public void loginout(Context context) {
        this.mTencent = Tencent.createInstance(SnsConfig.CONSUMER_KEY_TECENT, context);
        this.mTencent.logout((Activity)context);
    }

    private void sinaSSOLogin() {
        this.mAuthInfo = new AuthInfo(this.context, SnsConfig.CONSUMER_KEY_SINA, SnsConfig.CONSUMER_REDIRECT_URL_SINA, "");
        this.mSsoHandler = new SsoHandler((Activity)this.context);
        this.mSsoHandler.authorize(new WbAuthListener() {
            public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
                AccessTokenKeeper.writeAccessToken(SnsSSOLoginEngine.this.context, oauth2AccessToken);
                (new Thread(SnsSSOLoginEngine.this.new GetUserInforTask(oauth2AccessToken.getToken(), String.valueOf(oauth2AccessToken.getExpiresTime()), oauth2AccessToken.getUid()))).start();
            }

            public void cancel() {
                if(null != SnsSSOLoginEngine.this.authListener) {
                    SnsSSOLoginEngine.this.authListener.onFail(SnsSSOLoginEngine.this.context, "cancel_login");
                }

            }

            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                if(null != SnsSSOLoginEngine.this.authListener && null != wbConnectErrorMessage && null != wbConnectErrorMessage.getErrorMessage()) {
                    SnsSSOLoginEngine.this.authListener.onFail(SnsSSOLoginEngine.this.context, wbConnectErrorMessage.getErrorMessage());
                }

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(this.mSsoHandler != null) {
            this.mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        if(this.mTencent != null) {
            Tencent.onActivityResultData(requestCode, resultCode, data, this.tencentLoginListener);
        }

    }

    class GetUserInforTask implements Runnable {
        String access_token;
        String expires_in;
        String open_id;

        public GetUserInforTask(String access_token, String expires_in, String open_id) {
            this.access_token = access_token;
            this.expires_in = expires_in;
            this.open_id = open_id;
        }

        public  void run() {
            String result = "";
            if(SnsSSOLoginEngine.this.platform != SnsConfig.PLATFORM_QQ_WEIBO &&
                    SnsSSOLoginEngine.this.platform != SnsConfig.PLATFORM_QQ_QZONE) {
                if(SnsSSOLoginEngine.this.platform == SnsConfig.PLATFORM_SINA_WEIBO) {
                    result = (new SinaOauth(SnsSSOLoginEngine.this.context)).doResult(this.access_token, this.expires_in, this.open_id);
                } else if(SnsSSOLoginEngine.this.platform == SnsConfig.PLATFORM_WEIXIN) {
                    result = (new WechatOauth(SnsSSOLoginEngine.this.appContext)).doResult(this.access_token, this.expires_in, this.open_id);
                }
            } else {
                result = (new TencentOauth(SnsSSOLoginEngine.this.context)).doResult(this.access_token, this.expires_in, this.open_id);
            }

            Message msg = new Message();
            msg.obj = result;
            SnsSSOLoginEngine.this.doResultHandler.sendMessage(msg);
        }
    }
}
