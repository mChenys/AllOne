package blog.csdn.net.mchenys.model;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * 焦点图
 */
public class Focus{
    public String id;              //id
    public String image;           //列表图片
    public String pubDate;         //日期
    public String title;           //标题,描述信息
    public String url;             //url
    public String protocol;
    public String type;

    public static Focus parse(JSONObject jsonObject) {
        Focus focus = null;
        if (null != jsonObject) {
            focus = new Focus();
            focus.id=jsonObject.optString("id");
            focus.title=jsonObject.optString("title");
            String publishDate = jsonObject.optString("pubDate");

            //剔除时间后面的00:00:00的时间，保留2012-01-01时间
            if (!TextUtils.isEmpty(publishDate) && publishDate.length() > 9) {
                publishDate = publishDate.substring(0, 10);
            }
            focus.pubDate=publishDate;
            focus.url=jsonObject.optString("url");
            focus.image=jsonObject.optString("image");
            focus.type=jsonObject.optString("type");
            focus.protocol=jsonObject.optString("protocol");
        }
        return focus;
    }

}