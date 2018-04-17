package blog.csdn.net.mchenys.common.utils;

import android.net.Uri;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;

import java.io.File;

import cn.com.pc.framwork.module.imageloader.ImageLoader;
import cn.com.pc.framwork.module.imageloader.ImageLoaderConfig;
import cn.com.pc.framwork.module.imageloader.ImageTargetSize;
import cn.com.pc.framwork.module.imageloader.RoundedTransformationBuilder;
import cn.com.pconline.shopping.R;

/**
 * 图片加载工具类
 * Created by mChenys on 2017/12/27.
 */
public class ImageLoadUtils {

    public interface ImageLoadingListener {

        void onLoadingStarted(String url, View view);

        void onLoadingFailed(String url, View view);

        void onLoadingComplete(String url, View view);

        void onLoadingCancelled(String url, View view);
    }

    public static void disPlay(Uri uri, ImageView imageView) {
        ImageLoader.load(uri, imageView, getDefaultConfig(), null);
    }

    /**
     * 默认加载 R.drawable.app_thumb_default_80_60图片
     *
     * @param uri
     * @param imageAware
     */
    public static void disPlay(String uri, ImageView imageAware) {
        disPlay(uri, imageAware, null);
    }

    public static void disPlay(int resId, ImageView imageAware) {
        ImageLoaderConfig config = new ImageLoaderConfig.Builder()
                .setImageDefault(R.mipmap.default_icon).build();
        ImageLoader.load(resId, imageAware, config, null);
    }

    public static void disPlay(String uri, ImageView imageAware, int w, int h) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder().setImageSize(new ImageTargetSize(w, h)).
                setImageDefault(R.mipmap.default_icon).build();
        ImageLoader.load(uri, imageAware, imageLoaderConfig, null);
    }

    public static void loadBottomTab(String uri, ImageView imageAware, int h, int w, int defalutDrawable) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder().setImageSize(new ImageTargetSize(w, h)).
                setImageDefault(defalutDrawable).build();
        ImageLoader.load(uri, imageAware, imageLoaderConfig, null);
    }

    public static void disPlayWithCorner(String uri, ImageView imageAware, int defaultIcon) {
        disPlayWithCorner(uri, imageAware, -1, defaultIcon);
    }

    public static void disPlayWithCircle(String uri, ImageView imageAware, int defaultIcon) {
        disPlayWithCircle(uri, imageAware, -1, defaultIcon);
    }

    public static void disPlayWithCorner(String uri, ImageView imageAware) {
        disPlayWithCorner(uri, imageAware, -1, R.mipmap.default_icon);
    }
    public static void disPlayWithCornerUri(Uri uri, ImageView imageAware) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder()
                .setImageDefault(R.mipmap.default_icon)
                .setRoundedTransformationBuilder(new RoundedTransformationBuilder(imageAware.getContext()).cornerRadius(20).coverColor(-1))
                .build();
        ImageLoader.load(uri, imageAware, imageLoaderConfig, null);
    }
    public static void disPlayWithCircle(String uri, ImageView imageAware) {
        disPlayWithCircle(uri, imageAware, -1, R.mipmap.default_icon);
    }

    public static void disPlayWithCorner(int resId, ImageView imageAware, float corner) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder()
                .setImageDefault(R.mipmap.default_icon)
                .setRoundedTransformationBuilder(new RoundedTransformationBuilder(imageAware.getContext()).cornerRadius(corner).coverColor(-1))
                .build();
        ImageLoader.load(resId, imageAware, imageLoaderConfig, null);
    }

    public static void disPlayWithCorner(int resId, ImageView imageAware, float corner, int w, int h) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder()
                .setImageDefault(R.mipmap.default_icon)
                .setImageSize(new ImageTargetSize(w, h))
                .setRoundedTransformationBuilder(new RoundedTransformationBuilder(imageAware.getContext()).cornerRadius(corner).coverColor(-1))
                .build();
        ImageLoader.load(resId, imageAware, imageLoaderConfig, null);
    }

    public static void disPlayWithCorner(String uri, ImageView imageAware, int coverColor, int defaultIcon) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder()
                .setImageDefault(defaultIcon)
                .setRoundedTransformationBuilder(new RoundedTransformationBuilder(imageAware.getContext()).cornerRadius(20).coverColor(coverColor))
                .build();
        ImageLoader.load(uri, imageAware, imageLoaderConfig, null);
    }

    public static void disPlayWithCircle(String uri, ImageView imageAware, int coverColor, int defaultIcon) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder()
                .setImageDefault(defaultIcon)
                .setRoundedTransformationBuilder(new RoundedTransformationBuilder(imageAware.getContext()).circle(true).coverColor(coverColor))
                .build();
        ImageLoader.load(uri, imageAware, imageLoaderConfig, null);
    }

    public static void disPlayWithCircle(String uri, ImageView imageAware, int coverColor, int defaultIcon, cn.com.pc.framwork.module.imageloader.ImageLoadingListener listener) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder()
                .setImageDefault(defaultIcon)
                .setRoundedTransformationBuilder(new RoundedTransformationBuilder(imageAware.getContext()).circle(true).coverColor(coverColor))
                .build();
        ImageLoader.load(uri, imageAware, imageLoaderConfig, listener);
    }

    private static void disPlayWithCover(String uri, ImageView imageAware, int coverColor) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder()
                .setImageDefault(R.mipmap.default_icon)
                .setRoundedTransformationBuilder(new RoundedTransformationBuilder(imageAware.getContext()).coverColor(coverColor))
                .build();
        ImageLoader.load(uri, imageAware, imageLoaderConfig, null);
    }

    /**
     * 默认加载 R.drawable.app_thumb_default_80_60图片
     *
     * @param uri
     * @param imageAware
     */
    public static void disPlay(final String uri, final ImageView imageAware, final ImageLoadingListener listener) {
        if (StringUtils.isEmpty(uri)) {
            imageAware.setImageResource(R.mipmap.default_icon);
            if (listener != null) {
                listener.onLoadingFailed(uri, imageAware);
            }
            return;
        }
        if (listener != null) {
            listener.onLoadingStarted(uri, imageAware);
        }
        cn.com.pc.framwork.module.imageloader.ImageLoadingListener lis = new cn.com.pc.framwork.module.imageloader.ImageLoadingListener() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onLoadingComplete(uri, imageAware);
                }
            }

            @Override
            public void onError() {
                if (listener != null) {
                    listener.onLoadingFailed(uri, imageAware);
                }
            }
        };
        if (URLUtil.isNetworkUrl(uri)) {
            ImageLoader.load(uri, imageAware, getDefaultConfig(), lis);
        } else if (uri.startsWith("file://")) {
            ImageLoader.load(new File(uri.replace("file://", "")), imageAware, getDefaultConfig(), lis);
        } else {
            ImageLoader.load(new File(uri), imageAware, getDefaultConfig(), lis);
        }
    }


    public static ImageLoaderConfig getDefaultConfig() {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder().setImageDefault(R.mipmap.default_icon).build();
        return imageLoaderConfig;
    }

    public static ImageLoaderConfig getImageDefault(int resId) {
        ImageLoaderConfig imageLoaderConfig = new ImageLoaderConfig.Builder().setImageDefault(resId).build();
        return imageLoaderConfig;
    }

}
