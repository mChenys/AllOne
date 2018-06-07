package blog.csdn.net.mchenys.common.download;

import android.os.Environment;

import java.io.File;

/**
 * 下载对象封装
 * Created by mChenys on 2015/11/28.
 */
public class DownloadInfo {
    public String id;
    public String name; //文件名,如xx.apk xx.mp4
    public String downloadUrl;//下载路径
    public String path;//本地保存路径
    public int currentState;//下载状态
    public long size;//文件大小
    public int currentPos;//当前下载的位置
    public static final String GOOGLE_MARKET = "google_market";// sdcard根目录下的文件夹
    public static final String DOWNLOAD = "download";// google_market目录下有关下载的文件夹

    /**
     * 获取下载的进度
     *
     * @return
     */
    public float getProgress() {
        if (size > 0) {
            return currentPos / (float) size;
        }
        return 0;
    }

    /**
     * 获取文件的保存路径
     *
     * @return
     */
    private String getFilePath() {
        StringBuffer sb = new StringBuffer();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(GOOGLE_MARKET);
        sb.append(File.separator);
        sb.append(DOWNLOAD);
        sb.append(File.separator);
        if (createDir(sb.toString())) {
            //创建文件夹成功,则返回文件的保存路径
            return sb.toString() + name;
        }
        return null;
    }

    private boolean createDir(String path) {
        File fileDir = new File(path);
        // 文件不存在,或者不是文件夹
        if (!fileDir.exists() && !fileDir.isDirectory()) {
            return fileDir.mkdirs();
        }
        return true;
    }

    /**
     * 创建DownloadInfo
     * @param id
     * @param name 文件名
     * @param url 下载url
     * @param size 文件大小
     * @return
     */
    public static DownloadInfo create(String id,String name,String url,int size) {
        DownloadInfo info = new DownloadInfo();
        info.id =id;
        info.name = name;
        info.downloadUrl = url;
        info.size = size;

        info.currentPos = 0;
        info.path = info.getFilePath();
        info.currentState = DownloadManager.STATE_UNDO;
        return info;
    }
}
