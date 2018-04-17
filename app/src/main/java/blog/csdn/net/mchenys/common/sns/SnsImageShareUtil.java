//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.apache.http.HttpEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import blog.csdn.net.mchenys.common.sns.share.SnsShareEngine;


public class SnsImageShareUtil {
    public SnsImageShareUtil() {
    }

    public static void setImage(Context context, String url) {
        if(url.startsWith("http")) {
            SnsShareEngine.isHttpImage = true;
            if(!getCach(context, url)) {
                DownloadTask task = new DownloadTask(context);
                task.execute(url);
                return;
            }

            SnsShareEngine.imageDownDone = true;
        } else {
            SnsShareEngine.isHttpImage = false;
            SnsShareEngine.imageDownDone = true;
        }

    }

    public static class DownloadTask extends AsyncTask<String, Integer, Boolean> {
        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        protected Boolean doInBackground(String... params) {
            if(params != null && params[0] != null && !params[0].isEmpty()) {
                saveImage(this.context, params[0]);
                return getCach(this.context, params[0]);
            } else {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            SnsShareEngine.imageDownDone = result;
        }
    }
    /**
     * 下载图片并保存
     *
     * @param context
     * @param url
     * @return
     */
    public static File saveImage(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            InputStream is = null;
            FileOutputStream os = null;
            try {
                HttpEntity entity = SnsHttpUtils.download(url);
                is = entity.getContent();
                os = new FileOutputStream(new File(context.getDir("cacheImg", 0).getAbsolutePath(), "shareImg.cache"));
                byte[] buffer = new byte[1024];
                if (entity.getContentLength() > 0L) {
                    int readSize;
                    while ((readSize = is.read(buffer)) > 0) {
                        os.write(buffer, 0, readSize);
                        os.flush();
                    }
                    setCach(context, url);
                }
            } catch (Exception var15) {
                setCach(context, "");
                var15.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }

                    if (is != null) {
                        is.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

            }

            return new File(context.getDir("cacheImg", 0).getAbsolutePath(), "shareImg.cache");
        }
    }


    public static boolean getCach(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("uploadImage", 0);
        String url = sharedPreferences.getString("imageUrl", "");
        return key.equals(url);

    }

    public static void setCach(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("uploadImage", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("imageUrl", key);
        editor.commit();
    }

    public static String getCachUrl(Context context) {
        String path = context.getDir("cacheImg", 0).getAbsolutePath().toString() + "/shareImg.cache";
        return path;
    }
}
