package blog.csdn.net.mchenys;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.okhttp2.x.OkHttpEngine;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;


/**
 * Created by mChenys on 2017/12/26.
 */

public class AllOneApplication extends Application {
    public static Context mAppContext;
    //配置第三方登录信息
    static {
        //weixin
        SnsConfig.CONSUMER_WEIXIN_APPID = "wx581166e7ae2c351f";
        SnsConfig.CONSUMER_WEIXIN_SECRET = "a873fbdef9cf915cf1df2b10a8e91250";
        //qq
        //key RGRBPYz85WQ5BhVB
        SnsConfig.CONSUMER_KEY_TECENT = "1106707167";
        SnsConfig.CONSUMER_REDIRECT_URL_TECENT = "http://blog.csdn.net/mchenys";
        //sina
        //key bb9d95c09288ca0bca5d140bc25896a3
        SnsConfig.CONSUMER_KEY_SINA = "2856820674";
        SnsConfig.CONSUMER_REDIRECT_URL_SINA = "http://blog.csdn.net/mchenys";
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        initFramework();
        initDisplayMetrics();
        initAppVersionInfo();
        initSinaSDK();

    }

    private void initFramework() {
        new OkHttpEngine.Builder(this).build();

    }

    private void initDisplayMetrics() {
        WindowManager wm = (WindowManager) this.getApplicationContext().getSystemService(WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();//获取方向
        DisplayMetrics metrics = this.getApplicationContext().getResources().getDisplayMetrics();
        Env.screenWidth = rotation == 0 ? metrics.widthPixels : metrics.heightPixels;
        Env.screenHeight = rotation == 0 ? metrics.heightPixels : metrics.widthPixels;
        Env.density = metrics.density;
    }

    private void initAppVersionInfo() {
        try {
            PackageInfo e = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_CONFIGURATIONS /*16384*/);
            Env.packageName = e.packageName;
            Env.versionCode = e.versionCode;
            Env.versionName = e.versionName;
        } catch (PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
        }

    }

    private void initSinaSDK() {
        WbSdk.install(this, new AuthInfo(this, SnsConfig.CONSUMER_KEY_SINA, SnsConfig.CONSUMER_REDIRECT_URL_SINA,
                "email,direct_messages_read,direct_messages_write,"
                        + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                        + "follow_app_official_microblog," + "invitation_write"));

    }


}
