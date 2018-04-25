package blog.csdn.net.mchenys.common.photo.crop;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.BitmapUtils;
import blog.csdn.net.mchenys.common.utils.CropPhotoUtils;
import blog.csdn.net.mchenys.common.utils.FileUtils;


/**
 * 自定义剪裁照片Activity
 */
public class CropActivity extends MonitoredActivity implements View.OnClickListener {

    private static final String TAG = "CropImage";
    // 是否需要剪裁key调用者传递
    public final static String CROP_RECT_KEY = "key_crop_rect_key";

    public final static String ACTION_CROP_IMAGE = "android.intent.action.CROP_MODERN";
    public final static String SOURCE_IMAGE_URI = "key_origin_image_uri"; //原图uri

    public final static String CROP_IMAGE_URI = "key_crop_image_uri"; //裁剪后图片的uri
    public final static String CROP_IMAGE_NAME = "key_crop_image_name"; // 剪裁后照片名称
    public final static String CROP_IMAGE_DIR_PATH = "key_crop_photo_dir_path"; // 保存剪裁后照片的目录
    public final static String CROP_IMAGE_PATH = "key_crop_image_path"; // 剪裁后照片的文件路径

    private int mAspectX, mAspectY; //mAspectX为圆形裁剪框
    private final Handler mHandler = new Handler();

    // These options specifiy the output image size and whether we should
    // scale the output to fit it (or just crop it).
    // private int mOutputX, mOutputY;
    // private boolean mScale;
    // private boolean mScaleUp = true;
    private boolean mCircleCrop = false;

    boolean mSaving; // Whether the "save" button is already clicked.

    private CropImageView mImageView;

    private Bitmap mBitmap;

    // private RotateBitmap rotateBitmap;
    private HighlightView mCrop;

    private Uri targetUri;
    private File cropPhotoDir;
    private String cropPhotoName;
    //是否需要裁剪
    private boolean isHaveCrop = true;

    private HighlightView hv;
    private float mRatio = 1.0f;

    // private int rotation = 0;
    //
    // private static final int ONE_K = 1024;
    // private static final int ONE_M = ONE_K * ONE_K;

    private ContentResolver mContentResolver;

    private static final int DEFAULT_WIDTH = 2 * 512;
    private static final int DEFAULT_HEIGHT = 2 * 384;

