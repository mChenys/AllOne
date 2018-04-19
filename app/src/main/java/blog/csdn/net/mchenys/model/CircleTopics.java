package blog.csdn.net.mchenys.model;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mChenys on 2018/4/18.
 */

public class CircleTopics {
    public String topicId;
    public String title;
    public String desc;
    public int likes;//点赞数
    public Image image;
    public User user;
    public String type; //app ,wap

    public static class Image {
        public String imageUrl;
        public int height;
        public int width;

        public Image(JSONObject object) {
            if (null != object) {
                this.imageUrl = object.optString("imageUrl");
                this.height = object.optInt("height");
                this.width = object.optInt("width");
            }
        }
    }

    public static class User {
        public String nickName;
        public String imageUrl;//头像url
        public String techIconUrl;//头衔url

        public User(JSONObject object) {
            if (null != object) {
                this.imageUrl = object.optString("imageUrl");
                this.nickName = object.optString("nickname");
                this.techIconUrl = object.optString("techIconUrl");
            }
        }
    }

    public static CircleTopics parseBean(JSONObject obj) {
        CircleTopics bean = new CircleTopics();
        if (null != obj) {
            bean.topicId = obj.optString("topicId");
            bean.type = obj.optString("type");
            bean.title = obj.optString("title");
            bean.desc = obj.optString("desc");
            bean.likes = obj.optInt("likes");
            bean.image = new Image(obj.optJSONObject("image"));
            bean.user = new User(obj.optJSONObject("user"));

        }
        return bean;
    }

    public static List<CircleTopics> parseList(JSONArray array) {
        List<CircleTopics> list = new ArrayList<>();
        if (null != array) {
            for (int i = 0; i < array.length(); i++) {
                CircleTopics ct = CircleTopics.parseBean(array.optJSONObject(i));
                if ("wap".equals(ct.type)) {
                    list.add(ct);
                }

            }
        }
        return list;
    }
}
