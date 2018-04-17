//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns;

import android.content.Context;
import android.net.ConnectivityManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.List;

public class SnsHttpUtils {
    public static int CONNECT_TIMEOUT = 20;
    public static int DATA_TIMEOUT = 40;

    public SnsHttpUtils() {
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean netWorkStatus = false;
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService("connectivity");
        if(connManager.getActiveNetworkInfo() != null) {
            netWorkStatus = connManager.getActiveNetworkInfo().isAvailable();
        }

        return netWorkStatus;
    }

    public static String httpGet(String url, String queryString) throws Exception {
        String responseData = null;
        if(queryString != null && !queryString.equals("")) {
            url = url + "?" + queryString;
        }

        System.out.println(url);
        HttpGet method = new HttpGet(url);
        HttpClient httpClient = getNewHttpClient();

        try {
            HttpResponse response = httpClient.execute(method);
            int status = response.getStatusLine().getStatusCode();
            if(status == 200) {
                responseData = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            httpClient = null;
        }

        return responseData;
    }

    static HttpEntity download(String url) throws Exception {
        HttpEntity responseData = null;
        HttpGet method = new HttpGet(url);
        HttpClient httpClient = getNewHttpClient();

        try {
            HttpResponse response = httpClient.execute(method);
            int status = response.getStatusLine().getStatusCode();
            if(status == 200) {
                responseData = response.getEntity();
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            httpClient = null;
        }

        return responseData;
    }

    public static String httpPost(String url, List<NameValuePair> params) {
        String responseData = null;
        HttpResponse response = null;

        try {
            HttpClient httpClient = getNewHttpClient();
            HttpPost request = new HttpPost(url);
            if(params != null && params.size() > 0) {
                request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            }

            request.addHeader("User-Agent", "Android Multipart HTTP Client 1.0");
            HttpParams config = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(config, CONNECT_TIMEOUT * 1000);
            HttpConnectionParams.setSoTimeout(config, DATA_TIMEOUT * 1000);
            response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() == 200) {
                responseData = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                responseData = "{'error_code':'" + response.getStatusLine().getStatusCode() + "'}";
            }
        } catch (Exception var7) {
            responseData = "{'error_code':'500'}";
            var7.printStackTrace();
        }

        return responseData;
    }

    static String httpPostWithPic(String url, List<NameValuePair> params, File file) {
        String responseData = null;

        try {
            HttpClient httpClient = getNewHttpClient();
            HttpPost request = new HttpPost(url);
            if(file != null && file.isFile() && file.exists()) {
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("pic", new FileBody(file, "image/jpeg,image/png,image/gif"));
                if(params != null) {
                    Iterator var7 = params.iterator();

                    while(var7.hasNext()) {
                        NameValuePair param = (NameValuePair)var7.next();
                        entity.addPart(param.getName(), new StringBody(param.getValue(), Charset.forName("UTF-8")));
                    }
                }

                request.setEntity(entity);
                request.addHeader("User-Agent", "Android Multipart HTTP Client 1.0");
                request.addHeader("enctype", "multipart/form-data");
                HttpParams config = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(config, CONNECT_TIMEOUT * 1000);
                HttpConnectionParams.setSoTimeout(config, DATA_TIMEOUT * 1000);
                HttpResponse response = httpClient.execute(request);
                if(response.getStatusLine().getStatusCode() == 200) {
                    responseData = EntityUtils.toString(response.getEntity(), "UTF-8");
                } else {
                    responseData = "{'error_code':'" + response.getStatusLine().getStatusCode() + "'}";
                }
            } else {
                responseData = "{'error_code':'-1'}";
            }
        } catch (Exception var9) {
            responseData = "{'error_code':'500'}";
            var9.printStackTrace();
        }

        return responseData;
    }

    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load((InputStream)null, (char[])null);
            SSLSocketFactory sf = new SnsSSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception var5) {
            return new DefaultHttpClient();
        }
    }
}
