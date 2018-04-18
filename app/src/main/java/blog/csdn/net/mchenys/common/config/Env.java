package blog.csdn.net.mchenys.common.config;


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


}
