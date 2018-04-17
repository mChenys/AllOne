package blog.csdn.net.mchenys.common.okhttp2.x.listener;

import android.os.Message;

/**
 * 请求结果回调接口 带进度 如果创建RequestCallBackHandler在主线程,则回调在UI线程,如果创建在子线程,则回调在子线程
 * Created by mChenys on 2016/9/11.
 */
public abstract class ProgressCallBackHandler extends RequestCallBackHandler implements ProgressCallBack {

    protected static final int SUCCESS_PROGRESS = 1;

    protected void handleMessage(Message msg) {
        if (msg.what == SUCCESS_PROGRESS) {
            Object[] result = (Object[]) msg.obj;
            onProgress((Long) result[0], (Long) result[1], (Boolean) result[2]);
        } else {
            super.handleMessage(msg);
        }
    }

    protected abstract void onProgress(long curr, long total, boolean done);

    @Override
    public void onReceiveProgress(long currLength, long TotalLength, boolean done) {
        sendMessage(obtainMessage(SUCCESS_PROGRESS, new Object[]{currLength, TotalLength, done}));
    }

}
