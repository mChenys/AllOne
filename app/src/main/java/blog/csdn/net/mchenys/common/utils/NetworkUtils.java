package blog.csdn.net.mchenys.common.utils;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.support.v4.app.ActivityCompat;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class NetworkUtils {
    public static final String NETWORK_TYPE_WIFI_1 = Context.WIFI_SERVICE;
    public static final String NETWORK_TYPE_3G = "eg";
    public static final String NETWORK_TYPE_2G = "2g";
    public static final String NETWORK_TYPE_WAP = "wap";
    public static final String NETWORK_TYPE_UNKNOWN_1 = "unknown";
    public static final String NETWORK_TYPE_DISCONNECT = "disconnect";
    public static final int NONE = 0;
    public static final int WIFI = 1;
    public static final int MOBILE = 2;
    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    private static final int NETWORK_TYPE_WIFI = -101;
    private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    private static final int NETWORK_CLASS_2_G = 1;
    private static final int NETWORK_CLASS_3_G = 2;
    private static final int NETWORK_CLASS_4_G = 3;
    private static DecimalFormat df = new DecimalFormat("#.##");
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_UMTS = 3;
    public static final int NETWORK_TYPE_CDMA = 4;
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    public static final int NETWORK_TYPE_EVDO_A = 6;
    public static final int NETWORK_TYPE_1xRTT = 7;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_HSPAP = 15;

    public NetworkUtils() {
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean netWorkStatus = false;
        if (null != context) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connManager != null && connManager.getActiveNetworkInfo() != null) {
                netWorkStatus = connManager.getActiveNetworkInfo().isAvailable();
            }
        }

        return netWorkStatus;
    }

    public static int getNetworkState(Context context) {
        if (null != context) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connManager) {
                NetworkInfo networkInfo = connManager.getNetworkInfo(1);
                State state;
                if (null != networkInfo) {
                    state = networkInfo.getState();
                    if (state == State.CONNECTED || state == State.CONNECTING) {
                        return 1;
                    }
                }

                networkInfo = connManager.getNetworkInfo(0);
                if (null != networkInfo) {
                    state = networkInfo.getState();
                    if (state == State.CONNECTED || state == State.CONNECTING) {
                        return 2;
                    }
                }
            }
        }

        return 0;
    }

    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        return networkInfo == null ? -1 : networkInfo.getType();
    }

    public static String getNetworkTypeName(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        String type = "disconnect";
        NetworkInfo networkInfo;
        if (manager != null && (networkInfo = manager.getActiveNetworkInfo()) != null) {
            if (networkInfo.isConnected()) {
                String typeName = networkInfo.getTypeName();
                if (Context.WIFI_SERVICE.equalsIgnoreCase(typeName)) {
                    type = Context.WIFI_SERVICE;
                } else if ("MOBILE".equalsIgnoreCase(typeName)) {
                    String proxyHost = Proxy.getDefaultHost();
                    type = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? "eg" : "2g") : "wap";
                } else {
                    type = "unknown";
                }
            }

            return type;
        } else {
            return type;
        }
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        } else if (VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            if (context instanceof Activity && !ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, "android.permission.READ_PHONE_STATE")) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
            }

            return false;
        } else {
            switch (telephonyManager.getNetworkType()) {
                case 0:
                    return false;
                case 1:
                    return false;
                case 2:
                    return false;
                case 3:
                    return true;
                case 4:
                    return false;
                case 5:
                    return true;
                case 6:
                    return true;
                case 7:
                    return false;
                case 8:
                    return true;
                case 9:
                    return true;
                case 10:
                    return true;
                case 11:
                    return false;
                case 12:
                    return true;
                case 13:
                    return true;
                case 14:
                    return true;
                case 15:
                    return true;
                default:
                    return false;
            }
        }
    }

    public static String getIpAddress() {
        try {
            Enumeration en = NetworkInterface.getNetworkInterfaces();

            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration enumIpAddr = intf.getInetAddresses();

                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return null;
    }

    public static boolean isWifiAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == 1;
            }
        }

        return false;
    }

    public static String getMacAddress(Context context) {
        if (context == null) {
            return "";
        } else {
            String localMac = null;
            if (isWifiAvailable(context)) {
                localMac = getWifiMacAddress(context);
            }

            if (localMac != null && localMac.length() > 0) {
                localMac = localMac.replace(":", "-").toLowerCase();
                return localMac;
            } else {
                localMac = getMacFromCallCmd();
                if (localMac != null) {
                    localMac = localMac.replace(":", "-").toLowerCase();
                }

                return localMac;
            }
        }
    }

    private static String getWifiMacAddress(Context context) {
        String localMac = null;

        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (wifi.isWifiEnabled()) {
                localMac = info.getMacAddress();
                if (localMac != null) {
                    localMac = localMac.replace(":", "-").toLowerCase();
                    return localMac;
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return null;
    }

    private static String getMacFromCallCmd() {
        String result = "";
        result = callCmd("busybox ifconfig", "HWaddr");
        if (result != null && result.length() > 0) {
            if (result.length() > 0 && result.contains("HWaddr")) {
                String Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
                if (Mac.length() > 1) {
                    result = Mac.replaceAll(" ", "");
                }
            }

            return result;
        } else {
            return null;
        }
    }

    public static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";

        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            while ((line = br.readLine()) != null && !line.contains(filter)) {
                ;
            }

            result = line;
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return result;
    }

    public static boolean IsNetWorkEnable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            }

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected() && info.getState() == State.CONNECTED) {
                return true;
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return false;
    }

    public static String formatSize(long size) {
        String unit = "B";
        float len = (float) size;
        if (len > 900.0F) {
            len /= 1024.0F;
            unit = "KB";
        }

        if (len > 900.0F) {
            len /= 1024.0F;
            unit = "MB";
        }

        if (len > 900.0F) {
            len /= 1024.0F;
            unit = "GB";
        }

        if (len > 900.0F) {
            len /= 1024.0F;
            unit = "TB";
        }

        return df.format((double) len) + unit;
    }

    public static String formatSizeBySecond(long size) {
        String unit = "B";
        float len = (float) size;
        if (len > 900.0F) {
            len /= 1024.0F;
            unit = "KB";
        }

        if (len > 900.0F) {
            len /= 1024.0F;
            unit = "MB";
        }

        if (len > 900.0F) {
            len /= 1024.0F;
            unit = "GB";
        }

        if (len > 900.0F) {
            len /= 1024.0F;
            unit = "TB";
        }

        return df.format((double) len) + unit + "/s";
    }

    public static String format(long size) {
        String unit = "B";
        float len = (float) size;
        if (len > 1000.0F) {
            len /= 1024.0F;
            unit = "KB";
            if (len > 1000.0F) {
                len /= 1024.0F;
                unit = "MB";
                if (len > 1000.0F) {
                    len /= 1024.0F;
                    unit = "GB";
                }
            }
        }

        return df.format((double) len) + "\n" + unit + "/s";
    }

    public static String getProvider(Context context) {
        String provider = "未知";

        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
                if (context instanceof Activity && !ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, "android.permission.READ_PHONE_STATE")) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
                }
            } else {
                String IMSI = telephonyManager.getSubscriberId();
                Log.v("tag", "getProvider.IMSI:" + IMSI);
                if (IMSI == null) {
                    if (5 == telephonyManager.getSimState()) {
                        String operator = telephonyManager.getSimOperator();
                        Log.v("tag", "getProvider.operator:" + operator);
                        if (operator != null) {
                            if (!operator.equals("46000") && !operator.equals("46002") && !operator.equals("46007")) {
                                if (operator.equals("46001")) {
                                    provider = "中国联通";
                                } else if (operator.equals("46003")) {
                                    provider = "中国电信";
                                }
                            } else {
                                provider = "中国移动";
                            }
                        }
                    }
                } else if (!IMSI.startsWith("46000") && !IMSI.startsWith("46002") && !IMSI.startsWith("46007")) {
                    if (IMSI.startsWith("46001")) {
                        provider = "中国联通";
                    } else if (IMSI.startsWith("46003")) {
                        provider = "中国电信";
                    }
                } else {
                    provider = "中国移动";
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return provider;
    }

    public static String getCurrentNetworkType(Context context) {
        int networkClass = getNetworkClass(context);
        String type = "未知";
        switch (networkClass) {
            case -101:
                type = Context.WIFI_SERVICE;
                break;
            case -1:
                type = "无";
                break;
            case 0:
                type = "未知";
                break;
            case 1:
                type = "2G";
                break;
            case 2:
                type = "3G";
                break;
            case 3:
                type = "4G";
        }

        return type;
    }

    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case -101:
                return -101;
            case -1:
                return -1;
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return 1;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                return 2;
            case 13:
                return 3;
            default:
                return 0;
        }
    }

    private static int getNetworkClass(Context context) {
        int networkType = 0;

        try {
            NetworkInfo network = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (network != null && network.isAvailable() && network.isConnected()) {
                int type = network.getType();
                if (type == 1) {
                    networkType = -101;
                } else if (type == 0) {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    if (VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
                        if (context instanceof Activity && !ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, "android.permission.READ_PHONE_STATE")) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
                        }
                    } else {
                        networkType = telephonyManager.getNetworkType();
                    }
                }
            } else {
                networkType = -1;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return getNetworkClassByType(networkType);
    }

    public static String getWifiRssi(Context context) {
        int asu = 85;

        try {
            NetworkInfo network = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (network != null && network.isAvailable() && network.isConnected()) {
                int type = network.getType();
                if (type == 1) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        asu = wifiInfo.getRssi();
                    }
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return asu + "dBm";
    }

    public static String getWifiSsid(Context context) {
        String ssid = "";

        try {
            NetworkInfo network = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (network != null && network.isAvailable() && network.isConnected()) {
                int type = network.getType();
                if (type == 1) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        ssid = wifiInfo.getSSID();
                        if (ssid == null) {
                            ssid = "";
                        }

                        ssid = ssid.replaceAll("\"", "");
                    }
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return ssid;
    }

    public static boolean checkSimState(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            if (context instanceof Activity && !ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, "android.permission.READ_PHONE_STATE")) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
            }
        } else if (tm.getSimState() == 1 || tm.getSimState() == 0) {
            return false;
        }

        return true;
    }

    public static String getImei(Context context) {
        String imei = "000000000000000";
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            if (context instanceof Activity && !ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, "android.permission.READ_PHONE_STATE")) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
            }
        } else if (mTelephonyMgr.getDeviceId() != null) {
            imei = mTelephonyMgr.getDeviceId();
        }

        return imei;
    }

    public static String getPhoneImsi(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            if (context instanceof Activity && !ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, "android.permission.READ_PHONE_STATE")) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
            }

            return null;
        } else {
            return mTelephonyMgr.getSubscriberId();
        }
    }

    public static String getWifiBssid(Context context) {
        WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        return wifiInfo.getBSSID();
    }

    public static String[] getNetInfo(Context context) {
        String[] result = new String[2];

        try {
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
                if (context instanceof Activity && !ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, "android.permission.READ_PHONE_STATE")) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
                }
            } else {
                String operator = mTelephonyManager.getNetworkOperator();
                if (operator != null && operator.length() > 3) {
                    String mcc = operator.substring(0, 3);
                    String mnc = operator.substring(3);
                    result[0] = mcc;
                    result[1] = mnc;
                }

                int lac = 0;
                int cellId = 0;
                int phoneType = mTelephonyManager.getPhoneType();
                if (phoneType == 1) {
                    GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
                    lac = location.getLac();
                    cellId = location.getCid();
                } else if (phoneType == 2) {
                    CdmaCellLocation location = (CdmaCellLocation) mTelephonyManager.getCellLocation();
                    lac = location.getNetworkId();
                    cellId = location.getBaseStationId();
                    cellId /= 16;
                }

                if (lac == 0 || cellId == 0) {
                    List<NeighboringCellInfo> infos = mTelephonyManager.getNeighboringCellInfo();
                    int lc = 0;
                    int ci = 0;
                    int rssi = 0;
                    Iterator var11 = infos.iterator();

                    while (true) {
                        NeighboringCellInfo cell;
                        do {
                            if (!var11.hasNext()) {
                                rssi = -113 + 2 * rssi;
                                return result;
                            }

                            cell = (NeighboringCellInfo) var11.next();
                        } while (lc != 0 && ci != 0);

                        lc = cell.getLac();
                        ci = cell.getCid();
                        rssi = cell.getRssi();
                    }
                }
            }
        } catch (Exception var13) {
            var13.printStackTrace();
        }

        return result;
    }

    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            return text.matches(regex);
        } else {
            return false;
        }
    }
}
