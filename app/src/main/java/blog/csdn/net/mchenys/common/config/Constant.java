package blog.csdn.net.mchenys.common.config;

/**
 * 全局常量
 * Created by mChenys on 2017/12/27.
 */

public interface Constant {
     String KEY_WEB_CALLBACK = "key_web_callback";
     String KEY_PUSH = "key_push";
     String GE_TASKID = "geTaskId";
     String GE_MSGID = "geMsgId";
     String KEY_URL = "key_url";
     String KEY_URI = "key_uri";
     String KEY_TITLE = "key_title";
     String KEY_POSITION = "key_position";
     int REQ_WEB_LOGIN = 200;//webView登录
     int REQ_LOGIN = 100;//太平洋账号登录
     int REQ_PHONE_BIND = 101;//手机号绑定
     String ACCOUNT_FILE_NAME = "account_file_name"; //本地保存账号的文件名
     String ACCOUNT_KEY = "account_key";//本地保存账号的key
     String KEY_REFRESH = "key_refresh";
     int REQ_CAMERA_CODE = 10;
     int REQ_CROP_CODE = 11;
     int REQ_UPDATE_HEADER = 12;
     String PREFERENCES_KEY_SUBCOLUMN = "preferences_key_subcolumn";
     String PREFERENCES_SUBCOLUMN = "preferences_subcolumn";
}
