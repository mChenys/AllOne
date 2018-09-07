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

public class SinaOauth implements OauthBuilder {
    private String requestUrl = "";
    private String redirectUrl = "";
    private String clientId = "";
    private Context context;
    private int platform = 1;

    public SinaOauth(Context context) {
        this.clientId = SnsConfig.CONSUMER_KEY_SINA;
        this.redirectUrl = SnsConfig.CONSUMER_REDIRECT_URL_SINA;
        this.requestUrl = "https://api.weibo.com/oauth2/authorize?client_id=" + this.clientId + "&response_type=token&redirect_uri=" + this.redirectUrl + "&display=mobile&forcelogin=true&scope=all";
        this.context = context;
    }

    public String doResult(String param1, String param2, String param3) {
        String result = "failed";
        String usermessage = "";
        JSONObject obj = null;
        usermessage = SnsUtils.getUserInfo(this.platform, param1, param3);

        try {
            if(null != usermessage) {
                obj = new JSONObject(usermessage);
            }
        } catch (JSONException var10) {
            var10.printStackTrace();
        }

        if(obj != null && obj.optString("screen_name") != null && !obj.optString("screen_name").equals("")) {
            JSONObject userObj = new JSONObject();

            try {
                userObj.put("access_token", param1);
                userObj.put("openid", param3);
                userObj.put("nickname", obj.optString("screen_name"));
                userObj.put("brief", obj.optString("description"));
                String mGrander = obj.optString("gender");
                String grander;
                if(mGrander != null && mGrander.equals("f")) {
                    grander = "女";
                } else {
                    grander = "男";
                }

                System.out.println("****** grander:" + grander);
                userObj.put("gender", grander);
                userObj.put("icon_50", obj.optString("profile_image_url"));
                userObj.put("icon_180", obj.optString("profile_image_url").replace("/50/", "/180/"));
                userObj.put("expires_in", param2);
                userObj.put("login_time", System.currentTimeMillis());
                result = userObj.toString();
            } catch (Exception var11) {
                result = "failed";
                var11.printStackTrace();
            }

            SharedPreferences sharedPreferences = this.context.getSharedPreferences("platforms", 0);
            Editor editor = sharedPreferences.edit();
            editor.putString("platform1", result);
            editor.commit();
        }

        return result;
    }

}
