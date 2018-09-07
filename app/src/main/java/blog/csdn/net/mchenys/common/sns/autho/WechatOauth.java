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

import blog.csdn.net.mchenys.common.sns.SnsUtils;


public class WechatOauth implements OauthBuilder {
    private Context context;

    public WechatOauth(Context context) {
        this.context = context;
    }


    public String doResult(String access_token, String expire_in, String open_id) {
        String result = "failed";
        String usermessage = "";
        JSONObject obj = null;
        usermessage = SnsUtils.getUserInfo(5, access_token, open_id);

        try {
            if(null != usermessage) {
                obj = new JSONObject(usermessage);
            }
        } catch (JSONException var12) {
            var12.printStackTrace();
        }

        if(obj != null) {
            result = "success";
            JSONObject userObj = new JSONObject();

            try {
                userObj.put("access_token", access_token);
                userObj.put("openid", open_id);
                userObj.put("nickname", obj.optString("nickname"));
                userObj.put("brief", "");
                userObj.put("gender", obj.optInt("sex") == 1?"男":"女");
                String headUrl = obj.optString("headimgurl");
                String[] icon = new String[]{"", ""};
                if(headUrl != null && !headUrl.equals("")) {
                    icon = new String[2];
                    String iconPre = headUrl.substring(0, headUrl.lastIndexOf("/"));
                    icon[0] = iconPre + "/" + 46;
                    icon[1] = iconPre + "/" + 132;
                }

                userObj.put("icon_50", icon[0]);
                userObj.put("icon_180", icon[1]);
                userObj.put("expires_in", expire_in);
                userObj.put("login_time", System.currentTimeMillis());
                userObj.put("unionid", obj.optString("unionid"));
                result = userObj.toString();
            } catch (Exception var11) {
                result = "failed";
                var11.printStackTrace();
            }

            if(this.context != null) {
                SharedPreferences sharedPreferences = this.context.getSharedPreferences("platforms", 0);
                Editor editor = sharedPreferences.edit();
                editor.putString("platform5", result);
                editor.commit();
            } else {
                result = "failed";
            }
        }

        return result;
    }
}
