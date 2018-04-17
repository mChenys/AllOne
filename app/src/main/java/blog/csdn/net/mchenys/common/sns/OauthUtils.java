//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONException;
import org.json.JSONObject;

import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.sns.bean.SnsUser;
import blog.csdn.net.mchenys.common.sns.callback.SnsShareListener;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;


public class OauthUtils {

    private static String TAG = "OauthUtils";

    private static WbShareHandler shareHandler;

    public OauthUtils() {
    }

    public static String getUserInfo(int platform, String access_token, String openid) {
        String url = null;
        String commonUrl = null;
        String queryString = null;
        switch (platform) {
            case SnsConfig.PLATFORM_SINA_WEIBO:
                url = "https://api.weibo.com/2/users/show.json";
                commonUrl = "access_token=" + access_token + "&uid=" + openid;
                queryString = "";
                break;
            case SnsConfig.PLATFORM_QQ_WEIBO:
                url = "https://graph.qq.com/user/get_user_info";
                commonUrl = "&oauth_consumer_key=" + SnsConfig.CONSUMER_KEY_TECENT + "&access_token=" + access_token + "&openid=" + openid;
                queryString = "format=json";
                break;
            case SnsConfig.PLATFORM_QQ_QZONE:
                url = "https://graph.qq.com/user/get_user_info";
                commonUrl = "&oauth_consumer_key=" + SnsConfig.CONSUMER_KEY_TECENT + "&access_token=" + access_token + "&openid=" + openid;
                queryString = "format=json";
                break;
            case SnsConfig.PLATFORM_WEIXIN:
                url = "https://api.weixin.qq.com/sns/userinfo";
                commonUrl = "access_token=" + access_token + "&openid=" + openid;
                queryString = "";
        }

        String result = "";

        try {
            result = SnsHttpUtils.httpGet(url, queryString + commonUrl);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return result;
    }



    /**
     * 新浪分享
     *
     * @param context
     * @param contentMessage
     * @return
     */
    public static String uploadToSinaAllInOne(Context context, SnsShareContent contentMessage) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        String hideMessage = null == contentMessage.getHideContent() ? "" : contentMessage.getHideContent().toString();
        String tittle = null == contentMessage.getTitle() ? "" : contentMessage.getTitle().toString();
        String tittleUrl = null == contentMessage.getUrl() ? "" : contentMessage.getUrl().toString();
        TextObject textObject = new TextObject();
        textObject.text = contentMessage.getContent() + hideMessage;
        textObject.title = tittle;
        textObject.actionUrl = tittleUrl;
        weiboMessage.textObject = textObject;
        String cache = SnsImageShareUtil.getCachUrl(context);//获取图片缓存路径
        ImageObject imageObject;
        Bitmap bitmap;
        if (contentMessage.getShareImgFile() != null) {
            imageObject = new ImageObject();
            bitmap = BitmapFactory.decodeFile(contentMessage.getShareImgFile().getAbsolutePath());
            imageObject.setImageObject(bitmap);
            weiboMessage.imageObject = imageObject;
        } else if (!TextUtils.isEmpty(cache)) {
            imageObject = new ImageObject();
            bitmap = BitmapFactory.decodeFile(cache);
            imageObject.setImageObject(bitmap);
            weiboMessage.imageObject = imageObject;
        }

        shareHandler = new WbShareHandler((Activity) context);
        shareHandler.registerApp();
        shareHandler.shareMessage(weiboMessage, false);
        return null;
    }


    public static void doResultIntent(final Activity context, Intent intent, final SnsShareListener shareListener) {
        shareHandler = new WbShareHandler(context);
        shareHandler.registerApp();
        shareHandler.doResultIntent(intent, new WbShareCallback() {
            public void onWbShareSuccess() {
                if (shareListener != null) {
                    shareListener.onSucceeded(context);
                    shareListener.onSinaSucceeded(context);
                }

            }

            public void onWbShareCancel() {
                if (shareListener != null) {
                    shareListener.onFailed(context, "cancel");
                }

            }

            public void onWbShareFail() {
                if (shareListener != null) {
                    shareListener.onFailed(context, "cancel");
                }

            }
        });
    }



    public static String getUserOpenId(String access_token) {
        String result = "";
        String openid = "";

        try {
            result = SnsHttpUtils.httpGet("https://graph.qq.com/oauth2.0/me", "access_token=" + access_token);
            int start = result.indexOf("{");
            int end = result.indexOf("}");
            result = result.substring(start, end + 1);
            JSONObject obj = new JSONObject(result);
            openid = obj.optString("openid");
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return openid;
    }



    /**
     * 获取上一次的登录信息判断是否过期
     *
     * @param context
     * @param platform
     * @return
     */
    public static boolean isAuthorized(Context context, int platform) {
        SharedPreferences platforms = context.getSharedPreferences("platforms", 0);
        String message = platforms.getString("platform" + platform, "");
        if (message.equals("")) {
            return false;
        } else {
            SnsUser user = parseUser(message);
            long expires_in = user.getExpire();
            long loginTime = user.getLoginTime();
            long overTime = System.currentTimeMillis() - loginTime;
            return overTime <= (expires_in - 1800L) * 1000L;
        }
    }

    public static SnsUser getOpenUser(Context context, int platform) {
        SharedPreferences platforms = context.getSharedPreferences("platforms", 0);
        String message = platforms.getString("platform" + platform, "");
        SnsUser user = parseUser(message);
        return user;
    }

    public static boolean loginOut(Context context, int platform) {
        SharedPreferences platforms = context.getSharedPreferences("platforms", 0);
        Editor editor = platforms.edit();
        editor.putString("platform" + platform, "");
        editor.commit();
        return true;
    }

    private static SnsUser parseUser(String message) {
        if (null != message && !"".equals(message)) {
            SnsUser user = null;

            try {
                JSONObject obj = new JSONObject(message);
                user = new SnsUser();
                user.setAccessToken(obj.optString("access_token"));
                user.setOpenId(obj.optString("openid"));
                user.setBrief(obj.optString("brief"));
                user.setGender(obj.optString("gender"));
                user.setNickname(obj.optString("nickname"));
                user.setExpire(obj.optLong("expires_in"));
                String[] icons = new String[]{obj.optString("icon_50"), obj.optString("icon_180")};
                user.setIcons(icons);
                user.setLoginTime(obj.optLong("login_time"));
                if (obj.has("unionid")) {
                    user.setUnionid(obj.optString("unionid"));
                }
            } catch (JSONException var4) {
                var4.printStackTrace();
            }

            return user;
        } else {
            return null;
        }
    }



}
