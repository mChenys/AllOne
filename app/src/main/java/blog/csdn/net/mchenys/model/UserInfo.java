package blog.csdn.net.mchenys.model;

import org.json.JSONObject;

import java.util.List;

/**
 * 个人主页
 * Created by mChenys on 2018/8/9.
 */

public class UserInfo {
    public String userFace; //头像
    public String nickName; //昵称
    public int isDesigner; //是否是设计师
    public int isTalent; //是否是达人
    public List<Talent> talents; //达人标签列表

    public String fansCount; //粉丝数
    public String focusedCount; //关注数
    public String caseCount; //案例数
    public int isFocused; //是否已关注


    public int sex; //性别
    public String city; //居住地
    public String position; //职业
    public String desc; //简介
    public String price; //设计师报价

//    public String name; //用户名
//    public String phoneNumber; //电话号码
//    public String email; //邮箱
//    public String type; //设计师类型
//    public int seniority; //经验
//    public String company; //公司
//    public String style; //风格（可多选，需确认此处数据结构）


    public UserInfo(JSONObject object) {
        if (null != object) {
            this.userFace = object.optString("userFace");
            this.nickName = object.optString("nickName");
            this.isDesigner = object.optInt("isDesigner");
            this.isTalent = object.optInt("isTalent");
            this.talents = Talent.parseList(object.optJSONArray("talents"));
            this.fansCount = object.optString("fansCount");
            this.focusedCount = object.optString("focusedCount");
            this.caseCount = object.optString("caseCount");
            this.isFocused = object.optInt("isFocused");
            this.sex = object.optInt("sex");
            this.city = object.optString("city");
            this.position = object.optString("position");
            this.desc = object.optString("desc");
            this.price = object.optString("price");
        }
    }

}

