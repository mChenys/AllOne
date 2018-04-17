//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns.callback;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

public abstract class SnsShareListener {


    public void onSucceeded(Context context) {
    }

    public abstract void onFailed(Context var1, String var2);

    public abstract void onTencentFailed(Context var1, Object var2);

    public void onTencentWeiBoSucceeded(Context context) {
    }

    public void onTencentQzoneSucceeded(Context context) {
    }

    public void onSinaSucceeded(Context context) {
    }


    public void onWeiXinSucceeded(Context context) {
    }

    public void onWeiXinFriendsSucceeded(Context context) {
    }

    public void onWeiXinNoSupported(Context context, boolean supported) {
    }

    public void onTextSharedCopy(Context context) {
    }

    public void onTencentQQSucceeded(Context context, Object response) {
    }

    public void onPause(Context context) {
    }

    public void onResume(Context context) {
    }

    public void onSelectedPause(Context context) {
    }

    public void onSelectedResume(Context context) {
    }

    public void onWeiBoShareStep(final Context context) {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                ((Activity)context).finish();
            }
        }, 500L);
    }

    public void onWeiBoShareLoginSuccess(Context context) {
        ((Activity)context).finish();
    }
}
