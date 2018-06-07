package blog.csdn.net.mchenys.model;

import android.content.Context;

import org.json.JSONObject;

import java.util.List;

import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.HttpUtils;
import blog.csdn.net.mchenys.common.utils.PreferencesUtils;

/**
 * Created by mChenys on 2018/6/7.
 */

public class SubColumn {
    public List<LivingColumnPojo> livingColumn;

    public static class LivingColumnPojo {
        public String id;
        public String name;

        @Override
        public String toString() {
            return "LivingColumnPojo{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    public static void requestData(final Context context) {
        HttpUtils.getJSON(true, Urls.SUB_COLUMN, null, null, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {
                PreferencesUtils.setPreferences(context, Constant.PREFERENCES_SUBCOLUMN, Constant.PREFERENCES_KEY_SUBCOLUMN, okResponse.getResult());
            }
        });

    }
}
