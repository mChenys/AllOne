package blog.csdn.net.mchenys.common.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 剪裁照片工具
 * 
 * @author Liyang
 * 
 */
public class CropPhotoUtils {



    // 指定的拍照存储目录和照片名称
    public static File dir; // 拍照存储目录O
    public static String name; // 拍照照片的名称

    private static final int ONE_K = 1024;
    private static final int ONE_M = ONE_K * ONE_K;
    private static final int MAX_AVATAR_SIZE = 2 * ONE_M; // 2M

    // 获取已经剪裁好的照片
    public static File getCropPhoto(File dir, String photoName) {
        File photoFile = null;
        if (dir != null && dir.isDirectory()) {
            photoFile = new File(dir.getPath() + File.separator + getCropPhotoFileName(photoName));
        }
        return photoFile;
    }

    // 获取照片的文件名(相机拍的照片)
    public static String getPhotoFileName() {
        if (name != null && !"".equals(name)) {
            return name + ".jpg";
        }
        return "photo.jpg";
    }

    // 获取照片存储目录(相机拍的照片)
    public static File getPhotoFileDir() {
        if (dir != null && dir.isDirectory()) {
            return dir;
        }
        return null;
    }

    // 获取剪裁照片名称
    public static String getCropPhotoFileName(String cropName) {
        if (cropName != null && !"".equals(cropName)) {
            if (cropName.endsWith(".jpg") || cropName.endsWith(".png") || cropName.endsWith(".jpeg")) {
                return cropName;
            } else {
                return cropName + ".jpg";
            }
        }
        return "crop_photo.jpg";
    }


    /**
     * 获取存储相机拍照的图片uri
     * @param photoDir
     * @param photoName
     * @return
     */
    public static Uri getOutputMediaFileUri(File photoDir, String photoName) {
        dir = photoDir;
        name = photoName;
        return Uri.fromFile(getOutputMediaFile(photoDir, photoName));
    }

    /**
     * 获取保存照片的文件
     * @param dir
     * @param photoName
     * @return
     */
    public static File getOutputMediaFile(File dir, String photoName) {
        if (dir == null ) {
            return null;
        }
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("CropPhotoUtils", "failed to create directory");
                return null;
            }
        }

        File mediaFile;
        mediaFile = new File(dir.getPath() + File.separator + photoName + ".jpg");

        return mediaFile;
    }

    /**
     * 旋转图象
     */
    public static void rotateImage(String uriString) {
        try {
            // 读取图片信息
            ExifInterface exifInterface = new ExifInterface(uriString);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90
                    || orientation == ExifInterface.ORIENTATION_ROTATE_180
                    || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                String value = String.valueOf(orientation);
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, value);
                // exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                // "no");
                exifInterface.saveAttributes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果是拍照的话就直接获取了
     */
    private static Uri getDuplicateUri(Uri uri, String uriString) {
        Uri duplicateUri = null;
        String duplicatePath = null;
        duplicatePath = uriString.replace(".", "_duplicate.");
        // cropImagePath = uriString;
        // 判断原图是否旋转，旋转了进行修复
        rotateImage(uriString);
        duplicateUri = Uri.fromFile(new File(duplicatePath));
        return duplicateUri;
    }

    private static Uri getDuplicateUri(Context context, Uri uri) {
        Uri duplicateUri = null;
        String uriString = getUriString(context, uri);
        duplicateUri = getDuplicateUri(uri, uriString);
        return duplicateUri;
    }

    /**
     * 剪裁之前的预处理
     */
    public static Uri preCrop(Context context, Uri uri, String duplicatePath) {
        Uri duplicateUri = null;
        if (duplicatePath == null) {
            duplicateUri = getDuplicateUri(context, uri);
        } else {
            duplicateUri = getDuplicateUri(uri, duplicatePath);
        }
        // rotateImage();
        return duplicateUri;
    }

    /**
     * 获取输入流
     */
    public static InputStream getInputStream(Context context, Uri mUri) throws IOException {
        try {
            if (mUri.getScheme().equals("file")) {
                return new java.io.FileInputStream(mUri.getPath());
            } else {
                return context.getContentResolver().openInputStream(mUri);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据uri获取bitmap
     */
    public static Bitmap getBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            is = getInputStream(context, uri);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return bitmap;
    }

    /**
     * 根据Uri获取文件的路径
     */
    public static String getUriString(Context context, Uri uri) {
        String imgPath = null;
        if (uri != null) {
            String uriString = uri.toString();
            // 小米手机的适配问题，小米手机的uri以file开头，其他的手机都以content开头
            // 以content开头的uri表明图片插入数据库中了，而以file开头表示没有插入数据库
            // 所以就不能通过query来查询，否则获取的cursor会为null。
            if (uriString.startsWith("file")) {
                // uri的格式为file:///mnt....,将前七个过滤掉获取路径
                imgPath = uriString.substring(7, uriString.length());
                return imgPath;
            }
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            imgPath = cursor.getString(1); // 图片文件路径
        }
        return imgPath;
    }

    /**
     * 图片超过限制大小时的处理
     */
    public static boolean datasException(byte[] datas) {
        // 头像处理异常
        if (datas == null || datas.length <= 0) {
            return true;
        }
        // 头像尺寸不符
        if (datas.length > MAX_AVATAR_SIZE) {
            return true;
        }
        return false;
    }

}
