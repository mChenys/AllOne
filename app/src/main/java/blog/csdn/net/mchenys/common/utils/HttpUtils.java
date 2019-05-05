package blog.csdn.net.mchenys.common.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import blog.csdn.net.mchenys.common.okhttp2.x.HttpManager;
import blog.csdn.net.mchenys.common.okhttp2.x.listener.RequestCallBack;
import blog.csdn.net.mchenys.common.okhttp2.x.listener.RequestCallBackHandler;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;


/**
 * 网络请求工具类
 * Created by mChenys on 2017/12/27.
 */
public class HttpUtils {

    public interface JSONCallback {
        void onFailure(Exception e);

        void onSuccess(JSONObject jsonObject, OkResponse okResponse);
    }

    public interface BitmapCallback {
        void onSuccess(Bitmap bitmap);

        void onFailure(Exception e);
    }

    public static void getJSON(boolean forceNetwork, final String url, Map<String, String> headersMap, Map<String, String> bodyMap,
                               final JSONCallback handler) {
        HttpManager.getInstance().asyncRequest(url, new MyRequestCallBackHandler(handler), forceNetwork ? HttpManager.RequestType.FORCE_NETWORK :
                        HttpManager.RequestType.CACHE_FIRST,
                HttpManager.RequestMode.GET, "", headersMap, bodyMap);
    }

    public static OkResponse getResponse(boolean forceNetwork, String url, Map<String, String> headersMap, Map<String, String> bodyMap) {
        try {
            return HttpManager.getInstance().syncRequest(url, forceNetwork ? HttpManager.RequestType.FORCE_NETWORK :
                    HttpManager.RequestType.CACHE_FIRST, HttpManager.RequestMode.GET, "", headersMap, bodyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void postJSON(String url, Map<String, String> headersMap, Map<String, String> bodyMap, final JSONCallback handler) {
        HttpManager.getInstance().asyncRequest(url, new MyRequestCallBackHandler(handler), HttpManager.RequestType.FORCE_NETWORK,
                HttpManager.RequestMode.POST, "", headersMap, bodyMap);
    }

    public static void post(String url, Map<String, String> headersMap, Map<String, String> bodyMap, RequestCallBack handler) {
        HttpManager.getInstance().asyncRequest(url, handler, HttpManager.RequestType.FORCE_NETWORK,
                HttpManager.RequestMode.POST, "", headersMap, bodyMap);
    }

    public static void getBitmap(final String url, final int targetWidth, final int targetHeight, final BitmapCallback callback) {
        final ImageCacheUtils cacheUtils = ImageCacheUtils.getInstance();
        Bitmap bitmap = cacheUtils.loadBitmapFromMemoryCache(url);
        if (null != bitmap && null != callback) {
            callback.onSuccess(bitmap);
            return;
        }
        bitmap = cacheUtils.loadBitmapFromDiskCache(url, targetWidth, targetHeight);
        if (null != bitmap && null != callback) {
            callback.onSuccess(bitmap);
            return;
        }

        HttpManager.getInstance().asyncRequestForInputStream(url, new RequestCallBackHandler() {
            @Override
            public void onFailure(Exception e) {
                if (null != callback) {
                    callback.onFailure(e);
                }
            }

            @Override
            public Object doInBackground(OkResponse okResponse) {
                Bitmap bitmap = cacheUtils.loadBitmapFromHttp(url, okResponse.getInputStream(), targetWidth, targetHeight);
                if (null == bitmap && !cacheUtils.isDiskLruCacheCreated()) {
                    bitmap = BitmapUtils.decodeSampleBitmapFromStream2(okResponse.getInputStream(), targetWidth, targetHeight);
                    if (null != bitmap) {
                        cacheUtils.addBitmapToMemoryCache(url, bitmap);
                    }
                }
                return bitmap;
            }

            @Override
            public void onResponse(Object o, OkResponse okResponse) {
                Bitmap bitmap = (Bitmap) o;
                if (null != callback) {
                    if (null != bitmap) {
                        callback.onSuccess((Bitmap) o);
                    } else {
                        callback.onFailure(new NullPointerException("bitmap get fail"));
                    }
                }
            }
        }, HttpManager.RequestMode.GET, url, null, null);
    }

    /**
     * 返回结果JSON处理
     */
    public static class MyRequestCallBackHandler extends RequestCallBackHandler {

        private JSONCallback callback;

        public MyRequestCallBackHandler(JSONCallback handler) {
            this.callback = handler;
        }

        @Override
        public void onFailure(Exception e) {
            if (null != callback) {
                callback.onFailure(e);
            }
        }

        @Override
        public Object doInBackground(OkResponse okResponse) {
            if (null != okResponse && !TextUtils.isEmpty(okResponse.getResult())) {
                try {
                    return new JSONObject(okResponse.getResult());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void onResponse(Object o, OkResponse okResponse) {
            int code = okResponse.getCode();
            if (o == null || code != 200) {
                onFailure(new Exception(okResponse.getResult()));
            } else if (null != callback) {
                callback.onSuccess((JSONObject) o, okResponse);
            }
        }
    }
}
