package blog.csdn.net.mchenys.common.utils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import blog.csdn.net.mchenys.AllOneApplication;


/**
 * Created by 1 on 2018/2/28.
 */

public class BdLbsUtils {
    public static final String TAG = "BdLbsUtils";
    private static BdLbsUtils instance;
    private LocationClient mLocationClient = null;
    private PCLocationListener mLocationListener;

    public void setLocationListener(PCLocationListener mLocationListener) {
        this.mLocationListener = mLocationListener;
    }

    private BdLbsUtils() {
        init();
    }

    public static BdLbsUtils getInstance() {
        if (instance == null) {
            synchronized (BdLbsUtils.class) {
                if (instance == null) {
                    instance = new BdLbsUtils();
                }
            }
        }
        return instance;
    }

    private void init() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll");
        mLocationClient = new LocationClient(AllOneApplication.mAppContext);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation == null) {
                    start();
                } else {
                    PCLocationModel pcLocationModel = new PCLocationModel();
                    pcLocationModel.address = bdLocation.getAddrStr();    //获取详细地址信息
                    pcLocationModel.country = bdLocation.getCountry();    //获取国家
                    pcLocationModel.province = bdLocation.getProvince();    //获取省份
                    pcLocationModel.city = bdLocation.getCity();    //获取城市
                    pcLocationModel.district = bdLocation.getDistrict();    //获取区县
                    pcLocationModel.street = bdLocation.getStreet();    //获取街道信息
                    pcLocationModel.latitude = bdLocation.getLatitude();    //纬度
                    pcLocationModel.longitude = bdLocation.getLongitude();    //经度
                    if (mLocationListener != null) {
                        if (pcLocationModel.isEmpty()) {
                            mLocationListener.failure(bdLocation.getLocType(), "定位信息为空!");
                        } else {
                            mLocationListener.success(pcLocationModel);
                        }
                    }
                    stop();
                }
            }
        });
    }

    public void start() {
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    public void stop() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    public interface PCLocationListener {
        void success(PCLocationModel pcLocationModel);

        void failure(int errType, String errMsg);
    }

    public class PCLocationModel {
        public String address;    //详细地址信息
        public String country;    //国家
        public String province;    //省份
        public String city;    //城市
        public String district;    //区县
        public String street;    //街道信息
        public double longitude;    //经度
        public double latitude;    //纬度

        @Override
        public String toString() {
            return "PCLocationModel{" +
                    "address='" + address + '\'' +
                    ", country='" + country + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", district='" + district + '\'' +
                    ", street='" + street + '\'' +
                    ", longitude=" + longitude +
                    ", latitude=" + latitude +
                    '}';
        }

        public boolean isEmpty() {
            return StringUtils.isEmpty(address)
                    && StringUtils.isEmpty(country)
                    && StringUtils.isEmpty(province)
                    && StringUtils.isEmpty(city)
                    && StringUtils.isEmpty(district)
                    && StringUtils.isEmpty(street);
        }
    }
}
