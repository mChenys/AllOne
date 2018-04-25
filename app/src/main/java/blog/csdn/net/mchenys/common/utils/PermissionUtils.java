package blog.csdn.net.mchenys.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blog.csdn.net.mchenys.R;


/**
 * 权限工具类
 * Created by mChenys on 2017/12/29.
 */
public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();
    //权限申请code
    public static final int CODE_RECORD_AUDIO = 0; //访问录音
    public static final int CODE_GET_ACCOUNTS = 1;//访问联系人
    public static final int CODE_READ_PHONE_STATE = 2;//获取手机状态
    public static final int CODE_CALL_PHONE = 3;//打电话
    public static final int CODE_CAMERA = 4;//访问照相机
    public static final int CODE_ACCESS_FINE_LOCATION = 5;//gps定位
    public static final int CODE_ACCESS_COARSE_LOCATION = 6;//网络定位
    public static final int CODE_READ_EXTERNAL_STORAGE = 7;//读取文件
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 8;//写入文件
    public static final int CODE_MUlTI_PERMISSION = 100;//批量权限

    //9组权限code对应的权限名称
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    //所有权限名称列表
    private static final String[] requestPermissions = {
            PERMISSION_RECORD_AUDIO,
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CALL_PHONE,
            PERMISSION_CAMERA,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE
    };
    //所有权限code
    private static final int[] allPermissionCode = new int[]{
            CODE_RECORD_AUDIO,
            CODE_GET_ACCOUNTS,
            CODE_READ_PHONE_STATE,
            CODE_CALL_PHONE,
            CODE_CAMERA,
            CODE_ACCESS_FINE_LOCATION,
            CODE_ACCESS_COARSE_LOCATION,
            CODE_READ_EXTERNAL_STORAGE,
            CODE_WRITE_EXTERNAL_STORAGE,
    };

    //权限请求结果回调
    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    /**
     * 单个权限申请
     *
     * @param activity
     * @param requestCode request code, e.g. if you need request CAMERA permission,parameters is PermissionUtils.CODE_CAMERA
     */
    public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }
        LogUtils.i(TAG, "requestPermission requestCode:" + requestCode);
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            LogUtils.w(TAG, "requestPermission illegal requestCode:" + requestCode);
            return;
        }

        final String requestPermission = requestPermissions[requestCode];

        //如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
        // 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
        // 你可以使用try{}catch(){},处理异常，也可以在这个地方，低于23就什么都不做，
        // 个人建议try{}catch(){}单独处理，提示用户开启权限。
