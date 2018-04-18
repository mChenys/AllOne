package blog.csdn.net.mchenys.common.photo;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 相册
 * Created by mChenys on 2016/11/3.
 */
public class PhotoAlbum implements Comparable<PhotoAlbum> {
    public int photoCount;//当前相册下的相片个数
    public String albumName;//相册名称
    public List<Photo> photoList;//相片集合

    @Override
    public int compareTo(PhotoAlbum o) {
        return this.albumName.compareTo(o.albumName);//根据相册名字排序
    }

    /**
     * 相片
     */
    public static class Photo implements Serializable, Comparable<Photo> {
        public String imageId;
        public String thumbnailPath;//缩略图
        public String imagePath;//原图
        public long lastModified;
        public int imageWidth;
        public int imageHeight;

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        @Override
        public String toString() {
            return "Photo{" +
                    "imagePath='" + imagePath + '\'' +
                    '}';
        }

        @Override
        public int compareTo(Photo o) {
            return (int) (o.getLastModified() - getLastModified());//根据修改时间排序
        }
    }

    public interface Callback {
        void onComplete(List<PhotoAlbum> albumList);
    }

    public static void queryAlbum(Context ctx, final Callback callback) {
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(ctx.getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if (null != cursor) {
                    switch (token) {
                        case 0://获取缩略图
                            getThumbnailUrl(cursor);
                            break;
                        case 1:
                            callback.onComplete(getPhotoAlbumList(cursor));//获取相册列表和相片
                            break;
                    }
                }
            }
        };
        //查找缩略图
        queryHandler.startQuery(0, null, MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Images.Thumbnails._ID,
                        MediaStore.Images.Thumbnails.IMAGE_ID,
                        MediaStore.Images.Thumbnails.DATA}, null, null, null);

        //查找相片
        String selection = MediaStore.Images.Media.MIME_TYPE + "=? or " +
                MediaStore.Images.Media.MIME_TYPE + "=?";
        String[] selectionArgs = {"image/jpeg", "image/png"};
        String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT};
        queryHandler.startQuery(1, null, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, orderBy);
    }


    // 缩略图列表
    private static HashMap<String, String> thumbnailList = new HashMap<>();
    private static Map<String, PhotoAlbum> albumMap = new HashMap();//相册集合

    private static void getThumbnailUrl(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            thumbnailList.clear();
            do {
                int image_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                thumbnailList.put(String.valueOf(image_id), image_path);
            } while (cursor.moveToNext());
        }
    }

    private static List<PhotoAlbum> getPhotoAlbumList(Cursor cursor) {
        String cameraId = null;
        albumMap.clear();
        while (cursor.moveToNext()) {
            int height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));//The height of the image/video in pixels.
            int width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));//The width of the image/video in pixels.
            String _id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));//The unique ID for a row.
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));//Path to the file on disk.
            String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));//The bucket display albumName of the image
            String bucketId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));// The bucket id of the image.

//            PhotoAlbum album = albumMap.get(bucketId);//避免重复
            PhotoAlbum album = albumMap.get(albumName);//改成用相册名作为唯一id,否则会出现多个同名的相册
            if (album == null) {
                album = new PhotoAlbum();
                album.photoList = new ArrayList<>();
                album.albumName = albumName;
//                albumMap.put(bucketId, album);
                albumMap.put(albumName, album);
                if (albumName.equals("Camera")) {
//                    cameraId = bucketId;
                    cameraId = albumName;
                }
            }
            album.photoCount++;
            Photo photo = new Photo();
            photo.imageId = _id;
            photo.imagePath = path;
            photo.imageWidth = width;
            photo.imageHeight = height;
            photo.thumbnailPath = thumbnailList.get(_id);
            album.photoList.add(photo);//添加相片

        }
        //将camera相册放到集合第一个位置
        PhotoAlbum cameraAlbum = albumMap.remove(cameraId);
        List<PhotoAlbum> albumList = new ArrayList<>(albumMap.values());
        if (null != cameraAlbum) {
            albumList.add(0, cameraAlbum);
        }
        albumMap.put(cameraId, cameraAlbum);
        return albumList;
    }

    /**
     * 根据相册名获取已查找过的相册
     * @param albumName
     * @return
     */
    public static PhotoAlbum getAlbumByName(String albumName) {
        return albumMap.get(albumName);
    }
}
