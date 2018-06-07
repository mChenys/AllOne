package blog.csdn.net.mchenys.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 子栏目数据
 */

public class SubColumnData {
    public String id;
    public String title;
    public String type;
    public String imageUrl;
    public String userId;
    public String userName;
    public String userImage;
    public int isDesigner;
    public int isTalent;
    public String talentsName;

    public static SubColumnData parsePojo(JSONObject jsonObject){
        SubColumnData subColumnData = null;
        if(jsonObject != null){
            subColumnData = new SubColumnData();
            subColumnData.id = jsonObject.optString("id");
            subColumnData.title = jsonObject.optString("title");
            subColumnData.type = jsonObject.optString("type");
            subColumnData.imageUrl = jsonObject.optString("imageUrl");
            subColumnData.userId = jsonObject.optString("userId");
            subColumnData.userName = jsonObject.optString("userName");
            subColumnData.userImage = jsonObject.optString("userImage");
            subColumnData.isDesigner = jsonObject.optInt("isDesigner");
            subColumnData.isTalent = jsonObject.optInt("isTalent");
            subColumnData.talentsName = jsonObject.optString("talentsName");
        }
        return subColumnData;
    }

    public static List<SubColumnData> parseList(JSONArray jsonArray) throws JSONException {
        List<SubColumnData> subColumnDataList = null;
        if(jsonArray != null){
            subColumnDataList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                subColumnDataList.add(parsePojo(jsonArray.getJSONObject(i)));
            }
        }
        return subColumnDataList;
    }
}
