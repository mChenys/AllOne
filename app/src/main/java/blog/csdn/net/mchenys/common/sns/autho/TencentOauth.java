//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns.autho;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

import blog.csdn.net.mchenys.common.sns.config.SnsConfig;
import blog.csdn.net.mchenys.common.sns.SnsUtils;


public class TencentOauth implements OauthBuilder {
    private String requestUrl = "";
    private String redirectUrl = "";
    private String clientId = "";
    private Context context;

    public TencentOauth(Context context) {
        this.context = context;
        this.clientId = SnsConfig.CONSUMER_KEY_TECENT;
        this.redirectUrl = SnsConfig.CONSUMER_REDIRECT_URL_TECENT;
        this.requestUrl = "https://graph.z.qq.com/moc2/authorize?client_id=" + this.clientId + "&response_type=token&redirect_uri=" + this.redirectUrl + "&display=mobile&forcelogin=true&scope=all";
    }

    public String doResult(String param1, String param2, String param3) {
        String result = "failed";
        String QQResult = "";
        JSONObject obj = null;
        String openId = SnsUtils.getUserOpenId(param1);
        String usermessage = "";
        usermessage = SnsUtils.getUserInfo(3, param1, openId);
        if(usermessage != null && !usermessage.equals("")) {
            try {
                obj = new JSONObject(usermessage);
            } catch (JSONException var14) {
                var14.printStackTrace();
            }
        }

        if(obj != null) {
            JSONObject userObj = new JSONObject();

            try {
                userObj.put("access_token", param1);
                userObj.put("openid", openId);
                userObj.put("nickname", obj.optString("nickname"));
                userObj.put("brief", "");
                userObj.put("gender", obj.optString("gender"));
                userObj.put("icon_50", obj.optString("figureurl_qq_1"));
                userObj.put("icon_180", obj.optString("figureurl_qq_2"));
                userObj.put("expires_in", param2);
                userObj.put("login_time", System.currentTimeMillis());
                QQResult = userObj.toString();
            } catch (Exception var13) {
                QQResult = "";
                var13.printStackTrace();
            }
        }

        if(!QQResult.equals("")) {
            result = "success";
            SharedPreferences sharedPreferences = this.context.getSharedPreferences("platforms", 0);
            Editor editor = sharedPreferences.edit();
            editor.putString("platform2", QQResult);
            editor.commit();
            SharedPreferences sharedPreferencesQQ = this.context.getSharedPreferences("platforms", 0);
            Editor editorQQ = sharedPreferencesQQ.edit();
            editorQQ.putString("platform3", QQResult);
            editorQQ.commit();
        }

        return result;
    }

}
