package blog.csdn.net.mchenys.module.personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
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
 * Created by mChenys on 2018/4/25.
 */

public class SettingActivity extends BaseActivity {

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
        picture = findViewById(R.id.picture);
    }

    public void takePhoto(View view) {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
                outputImage.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            String authority = this.getPackageName() + ".fileprovider";
            imageUri = FileProvider.getUriForFile(this, authority, outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
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
                    //CropActivity 调用本地的裁剪程序
                    Intent intent = new Intent(CropActivity.ACTION_CROP_IMAGE);
                    intent.putExtra(CropActivity.SOURCE_IMAGE_URI, imageUri);
                    intent.putExtra("enableRatio", true);
                    File cropFile = FileUtils.getFileWithUri(this, imageUri);
                    intent.putExtra(CropActivity.CROP_IMAGE_DIR_PATH, cropFile.getParent());
                    intent.putExtra(CropActivity.CROP_IMAGE_NAME, cropFile.getName());
                    startActivityForResult(intent, Constant.REQ_CROP_CODE);

                    break;
                case Constant.REQ_CROP_CODE://处理裁剪返回结果
                    // 处理本地程序的裁剪结果
                    filePath = data.getStringExtra(CropActivity.CROP_IMAGE_PATH);
                    cropImgUri = data.getParcelableExtra(CropActivity.CROP_IMAGE_URI);
                    Log.e("cys", "filePath:" + filePath + " imageUri:" + cropImgUri);
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImgUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }

}
