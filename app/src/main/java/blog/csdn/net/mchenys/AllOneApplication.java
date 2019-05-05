package blog.csdn.net.mchenys;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.support.multidex.MultiDexApplication;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

import java.lang.reflect.Field;

import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.okhttp2.x.HttpManager;
import blog.csdn.net.mchenys.common.sns.config.SnsConfig;


/**
 * Created by mChenys on 2017/12/26.
 */

public class AllOneApplication extends MultiDexApplication {
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

    //设置全局的smartrefreshlayout刷新头和脚
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
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
        new HttpManager.Builder(this).build();

    }

    private void initDisplayMetrics() {
        WindowManager wm = (WindowManager) this.getApplicationContext().getSystemService(WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();//获取方向
        DisplayMetrics metrics = this.getApplicationContext().getResources().getDisplayMetrics();
        Env.screenWidth = rotation == 0 ? metrics.widthPixels : metrics.heightPixels;
        Env.screenHeight = rotation == 0 ? metrics.heightPixels : metrics.widthPixels;
        Env.density = metrics.density;
        Env.statusBarHeight = getStatusBarHeight(mAppContext);
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

    /**
     * 获取状态栏高度/像素
     *
     * @return
     */
    private static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
