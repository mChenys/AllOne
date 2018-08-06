package blog.csdn.net.mchenys.common.push;

import android.content.Context;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

/**
 * 从2.9.5.0版本开始，为了解决小概率发生的Android广播丢失问题，我们推荐应用开发者使用新的IntentService方式来接收推送服务事件
 * （包括CID获取通知、透传消息通知等）
 * Created by mChenys on 2018/7/19.
 */

public class AllOneIntentService extends GTIntentService {

    public AllOneIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String taskId = msg.getTaskId();
        String geMessageId = msg.getMessageId();
        String message = new String(msg.getPayload());
        Log.e("cys", "收到消息:" + message);
        PushHelper.onNotificationReceive(taskId, geMessageId, context,message);
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }
}