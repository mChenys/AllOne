package blog.csdn.net.mchenys.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.glide.ImageLoader;
import blog.csdn.net.mchenys.common.glide.cofig.ImageLoadConfig;
import blog.csdn.net.mchenys.common.glide.linstener.LoaderListener;
import blog.csdn.net.mchenys.common.glide.transformation.CornerTransform;


/**
 * 图片加载工具类
 * Created by mChenys on 2017/12/27.
 */
public class ImageLoadUtils {


    public interface ImageLoadingListener {

        void onLoadingStarted(String url, View view);

        void onLoadingFailed(String url, View view);

        void onLoadingComplete(String url, View view);

    }

    public static void disPlay(Uri uri, ImageView imageView) {
        ImageLoader.loadUri(imageView, uri, ImageLoader.defConfig, null);
    }


    /**
     * @param uri
     * @param imageAware
     */
    public static void disPlay(String uri, ImageView imageAware) {
        disPlay(uri, imageAware, null);
    }

    public static void disPlay(int resId, ImageView imageAware) {
        ImageLoader.loadResId(imageAware, resId, ImageLoader.defConfig, null);
    }

    public static void disPlay(String uri, ImageView imageAware, int w, int h) {
        ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                setSize(new ImageLoadConfig.OverrideSize(w, h)).
                build();
        ImageLoader.loadStringRes(imageAware, uri, imageLoaderConfig, null);
    }

    public static void disPlayWithFitCenter(String uri, ImageView imageView) {
        ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                setCropType(ImageLoadConfig.FIT_CENTER).build();
        ImageLoader.loadStringRes(imageView, uri, imageLoaderConfig, null);
    }

    public static void disPlayWithCorner(Uri uri, ImageView imageAware) {
        ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                setRoundedCorners(true).
                setCornerRadius(20).
                setCornerMargin(20).
                build();
        ImageLoader.loadUri(imageAware, uri, imageLoaderConfig, null);
    }


    public static void disPlayWithCorner(int resId, ImageView imageAware, int radiusDip) {
        ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                setRoundedCorners(true).
                setCornerRadius(DisplayUtils.convertDIP2PX(imageAware.getContext(),radiusDip)).
                setCornerMargin(0).
                build();
        ImageLoader.loadResId(imageAware, resId, imageLoaderConfig, null);
    }

    public static void disPlayWithCorner(int resId, ImageView imageAware, int radiusDip, int w, int h) {
        ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                setRoundedCorners(true).
                setCornerRadius(DisplayUtils.convertDIP2PX(imageAware.getContext(),radiusDip)).
                setCornerMargin(0).
                setSize(new ImageLoadConfig.OverrideSize(w, h)).
                build();
        ImageLoader.loadResId(imageAware, resId, imageLoaderConfig, null);
    }

    public static void disPlayWithCorner(String uri, ImageView imageAware, int radiusDip) {
        ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                setRoundedCorners(true).
                setCornerRadius(DisplayUtils.convertDIP2PX(imageAware.getContext(),radiusDip)).
                setCornerMargin(DisplayUtils.convertDIP2PX(imageAware.getContext(),radiusDip)).
                build();
        ImageLoader.loadStringRes(imageAware, uri, imageLoaderConfig, null);
    }

    public static void disPlayWithCircle(String uri, ImageView imageAware) {
        ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                setCropCircle(true).
                build();
        ImageLoader.loadStringRes(imageAware, uri, imageLoaderConfig, null);
    }

    public static void disPlayWithCircle(String uri, ImageView imageAware, int defaultIcon) {
        ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                setPlaceHolderResId(defaultIcon).
                setCropCircle(true).
                build();
        ImageLoader.loadStringRes(imageAware, uri, imageLoaderConfig, null);
    }


   /* private static void disPlayWithCover(String uri, ImageView imageAware, int coverColor) {
        LoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                .setImageDefault(R.mipmap.default_icon)
                .setRoundedTransformationBuilder(new RoundedTransformationBuilder(imageAware.getContext()).coverColor(coverColor))
                .build();
        ImageLoader.load(uri, imageAware, imageLoaderConfig, null);
    }*/

    public static void disPlayWitchCircleForceNetwork(File url, ImageView imageView) {
        Bitmap temp = BitmapFactory.decodeResource(imageView.getResources(), R.drawable.bg_error);
        imageView.setImageBitmap(BitmapUtils.cropCircle(temp));
        if (url == null || !url.exists()) {
            return;
        }
        try {
            ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig)
                    .setPrioriy(ImageLoadConfig.LoadPriority.HIGH)
                    .setCropCircle(true)
                    .setDiskCacheStrategy(ImageLoadConfig.DiskCache.NONE)
                    .setSkipMemoryCache(true)
                    .setCrossFade(false)
                    .build();
            ImageLoader.loadFile(imageView, url, imageLoaderConfig, null);
        } catch (Exception e) {

        }
    }
    public static void disPlayWitchCircleForceNetwork(String url, ImageView imageView) {
        Bitmap temp = BitmapFactory.decodeResource(imageView.getResources(), R.drawable.bg_error);
        imageView.setImageBitmap(BitmapUtils.cropCircle(temp));
        if (StringUtils.isEmpty(url)) {
            return;
        }
        try {
            ImageLoadConfig imageLoaderConfig = ImageLoadConfig.parseBuilder(ImageLoader.defConfig)
                    .setPrioriy(ImageLoadConfig.LoadPriority.HIGH)
                    .setCropCircle(true)
                    .setDiskCacheStrategy(ImageLoadConfig.DiskCache.NONE)
                    .setSkipMemoryCache(true)
                    .setCrossFade(false)
                    .build();
            ImageLoader.loadStringRes(imageView, url, imageLoaderConfig, null);
        } catch (Exception e) {

        }
    }
    /**
     * @param uri
     * @param imageAware
     */
    public static void disPlay(final String uri, final ImageView imageAware, final ImageLoadingListener listener) {
        if (StringUtils.isEmpty(uri)) {
            imageAware.setImageResource(R.drawable.default_img_640x420);
            if (listener != null) {
                listener.onLoadingFailed(uri, imageAware);
            }
            return;
        }
        if (listener != null) {
            listener.onLoadingStarted(uri, imageAware);
        }
        LoaderListener lis = new LoaderListener() {
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
        ImageLoader.loadStringRes(imageAware, uri, ImageLoader.defConfig, lis);
    }


    public static void displayWithRound(final String url, final ImageView imageView, final int w, final int h, final float radius) {
        if (null == imageView) return;
        final Context context = imageView.getContext();
        float corner = DisplayUtils.dip2px(context, radius);
        final CornerTransform cornerTransform = new CornerTransform(context, corner);
        Glide.with(context).load(url).asBitmap()
                .override(w,h)
                .transform(cornerTransform)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<Bitmap> target, boolean b) {
                        Glide.with(context).load(R.drawable.default_img_180x180).asBitmap()
                                .override(w,h)
                                .transform(cornerTransform).into(imageView);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, String s, Target<Bitmap> target, boolean b, boolean b1) {
                        return false;
                    }
                })
                .into(imageView);
    }

}
