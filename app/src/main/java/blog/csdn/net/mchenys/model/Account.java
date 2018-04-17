package blog.csdn.net.mchenys.model;

/**
 * 账号bean
 * Created by mChenys on 2017/12/28.
 */

public class Account {
    private int type = 0;// 登陆类型
    private String userName = "";// 用户名
    private String sessionId = "";// sessionid
    private String userId = "";// 用户id
    private String avatarUrl = "";// pc用户头像
    private String phoneNum = "";//绑定的手机号码
    private String description = "";// 个人简介
    private String password = "";// 密码
    private long loginTime = -1;//登陆时间

    //第三方登录的icons
    private String smallHeaderUrl;//第三方小头像
    private String bigHeaderUrl;//第三方大头像

    public int getType() {
        return type;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSmallHeaderUrl() {
        return smallHeaderUrl;
    }

    public void setSmallHeaderUrl(String smallHeaderUrl) {
        this.smallHeaderUrl = smallHeaderUrl;
    }

    public String getBigHeaderUrl() {
        return bigHeaderUrl;
    }

    public void setBigHeaderUrl(String bigHeaderUrl) {
        this.bigHeaderUrl = bigHeaderUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public void reset() {
        this.type = 0;// 登陆类型
        this.userName = "";// 用户名
        this.sessionId = "";// sessionid
        this.userId = "";// 用户id
        this.avatarUrl = "";// pc用户头像
        this.phoneNum = "";//绑定的手机号码
        this.description = "";// 个人简介
        this.password = "";// 密码
        this.loginTime = -1;//登陆时间
    }
}
