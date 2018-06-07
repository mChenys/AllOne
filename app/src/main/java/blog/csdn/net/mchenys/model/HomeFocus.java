package blog.csdn.net.mchenys.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mChenys on 2018/6/4.
 */

public class HomeFocus {
    public String image;
    public String toUrl;

    public HomeFocus(String image, String toUrl) {
        this.image = image;
        this.toUrl = toUrl;
    }
    public HomeFocus(JSONObject object) {
        if (null != object) {
            this.image = object.optString("image");
            this.toUrl = object.optString("toUrl");
        }
    }

    public static List<HomeFocus> parseList(JSONArray array) {
        List<HomeFocus> list = null;
        if (null != array && array.length()>0) {
            list = new ArrayList<>();
            for(int i=0;i<array.length();i++) {
                list.add(new HomeFocus(array.optJSONObject(i)));
            }
        }
        return list;
    }

    public static List<HomeFocus> getTest(){
        String json = "{\"focusInfo\": [{\"image\": \"http://img0.pchouse.com.cn/pchouse/app/iPhone_home/focus/1806/66_1.jpg\"}, {\"image\": \"http://img0.pchouse.com.cn/pchouse/app/topic/1805/topic0529.jpg\"}, {\"image\": \"http://img0.pchouse.com.cn/pchouse/app/gonglue/changdiding/1806/banner.jpg\"}, {\"image\": \"http://img0.pchouse.com.cn/pchouse/app/iPhone_home/focus/1806/750X380.jpg\"}, {\"image\": \"http://img0.pchouse.com.cn/pchouse/app/iPhone_home/focus/1806/750x380---.jpg\"}]}";
        try {
            JSONObject object = new JSONObject(json);
            return parseList(object.optJSONArray("focusInfo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
