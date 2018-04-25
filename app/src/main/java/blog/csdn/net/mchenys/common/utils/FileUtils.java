package blog.csdn.net.mchenys.common.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by mChenys on 2018/4/24.
 */

public class FileUtils {
    /**
     * 获取临时文件
     *
     * @param context
     * @param photoUri
     * @return
     */
    public static File getTempFile(Activity context, Uri photoUri) {
        String minType = getMimeType(context, photoUri);
        Log.e("cys", "minType: " + minType);
        File filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!filesDir.exists()) filesDir.mkdirs();
        File photoFile = new File(filesDir, UUID.randomUUID().toString() + "." + minType);
        Log.e("cys", "photoFile: " + photoFile.getAbsolutePath());
        return photoFile;
    }

    /**
     * To find out the extension of required object in given uri
     * Solution by http://stackoverflow.com/a/36514823/1171484
     */
    public static String getMimeType(Activity context, Uri uri) {
        String extension;
        //Check uri format to avoid null
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            //If scheme is a content
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
            if (TextUtils.isEmpty(extension))
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            if (TextUtils.isEmpty(extension))
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
        }
        if (TextUtils.isEmpty(extension)) {
            extension = getMimeTypeByFileName(getFileWithUri(context, uri).getName());
        }
        return extension;
    }

    public static String getMimeTypeByFileName(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }

    public static File getFileWithUri(Activity activity, Uri uri) {
        String picturePath = parseUri2Path(activity, uri);
        return TextUtils.isEmpty(picturePath) ? null : new File(picturePath);
    }

    /**
     * 将TakePhoto 提供的Uri 解析出文件绝对路径
     *
     * @param activity
     * @param uri
     * @return
     */
    public static String parseUri2Path(Activity activity, Uri uri) {
        if (uri == null) return null;
        String path = uri.getPath();
        String scheme = uri.getScheme();
        Log.e("cys", "parseUri2Path uri:" + uri + " \nuri-path:" + uri.getPath() + " \nuri-Authority:" +
                uri.getAuthority() + " \nuri-Scheme:" + uri.getScheme());

        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri,
                    filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if (columnIndex >= 0) {
                path = cursor.getString(columnIndex);  //获取照片路径
            }
            cursor.close();
        }
        if(path.contains("external_files")){
            path = path.replaceAll("/external_files", Environment.getExternalStorageDirectory().getAbsolutePath());
        }

        Log.e("cys", "parseUri2Path result-path:" + path);
        return path;
    }

    /**
     * 根据uri获取图片路径
     */
    private static String getFilePathByUri(Activity activity, Uri mUri) {
        String imgPath;
        Cursor cursor = activity.getContentResolver().query(mUri, null, null, null, null);
        cursor.moveToFirst();
        imgPath = cursor.getString(1); // 图片文件路径
        return imgPath;
    }

    /**
     * 获取输入流
     */
    public static InputStream getInputStream(Activity activity, Uri uri) {
        File file = getFileWithUri(activity, uri);
        if (null != file) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
