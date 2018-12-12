package blog.csdn.net.mchenys.module.demo.test;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据
 * Created by mChenys on 2018/11/1.
 */

public class ChartModel {
    public Long priceDate;
    public String price;

    public ChartModel(JSONObject object) {
        if (null != object) {
            this.priceDate = object.optLong("priceDate");
            this.price = object.optString("price");
        }
    }

    public static List<ChartModel> parseList(JSONArray array) {
        List<ChartModel> list = new ArrayList<>();
        if (null != array) {
            for (int i = 0; i < array.length(); i++) {
                list.add(new ChartModel(array.optJSONObject(i)));
            }
        }
        return list;
    }
}
