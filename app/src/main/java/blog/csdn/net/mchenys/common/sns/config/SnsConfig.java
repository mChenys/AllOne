//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns.config;

public class SnsConfig {

    //登录平台标识
    public static final int PLATFORM_SINA_WEIBO = 1;
    public static final int PLATFORM_QQ_WEIBO = 2;
    public static final int PLATFORM_QQ_QZONE = 3;
    public static final int PLATFORM_WEIXIN = 4;

    //分享平台标识
    public final static int SHARE_WECHAT = 1;//微信好友
    public final static int SHARE_WECHAT_FRIEND = 2;//微信朋友圈
    public final static int SHARE_TENCENT = 3;//qq好友
    public final static int SHARE_TENCENT_ZONE = 4;//qq空间
    public final static int SHARE_SINA = 5;//新浪微博
    public final static int SHARE_TENCENT_BLOG = 6;//qq微博

    public static String CONSUMER_KEY_SINA = "";
    public static String CONSUMER_REDIRECT_URL_SINA = "";
    public static String CONSUMER_KEY_TECENT = "";
    public static String CONSUMER_REDIRECT_URL_TECENT = "";
    public static String CONSUMER_WEIXIN_APPID;
    public static String CONSUMER_WEIXIN_SECRET;
    public static String AUTHORIZE_WEIXIN_Token = "https://api.weixin.qq.com/sns/oauth2/access_token";


}
