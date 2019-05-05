package blog.csdn.net.mchenys.common.config;

/**
 * 全局常量
 * Created by mChenys on 2017/12/27.
 */

public interface Constant {
     //params key
     String KEY_WEB_CALLBACK = "key_web_callback";
     String KEY_PUSH = "key_push";
     String GE_TASKID = "geTaskId";
     String GE_MSGID = "geMsgId";
     String KEY_URL = "key_url";
     String KEY_URI = "key_uri";
     String KEY_TITLE = "key_title";
     String KEY_POSITION = "key_position";
     String KEY_RUNNING = "key_running";
     String KEY_REFRESH = "key_refresh";
     String KEY_ID = "key_id";
     String KEY_IMAGES = "key_images";

     //requestCode
     int REQ_WEB_LOGIN = 200;//webView登录
     int REQ_LOGIN = 100;//太平洋账号登录
     int REQ_PHONE_BIND = 101;//手机号绑定
     int REQ_CAMERA_CODE = 10;
     int REQ_CROP_CODE = 11;
     int REQ_UPDATE_HEADER = 12;

     //preference key
     String DEFAULT_PREF = "default_pref";  //默认的preference name
     String PREFERENCES_KEY_SUBCOLUMN = "preferences_key_subcolumn";
     String PREFERENCES_SUBCOLUMN = "preferences_subcolumn";
     String KEY_ACCOUNT_FILE_NAME = "key_account_file_name"; //本地保存账号的文件名
     String KEY_ACCOUNT_KEY = "key_account_key";//本地保存账号的key

     //other
     String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC95Rla4etrOqdr5WiuSh" +
             "JHPb6ztEC1A6zigsB/+OfiLVTL65TtaJsAm1Pzf8Iwa/whIiS5NFAhW6WlLskShzOVwZdBvMfTB4vxa0F6AAKd+pqAGx5J" +
             "SELac6K1RYLQbkr7qx5PWql4S3c4n7UPsjDhJHpGEkRWQwnPI+aa/wsadwIDAQAB";  // RSA加密公钥
}
