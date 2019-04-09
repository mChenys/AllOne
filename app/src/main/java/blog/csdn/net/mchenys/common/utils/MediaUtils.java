package blog.csdn.net.mchenys.common.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import blog.csdn.net.mchenys.common.config.Env;

public class MediaUtils {

    interface BitmapCallback {
        void onSuccess(Bitmap bitmap);

        void onFailure(Exception e);
    }

    public static void loadVideoThumbnail(final ImageView imageView, String videoPath) {
        loadVideoThumbnail(imageView, videoPath, null, null);
    }

    /**
     * 获取视频的第一帧图片
     */
    public static void loadVideoThumbnail(final ImageView imageView, String videoPath, Integer loadingRes, final Integer errorRes) {
        if (null != loadingRes) {
            imageView.setImageResource(loadingRes);
        }
        final ImageCacheUtils cacheUtils = ImageCacheUtils.getInstance();
        //优先加载内存
        Bitmap bitmap = cacheUtils.loadBitmapFromMemoryCache(videoPath);
        if (null != bitmap) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        //其次从文件中找
        bitmap = cacheUtils.loadBitmapFromDiskCache(videoPath, Env.screenWidth, Env.screenHeight);
        if (null != bitmap) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        //最后才从网络找
        VideoThumbnailTask task = new VideoThumbnailTask(new BitmapCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(Exception e) {
                if (null != errorRes)
                    imageView.setImageResource(errorRes);
            }
        });
        task.execute(videoPath);
    }

    static class VideoThumbnailTask extends AsyncTask<String, Integer, Bitmap> {
        private BitmapCallback callback;

        VideoThumbnailTask(BitmapCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = BitmapUtils.createVideoThumbnail(url, Env.screenWidth, Env.screenHeight);
            if (null != bitmap) {
                ImageCacheUtils cacheUtils = ImageCacheUtils.getInstance();
                cacheUtils.addBitmapToMemoryCache(url, bitmap);
                cacheUtils.addBitmapToDiskCache(url, bitmap);
            }
            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (null != bitmap)
                callback.onSuccess(bitmap);
            else
                callback.onFailure(null);
        }


    }

}
