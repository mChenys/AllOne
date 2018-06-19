package blog.csdn.net.mchenys.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.HttpUtils;

/**
 * 设计师省份
 * Created by mChenys on 2018/6/11.
 */

public class DesignProvince {
    public int provinceId;
    public String provinceName;
    public boolean isSelected;

    public DesignProvince(){}

    public DesignProvince(JSONObject object) {
        if (null != object) {
            this.provinceId = object.optInt("id");
            this.provinceName = object.optString("name");
        }
    }

    public DesignProvince(int provinceId, String provinceName, boolean isSelected) {
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.isSelected = isSelected;
    }

    public static List<DesignProvince> parseList(JSONArray array) {
        List<DesignProvince> list = null;
        if (null != array && array.length() > 0) {
            list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                list.add(new DesignProvince(array.optJSONObject(i)));
            }
        }
        return list;
    }

    public interface ResultCallback {
        void onResult(List<DesignProvince> list);
    }

    public static void getList(final ResultCallback callback) {
        HttpUtils.getJSON(true, Urls.DESIGN_CITY_LIST, null, null, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(Exception e) {
                if (null != callback) callback.onResult(null);
            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {
                if (null != callback) {
                    callback.onResult(DesignProvince.parseList(jsonObject.optJSONArray("provinces")));
                }
            }


        });
    }

}
