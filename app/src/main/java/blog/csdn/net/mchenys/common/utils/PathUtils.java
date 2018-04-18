package blog.csdn.net.mchenys.common.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

/**
 * Created by mChenys on 2015/7/30.
 */
public class PathUtils {
    /**
     * 获取手机缓存目录,rootDir根节点的目录
     *
     * @param context
     * @param rootDir
     * @return
     */
    public static File getDiskCacheDir(Context context, String rootDir) {
        String cachePath = context.getCacheDir().getAbsolutePath();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            if (Build.VERSION.SDK_INT <= 8) {
                cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else if (context.getExternalCacheDir() != null) {
                cachePath = context.getExternalCacheDir().getAbsolutePath();
            }
        }
        return new File(cachePath + File.separator + rootDir);
    }

}
