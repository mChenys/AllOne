package blog.csdn.net.mchenys.common.utils;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONException;
import org.json.JSONObject;

import blog.csdn.net.mchenys.AllOneApplication;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.sns.bean.SnsUser;
import blog.csdn.net.mchenys.model.Account;


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
        PreferencesUtils.clearPreference(AllOneApplication.mAppContext,Constant.ACCOUNT_FILE_NAME);
    }
}
