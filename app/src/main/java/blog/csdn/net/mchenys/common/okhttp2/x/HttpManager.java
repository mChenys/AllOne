package blog.csdn.net.mchenys.common.okhttp2.x;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import blog.csdn.net.mchenys.common.okhttp2.x.listener.ProgressCallBack;
import blog.csdn.net.mchenys.common.okhttp2.x.listener.RequestCallBack;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.okhttp2.x.model.ProgressRequestBody;
import blog.csdn.net.mchenys.common.okhttp2.x.model.ProgressResponseBody;

/**
 * Created by mChenys on 2016/9/9.
 */
public class HttpManager {

    private static final boolean DEBUG = true;
    private static final String TAG = "HttpManager";
    private static final long CACHE_SIZE = 10485760L; //10M
    private static final long CACHE_TIME = 31536000L;//1year
    private static final long CONNECT_TIMEOUT = 10L;
    private static final long READ_TIMEOUT = 20L;
    private static final long WRITE_TIMEOUT = 20L;
    public static int RESPONSE_TYPE_CACHE = 1;//from cache
    public static int RESPONSE_TYPE_NETWORK = 2; //from network
    private static Context mContext;
    private final File cacheDir;
    private static Cache cache;
    private final Map<String, String> partHeaders;
    private long cacheTime;
    private static OkHttpClient client;
    private static HttpManager mHttpManager = new HttpManager((File) null, (Cache) null, (Map) null, CONNECT_TIMEOUT, READ_TIMEOUT, WRITE_TIMEOUT, CACHE_TIME);
    private List<String> urls;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FILE = MediaType.parse("multipart/form-data; charset=utf-8");
    private static final MediaType IMAGE_JPG = MediaType.parse("image/pjpeg");
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    private HttpManager(File cacheDir, Cache cache, Map<String, String> partHeaders, long connectTimeout,
                        long readTimeout, long writeTimeout, long cacheTime) {
        if (mContext != null) {
            SessionManager.init(mContext);
            urls = new ArrayList();
        }
        this.cacheDir = cacheDir;
        this.cache = cache;
        this.partHeaders = partHeaders;
        this.cacheTime = cacheTime;
        this.client = new OkHttpClient();
        client.setConnectTimeout(connectTimeout, TimeUnit.SECONDS);
        client.setReadTimeout(readTimeout, TimeUnit.SECONDS);
        client.setWriteTimeout(writeTimeout, TimeUnit.SECONDS);
        client.setRetryOnConnectionFailure(true);
        if (cache != null) {
            client.setCache(cache);
        }
        this.setHostnameVerifier();
    }

    public static HttpManager getInstance() {
        if (cache != null && cache.isClosed()) {
            try {
                cache.initialize();
            } catch (IOException var1) {
                var1.printStackTrace();
            }
        }
        return mHttpManager;
    }

    //支持https
    private void setHostnameVerifier() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");

