package blog.csdn.net.mchenys.common.config;

/**
 * 接口配置
 * Created by mChenys on 2017/12/26.
 */
public class Urls {

    public static int URL_TYPE = Env.TEST;

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
    public static final String GET_USER_INFO_URL = "https://mrobot.pclady.com.cn/modern/x/modern.pclady.com.cn/app/account/getUserInfo.do";
    //快速绑定第三方账号
    public static final String QUICK_BIND_URL = "https://passport3.pclady.com.cn/passport3/api/registerOpen4App.jsp";
    //测试RecycleView
    public static final String TEST = "https://mrobot.pclady.com.cn/modern/x/modern.pclady.com.cn/app/teacher/teacherList.do";


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
