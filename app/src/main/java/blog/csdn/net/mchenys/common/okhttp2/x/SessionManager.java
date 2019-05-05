package blog.csdn.net.mchenys.common.okhttp2.x;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SessionManager {
    protected static boolean isSessionEnabled = false;
    private static long sessionTimeout = 30000L;
    private static Context context;
    private String session;
    private long onStopTime;
    private long onResumeTime;
    private boolean isBackground = true;
    private static SessionManager sessionManager;
    private static ArrayList<OnAppRunInBackgroundListener> onAppRunInBackgroundListenerList;

    public SessionManager() {
    }

    public static void init(Context ctx) {
        context = ctx;
        sessionManager = new SessionManager();
        onAppRunInBackgroundListenerList = new ArrayList();
    }

    public static SessionManager getInstance() {
        return sessionManager;
    }

    public static void setSessionTimeout(long timeout) {
        sessionTimeout = timeout;
    }

    public static void setIsSessionEnabled(boolean enabled) {
        isSessionEnabled = enabled;
    }

    public void onActivityResume() {
        if(this.isBackground) {
            this.isBackground = false;
            this.onAppStateChange(false);
            this.onResumeTime = System.currentTimeMillis();
            if(this.onResumeTime - this.onStopTime > sessionTimeout) {
                this.session = this.createSeesion();
                if(HttpManager.getInstance() != null) {
                    HttpManager.getInstance().clearUrls();
                }
            }
        }

    }

    public void onActivityStop() {
        if(!this.isBackground && isBackground(context)) {
            this.isBackground = true;
            this.onAppStateChange(true);
            this.onStopTime = System.currentTimeMillis();
        }

    }

    private String createSeesion() {
        String result = String.valueOf(System.currentTimeMillis());
        return result;
    }

    public String getSession() {
        return this.session;
    }

    public long getOnStopTime() {
        return this.onStopTime;
    }

    public long getOnResumeTime() {
        return this.onResumeTime;
    }

    public String toString() {
        return "SessionManager [session=" + this.session + ", onStopTime=" + this.onStopTime + ", onResumeTime=" + this.onResumeTime + "]";
    }

    public static void addOnAppRunInBackgroundListenerList(OnAppRunInBackgroundListener listener) {
        if(onAppRunInBackgroundListenerList != null && !onAppRunInBackgroundListenerList.contains(listener)) {
            onAppRunInBackgroundListenerList.add(listener);
        }

    }

    public static void removeOnAppRunInBackgroundListenerList(OnAppRunInBackgroundListener listener) {
        if(onAppRunInBackgroundListenerList != null) {
            onAppRunInBackgroundListenerList.remove(listener);
        }

    }

    public static void clearOnAppRunInBackgroundListenerList() {
        if(onAppRunInBackgroundListenerList != null) {
            onAppRunInBackgroundListenerList.clear();
        }

    }

    private void onAppStateChange(boolean isBackground) {
        if(onAppRunInBackgroundListenerList != null) {
            Iterator var2 = onAppRunInBackgroundListenerList.iterator();

            while(var2.hasNext()) {
                OnAppRunInBackgroundListener listener = (OnAppRunInBackgroundListener)var2.next();
                if(listener != null) {
                    listener.onAppRunInBackground(isBackground);
                }
            }
        }

    }

    public interface OnAppRunInBackgroundListener {
        void onAppRunInBackground(boolean var1);
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService("activity");
        List appProcesses = activityManager.getRunningAppProcesses();
        if(appProcesses != null) {
            Iterator var3 = appProcesses.iterator();
            ActivityManager.RunningAppProcessInfo appProcess;
            do {
                if(!var3.hasNext()) {
                    return false;
                }

                appProcess = (ActivityManager.RunningAppProcessInfo)var3.next();
            } while(!appProcess.processName.equals(context.getPackageName()));

            return appProcess.importance > 100;
        } else {
            return false;
        }
    }
}