    private int width;
    private int height;
    private int sampleSize = 1;
    private boolean enableRatio;//支持比例自定义
    private View mRatioLayout;
    private LinearLayout m4_3Ll;//4:3比例
    private LinearLayout m5_4Ll;//5:4比例
    private LinearLayout m1_1Ll;//1:1比例

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        initViews();
        initListener();
        initData();
    }

    /**
     * 初始化view
     */
    private void initViews() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop);
        mImageView = (CropImageView) findViewById(R.id.image);
        mRatioLayout = findViewById(R.id.ll_ratio);
        mImageView.mContext = this;
        m4_3Ll = (LinearLayout) findViewById(R.id.ll_4_3);
        m5_4Ll = (LinearLayout) findViewById(R.id.ll_5_4);
        m1_1Ll = (LinearLayout) findViewById(R.id.ll_1_1);
    }

    private void initListener() {
        findViewById(R.id.discard).setOnClickListener(this);  // 舍弃
        findViewById(R.id.rotate).setOnClickListener(this);// 旋转
        findViewById(R.id.save).setOnClickListener(this);// 保存
        m4_3Ll.setOnClickListener(this);
        m5_4Ll.setOnClickListener(this);
        m1_1Ll.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_4_3:
                mRatio = 4 / 3.0f;
                if (isHaveCrop) {
                    mRunFaceDetection.run();
                }
                setSelected(0);
                break;
            case R.id.ll_5_4:
                mRatio = 5 / 4.0f;
                if (isHaveCrop) {
                    mRunFaceDetection.run();
                }
                setSelected(1);
                break;
            case R.id.ll_1_1:
                mRatio = 1 / 1.0f;
                if (isHaveCrop) {
                    mRunFaceDetection.run();
                }
                setSelected(2);
                break;
            case R.id.discard:
                if (enableRatio) {
                    setResult(RESULT_FIRST_USER);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
                break;
            case R.id.rotate:
                onRotateClicked();
                break;
            case R.id.save:
                onSaveClicked();
                break;
        }
    }

    private void setSelected(int index) {
        switch (index) {
            case 0:
                changeChildSelected(m4_3Ll, true);
                changeChildSelected(m5_4Ll, false);
                changeChildSelected(m1_1Ll, false);
                break;
            case 1:
                changeChildSelected(m4_3Ll, false);
                changeChildSelected(m5_4Ll, true);
                changeChildSelected(m1_1Ll, false);
                break;
            case 2:
                changeChildSelected(m4_3Ll, false);
                changeChildSelected(m5_4Ll, false);
                changeChildSelected(m1_1Ll, true);
                break;
        }
    }

    private void changeChildSelected(ViewGroup parent, boolean selected) {
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            view.setSelected(selected);
        }
    }

    protected void initData() {
        Intent intent = getIntent();
        targetUri = intent.getParcelableExtra(SOURCE_IMAGE_URI);
        cropPhotoDir = new File(intent.getStringExtra(CROP_IMAGE_DIR_PATH));
        cropPhotoName = "crop_"+intent.getStringExtra(CROP_IMAGE_NAME);
        isHaveCrop = intent.getBooleanExtra(CROP_RECT_KEY, true);
        enableRatio = intent.getBooleanExtra("enableRatio", false);
        mContentResolver = getContentResolver();
        boolean isBitmapRotate = false;
        if (mBitmap == null) {
            String path = FileUtils.parseUri2Path(this, targetUri);
            Log.e("cys", "path:" + path);
            // 判断图片是不是旋转了90度，是的话就进行纠正。
            isBitmapRotate = isRotateImage(path);
//            getBitmapSize();
//            getBitmap();
            InputStream is = FileUtils.getInputStream(this, targetUri);
            if (null != is) {
                mBitmap = BitmapUtils.decodeSampleBitmapFromStream2(is, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            }

        }

        if (mBitmap == null) {
            finish();
            return;
        }
        startFaceDetection(isBitmapRotate);
        if (enableRatio) {
            mRatioLayout.setVisibility(View.VISIBLE);
            mRatio = 4 / 3.0f;
            setSelected(0);
        } else {
            mRatioLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 获取Bitmap分辨率，太大了就进行压缩
     */
//    private void getBitmapSize() {
//        InputStream is = null;
//        try {
//
//            is = getInputStream(targetUri);
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(is, null, options);
//
//            width = options.outWidth;
//            height = options.outHeight;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException ignored) {
//                }
//            }
//        }
//    }
//
//    private void getBitmap() {
//        InputStream is = null;
//        try {
//            try {
//                is = getInputStream(targetUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            while ((width / sampleSize > DEFAULT_WIDTH * 2) || (height / sampleSize > DEFAULT_HEIGHT * 2)) {
//                sampleSize *= 2;
//            }
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = sampleSize;
//            mBitmap = BitmapFactory.decodeStream(is, null, options);
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException ignored) {
//                }
//            }
//        }
//    }

    /**
     * 判断照片是否需要旋转
     */
    private boolean isRotateImage(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * `
     * 检测是否旋转图片
     */
    private void startFaceDetection(final boolean isRotate) {
        if (isFinishing()) {
            return;
        }
        if (isRotate) {
            initBitmap();
        }

        mImageView.setImageBitmapResetBase(mBitmap, true);

        startBackgroundJob(this, null, "请稍候", new Runnable() {
            @Override
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap b = mBitmap;
                        if (b != mBitmap && b != null) {
                            mImageView.setImageBitmapResetBase(b, true);
                            mBitmap.recycle();
                            mBitmap = b;
                        }
                        if (mImageView.getScale() == 1F) {
                            mImageView.center(true, true);
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (isHaveCrop) {
                    mRunFaceDetection.run();
                }
            }
        }, mHandler);
    }

    /**
     * 旋转原图
     */
    private void initBitmap() {
        Matrix m = new Matrix();
        m.setRotate(90);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        try {
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, m, true);
        } catch (OutOfMemoryError ooe) {

            m.postScale((float) 1 / sampleSize, (float) 1 / sampleSize);
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, m, true);
        }
    }


    private static class BackgroundJob extends MonitoredActivity.LifeCycleAdapter implements Runnable {

        private final MonitoredActivity mActivity;
        private final ProgressDialog mDialog;
        private final Runnable mJob;
        private final Handler mHandler;
        private final Runnable mCleanupRunner = new Runnable() {
            @Override
            public void run() {
                mActivity.removeLifeCycleListener(BackgroundJob.this);
                if (mDialog.getWindow() != null)
                    mDialog.dismiss();
            }
        };

        public BackgroundJob(MonitoredActivity activity, Runnable job, ProgressDialog dialog, Handler handler) {
            mActivity = activity;
            mDialog = dialog;
            mJob = job;
            mActivity.addLifeCycleListener(this);
            mHandler = handler;
        }

        @Override
        public void run() {
            try {
                mJob.run();
            } finally {
                mHandler.post(mCleanupRunner);
            }
        }

        @Override
        public void onActivityDestroyed(MonitoredActivity activity) {
            // We get here only when the onDestroyed being called before
            // the mCleanupRunner. So, run it now and remove it from the queue
            mCleanupRunner.run();
            mHandler.removeCallbacks(mCleanupRunner);
        }

        @Override
        public void onActivityStopped(MonitoredActivity activity) {
            mDialog.hide();
        }

        @Override
        public void onActivityStarted(MonitoredActivity activity) {
            mDialog.show();
        }
    }

    private static void startBackgroundJob(MonitoredActivity activity, String title, String message, Runnable job,
                                           Handler handler) {
        // Make the progress dialog uncancelable, so that we can gurantee
        // the thread will be done before the activity getting destroyed.
        if (activity == null) return;
        ProgressDialog dialog = ProgressDialog.show(activity, title, message, true, false);
        new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
    }

    //显示裁剪框
    Runnable mRunFaceDetection = new Runnable() {
        Matrix mImageMatrix;

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {
            // mImageView.re
            if (hv != null) {
                mImageView.remove(hv);
            }
            hv = new HighlightView(mImageView);
            //图片宽高
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            Rect imageRect = new Rect(0, 0, width, height);

            // 裁剪框的宽高 make the default size about 4/5 of the width or height
//            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropWidth = Math.min(width, height); //2.0修改
            int cropHeight = (int) (cropWidth / mRatio);

            if (mAspectX != 0 && mAspectY != 0) {
                //根据mAspectX, mAspectY比例重新设置裁剪框的宽高
                if (mAspectX > mAspectY) {
                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {
                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }
            //裁剪框左上角位置
            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);
            mImageView.add(hv);
        }

        @Override
        public void run() {
            mImageMatrix = mImageView.getImageMatrix();
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    makeDefault();

                    mImageView.invalidate();
                    if (mImageView.mHighlightViews.size() == 1) {
                        mCrop = mImageView.mHighlightViews.get(0);
                        mCrop.setFocus(true);
                    }
                }
            });
        }
    };

    /**
     * 旋转图片，每次以90度为单位
     */
    private void onRotateClicked() {
        startFaceDetection(true);
    }

    /**
     * 点击保存的处理，这里保存成功回传的是一个Uri，系统默认传回的是一个bitmap图，
     * 如果传回的bitmap图比较大的话就会引起系统出错。会报这样一个异常：
     * android.os.transactiontoolargeexception。为了规避这个异常， 采取了传回Uri的方法。
     */
    Bitmap croppedImage = null;

    private void onSaveClicked() {
        // step api so that we don't require that the whole (possibly large)
        // bitmap doesn't have to be read into memory
        if (isHaveCrop && mCrop == null) {
            return;
        }

        if (mSaving)
            return;
        mSaving = true;

        // final Bitmap croppedImage = null;
        if (isHaveCrop) {
            Rect r = mCrop.getCropRect();

            int width = r.width();
            int height = r.height();

            // If we are circle cropping, we want alpha channel, which is the
            // third param here.
            croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);

            canvas.drawBitmap(mBitmap, r, dstRect, null);

            // Release bitmap memory as soon as possible
            mImageView.clear();
            mBitmap.recycle();
            mBitmap = null;
        } else {
            croppedImage = mBitmap;
        }

        mImageView.setImageBitmapResetBase(croppedImage, true);
        mImageView.center(true, true);
        mImageView.mHighlightViews.clear();

        //保存最终图片
        saveDrawableToSDCard(croppedImage);

        Uri cropUri = Uri.fromFile(mFile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cropUri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", mFile);
        }
        Intent intent = new Intent("inline-data");
        intent.putExtra(CROP_IMAGE_URI, cropUri);
        intent.putExtra(CROP_IMAGE_PATH, mFile.getAbsolutePath());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 保存处理文件的地址
     */
    private File mFile = null;

    /**
     * 将将剪裁后的Bitmap放入sd卡
     */
    private void saveDrawableToSDCard(Bitmap bitmap) {
        try {
            if (cropPhotoDir != null && cropPhotoDir.isDirectory()) {
                mFile = new File(cropPhotoDir, CropPhotoUtils.getCropPhotoFileName(cropPhotoName));
                Log.d(TAG, "save file name = " + cropPhotoName);
                if (!mFile.exists()) {
                    mFile.createNewFile();
                }
            }
            if (mFile == null || !mFile.exists()) {
                return;
            }
            FileOutputStream fos = new FileOutputStream(mFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
