package blog.csdn.net.mchenys.common.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.URLUtil;

import org.json.JSONException;
import org.json.JSONObject;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.utils.AppUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.common.utils.URIUtils;
import blog.csdn.net.mchenys.module.main.MainActivity;

/**
 * 推送消息显示及处理
 *
 * @author 推送消息格式 ： "type": "2","content":"普通文章终端","url":"pchousebrowser://infor_article/123213"
 */
public class PushHelper {

    //消息类型
    public final static int MESSAGE_TXT = 1;   //文本消息推送
    public final static int MESSAGE_EVENT = 2; //json格式消息事件
    public final static int MESSAGE_NO_JSON = -1; //非JSON格式

    //1:有更新版本 ，2：跳转协议
    private static int MESSAGE_TYPE;

    //消息内容
    private static String messageTitle;

    //消息url
    private static String messageUrl;

    public static final String PRIMARY_CHANNEL = "default";

    private static NotificationCompat.BigTextStyle bitTextStyle;
    private static int NOTIFICATION_ID = 100;
    private static int REQUEST_CODE = 1;

    /**
     * @param context
     * @param content
     */
    public static void onNotificationReceive(String taskId, String geMessageId, Context context, String content) {
        String messageId = parseGetMessageId(content);
        Log.e("cys", "messageId:" + messageId);
        // 接受到消息时自己统计服务器的处理
        // Mofang.onNotificationReceive(context, messageId);
        pushToClientMessage(taskId, geMessageId, context, content);

    }


    /**
     * 客户端推送消息到通知栏
     *
     * @param context
     * @param msgContent
     */
    public static void pushToClientMessage(String taskId, String geMessageId, Context context, String msgContent) {
        if (null != msgContent && !"".equals(msgContent)) {
            //解析消息
            parseMessage(msgContent);

            boolean isRunning = AppUtils.isRunning(context);
            PendingIntent messagePendingIntent = null;
            Intent messageIntent;
            REQUEST_CODE++;
            if (MESSAGE_TYPE == MESSAGE_TXT) {
                if (!isRunning) {
                    messageIntent = new Intent(context, MainActivity.class);
                    messageIntent.putExtra(Constant.KEY_PUSH, true);
                    messageIntent.putExtra(Constant.GE_TASKID, taskId);
                    messageIntent.putExtra(Constant.GE_MSGID, geMessageId);
                    messagePendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                } else {
                    //单纯带入前端
                    messageIntent = new Intent();
                    messageIntent.putExtra(Constant.GE_TASKID, taskId);
                    messageIntent.putExtra(Constant.GE_MSGID, geMessageId);
                    messageIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    messagePendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            } else if (MESSAGE_TYPE == MESSAGE_EVENT) {  //跳转协议
                if (messageUrl != null && URIUtils.hasURI(messageUrl)) {
                    messageIntent = URIUtils.getIntent(messageUrl, context);
                    messageIntent.putExtra(Constant.GE_TASKID, taskId);
                    messageIntent.putExtra(Constant.GE_MSGID, geMessageId);
                    messageIntent.putExtra(Constant.KEY_PUSH, true);
                    messageIntent.setData(Uri.parse(messageUrl));
                    messagePendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, messageIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                } else {
                    if (!isRunning) {
                        messageIntent = new Intent(context, MainActivity.class);
                        messageIntent.putExtra(Constant.GE_TASKID, taskId);
                        messageIntent.putExtra(Constant.GE_MSGID, geMessageId);
                        messageIntent.putExtra(Constant.KEY_PUSH, true);
                        messagePendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        //单纯带入前端
                        messageIntent = new Intent();
                        messageIntent.putExtra(Constant.GE_TASKID, taskId);
                        messageIntent.putExtra(Constant.GE_MSGID, geMessageId);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        messagePendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                }
            } else if (MESSAGE_TYPE == MESSAGE_NO_JSON) {
                return;
            }

            //创建通知
            NotificationManager messageNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //判断是否是8.0Android.O如果将编译版本设置26以上且未进行Android O的适配会导致在Android O以上的手机无法弹出通知栏
            NotificationCompat.Builder messageNotification;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                        "Primary Channel", NotificationManager.IMPORTANCE_DEFAULT);
                chan1.setLightColor(Color.GREEN);
                chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                messageNotificationManager.createNotificationChannel(chan1);
                messageNotification = new NotificationCompat.Builder(context, PRIMARY_CHANNEL);
            } else {
                messageNotification = new NotificationCompat.Builder(context);
            }

            String appName = context.getString(R.string.app_name);
            bitTextStyle = new NotificationCompat.BigTextStyle();
            bitTextStyle.bigText(messageTitle);
            messageNotification.setContentTitle(appName)
                    .setContentText(messageTitle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(messagePendingIntent)
                    .setAutoCancel(true)
                    .setTicker(messageTitle) // 设置状态栏提示信息
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)  // 设置使用所有默认值（声音、震动、闪屏等）
                    .setStyle(bitTextStyle)
                    .build();

            NOTIFICATION_ID++;
            messageNotificationManager.notify(NOTIFICATION_ID, messageNotification.build());
        }
    }

