package blog.csdn.net.mchenys.common.okhttp2.x.listener;

/**
 * 文件上传/下载的进度回调接口 在子线程中回调
 * Created by mChenys on 2016/9/11.
 */
public interface ProgressCallBack extends RequestCallBack {
    void onReceiveProgress(long currLength, long TotalLength, boolean done);
}
