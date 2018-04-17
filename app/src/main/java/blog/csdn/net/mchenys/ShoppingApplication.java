package blog.csdn.net.mchenys;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.okhttp2.x.OkHttpEngine;


/**
 * Created by mChenys on 2017/12/26.
 */

public class ShoppingApplication extends Application {
    public static Context mAppContext;

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
        ImageLoader.init(this);
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
        WbSdk.install(this, new AuthInfo(this, MFSnsConfig.CONSUMER_KEY_SINA, MFSnsConfig.CONSUMER_REDIRECT_URL_SINA,
                "email,direct_messages_read,direct_messages_write,"
                        + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                        + "follow_app_official_microblog," + "invitation_write"));
    }


}
