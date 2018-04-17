package blog.csdn.net.mchenys.common.utils;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.config.Urls;


/**
 * Created by mChenys on 2017/12/27.
 */

public class AccountUtils {

    public static final int FREELOGIN = 0;// 快捷登录类型
    public static final int PASSPORT = 1;// 太平洋账号登录类型
    public static final int SINA = 2; // 新浪微博登录类型
    public static final int TENCENT = 3;// 腾讯微博登录类型
    public static final int WECHAT = 4;//微信登录类型
    public final static String COOKIE_EXPIRED = "90";

    public interface Callback {
        void onResult(Account account);
    }

    /**
     * 检查绑定
     * @param user
     * @param type
     * @param loginResult
     */
    public static void checkBind(final MFSnsUser user, final int type, final LoginResult loginResult) {
        String appType = "";
        if (type == TENCENT) {
            appType = "qq_lady_mdkt_an";
        } else if (type == SINA) {
            appType = "sina_lady_mdkt";
        } else if (type == WECHAT) {
            appType = "weixin_lady_mdkt";
        }
        String url = Urls.CHECK_BIND + "?type=" + appType + "&resp_enc=utf-8";

        Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put("open_account_id", user.getOpenId());
        bodyMap.put("auto_login", COOKIE_EXPIRED);
        bodyMap.put("accessToken", user.getAccessToken());
        bodyMap.put("screen_name", user.getNickname());
        HttpUtils.postJSON(url, null, bodyMap, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(int code, Exception e) {
                if (loginResult != null)
                    loginResult.onFailure(code, "网络错误,登录失败");
            }

            @Override
            public void onSuccess(JSONObject jsonObject, HttpManager.PCResponse pcResponse) {
                if (jsonObject.optInt("status") == 0) {// 0已经绑定
                    String passportId = jsonObject.optString("user_id");
                    String sessionId = jsonObject.optString("session");
                    String nickName = jsonObject.optString("cmu");
                    Account account = createAccount(user, passportId, sessionId, nickName, type);
                    getUserInfo(true, account, loginResult);
                } else {// 未绑定，进行快速绑定
                    quickBind(user, type, loginResult);
                }
            }
        });
    }

    /**
     * 快速绑定
     * @param user
     * @param type
     * @param loginResult
     */
    public static void quickBind(final MFSnsUser user, final int type, final LoginResult loginResult) {
        String appType = "";
        if (type == TENCENT) {
            appType = "qq_lady_mdkt_an";
        } else if (type == SINA) {
            appType = "sina_lady_mdkt";
        } else if (type == WECHAT) {
            appType = "weixin_lady_mdkt";
        }
        String url = Urls.QUICK_BIND_URL + "?type=" + appType + "&resp_enc=utf-8";
        Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put("open_account_id", user.getOpenId());
        bodyMap.put("auto_login", COOKIE_EXPIRED);
        bodyMap.put("accessToken", user.getAccessToken());
        bodyMap.put("screen_name", user.getNickname());
        HttpUtils.postJSON(url, null, bodyMap, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(int code, Exception e) {
                if (loginResult != null)
                    loginResult.onFailure(code, e.getMessage());
            }

            @Override
            public void onSuccess(JSONObject jsonObject, HttpManager.PCResponse pcResponse) {
                int status = jsonObject.optInt("status");
                if (status == 0) {// 快速绑定成功
                    String passportId = jsonObject.optString("accountId");
                    String sessionId = jsonObject.optString("session");
                    String nickName = jsonObject.optString("cmu");
                    final Account account = createAccount(user, passportId, sessionId, nickName, type);
                    getUserInfo(true, account, loginResult);
                } else {// 失败
                    if (loginResult != null)
                        loginResult.onFailure(pcResponse.getCode(), jsonObject.optString("desc"));
                }
            }
        });
    }

    /**
     * 根据第三方账号初始Account
     *
     * @param user
     * @param passportId
     * @param sessionId
     * @param nickName
     * @param type
     * @return
     */
    private static Account createAccount(MFSnsUser user, String passportId, String sessionId, String nickName, int type) {
        Account account = new Account();
        account.setSessionId(sessionId);
        account.setUserId(passportId);
        account.setType(type);
        account.setPassword("");
        account.setLoginTime(System.currentTimeMillis());
        if (user != null) {
            account.setUserName(StringUtils.replaceIllegalChars(user.getNickname()));
            account.setDescription(user.getBrief());
            if (user.getIcons() != null && user.getIcons().length > 0) {
                if (user.getIcons().length == 1) {
                    account.setSmallHeaderUrl(user.getIcons()[0]);
                }
                if (user.getIcons().length > 1) {
                    account.setSmallHeaderUrl(user.getIcons()[0]);
                    account.setBigHeaderUrl(user.getIcons()[1]);
                }
            }
        } else {
            account.setUserName(nickName);
        }
        return account;
    }

    /**
     * 外部调用查询用户信息
     *
     * @param callback
     */
    public static void getUserInfo(final Callback callback) {
        getUserInfo(false, getLoginAccount(), new LoginResult() {
            @Override
            public void handleSuccess(Account account) {
                callback.onResult(account);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                callback.onResult(null);
            }
        });
    }

    /**
     * 内部调用获取用户信息
     *
     * @param isForceNetwork
     * @param account
     * @param loginResul
     */
    private static void getUserInfo(boolean isForceNetwork, final Account account, final LoginResult loginResul) {
        java.net.CookieManager.setDefault(null);
        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Cookie", Urls.COMMON_SESSION_ID + "=" + account.getSessionId());
        String url = Urls.GET_USER_INFO_URL + "?userId=" + account.getUserId() + "&version=" + Env.versionCode;
        HttpUtils.getJSON(isForceNetwork, url, headersMap, null, new HttpUtils.JSONCallback() {

            @Override
            public void onFailure(int code, Exception e) {
                if (loginResul != null) loginResul.onFailure(code, "获取用户信息失败");
            }

            @Override
            public void onSuccess(JSONObject jsonObject, HttpManager.PCResponse pcResponse) {
                int status = jsonObject.optInt("status");
                if (status == -1) {
                    if (loginResul != null)
                        loginResul.onFailure(status, jsonObject.optString("msg"));
                } else {
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (!StringUtils.isEmpty(data.optString("userNickName"))) {
                        account.setUserName(StringUtils.replaceIllegalChars(data.optString("userNickName")));
                    }
                    account.setAvatarUrl(data.optString("techHeadUrl"));
                    account.setPhoneNum(data.optString("phoneNum"));
                    //保存用户信息
                    saveAccount(account);
                    if (loginResul != null) loginResul.onSuccess(account);
                }
            }
        });
    }

    private static void saveAccount(Account account) {
        try {
            JSONObject accountJson = new JSONObject();
            accountJson.put("type", account.getType());
            accountJson.put("userName", account.getUserName());
            accountJson.put("sessionId", account.getSessionId());
            accountJson.put("userId", account.getUserId());
            accountJson.put("loginTime", account.getLoginTime());
            accountJson.put("avatarUrl", account.getAvatarUrl());
            accountJson.put("description", account.getDescription());
            accountJson.put("smallHeaderUrl", account.getSmallHeaderUrl());
            accountJson.put("bigHeaderUrl", account.getBigHeaderUrl());
            accountJson.put("phoneNum", account.getPhoneNum());
            PreferencesUtils.setPreferences(ShoppingApplication.mAppContext, Constant.ACCOUNT_FILE_NAME,
                    Constant.ACCOUNT_KEY, accountJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取登录账号
     * @return
     */
    public static Account getLoginAccount() {
        String account_message = PreferencesUtils.getPreference(ShoppingApplication.mAppContext,
                Constant.ACCOUNT_FILE_NAME, Constant.ACCOUNT_KEY, "");
        Account account = null;
        try {
            if (!StringUtils.isEmpty(account_message)) {
                account = new Account();
                JSONObject accountJson = new JSONObject(account_message);
                account.setType(accountJson.optInt("type"));
                account.setUserName(accountJson.optString("userName"));
                account.setSessionId(accountJson.optString("sessionId"));
                account.setUserId(accountJson.optString("userId"));
                account.setLoginTime(accountJson.optLong("loginTime"));
                account.setAvatarUrl(accountJson.optString("avatarUrl"));
                account.setPhoneNum(accountJson.optString("phoneNum"));
                account.setDescription(accountJson.optString("description"));
                account.setSmallHeaderUrl(accountJson.optString("smallHeaderUrl"));
                account.setBigHeaderUrl(accountJson.optString("bigHeaderUrl"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }

    /**
     * 是否登录
     * @return
     */
    public static boolean isLogin() {
        Account account = getLoginAccount();
        return null != account && !StringUtils.isEmpty(account.getSessionId());
    }

    /**
     * 获取sessionId
     * @return
     */
    public static String getSessionId() {
        if (isLogin()) {
            return getLoginAccount().getSessionId();
        }
        return "";
    }

    /**
     * 退出登录
     */
    public static void logout() {
        CookieSyncManager.createInstance(ShoppingApplication.mAppContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        cookieManager.removeSessionCookie();
        CookieSyncManager.getInstance().sync();
        if (isLogin()) {
            Account account = getLoginAccount();
            account.reset();
        }
        PreferencesUtils.clearPreference(ShoppingApplication.mAppContext,Constant.ACCOUNT_FILE_NAME);
    }
}
