package blog.csdn.net.mchenys.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public  class Talent implements Parcelable {
        public int seq;
        public int talentId;
        public String talentsName;
        public String talentDesc;
        public String imgUrl;
        public String titleLogo;

        public static List<Talent> parseList(JSONArray talentArr) {
            List<Talent> list = new ArrayList<>();
            if (null != talentArr) {
                for (int i = 0; i < talentArr.length(); i++) {
                    list.add(parseBan(talentArr.optJSONObject(i)));
                }
            }
            return list;

        }

        private static Talent parseBan(JSONObject object) {
            Talent bean = new Talent();
            if (null != object) {
                bean.seq = object.optInt("seq");
                bean.talentId = object.optInt("talentId");
                bean.talentsName = object.optString("talentsName");
                bean.talentDesc = object.optString("talentDesc");
                bean.imgUrl = object.optString("imgUrl");
                bean.titleLogo = object.optString("titleLogo");
            }
            return bean;
        }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.seq);
        dest.writeInt(this.talentId);
        dest.writeString(this.talentsName);
        dest.writeString(this.talentDesc);
        dest.writeString(this.imgUrl);
    }

    public Talent() {
    }

    protected Talent(Parcel in) {
        this.seq = in.readInt();
        this.talentId = in.readInt();
        this.talentsName = in.readString();
        this.talentDesc = in.readString();
        this.imgUrl = in.readString();
    }

    public static final Parcelable.Creator<Talent> CREATOR = new Parcelable.Creator<Talent>() {
        @Override
        public Talent createFromParcel(Parcel source) {
            return new Talent(source);
        }

        @Override
        public Talent[] newArray(int size) {
            return new Talent[size];
        }
    };
}