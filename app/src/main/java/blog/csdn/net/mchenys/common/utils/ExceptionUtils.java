package blog.csdn.net.mchenys.common.utils;

import android.content.Context;
import android.widget.Toast;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import blog.csdn.net.mchenys.AllOneApplication;


/**
 * Created by mChenys on 2017/12/27.
 */

public class ExceptionUtils {
    public static void exceptionHandler(Throwable error) {
        Context context = AllOneApplication.mAppContext;
        if (context == null) return;
        if (error == null) return;
        if (error instanceof JSONException) {
            //json异常
            Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show();
        } else if (error instanceof NullPointerException) {
            //获取数据失败数据为空
            Toast.makeText(context, "获取不到数据", Toast.LENGTH_SHORT).show();
        } else if (error instanceof UnknownHostException) {
            //网络异常
            Toast.makeText(context, "网络不给力,请检查网络设置", Toast.LENGTH_SHORT).show();
        } else if (error instanceof SocketException) {
            //网络异常
            Toast.makeText(context, "网络不给力,请检查网络设置", Toast.LENGTH_SHORT).show();
        } else if (error instanceof SocketTimeoutException) {
            //网络异常
            Toast.makeText(context, "网络不给力,请检查网络设置", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ConnectTimeoutException) {
            Toast.makeText(context, "网络不给力,请检查网络设置", Toast.LENGTH_SHORT).show();
        } else if(!StringUtils.isEmpty(error.getMessage())){
            //加载失败
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
