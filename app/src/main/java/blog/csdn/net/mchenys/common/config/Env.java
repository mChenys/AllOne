package blog.csdn.net.mchenys.common.config;

import com.imofan.android.develop.sns.MFSnsConfig;

/**
 * 全局配置
 * Created by mChenys on 2017/12/26.
 */

public class Env {
    public static final int RELEASE = 1;    //线上环境
    public static final int DEVELOP = 2;    //开发环境
    public static final int TEST = 3;    //测试环境
    public static String versionName;
    public static int versionCode;
    public static String packageName;
    public static int screenWidth;
    public static int screenHeight;
    public static float density;
    public static String userApp = "PCONLINE_SHOP_ANDR";

    //配置第三方登录信息
    static {
        //weixin
        MFSnsConfig.CONSUMER_WEIXIN_APPID = "wx581166e7ae2c351f";
        MFSnsConfig.CONSUMER_WEIXIN_SECRET = "9b57d7a8ddbd56645dda5095538e8ab3";
        //qq
        //key RGRBPYz85WQ5BhVB
        MFSnsConfig.CONSUMER_KEY_TECENT = "1105253527";
        MFSnsConfig.CONSUMER_REDIRECT_URL_TECENT = "http://app.pconline.com.cn";
        //sina
        //key bb9d95c09288ca0bca5d140bc25896a3
        MFSnsConfig.CONSUMER_KEY_SINA = "3278750343";
        MFSnsConfig.CONSUMER_REDIRECT_URL_SINA = "http://www.pclady.com.cn";
    }
}
