//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build.VERSION;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AppUtils {

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }


    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService("activity");
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(100);
        Iterator var3 = runningTaskInfos.iterator();

        RunningTaskInfo info;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            info = (RunningTaskInfo) var3.next();
        }
        while (!info.topActivity.getPackageName().equals(context.getPackageName()) || !info.baseActivity.getPackageName().equals(context.getPackageName()));

        return info.topActivity.getClassName();
    }

    public static boolean isRunning(Context context) {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        Iterator var4 = list.iterator();

        while (var4.hasNext()) {
            RunningTaskInfo info = (RunningTaskInfo) var4.next();
            if (info.topActivity.getPackageName().equals(context.getPackageName()) && info.baseActivity.getPackageName().equals(context.getPackageName())) {
                isAppRunning = true;
                break;
            }
        }

        return isAppRunning;
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            Iterator var3 = appProcesses.iterator();

            RunningAppProcessInfo appProcess;
            do {
                if (!var3.hasNext()) {
                    return false;
                }

                appProcess = (RunningAppProcessInfo) var3.next();
            } while (!appProcess.processName.equals(context.getPackageName()));

            return appProcess.importance > 100;
        } else {
            return false;
        }
    }

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
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return apkPackage;
    }

    public static boolean isExistApp(Context context, String apkPackage) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(8192);
        List<String> packageList = new ArrayList();
        Iterator var5 = packs.iterator();

        while (var5.hasNext()) {
            PackageInfo pi = (PackageInfo) var5.next();
            packageList.add(pi.packageName);
        }

        return packageList.contains(apkPackage);
    }

    public static boolean isCurrentAppPackage(Context context, String apkPackage) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String packageName = info.packageName;
            if (apkPackage.equals(packageName)) {
                return true;
            }
        } catch (NameNotFoundException var4) {
            var4.printStackTrace();
        }

        return false;
    }

    public static void startApp(Context context, String apkPackage) {
        PackageManager pm = context.getPackageManager();
        Intent i = pm.getLaunchIntentForPackage(apkPackage);
        if (i != null) {
            context.startActivity(i);
        }

    }

    public static void installApp(Context context, File appFile) {
        Intent intent = new Intent();
        intent.setFlags(335544320);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static boolean isScreenLocked(Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int statusBarHeight = 0;

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return statusBarHeight;
    }

    public static boolean maaIsEnabled(Context context, List<String> maaMarkets) {
        try {
            String channelName = (String) context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.get("MOFANG_CHANNEL");
            if (null != maaMarkets) {
                return maaMarkets.contains(channelName);
            }
        } catch (NameNotFoundException var3) {
            var3.printStackTrace();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return false;
    }

    public static void updataJuInterface(Class<Object> clazz, Map<String, String> configMap) {
        Field[] fields = clazz.getDeclaredFields();
        Field[] var3 = fields;
        int var4 = fields.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            field.setAccessible(true);
            String name = field.getName();
            String low = name.toLowerCase();
            if (configMap.containsKey(low)) {
                try {
                    field.set(field.get(name), configMap.get(low));
                } catch (IllegalArgumentException var10) {
                    var10.printStackTrace();
                } catch (IllegalAccessException var11) {
                    var11.printStackTrace();
                }
            }
        }

    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (NameNotFoundException var2) {
            var2.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException var2) {
            var2.printStackTrace();
            return "";
        }
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService("activity");
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        } else {
            Iterator var4 = runningApps.iterator();

            RunningAppProcessInfo procInfo;
            do {
                if (!var4.hasNext()) {
                    return null;
                }

                procInfo = (RunningAppProcessInfo) var4.next();
            } while (procInfo.pid != pid);

            return procInfo.processName;
        }
    }
}