            try {
                sc.init((KeyManager[]) null, new TrustManager[]{new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }}, new SecureRandom());
            } catch (KeyManagementException var3) {
                var3.printStackTrace();
            }

            client.setSslSocketFactory(sc.getSocketFactory());
            client.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    /**
     * 异步上传文件
     *
     * @param name       <input>标签的属性name
     * @param filename   <input>标签的属性filename
     * @param url
     * @param callback
     * @param file
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostFile(String name, String filename, String url, RequestCallBack callback, File file, String tag,
                              Map<String, String> headersMap, Map<String, String> bodyMap) {

        asyncPostContent(name, filename, url, callback,
                RequestBody.create(getMediaType(RequestMediaType.FILE), file), tag, headersMap, bodyMap);
    }

    /**
     * 异步上传图片
     *
     * @param name
     * @param filename
     * @param url
     * @param callback
     * @param file
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostImage(String name, String filename, String url, RequestCallBack callback, File file, String tag,
                               Map<String, String> headersMap, Map<String, String> bodyMap) {

        asyncPostContent(name, filename, url, callback,
                RequestBody.create(getMediaType(RequestMediaType.IMAGE_JPG), file), tag, headersMap, bodyMap);
    }

    /**
     * 异步提交json数据
     *
     * @param url
     * @param callback
     * @param json
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostJson(String url, RequestCallBack callback, String json, String tag,
                              Map<String, String> headersMap, Map<String, String> bodyMap) {

        asyncPostContent(null, null, url, callback,
                RequestBody.create(getMediaType(RequestMediaType.JSON), json), tag, headersMap, bodyMap);
    }

    /**
     * 异步提交String
     *
     * @param url
     * @param callback
     * @param string
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostString(String url, RequestCallBack callback, String string, String tag,
                                Map<String, String> headersMap, Map<String, String> bodyMap) {

        asyncPostContent(null, null, url, callback,
                RequestBody.create(getMediaType(RequestMediaType.MEDIA_TYPE_MARKDOWN), string), tag, headersMap, bodyMap);
    }

    /**
     * 异步提交byte数据
     *
     * @param name
     * @param filename
     * @param url
     * @param callback
     * @param requestMediaType
     * @param bytes
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostByte(String name, String filename, String url, RequestCallBack callback, RequestMediaType requestMediaType,
                              byte[] bytes, String tag, Map<String, String> headersMap, Map<String, String> bodyMap) {

        asyncPostContent(name, filename, url, callback, RequestBody.create(this.getMediaType(requestMediaType), bytes), tag, headersMap, bodyMap);
    }

    /**
     * 异步提交内容
     *
     * @param name
     * @param filename
     * @param url
     * @param callback
     * @param fromBody
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostContent(String name, String filename, String url, final RequestCallBack callback,
                                 RequestBody fromBody, String tag, Map<String, String> headersMap, Map<String, String> bodyMap) {
        if (DEBUG) {
            Log.d(TAG, "asyncPostContent START " + url);
        }
        if (TextUtils.isEmpty(url)) {
            if (callback != null) {
                callback.onReceiveFailure(new NullPointerException("url is empty"));
            }
        } else {
            Request.Builder requestBuilder = initRequestBuilder(RequestMode.POST, url, tag, headersMap, bodyMap);
            RequestBody requestBody = fromBody;
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(filename)) {
                //构建复杂的请求体,与HTML文件上传形式兼容
                requestBody = new MultipartBuilder().
                        type(MultipartBuilder.FORM).
                        addPart(Headers.of(new String[]{"Content-Disposition", "form-data; name=\"" + name +
                                "\"; filename=\"" + filename + "\""}), fromBody).
                        addPart(requestBuilder.build().body()). //附加参数
                        build();
            }
            requestBuilder.post(requestBody);

            try {
                enqueueForceNetwork(url, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        if (DEBUG) {
                            Log.d(TAG, "asyncPostContent fail " + e);
                        }
                        if (callback != null) {
                            callback.onReceiveFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (DEBUG) {
                            Log.d(TAG, "asyncPostContent success " + response);
                        }
                        if (callback != null) {
                            callback.onReceiveResponse(wrapResponse(response));
                        }
                    }
                }, requestBuilder);
            } catch (IOException e) {
                if (callback != null) {
                    callback.onReceiveFailure(e);
                }
            }
        }

    }

    /**
     * 异步上传文件带进度
     *
     * @param name       <input>标签的属性name
     * @param filename   <input>标签的属性filename
     * @param url
     * @param callback
     * @param file
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostFileWithProgress(String name, String filename, String url, ProgressCallBack callback, File file, String tag,
                                          Map<String, String> headersMap, Map<String, String> bodyMap) {

        asyncPostContentWithProgress(name, filename, url, callback,
                RequestBody.create(getMediaType(RequestMediaType.FILE), file), tag, headersMap, bodyMap);
    }

    /**
     * 异步上传图片,带进度
     *
     * @param name
     * @param filename
     * @param url
     * @param callback
     * @param file
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostImageWithProgress(String name, String filename, String url, ProgressCallBack callback, File file, String tag,
                                           Map<String, String> headersMap, Map<String, String> bodyMap) {

        asyncPostContentWithProgress(name, filename, url, callback,
                RequestBody.create(getMediaType(RequestMediaType.IMAGE_JPG), file), tag, headersMap, bodyMap);
    }

    /**
     * 异步提交json数据,带进度
     *
     * @param url
     * @param callback
     * @param json
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostJsonWithProgress(String url, ProgressCallBack callback, String json, String tag,
                                          Map<String, String> headersMap, Map<String, String> bodyMap) {

        asyncPostContentWithProgress(null, null, url, callback,
                RequestBody.create(getMediaType(RequestMediaType.JSON), json), tag, headersMap, bodyMap);
    }

    /**
     * 异步提交String,带进度
     *
     * @param url
     * @param callback
     * @param string
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostStringWithProgress(String url, ProgressCallBack callback, String string, String tag,
                                            Map<String, String> headersMap, Map<String, String> bodyMap) {

        asyncPostContentWithProgress(null, null, url, callback,
                RequestBody.create(getMediaType(RequestMediaType.MEDIA_TYPE_MARKDOWN), string), tag, headersMap, bodyMap);
    }

    /**
     * 异步上传内容,可监听上传进度
     *
     * @param name
     * @param filename
     * @param url
     * @param callback
     * @param fromBody
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncPostContentWithProgress(String name, String filename, String url, final ProgressCallBack callback,
                                             RequestBody fromBody, String tag, Map<String, String> headersMap, Map<String, String> bodyMap) {
        if (DEBUG) {
            Log.d(TAG, "asyncPostContentWithProgress START " + url);
        }
        if (TextUtils.isEmpty(url)) {
            if (callback != null) {
                callback.onReceiveFailure(new NullPointerException("url is empty"));
            }
        } else {
            Request.Builder requestBuilder = initRequestBuilder(RequestMode.POST, url, tag, headersMap, bodyMap);
            RequestBody requestBody = fromBody;
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(filename)) {
                //构建复杂的请求体,与HTML文件上传形式兼容
                requestBody = new MultipartBuilder().
                        type(MultipartBuilder.FORM).
                        addPart(Headers.of(new String[]{"Content-Disposition", "form-data; name=\"" + name + "\"; filename=\"" + filename + "\""}), fromBody).
                        addPart(requestBuilder.build().body()). //附加参数
                        build();
            }
            //监听进度
            ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody);
            progressRequestBody.setProgressListener(new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    if (DEBUG) {
                        Log.d(TAG, "asyncPostContentWithProgress onRequestProgress " + bytesWritten + "/" + contentLength);
                    }
                    if (callback != null) {
                        callback.onReceiveProgress(bytesWritten, contentLength, done);
                    }
                }
            });
            //提交最终的body
            requestBuilder.post(progressRequestBody);
            try {

                enqueueForceNetwork(url, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        if (DEBUG) {
                            Log.d(TAG, "asyncPostContentWithProgress fail " + e);
                        }
                        if (callback != null) {
                            callback.onReceiveFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (DEBUG) {
                            Log.d(TAG, "asyncPostContentWithProgress success " + response);
                        }
                        if (callback != null) {
                            callback.onReceiveResponse(wrapResponse(response));
                        }
                    }
                }, requestBuilder);
            } catch (IOException e) {
                if (callback != null) {
                    callback.onReceiveFailure(e);
                }
            }
        }
    }

    /**
     * 异步下载内容,带进度
     *
     * @param url
     * @param callBack
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncDownloadContentWithProgress(String url, final ProgressCallBack callBack, RequestMode requestMode, String tag,
                                                 Map<String, String> headersMap, Map<String, String> bodyMap) {
        if (DEBUG) {
            Log.d(TAG, "asyncDownloadContentWithProgress START " + url);
        }
        if (TextUtils.isEmpty(url)) {
            if (callBack != null) {
                callBack.onReceiveFailure(new NullPointerException("url is empty"));
            }

        } else {
            //构建请求
            Request request = initRequestBuilder(requestMode, url, tag, headersMap, bodyMap).
                    cacheControl(CacheControl.FORCE_NETWORK).
                    build();
            //克隆
            OkHttpClient clone = client.clone();
            //增加拦截器
            clone.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    //拦截
                    Response originalResponse = chain.proceed(chain.request());
                    ProgressResponseBody progressResponseBody = new ProgressResponseBody(originalResponse.body());
                    progressResponseBody.setProgressListener(new ProgressResponseBody.ProgressResponseListener() {
                        @Override
                        public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
                            if (DEBUG) {
                                Log.d(TAG, "asyncDownloadContentWithProgress onResponseProgress " + bytesRead + "/" + contentLength);
                            }
                            if (null != callBack) {
                                callBack.onReceiveProgress(bytesRead, contentLength, done);
                            }
                        }
                    });
                    //包装响应体并返回
                    return originalResponse.newBuilder().body(progressResponseBody).build();
                }
            });

            try {

                clone.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        if (DEBUG) {
                            Log.d(TAG, "asyncDownloadContentWithProgress fail " + e);
                        }
                        if (callBack != null) {
                            callBack.onReceiveFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (DEBUG) {
                            Log.d(TAG, "asyncDownloadContentWithProgress success " + response);
                        }
                        if (callBack != null) {
                            callBack.onReceiveResponse(new OkResponse("", response.headers().toMultimap(),
                                    response.code(), response.body().byteStream()));
                        }
                    }
                });
            } catch (Exception e) {
                if (callBack != null) {
                    callBack.onReceiveFailure(e);
                }
            }
        }
    }

    /**
     * 异步流请求
     *
     * @param url
     * @param callBack
     * @param requestMode
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncRequestForInputStream(String url, final RequestCallBack callBack, RequestMode requestMode,
                                           String tag, Map<String, String> headersMap, Map<String, String> bodyMap) {
        if (DEBUG) {
            Log.d(TAG, "asyncRequestForInputStream START " + url);
        }
        if (TextUtils.isEmpty(url)) {
            if (callBack != null) {
                callBack.onReceiveFailure(new NullPointerException("url is empty"));
            }
        } else {
            Request.Builder requestBuilder = initRequestBuilder(requestMode, url, tag, headersMap, bodyMap);
            if (requestBuilder != null && requestBuilder.build() != null && requestBuilder.build().url() != null) {
                url = requestBuilder.build().url().toString();
            }
            try {
                enqueueForceNetwork(url, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        if (DEBUG) {
                            Log.d(TAG, "asyncRequestForInputStream fail " + e);
                        }
                        if (callBack != null) {
                            callBack.onReceiveFailure(e);
                        }
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (DEBUG) {
                            Log.d(TAG, "asyncRequestForInputStream success " + response);
                        }
                        if (callBack != null) {
                            callBack.onReceiveResponse(new OkResponse("", response.headers().toMultimap(),
                                    response.code(), response.body().byteStream()));
                        }
                    }
                }, requestBuilder);
            } catch (IOException e) {
                if (callBack != null) {
                    callBack.onReceiveFailure(e);
                }
            }
        }
    }

    /**
     * 异步请求
     *
     * @param url
     * @param callback
     * @param requestType
     * @param requestMode
     * @param tag
     * @param headersMap
     * @param bodyMap
     */
    public void asyncRequest(String url, final RequestCallBack callback, RequestType requestType, RequestMode requestMode,
                             String tag, Map<String, String> headersMap, Map<String, String> bodyMap) {
        if (TextUtils.isEmpty(url)) {
            callback.onReceiveFailure(new NullPointerException("url is empty"));
        } else {
            Request.Builder requestBuilder = initRequestBuilder(requestMode, url, tag, headersMap, bodyMap);
            if (requestBuilder != null && requestBuilder.build() != null && requestBuilder.build().url() != null) {
                url = requestBuilder.build().url().toString();
            }
            Callback rawCallBack = new Callback() {
                OkResponse cacheResponse;

                @Override
                public void onFailure(Request request, IOException e) {
                    if (DEBUG) {
                        Log.d(TAG, "syncRequest onFailure: " + e.getMessage());
                    }
                    if (callback != null) {
                        callback.onReceiveFailure(e);
                    }
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response != null && response.body() != null) {
                        OkResponse okResponse = wrapResponse(response);
                        if (DEBUG) {
                            Log.d(TAG, "asyncRequest onResponse:type :" + okResponse.getResponseType() + " result: " +
                                    okResponse.toString());
                        }
                        if (okResponse.getResponseType() == RESPONSE_TYPE_CACHE) {
                            if (DEBUG) {
                                Log.d(TAG, "asyncRequest onResponse: cache response");
                            }
                            this.cacheResponse = okResponse;
                        } else if (okResponse.getResponseType() == RESPONSE_TYPE_NETWORK) {
                            if (DEBUG) {
                                Log.d(TAG, "asyncRequest onResponse: network response");
                            }
                            if (equalsResult(cacheResponse, okResponse)) {
                                if (DEBUG) {
                                    Log.d(TAG, "asyncRequest onResponse: network response is same to cache,result will not callback");
                                }
                                return;
                            }
                        }
                        callback.onReceiveResponse(okResponse);
                    } else if (callback != null) {
                        callback.onReceiveFailure(new NullPointerException("asyncRequest Response is null"));
                    }

                }
            };
            if (DEBUG) {
                Log.d(TAG, "asyncRequest START: request: " + requestBuilder.build().toString());
                Log.d(TAG, "asyncRequest START: type: " + requestType);
            }
            try {
                switch (requestType) {
                    case CACHE_FIRST:
                        enqueueNetworkCacheFirst(url, rawCallBack, requestBuilder);
                        break;
                    case NETWORK_FIRST:
                        enqueueNetworkFirst(url, rawCallBack, requestBuilder);
                        break;
                    case FORCE_CACHE:
                        enqueueForceCache(url, rawCallBack, requestBuilder);
                        break;
                    case FORCE_NETWORK:
                        enqueueForceNetwork(url, rawCallBack, requestBuilder);
                        break;
                    case CACHE_FIRST_AND_FORCE_NETWORK:
                        enqueueNetworkCacheFirstAndForceNetwork(url, rawCallBack, requestBuilder);
                        break;
                }
            } catch (IOException e) {
                if (callback != null) {
                    callback.onReceiveFailure(e);
                }
            }

        }
    }

    /**
     * 缓存优先
     * 有缓存且请求过,则直接从缓存获取;
     * 有缓存且没有请求过(启用session策略)或者有缓存且接口响应noCache(没有启动session策略),先从缓存获取,无论获取成功与否,
     * 都会再请求网络,如果数据发生变化则再次回调结果
     * 没有缓存,则直接请求网络
     *
     * @param url
     * @param callback
     * @param requestBuilder
     */
    private void enqueueNetworkCacheFirst(final String url, final Callback callback, final Request.Builder requestBuilder) throws IOException {
        if (DEBUG) {
            Log.d(TAG, "enqueueNetworkCacheFirst: START " + url);
        }
        if (this.hasCache(url)) {
            if (this.hasRequested(url)) {
                if (DEBUG) {
                    Log.d(TAG, "enqueueNetworkCacheFirst: hasCache and  hasRequested " + url);
                }

                this.enqueueForceCache(url, callback, requestBuilder);
            } else {
                if (DEBUG) {
                    Log.d(TAG, "enqueueNetworkCacheFirst: hasCache and  noRequested " + url);
                }

                this.enqueueForceCache(url, new Callback() {
                    public void onFailure(Request request, IOException e) {
                        if (DEBUG) {
                            Log.d(TAG, "enqueueNetworkCacheFirst: on load cache fail " + e.getMessage());
                        }

                        try {
                            executeNetworkCompareCache(url, callback, requestBuilder);
                        } catch (IOException var4) {
                        }

                    }

                    public void onResponse(Response response) throws IOException {
                        if (DEBUG) {
                            Log.d(TAG, "enqueueNetworkCacheFirst: onResponse cache suc ");
                        }

                        try {
                            callback.onResponse(response);
                        } catch (Exception var3) {
                            var3.printStackTrace();
                        }

                        executeNetworkCompareCache(url, callback, requestBuilder);
                    }
                }, null);
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "enqueueNetworkCacheFirst: noCache and  enqueueNetworkFirst " + url);
            }

            this.enqueueNetworkFirst(url, callback, requestBuilder);
        }

    }

    /**
     * 缓存优先和强制网络
     * 有缓存由请求过,直接返回缓存
     * 有缓存没请求过,先返回缓存,无论成功失败都强制网络一次
     * 没缓存直接网络优先策略
     *
     * @param url
     * @param callback
     * @param requestBuilder
     * @throws IOException
     */
    private void enqueueNetworkCacheFirstAndForceNetwork(final String url, final Callback callback, final com.squareup.okhttp.Request.Builder requestBuilder) throws IOException {
        if (DEBUG) {
            Log.d(TAG, "enqueueNetworkCacheFirstAndForceNetwork: START " + url);
        }

        if (this.hasCache(url)) {
            if (this.hasRequested(url)) {
                if (DEBUG) {
                    Log.d(TAG, "enqueueNetworkCacheFirstAndForceNetwork: hasCache and  hasRequested " + url);
                }

                this.enqueueForceCache(url, callback, requestBuilder);
            } else {
                if (DEBUG) {
                    Log.d(TAG, "enqueueNetworkCacheFirstAndForceNetwork: hasCache and  noRequested " + url);
                }

                this.enqueueForceCache(url, new Callback() {
                    public void onFailure(Request request, IOException e) {
                        if (DEBUG) {
                            Log.d(TAG, "enqueueNetworkCacheFirstAndForceNetwork: on load cache fail " + e.getMessage());
                        }

                        try {
                            enqueueForceNetwork(url, callback, requestBuilder);
                        } catch (IOException var4) {
                        }

                    }

                    public void onResponse(Response response) throws IOException {
                        if (DEBUG) {
                            Log.d(TAG, "enqueueNetworkCacheFirstAndForceNetwork: on load cache suc ");
                        }

                        try {
                            callback.onResponse(response);
                        } catch (Exception var3) {
                            var3.printStackTrace();
                        }

                        enqueueForceNetwork(url, callback, requestBuilder);
                    }
                }, null);
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "enqueueNetworkCacheFirstAndForceNetwork: noCache and  enqueueNetwork " + url);
            }

            this.enqueueNetworkFirst(url, callback, requestBuilder);
        }

    }

    /**
     * 强制网络
     *
     * @param url
     * @param rawCallBack
     * @param requestBuilder
     */
    private void enqueueForceNetwork(final String url, final Callback rawCallBack, Request.Builder requestBuilder) throws IOException {
        if (DEBUG) {
            Log.d(TAG, "enqueueForceNetwork:  START " + url);
        }
        if (requestBuilder == null) {
            requestBuilder = new Request.Builder();
            requestBuilder.url(url);
        }
        requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (DEBUG) {
                    Log.d(TAG, "enqueueForceNetwork:  fail " + url);
                }
                rawCallBack.onFailure(request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    addRequest(url);
                }
                if (DEBUG) {
                    Log.d(TAG, "enqueueForceNetwork:  success " + url);
                }
                rawCallBack.onResponse(response);
            }
        });
    }

    /**
     * 强制缓存
     *
     * @param url
     * @param rawCallBack
     * @param requestBuilder
     */
    private void enqueueForceCache(final String url, final Callback rawCallBack, Request.Builder requestBuilder) {
        if (DEBUG) {
            Log.d(TAG, "enqueueForceCache:  START " + url);
        }
        if (requestBuilder == null) {
            requestBuilder = new Request.Builder();
            requestBuilder.url(url);
        }
        requestBuilder.cacheControl(CacheControl.FORCE_CACHE);
        final Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (DEBUG) {
                    Log.d(TAG, "enqueueForceCache:  fail " + url);
                }
                rawCallBack.onFailure(request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (DEBUG) {
                    Log.d(TAG, "enqueueForceCache:  success " + url);
                }
                if (response.isSuccessful()) {
                    addRequest(url);
                }
                rawCallBack.onResponse(response);
            }
        });
    }

    /**
     * 网络优先
     * 优先请求网络,如果成功则回调,如果失败则从缓存获取,成功和失败都回调
     *
     * @param url
     * @param rawCallBack
     * @param requestBuilder
     */
    private void enqueueNetworkFirst(final String url, final Callback rawCallBack, Request.Builder requestBuilder) {
        if (DEBUG) {
            Log.d(TAG, "enqueueNetworkFirst: START" + url);
        }
        if (requestBuilder == null) {
            requestBuilder = new Request.Builder();
            requestBuilder.url(url);
        }
        final Request.Builder finalRequestBuilder = requestBuilder;
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //rawCallBack.onFailure(request, e);
                if (DEBUG) {
                    Log.d(TAG, "enqueueNetworkFirst: fail will try to executeForceCache " + url);
                }
                try {
                    Response response = executeForceCache(url, finalRequestBuilder);
                    if (response.isSuccessful()) {
                        rawCallBack.onResponse(response);
                        if (DEBUG) {
                            Log.d(TAG, "enqueueNetworkFirst: fail but executeForceCache success " + url);
                        }
                    } else {
                        rawCallBack.onFailure(request, e);
                        if (DEBUG) {
                            Log.d(TAG, "enqueueNetworkFirst: fail executeForceCache fail " + url);
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (DEBUG) {
                        Log.d(TAG, "enqueueNetworkFirst: success " + url);
                    }
                    addRequest(url);
                }
                rawCallBack.onResponse(response);
            }
        });
    }


    /**
     * 执行默认的网络请求策略,返回网络结果
     *
     * @param url
     * @param callback
     * @param requestBuilder
     * @throws IOException
     */
    @WorkerThread
    private void executeNetworkCompareCache(String url, Callback callback, Request.Builder requestBuilder) throws IOException {
        if (DEBUG) {
            Log.d(TAG, "executeNetworkCompareCache: START " + url);
        }
        if (requestBuilder == null) {
            requestBuilder = new Request.Builder();
            requestBuilder.url(url);
        }

        Request request = requestBuilder.build();
        Response response = this.executeNetwork(url, requestBuilder);
        if (response.isSuccessful()) {
            if (response.networkResponse() != null && response.code() == 200) {
                if (DEBUG) {
                    Log.d(TAG, "executeNetworkCompareCache: success " + url);
                }
                callback.onResponse(response);
                addRequest(url);
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "executeNetworkCompareCache: fail " + url);
            }
            callback.onFailure(request, null);
        }

    }

    /**
     * 同步请求
     *
     * @param url
     * @param requestType
     * @param requestMode
     * @param tag
     * @param headersMap
     * @param bodyMap
     * @return
     * @throws IOException
     */
    public OkResponse syncRequest(String url, RequestType requestType, RequestMode requestMode, String tag,
                                  Map<String, String> headersMap, Map<String, String> bodyMap) throws IOException {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            if (DEBUG) {
                Log.d(TAG, "syncRequest START: type: " + requestType);
            }
            Request.Builder requestBuilder = this.initRequestBuilder(requestMode, url, tag, headersMap, bodyMap);
            Response response = null;
            OkResponse result = null;
            switch (requestType) {
                case NETWORK_FIRST:
                    response = executeNetworkFirst(url, requestBuilder);
                    if (response != null && response.body() != null) {
                        result = wrapResponse(response);
                    }
                    break;
                case FORCE_CACHE:
                    response = executeForceCache(url, requestBuilder);
                    if (response != null && response.body() != null) {
                        result = wrapResponse(response);
                    }
                    break;
                case FORCE_NETWORK:
                    response = executeForceNetwork(url, requestBuilder);
                    if (response != null && response.body() != null) {
                        result = wrapResponse(response);
                    }
            }
            return result;
        }
    }

    private Response executeForceNetwork(String url, Request.Builder requestBuilder) throws IOException {
        if (DEBUG) {
            Log.d(TAG, "executeForceNetwork: START " + url);
        }
        if (null == requestBuilder) {
            requestBuilder = new Request.Builder();
            requestBuilder.url(url);
        }
        requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            addRequest(url);
            if (DEBUG) {
                Log.d(TAG, "executeForceNetwork: success " + url);
            }

        } else {
            if (DEBUG) {
                Log.d(TAG, "executeForceNetwork: fail " + url);
            }

        }
        return response;
    }

    private Response executeForceCache(String url, Request.Builder requestBuilder) throws IOException {
        if (DEBUG) {
            Log.d(TAG, "executeForceCache: START " + url);
        }
        if (null == requestBuilder) {
            requestBuilder = new Request.Builder();
            requestBuilder.url(url);
        }
        requestBuilder.cacheControl(CacheControl.FORCE_CACHE);
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            if (DEBUG) {
                Log.d(TAG, "executeForceCache: success " + url);
            }
            addRequest(url);
        } else {
            if (DEBUG) {
                Log.d(TAG, "executeForceCache: fail " + url);
            }

        }
        return response;
    }

    private Response executeNetworkFirst(String url, Request.Builder requestBuilder) throws IOException {
        if (DEBUG) {
            Log.d(TAG, "executeNetworkFirst: START " + url);
        }
        if (null == requestBuilder) {
            requestBuilder = new Request.Builder();
            requestBuilder.url(url);
        }
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            if (DEBUG) {
                Log.d(TAG, "executeNetworkFirst: success " + url);
            }
            addRequest(url);
            return response;
        } else {
            if (DEBUG) {
                Log.d(TAG, "executeNetworkFirst: fail and will executeForceCache " + url);
            }

            return this.executeForceCache(url, requestBuilder);
        }
    }

    private Response executeNetwork(String url, com.squareup.okhttp.Request.Builder requestBuilder) throws IOException {
        if (DEBUG) {
            Log.d(TAG, "executeNetwork: START " + url);
        }
        if (null == requestBuilder) {
            requestBuilder = new com.squareup.okhttp.Request.Builder();
            requestBuilder.url(url);
        }

        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            if (DEBUG) {
                Log.d(TAG, "executeNetwork: success " + url);
            }
            addRequest(url);
        } else {
            if (DEBUG) {
                Log.d(TAG, "executeNetwork: fail " + url);
            }
        }
        return response;
    }

    public OkResponse syncPostFile(String name, String filename, String url, RequestMediaType requestMediaType,
                                   File file, String tag, Map<String, String> headersMap, Map<String, String> bodyMap) {
        return this.syncPostContent(name, filename, url, RequestBody.create(this.getMediaType(requestMediaType), file), tag, headersMap, bodyMap);
    }

    public OkResponse syncPostFile(String name, String filename, String url, RequestMediaType requestMediaType,
                                   String string, String tag, Map<String, String> headersMap, Map<String, String> bodyMap) {
        return this.syncPostContent(name, filename, url, RequestBody.create(this.getMediaType(requestMediaType), string), tag, headersMap, bodyMap);
    }

    public OkResponse syncPostFile(String name, String filename, String url, RequestMediaType requestMediaType,
                                   byte[] bytes, String tag, Map<String, String> headersMap, Map<String, String> bodyMap) {
        return this.syncPostContent(name, filename, url, RequestBody.create(this.getMediaType(requestMediaType), bytes), tag, headersMap, bodyMap);
    }

    /**
     * 同步文件上传
     *
     * @param name
     * @param filename
     * @param url
     * @param fromBody
     * @param tag
     * @param headersMap
     * @param bodyMap
     * @return
     */
    private OkResponse syncPostContent(String name, String filename, String url, RequestBody fromBody,
                                       String tag, Map<String, String> headersMap, Map<String, String> bodyMap) {
        if (url != null && !url.trim().equals("")) {
            Request.Builder requestBuilder = initRequestBuilder(RequestMode.POST, url, tag, headersMap, bodyMap);
            RequestBody requestBody = fromBody;//默认非文件上传
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(filename)) {
                //文件上传
                requestBody = (new MultipartBuilder()).
                        type(MultipartBuilder.FORM).
                        addPart(Headers.of(new String[]{"Content-Disposition", "form-data; name=\"" + name + "\"; filename=\"" + filename + "\""}), fromBody).
                        addPart(requestBuilder.build().body()).
                        build();
            }
            requestBuilder.post(requestBody);
            try {
                OkResponse result = null;
                Response response = executeForceNetwork(url, requestBuilder);
                if (response != null && response.body() != null) {
                    result = wrapResponse(response);
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Request.Builder initRequestBuilder(RequestMode requestMode, String url, String tag,
                                               Map<String, String> headersMap, Map<String, String> bodyMap) {
        Request.Builder builder = new Request.Builder();
        addHeaders(builder, tag, headersMap);
        if (requestMode == RequestMode.GET) {
            url = attachHttpGetParams(url, bodyMap);
        } else if (requestMode == RequestMode.POST) {
            postBody(builder, bodyMap);
        }
        builder.url(url);
        return builder;
    }

    /**
     * 设置post的请求参数
     *
     * @param builder
     * @param bodyMap
     */
    private void postBody(Request.Builder builder, Map<String, String> bodyMap) {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        if (null != bodyMap && bodyMap.size() > 0) {
            Iterator<Map.Entry<String, String>> it = bodyMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> next = it.next();
                formEncodingBuilder.add(next.getKey(), next.getValue());
            }
        }
        RequestBody body = formEncodingBuilder.build();
        builder.post(body);
    }


    /**
     * 请求头设置
     *
     * @param builder
     * @param tag
     * @param headersMap
     */
    private void addHeaders(Request.Builder builder, String tag, Map<String, String> headersMap) {
        if (!TextUtils.isEmpty(tag)) {
            builder.tag(tag);
        }
        Map<String, String> temp = new HashMap<>();
        if (null != partHeaders && partHeaders.size() > 0) {
            temp.putAll(partHeaders);
        }
        if (null != headersMap && headersMap.size() > 0) {
            temp.putAll(headersMap);
        }
        if (temp.size() > 0) {
            Set<Map.Entry<String, String>> entrySet = temp.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * get请求参数封装
     *
     * @param url
     * @param bodyMap
     * @return
     */
    private String attachHttpGetParams(String url, Map<String, String> bodyMap) {
        if (null == bodyMap) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entrySet = bodyMap.entrySet();
        Iterator<Map.Entry<String, String>> it = entrySet.iterator();
        Map.Entry<String, String> first = it.next();
        sb.append(url.contains("?") ? url : url + "?").append(first.getKey() + "=").append(encodeValue(first.getValue(), "UTF-8"));
        while (it.hasNext()) {
            Map.Entry<String, String> next = it.next();
            sb.append("&").append(next.getKey().concat("=")).append(encodeValue(next.getValue(), "UTF-8"));
        }
        return sb.toString();
    }

    private String encodeValue(String value, String charset) {
        try {
            return URLEncoder.encode(value, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    public void setEnableSession(boolean enable) {
        SessionManager.setIsSessionEnabled(enable);
    }

    public boolean hasRequested(String url) {
        boolean isContains = false;
        if (urls != null && SessionManager.isSessionEnabled) {
            isContains = urls.contains(url);
        }
        return isContains;
    }

    private void addRequest(String url) {
        if (!hasRequested(url) && null != urls) {
            urls.add(url);
        }
    }

    public void removeRequested(String url) {
        if (urls != null) {
            urls.remove(url);
        }
    }

    public void clearRequested(String url) {
        if (urls != null) {
            urls.clear();
        }
    }

    public void clearUrls() {
        if (urls != null) {
            urls.clear();
        }

    }

    public boolean hasCache(String url) throws IOException {
        boolean hasCache = false;
        if (client != null && client.getCache() != null && client.getCache().urls() != null) {
            Iterator list = client.getCache().urls();
            String cacheUrl;
            do {
                if (!list.hasNext()) {
                    return hasCache;
                }
                cacheUrl = (String) list.next();
                if (!url.contains("?") && cacheUrl.contains("?")) { //去参数比较
                    cacheUrl = cacheUrl.substring(0, cacheUrl.indexOf("?"));
                }
            } while (!url.equals(cacheUrl));
            hasCache = true;
        }
        return hasCache;
    }

    public static void deleteCache() {
        if (cache != null) {
            try {
                cache.delete();
                cache = new Cache(createCacheDir(mContext), 10 * 1024 * 1024L);
                client.setCache(cache);
            } catch (Exception var1) {
                var1.printStackTrace();
            }
        }

    }

    private boolean equalsResult(OkResponse cacheResponse, OkResponse okResponse) {
        if (null != cacheResponse && null != okResponse &&
                !TextUtils.isEmpty(cacheResponse.getResult()) && !TextUtils.isEmpty(okResponse.getResult())) {
            return cacheResponse.getResult().equals(okResponse.getResult());
        }
        return false;
    }

    private OkResponse wrapResponse(Response response) throws IOException {
        String result = "";
        if (response.header("Content-Encoding", "").equals("gzip")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPInputStream gis = new GZIPInputStream(response.body().byteStream());
            byte[] buffer = new byte[4096];
            int l;
            while ((l = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, l);
            }
            result = new String(baos.toByteArray(), "utf-8");
        } else {
            result = response.body().string();
        }
        int responseType = 0;
        if (response.networkResponse() != null) {
            responseType = RESPONSE_TYPE_NETWORK;
        } else if (response.cacheResponse() != null) {
            responseType = RESPONSE_TYPE_CACHE;
        }
        return new OkResponse(result, response.headers().toMultimap(), response.code(), responseType);
    }


    public static class Builder {
        private Application context;
        private File cacheDir;
        private long cacheSize;
        private Cache cache;
        private Map<String, String> partHeaders;
        private long connectTimeout = CONNECT_TIMEOUT;
        private long readTimeout = READ_TIMEOUT;
        private long writeTimeout = WRITE_TIMEOUT;
        private long cacheTime = CACHE_TIME;

        public Builder(Application context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            } else {
                this.context = context;
                this.partHeaders = new HashMap();
            }
        }

        public Builder setHeaders(HashMap<String, String> headers) {
            if (headers != null) {
                this.partHeaders.putAll(headers);
            }
            return this;
        }

        public Builder setTimeOut(int connectTime, int readTime, int writeTime) {
            this.connectTimeout = connectTime;
            this.readTimeout = readTime;
            this.writeTimeout = writeTime;
            return this;
        }

        public Builder setCacheDirFile(File cacheDir, int cacheSize) {
            this.cacheDir = cacheDir;
            this.cacheSize = (long) cacheSize;
            return this;
        }

        public HttpManager build() {
            if (this.cacheDir == null) {
                this.cacheDir = HttpManager.createCacheDir(this.context);
            }
            if (this.cacheSize <= 0L) {
                this.cacheSize = CACHE_SIZE;
            }
            this.cache = new Cache(this.cacheDir, this.cacheSize);
            HttpManager.mContext = this.context.getApplicationContext();

            if (this.context != null) {
                this.context.registerActivityLifecycleCallbacks(new SessionActivityLifecycleCallbacks());
            }
            HttpManager.mHttpManager = new HttpManager(this.cacheDir, this.cache, this.partHeaders,
                    this.connectTimeout, this.readTimeout, this.writeTimeout, this.cacheTime);
            return HttpManager.mHttpManager;
        }

    }

    public enum RequestMediaType {
        JSON,
        FILE,
        IMAGE_JPG,
        MEDIA_TYPE_MARKDOWN;
    }

    public enum RequestMode {
        GET,
        POST;
    }

    public enum RequestType {
        CACHE_FIRST,
        NETWORK_FIRST,
        FORCE_CACHE,
        FORCE_NETWORK,
        CACHE_FIRST_AND_FORCE_NETWORK;
    }

    private MediaType getMediaType(RequestMediaType requestMediaType) {
        MediaType mediaType = null;
        switch (requestMediaType) {
            case JSON:
                mediaType = JSON;
                break;
            case FILE:
                mediaType = FILE;
                break;
            case IMAGE_JPG:
                mediaType = IMAGE_JPG;
                break;
            case MEDIA_TYPE_MARKDOWN:
                mediaType = MEDIA_TYPE_MARKDOWN;
                break;
            default:
                mediaType = MEDIA_TYPE_MARKDOWN;
                break;

        }
        return mediaType;
    }

    private static File createCacheDir(Context context) {
        String cachePath = context.getCacheDir().getAbsolutePath();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT <= 8) {
                cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else if (null != context.getExternalCacheDir()) {
                cachePath = context.getExternalCacheDir().getAbsolutePath();
            }
        }
        File cacheDir = new File(cachePath.concat(File.separator).concat("okHttp"));
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if (DEBUG) {
            Log.d(TAG, "createCacheDir " + cacheDir.getAbsolutePath());
        }
        return cacheDir;
    }
}
