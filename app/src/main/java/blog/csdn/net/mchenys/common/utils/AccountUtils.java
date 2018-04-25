package blog.csdn.net.mchenys.common.utils;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import blog.csdn.net.mchenys.AllOneApplication;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Env;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.sns.bean.SnsUser;
import blog.csdn.net.mchenys.model.Account;
import blog.csdn.net.mchenys.module.account.LoginResult;


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

    public static void login(String username, final String password, final LoginResult loginResult) {
        Map<String, String> bodyMap = new HashMap<>();
        Map<String, String> headersMap = new HashMap<>();
        bodyMap.put("username", username);
        bodyMap.put("password", password);
        bodyMap.put("auto_login", "90");//cookie过期时间，不填默认为15天，最大不超过90天
        HttpUtils.postJSON(Urls.ACCOUNT_LOGIN, headersMap, bodyMap, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure( Exception e) {
                loginResult.onFailure(-1, e.getMessage());
            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {
                int code = jsonObject.optInt("status");
                if (code == 0) {//登录成功
                    final Account account = new Account();
                    String sessionId = jsonObject.optString("session");
                    account.setSessionId(sessionId);
                    account.setUserId(jsonObject.optString("userId"));
                    account.setType(PASSPORT);
                    account.setLoginTime(System.currentTimeMillis());
                    account.setPassword(password);
                    //检查手机是否绑定
                    checkPhoneBind(sessionId, new HttpUtils.JSONCallback() {
                        @Override
                        public void onFailure(Exception e) {
                            getUserInfo(true, account, loginResult);
                        }

                        @Override
                        public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {
                            int code = jsonObject.optInt("code");
                            if (code == 1) {
                                account.setPhoneBind(true);
                            }
                            getUserInfo(true, account, loginResult);
                        }
                    });
                } else if (code == -1) {
                    loginResult.onFailure(code, "登录失败");
                } else if (code == 2) {
                    loginResult.onFailure(code, "用户不存在,登录失败");
                } else if (code == 3) {
                    loginResult.onFailure(code, "账号或密码错误,登录失败");
                }else if(code == 4){
                    loginResult.onFailure(code, "为了您的帐号安全，请输入验证码");
                }else if (code == 6) {
                    loginResult.onFailure(code, "验证码失效");
                } else if (code == 7) {
                    loginResult.onFailure(code, "验证码错误");
                } else {
                    loginResult.onFailure(code, "获取错误数据,登录失败");
                }
            }
        });
    }
    /**
     * 检查手机是否绑定
     *
     * @param callback
     */
    private static void checkPhoneBind(String sessionId, HttpUtils.JSONCallback callback) {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("act", "check");
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Cookie", Urls.COMMON_SESSION_ID + sessionId);
        HttpUtils.postJSON(Urls.PHONE_BIND2, headersMap, bodyMap, callback);
    }

    /**
     * 获取用户信息
     * @param isForceNetwork
     * @param account
     * @param loginResul
     */
    public static void getUserInfo(boolean isForceNetwork, final Account account, final LoginResult loginResul) {
        java.net.CookieManager.setDefault(null);
        Map<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Cookie", Urls.COMMON_SESSION_ID + account.getSessionId());
        String url = Urls.GET_USER_INFO_URL + "?uid=" + account.getUserId() + "&version=" + Env.versionCode;
        HttpUtils.getJSON(isForceNetwork, url, headersMap, null, new HttpUtils.JSONCallback() {

            @Override
            public void onFailure(Exception e) {
                if (loginResul != null) loginResul.onFailure(-1,"获取用户信息失败");
            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {
                int status = jsonObject.optInt("status");
                if (status == -1) {
                    if (loginResul != null)
                        loginResul.onFailure(status, "获取用户信息失败");
                } else {
                    if (!StringUtils.isEmpty(jsonObject.optString("nickName"))) {
                        account.setUserName(StringUtils.replaceIllegalChars(jsonObject.optString("nickName")));
                    }
                    account.setAvatarUrl(jsonObject.optString("image"));
                    account.setPhoneNum(jsonObject.optString("telephone"));
                    saveAccount(account);
                    if (loginResul != null) loginResul.onSuccess(account);
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
    private static Account createAccount(SnsUser user, String passportId, String sessionId, String nickName, int type) {
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
            PreferencesUtils.setPreferences(AllOneApplication.mAppContext, Constant.ACCOUNT_FILE_NAME,
                    Constant.ACCOUNT_KEY, accountJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取登录账号
     *
     * @return
     */
    public static Account getLoginAccount() {
        String account_message = PreferencesUtils.getPreference(AllOneApplication.mAppContext,
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
     *
     * @return
     */
    public static boolean isLogin() {
        Account account = getLoginAccount();
        return null != account && !StringUtils.isEmpty(account.getSessionId());
    }

    /**
     * 获取sessionId
     *
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
        CookieSyncManager.createInstance(AllOneApplication.mAppContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        cookieManager.removeSessionCookie();
        CookieSyncManager.getInstance().sync();
        if (isLogin()) {
            Account account = getLoginAccount();
            account.reset();
        }
        PreferencesUtils.clearPreference(AllOneApplication.mAppContext, Constant.ACCOUNT_FILE_NAME);
    }
}
