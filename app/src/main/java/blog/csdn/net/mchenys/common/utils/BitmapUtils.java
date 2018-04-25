package blog.csdn.net.mchenys.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BitmapUtils {

    public static Bitmap getScaleBitmap(Context context, int resId, int dstWidth, int dstHeight) {
        Bitmap origialBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(origialBitmap, dstWidth, dstHeight, true);
        if (origialBitmap != null && !origialBitmap.isRecycled()) {
            origialBitmap.recycle();
        }
        return scaleBitmap;
    }

    public static Bitmap getScaleBitmap(Bitmap origialBitmap, int dstWidth, int dstHeight) {
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(origialBitmap, dstWidth, dstHeight, true);
        if (origialBitmap != null && !origialBitmap.isRecycled()) {
            origialBitmap.recycle();
        }
        return scaleBitmap;
    }

    /**
     * 将Bitmap旋转指定角度
     *
     * @param bitmap
     * @param angle
     * @return
     */
    public static Bitmap rotate(Bitmap bitmap, int angle) {
        Matrix m = new Matrix();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        m.setRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
    }

    /**
     * 将Bitmap写入指定文件
     *
     * @param bitmap
     * @param filePath
     * @param quality  图片质量:0~100
     */
    public static String writeToFile(Bitmap bitmap, String filePath, int quality) {
        File f = new File(filePath);
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            fOut.flush();
            fOut.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 压缩图片:将原始图片压缩至指定的width宽度,其高度自动等比压缩.
     * 注:若原始图片的宽度小于width参数,则不对其进行压缩,直接返回原始图片的Bitmap.
     *
     * @param filePath 原始图片文件的路径
     * @param width    压缩后的图片宽度
     * @return 压缩后的Bitmap
     */
    public static Bitmap compressWithWidth(String filePath, int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        int bmpWidth = options.outWidth;
        int bmpHeight = options.outHeight;

        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, options);

        float scaleWidth = bmpWidth;
        float scaleHeight = bmpHeight;

        if (bmpWidth > width) {
            scaleWidth = width / scaleWidth;
            scaleHeight = scaleWidth;
        } else {
            scaleWidth = 1;
            scaleHeight = 1;
        }

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);

        //因为如果scaleWidth与scaleHeight都是1的话,那么就等于是原图,createBitmap返回的也是原始bitmap.
        //所以只有新建了bitmap,才能将原始bitmap回收掉,不然调用的时候就会因为bitmap已回收而挂掉.
        if (bitmap != resizeBitmap) {
            bitmap.recycle();
        }
        return resizeBitmap;
    }

    /**
     * 压缩图片至指定宽高.
     * 注:
     * 1.若指定宽度小于原始bitmap宽度,则宽度不压缩
     * 2.若指定高度小于原始bitmap高度,则高度不压缩
     *
     * @param filePath 原始图片文件的路径
     * @param width    压缩后的图片宽度
     * @param height   压缩后的图片高度
     * @return 压缩后的Bitmap
     */
    public static Bitmap compress(String filePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        int bmpWidth = options.outWidth;
        int bmpHeight = options.outHeight;

        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, options);

        float scaleWidth = bmpWidth;
        float scaleHeight = bmpHeight;

        //计算宽度压缩比例
        if (bmpWidth > width) {
            scaleWidth = width / scaleWidth;
        } else {
            scaleWidth = 1;
        }

        //计算高度压缩比例
        if (bmpHeight > height) {
            scaleHeight = height / scaleHeight;
        } else {
            scaleHeight = 1;
        }

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);

        //因为如果scaleWidth与scaleHeight都是1的话,那么就等于是原图,createBitmap返回的也是原始bitmap.
        //所以只有新建了bitmap,才能将原始bitmap回收掉,不然调用的时候就会因为bitmap已回收而挂掉.
        if (bitmap != resizeBitmap) {
            bitmap.recycle();
        }
        return resizeBitmap;
    }


    /**
     * 压缩图片:将原始图片压缩至指定的width宽度,其高度自动等比压缩.
     * 注:若原始图片的宽度小于width参数,则不对其进行压缩,直接返回原始图片的Bitmap.
     *
     * @param bitmap 原始图片文件的bitmap文件
     * @param width  压缩后的图片宽度
     * @return 压缩后的Bitmap
     */
    public static Bitmap compressWithWidth(Bitmap bitmap, int width) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();

        float scaleWidth = bmpWidth;
        float scaleHeight = bmpHeight;

        if (bmpWidth > width) {
            scaleWidth = width / scaleWidth;
            scaleHeight = scaleWidth;
        } else {
            scaleWidth = 1;
            scaleHeight = 1;
        }

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);

        //因为如果scaleWidth与scaleHeight都是1的话,那么就等于是原图,createBitmap返回的也是原始bitmap.
        //所以只有新建了bitmap,才能将原始bitmap回收掉,不然调用的时候就会因为bitmap已回收而挂掉.
        if (bitmap != resizeBitmap) {
            bitmap.recycle();
        }
        return resizeBitmap;
    }


    /**
     * 压缩图片:将原始图片压缩至指定的height宽度,其宽度自动等比压缩.
     * 注:若原始图片的高度小于height参数,则不对其进行压缩,直接返回原始图片的Bitmap.
     *
     * @param file   原始图片文件
     * @param height 压缩后的图片高度
     * @return 压缩后的Bitmap
     */
    public static Bitmap compressWithHeight(File file, int height) {

        return null;
    }

    /**
     * 压缩图片:将原始图片压缩至指定的height宽度,其宽度自动等比压缩.
     * 注:若原始图片的高度小于height参数,则不对其进行压缩,直接返回原始图片的Bitmap.
     *
     * @param filePath 原始图片文件的路径
     * @param height   压缩后的图片高度
     * @return 压缩后的Bitmap
     */
    public static Bitmap compressWithHeight(String filePath, int height) {

        return null;
    }

    /**
     * 压缩图片:将原始图片压缩至指定的height宽度,其宽度自动等比压缩.
     * 注:若原始图片的高度小于height参数,则不对其进行压缩,直接返回原始图片的Bitmap.
     *
     * @param bitmap 原始图片文件的bitmap文件
     * @param height 压缩后的图片高度
     * @return 压缩后的Bitmap
     */
    public static Bitmap compressWithHeight(Bitmap bitmap, int height) {

        return null;
    }

    /**
     * 将bitmap文件转换成字节数组.
     *
     * @param bm     需要转换的bitmap文件
     * @param format 转换后的文件格式:分别有Bitmap.CompressFormat.PNG、Bitmap.CompressFormat.JPEG、Bitmap.CompressFormat.WEBP
     * @return 字节数组
     */
    public static byte[] bitmap2Bytes(Bitmap bm, Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(format, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 获取圆角bitmap
     *
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    /**
     * 根据uri获取视频缩略图
     * 默认为512 x 384
     *
     * @param cr
     * @param uri
     * @return
     */
    public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {
        return getVideoThumbnail(cr, uri, MediaStore.Images.Thumbnails.MICRO_KIND);
    }

    public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri, int kind) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Cursor cursor = cr.query(uri, new String[]{MediaStore.Video.Media._ID}, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));  //image id in image table.s

        if (videoId == null) {
            return null;
        }
        cursor.close();
        long videoIdLong = Long.parseLong(videoId);
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong, kind, options);

        return bitmap;
    }

    public static Bitmap getMediaThumbnail(ContentResolver cr, Uri uri) {
        return getMediaThumbnail(cr, uri, MediaStore.Images.Thumbnails.MICRO_KIND);
    }

    /**
     * 根据uri获取图像缩略图
     * 默认为512 x 384
     *
     * @param cr
     * @param uri
     * @return
     */
    public static Bitmap getMediaThumbnail(ContentResolver cr, Uri uri, int kind) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Cursor cursor = cr.query(uri, new String[]{MediaStore.Images.Media._ID}, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        String mediaId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));  //image id in image table.s

        if (mediaId == null) {
            return null;
        }
        cursor.close();
        long videoIdLong = Long.parseLong(mediaId);
        bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, videoIdLong, kind, options);

        return bitmap;
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     * 1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     * 2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     * 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w" + bitmap.getWidth());
        System.out.println("h" + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    //释放bitmap
    public static void recycleBitmap(Bitmap bitmap) {
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public static void blur(int[]
                                    in, int[]
                                    out, int width,
                            int height,
                            float radius) {
        int widthMinus1
                = width - 1;
        int r
                = (int)
                radius;
        int tableSize
                = 2 *
                r + 1;
        int divide[]
                = new int[256 *
                tableSize];

        for (int i
             = 0;
             i < 256 *
                     tableSize; i++)
            divide[i]
                    = i / tableSize;

        int inIndex
                = 0;

        for (int y
             = 0;
             y < height; y++) {
            int outIndex
                    = y;
            int ta
                    = 0,
                    tr = 0,
                    tg = 0,
                    tb = 0;

            for (int i
                 = -r; i <= r; i++) {
                int rgb
                        = in[inIndex + clamp(i, 0,
                        width - 1)];
                ta
                        += (rgb >> 24)
                        & 0xff;
                tr
                        += (rgb >> 16)
                        & 0xff;
                tg
                        += (rgb >> 8)
                        & 0xff;
                tb
                        += rgb & 0xff;
            }

            for (int x
                 = 0;
                 x < width; x++) {
                out[outIndex]
                        = (divide[ta] << 24)
                        | (divide[tr] << 16)
                        |
                        (divide[tg] << 8)
                        | divide[tb];

                int i1
                        = x + r + 1;
                if (i1
                        > widthMinus1)
                    i1
                            = widthMinus1;
                int i2
                        = x - r;
                if (i2
                        < 0)
                    i2
                            = 0;
                int rgb1
                        = in[inIndex + i1];
                int rgb2
                        = in[inIndex + i2];

                ta
                        += ((rgb1 >> 24)
                        & 0xff)
                        - ((rgb2 >> 24)
                        & 0xff);
                tr
                        += ((rgb1 & 0xff0000)
                        - (rgb2 & 0xff0000))
                        >> 16;
                tg
                        += ((rgb1 & 0xff00)
                        - (rgb2 & 0xff00))
                        >> 8;
                tb
                        += (rgb1 & 0xff)
                        - (rgb2 & 0xff);
                outIndex
                        += height;
            }
            inIndex
                    += width;
        }
    }

    public static void blurFractional(int[]
                                              in, int[]
                                              out, int width,
                                      int height,
                                      float radius) {
        radius
                -= (int)
                radius;
        float f
                = 1.0f
                / (1 +
                2 *
                        radius);
        int inIndex
                = 0;

        for (int y
             = 0;
             y < height; y++) {
            int outIndex
                    = y;

            out[outIndex]
                    = in[0];
            outIndex
                    += height;
            for (int x
                 = 1;
                 x < width - 1;
                 x++) {
                int i
                        = inIndex + x;
                int rgb1
                        = in[i - 1];
                int rgb2
                        = in[i];
                int rgb3
                        = in[i + 1];

                int a1
                        = (rgb1 >> 24)
                        & 0xff;
                int r1
                        = (rgb1 >> 16)
                        & 0xff;
                int g1
                        = (rgb1 >> 8)
                        & 0xff;
                int b1
                        = rgb1 & 0xff;
                int a2
                        = (rgb2 >> 24)
                        & 0xff;
                int r2
                        = (rgb2 >> 16)
                        & 0xff;
                int g2
                        = (rgb2 >> 8)
                        & 0xff;
                int b2
                        = rgb2 & 0xff;
                int a3
                        = (rgb3 >> 24)
                        & 0xff;
                int r3
                        = (rgb3 >> 16)
                        & 0xff;
                int g3
                        = (rgb3 >> 8)
                        & 0xff;
                int b3
                        = rgb3 & 0xff;
                a1
                        = a2 + (int)
                        ((a1 + a3) * radius);
                r1
                        = r2 + (int)
                        ((r1 + r3) * radius);
                g1
                        = g2 + (int)
                        ((g1 + g3) * radius);
                b1
                        = b2 + (int)
                        ((b1 + b3) * radius);
                a1
                        *= f;
                r1
                        *= f;
                g1
                        *= f;
                b1
                        *= f;
                out[outIndex]
                        = (a1 << 24)
                        | (r1 << 16)
                        | (g1 << 8)
                        | b1;
                outIndex
                        += height;
            }
            out[outIndex]
                    = in[width - 1];
            inIndex
                    += width;
        }
    }

    public static int clamp(int x,
                            int a,
                            int b) {
        return (x
                < a) ? a : (x > b) ? b : x;
    }

    /**
     * 水平方向模糊度
     */
    private static float hRadius = 3;
    /**
     * 竖直方向模糊度
     */
    private static float vRadius = 3;
    /**
     * 模糊迭代度
     */
    private static int iterations = 8;

    public static Drawable BoxBlurFilter(Bitmap bmp) {
        int width
                = bmp.getWidth();
        int height
                = bmp.getHeight();
        int[]
                inPixels = new int[width
                * height];
        int[]
                outPixels = new int[width
                * height];
        Bitmap
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels,
                0,
                width, 0,
                0,
                width, height);
        for (int i
             = 0;
             i < iterations; i++) {
            blur(inPixels,
                    outPixels, width, height, hRadius);
            blur(outPixels,
                    inPixels, height, width, vRadius);
        }
        blurFractional(inPixels,
                outPixels, width, height, hRadius);
        blurFractional(outPixels,
                inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels,
                0,
                width, 0,
                0,
                width, height);
        Drawable
                drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    public static Bitmap BoxBlurFilterGrey(Bitmap bmp) {
        bmp = grey(bmp);
        int width
                = bmp.getWidth();
        int height
                = bmp.getHeight();
        int[]
                inPixels = new int[width
                * height];
        int[]
                outPixels = new int[width
                * height];
        Bitmap
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels,
                0,
                width, 0,
                0,
                width, height);
        for (int i
             = 0;
             i < iterations; i++) {
            blur(inPixels,
                    outPixels, width, height, hRadius);
            blur(outPixels,
                    inPixels, height, width, vRadius);
        }
        blurFractional(inPixels,
                outPixels, width, height, hRadius);
        blurFractional(outPixels,
                inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels,
                0,
                width, 0,
                0,
                width, height);

        return bitmap;
    }

    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {
        sentBitmap = grey(sentBitmap);
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    public static final Bitmap grey(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap
                .createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.argb(170, 60, 60, 60));
//        ColorMatrix colorMatrix = new ColorMatrix();
//        colorMatrix.setSaturation(0);
//        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
//                colorMatrix);
//        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

//        Paint paint1 = new Paint();
//        paint1.setAntiAlias(true);
//        paint1.setColor(Color.argb(170,60,60,60));
//        RectF rectF = new RectF(0f, 0f, canvas.getWidth(), canvas.getHeight());
//        canvas.drawRoundRect(rectF, 0, 0, paint1);

        return faceIconGreyBitmap;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        //Decode bitmap with inSampleSize set
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(String file, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);
        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        //Decode bitmap with inSampleSize set
        return BitmapFactory.decodeFile(file, options);
    }

    /**
     * 该方法返回有问题,用decodeSampleBitmapFromByte替代
     *
     * @param in
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    @Deprecated
    public static Bitmap decodeSampledBitmapFromStream(InputStream in, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        //Decode bitmap with inSampleSize set
        return BitmapFactory.decodeStream(in, null, options);
    }

    public static Bitmap decodeSampleBitmapFromStream2(InputStream in, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte[] bytes = input2Byte(in);
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public static Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static byte[] input2Byte(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        try {
            while ((len = in.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitmap, int reqWidth, int reqHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) reqWidth) / width;
        float scaleHeight = ((float) reqHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newBitMap;
    }

    /**
     * 加载本地图片时进行大小和质量压缩
     *
     * @param file
     * @param reqWidth
     * @param reqHeight
     * @param kb
     * @return
     */
    public static Bitmap compressAndResize(String file, int reqWidth, int reqHeight, int kb) {
        if (reqWidth <= 0) reqWidth = 720;//def w 720px
        if (reqHeight <= 0) reqHeight = 1280;//def h 1280px
        Bitmap bitmap = decodeSampledBitmapFromFile(file, reqWidth, reqHeight);
        return compress(bitmap, kb);
    }

    /**
     * 加载资源图片时进行大小和质量压缩
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @param kb
     * @return
     */
    public static Bitmap compressAndResize(Resources res, int resId, int reqWidth, int reqHeight, int kb) {
        if (reqWidth <= 0) reqWidth = 720;//def w 720px
        if (reqHeight <= 0) reqHeight = 1280;//def h 1280px
        Bitmap bitmap = decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight);
        return compress(bitmap, kb);
    }

    /**
     * 加载资源图片时进行大小和质量压缩
     *
     * @param is
     * @param reqWidth
     * @param reqHeight
     * @param kb
     * @return
     */
    public static Bitmap compressAndResize(InputStream is, int reqWidth, int reqHeight, int kb) {
        if (reqWidth <= 0) reqWidth = 720;//def w 720px
        if (reqHeight <= 0) reqHeight = 1280;//def h 1280px
        Bitmap bitmap = decodeSampledBitmapFromStream(is, reqWidth, reqHeight);
        return compress(bitmap, kb);
    }

    /**
     * 加载资源图片时进行大小和质量压缩
     *
     * @param file
     * @param reqWidth
     * @param reqHeight
     * @param kb
     * @return
     */
    public static byte[] compressAndResizeBackByte(String file, int reqWidth, int reqHeight, int kb) {
        if (reqWidth <= 0) reqWidth = 720;//def w 720px
        if (reqHeight <= 0) reqHeight = 1280;//def h 1280px
        Bitmap bitmap = decodeSampledBitmapFromFile(file, reqWidth, reqHeight);
        return compressBackByte(bitmap, kb);
    }

    /**
     * 质量压缩
     *
     * @param image
     * @param kb
     * @return
     */
    public static Bitmap compress(Bitmap image, int kb) {
        ByteArrayInputStream isBm = new ByteArrayInputStream(compressBackByte(image, kb));
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    public static byte[] compressBackByte(Bitmap image, int kb) {
        if (null == image) return null;
        if (kb <= 0) kb = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length * 1.0f / 1024 > kb) {  //循环判断如果压缩后图片是否大于目标kb,大于继续压缩
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
            System.out.println("当前压缩为:" + baos.toByteArray().length * 1.0f / (1024 * 1024) + "M");
        }
        return baos.toByteArray();
    }

    /**
     * 转换byte数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] convertByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bytes;
    }

    /**
     * 从给定的路径加载图片，并指定是否自动旋转方向
     */
    public static Bitmap rotateBitmapByExif(String imgpath, Bitmap bitmap) {

        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgpath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        if (digree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(digree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }
        return bitmap;

    }


    /**
     * v2.8转换拍照生成的bitmap成文件
     *
     * @param bitmap
     * @param photoDir 保存相片的根目录
     * @return File
     */
    public static File convertBitmap2File(Bitmap bitmap, File photoDir) {
        FileOutputStream fileOutputStream = null;
        File cameraPhotoFile = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        try {
            //保存拍照的文件
            cameraPhotoFile = new File(photoDir, dateFormat.format(new Date()) + ".jpg");
            if (!cameraPhotoFile.exists()) {
                cameraPhotoFile.createNewFile();
            }
            fileOutputStream = new FileOutputStream(cameraPhotoFile);
            // 把数据写入文件
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cameraPhotoFile;
    }

    public static Bitmap cropCircle(Bitmap source) {
        if (null != source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int width = (source.getWidth() - size) / 2;
            int height = (source.getHeight() - size) / 2;
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader =
                    new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            if (width != 0 || height != 0) {
                // source isn't square, move viewport to center
                Matrix matrix = new Matrix();
                matrix.setTranslate(-width, -height);
                shader.setLocalMatrix(matrix);
            }
            paint.setShader(shader);
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return bitmap;
        }
        return null;
    }
}
