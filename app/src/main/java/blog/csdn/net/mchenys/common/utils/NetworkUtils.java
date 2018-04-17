package blog.csdn.net.mchenys.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by mChenys on 2018/4/17.
 */

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context) {
        boolean netWorkStatus = false;
        if(null != context) {
            ConnectivityManager connManager = (ConnectivityManager)context.getSystemService("connectivity");
            if(connManager != null && connManager.getActiveNetworkInfo() != null) {
                netWorkStatus = connManager.getActiveNetworkInfo().isAvailable();
            }
        }

        return netWorkStatus;
    }
}
