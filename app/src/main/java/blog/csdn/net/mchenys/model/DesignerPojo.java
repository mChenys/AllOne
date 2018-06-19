package blog.csdn.net.mchenys.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 设计师数据Pojo实体
 */

public class DesignerPojo {
    public String userId;//设计师ID0
    public String userName;//设计师昵称
    public String userImage;//设计师头像
    public String cityName;//所属城市
    public String price;//价格
    public int type;
    public List<String> image;//4.4改成2个图

    public DesignerPojo(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.userId = jsonObject.optString("userId");
            this.userName = jsonObject.optString("userName");
            this.userImage = jsonObject.optString("userImage");
            this.cityName = jsonObject.optString("cityName");
            this.price = jsonObject.optString("price");
            this.type = jsonObject.optInt("type");
            List<String> images = new ArrayList();
            JSONArray array = jsonObject.optJSONArray("image");
            if (null != array) {
                for (int i = 0; i < array.length(); i++) {
                    images.add(array.optString(i));
                }
            } else {
                images.add(jsonObject.optString("image"));
            }
            this.image = images;


        }
    }

    public static List<DesignerPojo> parseList(JSONArray jsonArray) {
        List<DesignerPojo> mDesignerPojoList = null;
        if (jsonArray != null) {
            mDesignerPojoList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    mDesignerPojoList.add(new DesignerPojo(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return mDesignerPojoList;
    }
}
