package blog.csdn.net.mchenys.common.okhttp2.x;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class SessionActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    public SessionActivityLifecycleCallbacks() {
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
        if(SessionManager.getInstance() != null) {
            SessionManager.getInstance().onActivityResume();
        }

    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        if(SessionManager.getInstance() != null) {
            SessionManager.getInstance().onActivityStop();
        }

    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }
}
