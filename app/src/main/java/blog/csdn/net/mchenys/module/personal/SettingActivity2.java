package blog.csdn.net.mchenys.module.personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.photo.crop.CropActivity;
import blog.csdn.net.mchenys.common.utils.FileUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;

/**
 * 该类展示,屏蔽FileProvider.getUriForFile方式获取uri的操作
 * Created by mChenys on 2018/4/25.
 */

public class SettingActivity2 extends BaseActivity {

    private ImageView picture;
    private Uri imageUri;
    private Uri cropImgUri;
    private String filePath;

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_setting;
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setCenterTv("设置");
        titleBar.setRightTv("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.setData(cropImgUri);
                intent.putExtra(CropActivity.CROP_IMAGE_PATH, filePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        //屏蔽7.0中使用 Uri.fromFile爆出的FileUriExposureException
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >=24) {
            builder.detectFileUriExposure();
        }
        picture = findViewById(R.id.picture);
    }

    public void takePhoto(View view) {
        File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
                outputImage.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Constant.REQ_CAMERA_CODE);
    }

    public void chooseAlbum(View view) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQ_CAMERA_CODE: //处理拍照返回结果
                    startPhotoCrop();
                    break;
                case Constant.REQ_CROP_CODE://处理裁剪返回结果
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImgUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    filePath = FileUtils.parseUri2Path(this, cropImgUri);
                    break;

            }
        }
    }


    /**
     * 开启裁剪相片(拍照和从相册获取都需要裁剪)
     */
    public void startPhotoCrop() {
        //创建file文件，用于存储剪裁后的照片
        File cropImage = new File(Environment.getExternalStorageDirectory(), "crop_image.jpg");
        try {
            if (cropImage.exists()) {
                cropImage.delete();
            }
            cropImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cropImgUri = Uri.fromFile(cropImage);
        Intent intent = new Intent("com.android.camera.action.CROP");
        //设置源地址uri
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        //设置目的地址uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImgUri);
        //设置图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", false);//data不需要返回,避免图片太大异常
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, Constant.REQ_CROP_CODE);
    }
}
