package blog.csdn.net.mchenys.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import blog.csdn.net.mchenys.AllOneApplication;


/**
 * 图片缓存工具类
 * Created by mChenys on 2017/5/24.
 */
public class ImageCacheUtils {
    private static final String TAG = "ImageCacheUtils";
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final long DISK_CACHE_SIZE = 50 * 1024 * 1024;
    private static final int DISK_CACHE_INDEX = 0;
    private DiskLruCache mDiskLruCache;
    private LruCache<String, Bitmap> mMemoryCache;
    private boolean mIsDiskLruCacheCreated;
    private Context mContext;

    public boolean isDiskLruCacheCreated() {
        return mIsDiskLruCacheCreated;
    }

    private ImageCacheUtils(Context context) {
        this.mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
        File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
        Log.d(TAG, "Diskcache path is:" + diskCacheDir.getAbsolutePath());
        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 构建ImageCacheUtils实例
     *
     * @param context
     * @return
     */
    private static volatile ImageCacheUtils instance;

    public static ImageCacheUtils getInstance() {
        if (null == instance) {
            synchronized (ImageCacheUtils.class) {
                if (null == instance) {
                    instance = new ImageCacheUtils(AllOneApplication.mAppContext);
                }
            }
        }
        return instance;
    }

    /**
     * 添加Bitmap到内存
     *
     * @param url
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String url, Bitmap bitmap) {
        String key = hashKeyFormUrl(url);
        if (mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }


    /**
     * 通过url从内存中获取bitmap
     *
     * @param url
     * @return
     */
    public Bitmap loadBitmapFromMemoryCache(String url) {
        String key = hashKeyFormUrl(url);
        return mMemoryCache.get(key);
    }


    /**
     * 从网络获取Bitmap,然后保存到diskCache再返回
     *
     * @param url
     * @param in
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws IOException
     */
    public Bitmap loadBitmapFromHttp(String url, InputStream in, int reqWidth, int reqHeight) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network from UI Thread");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        try {
            String key = hashKeyFormUrl(url);
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                if (saveToDiskCache(in, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                mDiskLruCache.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
    }

    /**
     * 将输入流保存到自定的输出流中
     *
     * @param inputStream
     * @param outputStream
     * @return 返回是否下载成功
     */
    private boolean saveToDiskCache(InputStream inputStream, OutputStream outputStream) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(inputStream);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "downloadBitmap failed." + e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 从磁盘中获取Bitmap,成功后再保存到内存
     *
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws IOException
     */
    public Bitmap loadBitmapFromDiskCache(String url, int reqWidth, int reqHeight) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap from UI Thread,it's not recommended!");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            String key = hashKeyFormUrl(url);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (null != snapshot) {
                FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                FileDescriptor fileDescriptor = fileInputStream.getFD();
                bitmap = BitmapUtils.decodeSampledBitmapFromFileDescriptor(fileDescriptor, reqWidth, reqHeight);
                if (null != bitmap) {
                    addBitmapToMemoryCache(key, bitmap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(url.getBytes());
            cacheKey = byteToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 获取路径的可用空间
     *
     * @param path
     * @return
     */
    private long getUsableSpace(File path) {
        return path.getUsableSpace();

    }

    /**
     * 获取磁盘缓存目录File
     *
     * @param context
     * @param uniqueName
     * @return
     */
    private File getDiskCacheDir(Context context, String uniqueName) {
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

}