    /**
     * 解析消息
     *
     * @param message
     */
    private static void parseMessage(String message) {
        try {
            JSONObject json = parse2JSON(message);
            if (null != json) {
                String type = json.optString("type");
                messageTitle = json.optString("content");
                messageUrl = json.optString("url");
                MESSAGE_TYPE = !StringUtils.isEmpty(type) ? Integer.valueOf(type) : MESSAGE_TXT;

                if (MESSAGE_TYPE == MESSAGE_EVENT) {
                    // 发送跳转协议为空或忘记发送时直接不做处理，不显示
                    if (StringUtils.isEmpty(messageUrl) || StringUtils.isEmpty(messageTitle)) {
                        MESSAGE_TYPE = MESSAGE_NO_JSON;
                    }
                } else if (MESSAGE_TYPE == MESSAGE_TXT) {
                    if (StringUtils.isEmpty(messageTitle)) {
                        MESSAGE_TYPE = MESSAGE_NO_JSON;
                    }
                }
            } else {
                if (!StringUtils.isEmpty(message)) {
                    messageTitle = message;
                    messageUrl = message;
                    MESSAGE_TYPE = URLUtil.isNetworkUrl(message) ? MESSAGE_EVENT : MESSAGE_TXT; //2.0修改,针对直接推送url的处理
                }
            }


        } catch (JSONException e) {
            MESSAGE_TYPE = MESSAGE_NO_JSON;
            e.printStackTrace();
        }
    }

    private static String parseGetMessageId(String content) {
        try {
            JSONObject json = parse2JSON(content);
            return json.optString("push_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject parse2JSON(String content) throws JSONException {
        if (!StringUtils.isEmpty(content) && isJsonData(content)) {
            int jsonStart = content.indexOf("{");
            if (jsonStart < 0) {
                content = "{" + content;
                jsonStart = 0;
            }
            int jsonEnd = content.lastIndexOf("}") + 1;
            if (jsonEnd <= 0) {
                content += "}";
                jsonEnd = content.length();
            }
            return new JSONObject(content.substring(jsonStart, jsonEnd));
        }
        return null;
    }

    /**
     * 判断发送的消息内容是否为json格式
     *
     * @param message
     * @return
     */
    private static boolean isJsonData(String message) {
        boolean isJson = false;
        if (null != message && !"".equals(message)) {
            int type = message.indexOf("type");
            int content = message.indexOf("content");
            //这里不能判断url，因为文本模式中没有url
            if (type > -1 && content > -1) {
                isJson = true;
            } else {
                isJson = false;
            }
        }
        return isJson;
    }


}
