package blog.csdn.net.mchenys.common.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Protocols;
import blog.csdn.net.mchenys.module.terminal.LiveColumnTerminalActivity;
import blog.csdn.net.mchenys.module.terminal.PageTerminalActivity;
import blog.csdn.net.mchenys.module.terminal.PersonalPageTerminalActivity;
import blog.csdn.net.mchenys.module.terminal.WapTopicsTerminalActivity;


/**
 * 推送协议跳转处理
 */
public class URIUtils {
    private final static String TAG = "URIUtils";
    private static Map<String, URIInfo> uriMap = new HashMap<>();
    /**
     * 推送协议格式:
     * "type":"2","content":"居住专栏终端","url":"allonebrowser://live_detail/123123"
     * "type":"2","content":"分页详情页","url":"allonebrowser://page_detail/123123"
     * "type":"2","content":"个人主页终端","url":"allonebrowser://personal_detail/12312 3"
     * "type":"2","content":"话题终端","url":"allonebrowser://topic_detail/123123"
     */
    //协议名称

    public static final String ARTICLE_URL = "http";    //文章终端页
    public static final String ARTICLE_URLS = "https";    //文章终端页

    static {
        uriMap.put(Protocols.LIVE_TERMINAL, new URIInfo("居住专栏终端", LiveColumnTerminalActivity.class));
        uriMap.put(Protocols.PAGE_TERMINAL, new URIInfo("分页详情页", PageTerminalActivity.class));
        uriMap.put(Protocols.PERSONAL_TERMINAL, new URIInfo("个人主页终端", PersonalPageTerminalActivity.class));
        uriMap.put(Protocols.TOPIC_TERMINAL, new URIInfo("话题终端", WapTopicsTerminalActivity.class));
    }

    /**
     * 获取协议,有两种,一种带'/',一种不带'/',不带'/'的协议要么是列表,要么是通过?带参数的
     *
     * @param uri
     * @return 例如:allonebrowser://topic_detail/
     * allonebrowser://tryout_form
     * allonebrowser://infor_topic
     */
    private static String parseURIKey(String uri) {
        if (null != uri || !"".equals(uri)) {
            if (uri.startsWith(ARTICLE_URL)) {  //如果为文章终端页则直接返回
                return ARTICLE_URL;
            } else if (uri.startsWith(ARTICLE_URLS)) {
                return ARTICLE_URLS;
            }
            if (uri.contains("?")) {//例如:allonebrowser://infor_topic?topicUrl={{H5链接(wap)}
                return uri.substring(0, uri.lastIndexOf("?"));
            } else if (uri.lastIndexOf("/") == 16) {//例如:allonebrowser://tryout_form
                return uri;
            } else if (uri.lastIndexOf("/") > 16) { //例如:allonebrowser://topic_detail/id
                return uri.substring(0, uri.lastIndexOf("/") + 1);
            }
        }
        return "";
    }

    public static boolean hasURI(String uri) {
        if (uriMap != null) {
            return uriMap.containsKey(parseURIKey(uri));
        }
        return false;
    }



    public static Intent getIntent(String uri, Context context) {
        Intent intent = new Intent();
        intent.setClass(context, uriMap.get(parseURIKey(uri)).getTargetActivity());
        Bundle b = getBundle(uri);
        intent.putExtras(b);
        return intent;
    }

    private static Bundle getBundle(String uri) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.KEY_PUSH, true);
        try {
            if (uri.startsWith(ARTICLE_URL) || uri.startsWith(ARTICLE_URLS)) {   //文章终端页
                bundle.putString("url", uri);
                return bundle;
            }
            //居住专栏终端
            if (uri.startsWith(Protocols.LIVE_TERMINAL)) {
                String id = uri.replace(Protocols.LIVE_TERMINAL , "");
                bundle.putString("columnId", id);
            }
            //个人主页终端
            if (uri.startsWith(Protocols.PERSONAL_TERMINAL)) {
                String id = uri.replace(Protocols.PERSONAL_TERMINAL , "");
                bundle.putString("userId", id);
            }
            //话题终端
            if (uri.startsWith(Protocols.TOPIC_TERMINAL)) {
                String id = uri.replace(Protocols.TOPIC_TERMINAL , "");
                bundle.putString("topicId", id);
            }

        } catch (Exception e) {
        }
        return bundle;
    }

    public static class URIInfo {
        private String name;
        private Class targetActivity;

        public URIInfo(String name, Class targetActivity) {
            this.name = name;
            this.targetActivity = targetActivity;
        }

        public String getName() {
            return name;
        }

        public Class getTargetActivity() {
            return targetActivity;
        }
    }


    /**
     * 从uri中获取APK包名
     *
     * @param uri
     * @return
     */
    public static String getApkPackage(String uri) {
        String apkPackage = "";
        try {
            if (uri.indexOf("&") == -1) {
                apkPackage = uri.substring(uri.indexOf("?"));
                apkPackage = apkPackage.substring(apkPackage.indexOf("=") + 1);
            } else {
                apkPackage = uri.substring(uri.indexOf("?"), uri.indexOf("&"));
                apkPackage = apkPackage.substring(apkPackage.indexOf("=") + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apkPackage;
    }

    /**
     * 获取 url里面参数键值对
     *
     * @param schemas
     * @param url
     * @return
     */
    public static Map<String, String> getParams(String schemas, String url) {
        Map<String, String> map = new HashMap<String, String>();
        if (!url.contains(schemas))
            return map;
        String paramStr = url.substring(url.indexOf("?") + 1);
        String[] paramArray = paramStr.split("&");
        if (paramArray != null) {
            String param = "";
            String key = "";
            String value = "";
            for (int i = 0; i < paramArray.length; i++) {
                param = paramArray[i];
                key = param.substring(0, param.indexOf("="));
                value = param.substring(param.indexOf("=") + 1);
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 获取 url里面参数键值对
     *
     * @param url
     * @return
     */
    public static Map<String, String> getParams(String url) {
        Map<String, String> map = new HashMap<String, String>();

        String paramStr = url.substring(url.indexOf("?") + 1);

        String[] paramArray = paramStr.split("&");

        if (paramArray != null) {
            String param = "";
            String key = "";
            String value = "";

            for (int i = 0; i < paramArray.length; i++) {
                param = paramArray[i];
                key = param.substring(0, param.indexOf("="));
                value = param.substring(param.indexOf("=") + 1);
                map.put(key, value);
            }
        }
        return map;
    }
    /**
     * 根据协议跳转页面
     * @param activity
     * @param protocol
     * @return
     */
    public static boolean dispatch(Context activity, String protocol) {
        switch (protocol) {

        }
        return true;
    }
}
