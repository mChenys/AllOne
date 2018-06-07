package blog.csdn.net.mchenys.common.config;

/**
 * 接口配置
 * Created by mChenys on 2017/12/26.
 */
public class Urls {


    public static int URL_TYPE = Env.RELEASE;

    public static String COMMON_SESSION_ID = URL_TYPE != Env.RELEASE ? "common_session_id1=" : "common_session_id=";

    public static final String GET_PHOTO_CAPTCHA = getTargetUrl("https://passport3.pconline.com.cn/passport3/api/sendVerificationCode.jsp");

    public static final String CONFIRM_PHONE_NUM = getTargetUrl("https://passport3.pconline.com.cn/passport3/api/validate_mobile.jsp");

    public static final String CAPTCHA_URL = getTargetUrl("http://captcha.pcauto.com.cn/captcha/captcha_flush.jsp");

    public static final String CAPTCHA_THUMB = getTargetUrl("http://captcha.pcauto.com.cn/captcha/captchaThumb");

    public static final String CAPTCHA_IMG = getTargetUrl("http://captcha.pcauto.com.cn/captcha/captchaImg");

    public static final String CAPTCHA_CHECK = getTargetUrl("http://captcha.pcauto.com.cn/captcha/captcha_check.jsp");

    public static final String MOBILE_BIND = getTargetUrl("https://passport3.pconline.com.cn/passport3/api/mobile_bind2.jsp");

    public static final String MOBILE_OVER_BIND = getTargetUrl("https://passport3.pconline.com.cn/passport3/api/mobile_over_bind.jsp");
    //绑定第三方账号
    public static final String CHECK_BIND = getTargetUrl("https://passport3.pclady.com.cn/passport3/api/login_xauth.jsp");
    //获取用户信息
    public static final String GET_USER_INFO_URL = getTargetUrl("https://csc.pconline.com.cn/s/buy/user/getUserInfo.xsp");
    //快速绑定第三方账号
    public static final String QUICK_BIND_URL = "https://passport3.pclady.com.cn/passport3/api/registerOpen4App.jsp";

    public static final String TEST = getTargetUrl("https://mrobot.pclady.com.cn/modern/x/modern.pclady.com.cn/app/teacher/teacherList.do");
    public static final String CIRCLE_HOME = getTargetUrl("https://mrobot.pclady.com.cn/modern/s/bbs/circleHome2.xsp");
    public static final String WAP_TOPIC_TERMINAL = getTargetUrl("https://mrobot.pclady.com.cn/modern/s/bbs/topicDetail2.xsp") ;
    public static String FREE_LOGIN_URL =getTargetUrl("https://passport3.pconline.com.cn/passport3/passport/mobile_login.jsp");
    public static final String ACCOUNT_LOGIN = getTargetUrl("https://passport3.pconline.com.cn/passport3/rest/login.jsp");
    public static final String PHONE_BIND2 = getTargetUrl("https://passport3.pconline.com.cn/passport3/api/mobile_bind2.jsp");

    public static final String HOME_COLUMN_LIST = "https://mrobot.pchouse.com.cn/s/magazine/pchouse/cms/houseArticleList.xsp";
    public static final String SUB_COLUMN = "https://mrobot.pchouse.com.cn/s/magazine/pchouse/cms/preloadChannels.xsp" ;
    public static final String URL_ARTICLE = "https://mrobot.pchouse.com.cn/v3/cms/articles/";


    public static String getTargetUrl(String url) {
        if (URL_TYPE == Env.TEST) {
            if (url.contains("https://passport3.pconline.com.cn/passport3")) {
                url = url.replace("https://passport3.pconline.com.cn/passport3", "https://v46.pconline.com.cn/passport3");
            } else if (url.contains("http://captcha.pcauto.com.cn/")) {
                url = url.replace("http://captcha.pcauto.com.cn/", "https://v84.pconline.com.cn/");
            } else if (url.contains("http://mrobot.pconline.com.cn/")) {
                url = url.replace("http://mrobot.pconline.com.cn/", "https://v80.pconline.com.cn/");
            } else if (url.contains("https://mrobot.pconline.com.cn/")) {
                url = url.replace("https://mrobot.pconline.com.cn/", "https://v80.pconline.com.cn/");
            } else if (url.contains("https://live.pcvideo.com.cn/")) {
                url = url.replace("https://live.pcvideo.com.cn/", "https://v67.pconline.com.cn/live/");
            }
        } else if (URL_TYPE == Env.DEVELOP) {
            if (url.contains("https://passport3.pcauto.com.cn/passport3")) {
                url = url.replace("https://passport3.pcauto.com.cn/passport3", "https://dev30.pconline.com.cn/passport3");
            } else if (url.contains("http://captcha.pcauto.com.cn/")) {
                url = url.replace("http://captcha.pcauto.com.cn/", "http://v46.pcauto.com.cn:81/");
            } else if (url.contains("http://mrobot.pconline.com.cn/")) {
                url = url.replace("http://mrobot.pconline.com.cn/", "https://v40.pconline.com.cn/");
            } else if (url.contains("https://mrobot.pconline.com.cn/")) {
                url = url.replace("https://mrobot.pconline.com.cn/", "https://v40.pconline.com.cn/");
            } else if (url.contains("https://live.pcvideo.com.cn/")) {
                url = url.replace("https://live.pcvideo.com.cn/", "http://dev193.pconline.com.cn:8099/live/");
            }
        }
        return url;
    }
}
