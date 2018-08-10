package blog.csdn.net.mchenys.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 整屋案例数据bean
 */

public class HousingCase implements Parcelable, Comparable<HousingCase> {

    public String id;//案例ID
    public String title;//案例标题
    public String coverImage;//案例封面图
    public String userName;//设计师名
    public String userImage;//设计师头像
    public String houseArea;//房屋面积
    public String housePattern;//房屋户型
    public String budget;//预算
    public int isCollected;// 0 未收藏，1 已收藏
    public int isDesigner;//是否是设计师标志 0 普通用户 1设计师
    public int isTalent;//4.3.0。是否达人标志 0 普通用户 1 达人
    public String talentsName;//4.3.0。达人名称
    public int editTag;//编辑推荐标签	是否精选：0=不是精选，1=精选
    public long collectdTime;//收藏时间

    public List<Talent> talents;
    public HousingCase() {

    }

    public static HousingCase parseBean(JSONObject jsonObject) {
        HousingCase housingCase = null;
        if (jsonObject != null) {
            housingCase = new HousingCase();
            housingCase.id = jsonObject.optString("id");
            housingCase.title = jsonObject.optString("title");
            housingCase.coverImage = jsonObject.optString("coverImage");
            housingCase.userName = jsonObject.optString("userName");
            housingCase.userImage = jsonObject.optString("userImage");
            housingCase.houseArea = jsonObject.optString("houseArea");
            housingCase.housePattern = jsonObject.optString("housePattern");
            housingCase.budget = jsonObject.optString("budget");
            housingCase.talentsName = jsonObject.optString("talentsName");
            housingCase.isDesigner = jsonObject.optInt("isDesigner");
            housingCase.editTag = jsonObject.optInt("editTag");
            housingCase.isTalent = jsonObject.optInt("isTalent");
            housingCase.talents = Talent.parseList(jsonObject.optJSONArray("talents"));
        }
        return housingCase;
    }

    public static List<HousingCase> parseList(JSONArray jsonArray) throws JSONException {
        List<HousingCase> housingCases = null;
        if (jsonArray != null && jsonArray.length() > 0) {
            housingCases = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                housingCases.add(HousingCase.parseBean(jsonArray.getJSONObject(i)));
            }
        }
        return housingCases;
    }

    /**
     * @param another
     * @return 0相等 1不相等
     */
    @Override
    public int compareTo(@NonNull HousingCase another) {
        if (this.id.equals(another.id)) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.coverImage);
        dest.writeString(this.userName);
        dest.writeString(this.userImage);
        dest.writeString(this.houseArea);
        dest.writeString(this.housePattern);
        dest.writeString(this.budget);
        dest.writeInt(this.isCollected);
        dest.writeInt(this.isDesigner);
        dest.writeInt(this.isTalent);
        dest.writeString(this.talentsName);
        dest.writeInt(this.editTag);
        dest.writeLong(this.collectdTime);
        dest.writeList(this.talents);
    }

    protected HousingCase(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.coverImage = in.readString();
        this.userName = in.readString();
        this.userImage = in.readString();
        this.houseArea = in.readString();
        this.housePattern = in.readString();
        this.budget = in.readString();
        this.isCollected = in.readInt();
        this.isDesigner = in.readInt();
        this.isTalent = in.readInt();
        this.talentsName = in.readString();
        this.editTag = in.readInt();
        this.collectdTime = in.readLong();
        this.talents = new ArrayList<Talent>();
        in.readList(this.talents, Talent.class.getClassLoader());
    }

    public static final Creator<HousingCase> CREATOR = new Creator<HousingCase>() {
        @Override
        public HousingCase createFromParcel(Parcel source) {
            return new HousingCase(source);
        }

        @Override
        public HousingCase[] newArray(int size) {
            return new HousingCase[size];
        }
    };
}
