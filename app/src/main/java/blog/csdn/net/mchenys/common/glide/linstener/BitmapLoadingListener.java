package blog.csdn.net.mchenys.common.glide.linstener;

import android.graphics.Bitmap;

public interface BitmapLoadingListener {
    void onSuccess(Bitmap var1);

    void onError();
}