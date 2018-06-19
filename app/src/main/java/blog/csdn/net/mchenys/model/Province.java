package blog.csdn.net.mchenys.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/6/5.
 */

public class Province {
    private int id;
    private String name;
    private List<City> cityList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    public static Province parseProvince(JSONObject jsonObject) throws JSONException {
        Province province = new Province();
        province.setId(jsonObject.optInt("id"));
        province.setName(jsonObject.optString("name"));
        province.setCityList(City.parseCityList(jsonObject.getJSONArray("cityList")));
        return province;
    }

    public static List<Province> parseProvinceList(JSONArray jsonArray) throws JSONException {
        List<Province> provinceList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            provinceList.add(Province.parseProvince(jsonArray.getJSONObject(i)));
        }
        return provinceList;
    }

    public static int getCityIdByName(List<Province> provinceList, String province, String city) {
        for (int i = 0; i < provinceList.size(); i++) {
            Province p = provinceList.get(i);
            if (province.equals(p.getName())) {
                for (int j = 0; j < p.getCityList().size(); j++) {
                    City c = p.getCityList().get(j);
                    if (city.equals(c.getCityName()))
                        return c.getCityId();
                }
            }
        }
        return -1;
    }

    public static class City {
        private int cityId;
        private String cityName;

        public static City parseCity(JSONObject jsonObject) {
            City city = new City();
            city.setCityId(jsonObject.optInt("cityId"));
            city.setCityName(jsonObject.optString("cityName"));
            return city;
        }

        public static List<City> parseCityList(JSONArray jsonArray) throws JSONException {
            List<City> cityList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                cityList.add(parseCity(jsonArray.getJSONObject(i)));
            }
            return cityList;
        }

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }
    }
}
