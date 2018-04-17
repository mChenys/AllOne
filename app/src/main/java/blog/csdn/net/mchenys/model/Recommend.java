package blog.csdn.net.mchenys.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mChenys on 2018/1/2.
 */

public class Recommend {
    //以下指定仅做测试
    public int courseTotal;
    public int followTotal;
    public String techDesc;
    public String techHeadUrl;
    public String techIconUrl;
    public String techJobName;
    public String techNickName;

    public static Recommend parseBean(JSONObject object) {
        Recommend bean = new Recommend();
        if (null != object) {
            bean.courseTotal = object.optInt("courseTotal");
            bean.followTotal = object.optInt("followTotal");
            bean.techDesc = object.optString("techDesc");
            bean.techHeadUrl = object.optString("techHeadUrl");
            bean.techIconUrl = object.optString("techIconUrl");
            bean.techJobName = object.optString("techJobName");
            bean.techNickName = object.optString("techNickName");
        }
        return bean;
    }

    public static List<Recommend> parseList(JSONArray array) {
        List<Recommend> list = new ArrayList<>();
        if (null != array) {
            for(int i=0;i<array.length();i++) {
                list.add(parseBean(array.optJSONObject(i)));
            }
        }
        return list;
    }
}
