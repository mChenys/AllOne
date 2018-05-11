package blog.csdn.net.mchenys.common.utils;

import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 解析url后面的参数，将key和value分别放置到bundle中的key和value
 */

public class ParseUrlUtils {

    //pchousebrowser://reply?topicId=13191196&topicUrl=http://tuliao.pchouse.com.cn/111/1112159.html&name=广东省广州市网友&replyFloor=6
    public static Bundle getUrlParamsBundle(String url) {
        Bundle params = new Bundle();
        try {
            if (!StringUtils.isEmpty(url)) {
                if (url.contains("?")) {
                    params.putString("protocol", url.substring(0, url.indexOf("?")));
                    url = url.substring(url.indexOf("?") + 1);//截取?号后面的子串
                    String array[] = url.split("&");
                    for (String parameter : array) {
                        String v[] = parameter.split("=");
                        if (v.length > 1) {
                            String value = v[1]; //值
                            try {
                                value = URLDecoder.decode(v[1], "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            params.putString(v[0], value);//key-value
                        }
                    }
                } else {
                    params.putString("protocol", url);
                }
            }
        } catch (Exception e) {
        }
        return params;
    }
}