//        if (Build.VERSION.SDK_INT < 23) {
//            return;
//        }

        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        } catch (RuntimeException e) {
            Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT).show();
            LogUtils.e(TAG, "RuntimeException:" + e.getMessage());
            return;
        }

        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            LogUtils.i(TAG, "ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED");

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                LogUtils.i(TAG, "requestPermission shouldShowRequestPermissionRationale");
                shouldShowRationale(activity, requestCode, requestPermission);

            } else {
                LogUtils.d(TAG, "requestCameraPermission else");
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }

        } else { //权限已打开
            LogUtils.d(TAG, "ActivityCompat.checkSelfPermission ==== PackageManager.PERMISSION_GRANTED");
            Toast.makeText(activity, "opened:" + requestPermissions[requestCode], Toast.LENGTH_SHORT).show();
            permissionGrant.onPermissionGranted(requestCode);
        }
    }

    /**
     * 一次申请所有权限
     *
     * @param activity
     * @param grant
     */
    public static void requestAllPermissions(final Activity activity, PermissionGrant grant) {
        requestMultiPermission(activity, null, grant);

    }

    /**
     * 一次申请多个权限
     *
     * @param activity
     * @param requestCode null表示申请所有权限
     * @param grant
     * @see PermissionUtils#CODE_RECORD_AUDIO
     * @see PermissionUtils#CODE_GET_ACCOUNTS
     * @see PermissionUtils#CODE_READ_PHONE_STATE
     * @see PermissionUtils#CODE_CALL_PHONE
     * @see PermissionUtils#CODE_CAMERA
     * @see PermissionUtils#CODE_ACCESS_FINE_LOCATION
     * @see PermissionUtils#CODE_ACCESS_COARSE_LOCATION
     * @see PermissionUtils#CODE_READ_EXTERNAL_STORAGE
     * @see PermissionUtils#CODE_WRITE_EXTERNAL_STORAGE
     */
    public static void requestMultiPermission(final Activity activity, int[] requestCode, PermissionGrant grant) {
        requestMultiPermission(activity, requestCode, CODE_MUlTI_PERMISSION, grant);
    }

    /**
     * 一次申请多个权限
     *
     * @param activity
     * @param requestCode
     * @param groupCode   标记该多组权限申请的请求code
     * @param grant
     * @see PermissionUtils#CODE_RECORD_AUDIO
     * @see PermissionUtils#CODE_GET_ACCOUNTS
     * @see PermissionUtils#CODE_READ_PHONE_STATE
     * @see PermissionUtils#CODE_CALL_PHONE
     * @see PermissionUtils#CODE_CAMERA
     * @see PermissionUtils#CODE_ACCESS_FINE_LOCATION
     * @see PermissionUtils#CODE_ACCESS_COARSE_LOCATION
     * @see PermissionUtils#CODE_READ_EXTERNAL_STORAGE
     * @see PermissionUtils#CODE_WRITE_EXTERNAL_STORAGE
     */
    public static void requestMultiPermission(final Activity activity, int[] requestCode, int groupCode, PermissionGrant grant) {
        //获取没有申请的权限
        List<String> permissionsList = null;
        //获取没有申请且需要提示建议信息的权限
        List<String> shouldRationalePermissionsList = null;
        if (null == requestCode) { //申请所有权限
            permissionsList = getAllNoGrantedPermission(activity, false);
            shouldRationalePermissionsList = getAllNoGrantedPermission(activity, true);
        } else {
            permissionsList = getNoGrantedPermission(activity, requestCode, false);
            shouldRationalePermissionsList = getNoGrantedPermission(activity, requestCode, true);
        }
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        if (permissionsList.size() > 0) {
            //批量申请权限
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), groupCode);
        } else if (shouldRationalePermissionsList.size() > 0) {
            //提示建议
            shouldShowRationale(activity,groupCode, shouldRationalePermissionsList);
        } else { //已有权限
            grant.onPermissionGranted(groupCode);
        }
    }

    /**
     * 多个权限申请后结果处理
     *  @param activity
     * @param permissions
     * @param grantResults
     * @param requestCode
     * @param permissionGrant
     */
    private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, int requestCode, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }
        //TODO
        LogUtils.d(TAG, "onRequestPermissionsResult permissions length:" + permissions.length);
        Map<String, Integer> perms = new HashMap<>();

        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            LogUtils.d(TAG, "permissions: [i]:" + i + ", permissions[i]" + permissions[i] + ",grantResults[i]:" + grantResults[i]);
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }

        if (notGranted.size() == 0) {
            permissionGrant.onPermissionGranted(requestCode);
        } else {
            //部分权限未申请成功,跳去权限设置
            openSettingActivity(activity, "检测到一些权限申请被拒绝了,请到设置页面进行权限管理");
        }

    }

    /**
     * 提示开启这些权限
     *
     * @param activity
     * @param groupCode
     * @param shouldRationalePermissionsList
     */
    private static void shouldShowRationale(final Activity activity, final int groupCode, final List<String> shouldRationalePermissionsList) {
        showMessageOKCancel(activity, "为保证程序的正常运行,请允许相关权限申请",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //用户确认后再申请
                        ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(
                                new String[shouldRationalePermissionsList.size()]),
                                groupCode);
                        LogUtils.d(TAG, "showMessageOKCancel requestPermissions");
                    }
                });
    }

    /**
     * 提示为什么要申请该权限
     *
     * @param activity
     * @param requestCode
     * @param requestPermission
     */
    private static void shouldShowRationale(final Activity activity, final int requestCode, final String requestPermission) {
        //TODO
        String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
        showMessageOKCancel(activity, permissionsHint[requestCode], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{requestPermission},
                        requestCode);
                LogUtils.d(TAG, "showMessageOKCancel requestPermissions:" + requestPermission);
            }
        });
    }

    /**
     * 显示提示信息
     *
     * @param context
     * @param message
     * @param okListener
     */
    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .create()
                .show();

    }



    /**
     * 权限返回结果处理
     *
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }
        LogUtils.d(TAG, "requestPermissionsResult requestCode:" + requestCode);

        if (requestCode >= CODE_MUlTI_PERMISSION) {
            //多个权限申请结果处理
            requestMultiResult(activity, permissions, grantResults,requestCode, permissionGrant);
            return;
        }

        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            LogUtils.w(TAG, "requestPermissionsResult illegal requestCode:" + requestCode);
            Toast.makeText(activity, "illegal requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }

        LogUtils.i(TAG, "onRequestPermissionsResult requestCode:" + requestCode + ",permissions:" + permissions.toString()
                + ",grantResults:" + grantResults.toString() + ",length:" + grantResults.length);

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //单个权限申请成功
            LogUtils.i(TAG, "onRequestPermissionsResult PERMISSION_GRANTED");
            permissionGrant.onPermissionGranted(requestCode);

        } else {
            //单个权限申请失败 打开权限设置列表
            LogUtils.i(TAG, "onRequestPermissionsResult PERMISSION NOT GRANTED");
            String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
            openSettingActivity(activity, permissionsHint[requestCode]);
        }

    }

    /**
     * 打开权限设置列表
     *
     * @param activity
     * @param message
     */
    private static void openSettingActivity(final Activity activity, String message) {
        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                LogUtils.d(TAG, "getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }

    /**
     * 获取所有没有申请的权限
     *
     * @param activity
     * @param isShouldRationale true:返回没有申请的且需要显示建议的权限,false:返回没有申请的且不需要显示建议的权限
     * @return
     */
    public static ArrayList<String> getAllNoGrantedPermission(Activity activity, boolean isShouldRationale) {
        return getNoGrantedPermission(activity, allPermissionCode, isShouldRationale);
    }

    /**
     * 根据请求code查询那些权限没有申请
     *
     * @param activity
     * @param requestCode
     * @param isShouldRationale true:返回没有申请的且需要显示建议的权限,false:返回没有申请的且不需要显示建议的权限
     * @return
     */
    private static ArrayList<String> getNoGrantedPermission(Activity activity, int[] requestCode, boolean isShouldRationale) {
        ArrayList<String> permissions = new ArrayList<>();

        for (int i = 0; i < requestCode.length; i++) {
            int code = requestCode[i];
            String requestPermission = requestPermissions[code];
            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open those permission", Toast.LENGTH_SHORT).show();
                LogUtils.e(TAG, "RuntimeException:" + e.getMessage());
                return null;
            }

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                LogUtils.i(TAG, "getNoGrantedPermission ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED:" + requestPermission);

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    //需要显示建议
                    LogUtils.d(TAG, "shouldShowRequestPermissionRationale if");
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }

                } else {
                    //不需要显示建议
                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                    LogUtils.d(TAG, "shouldShowRequestPermissionRationale else");
                }

            }
        }

        return permissions;
    }


}