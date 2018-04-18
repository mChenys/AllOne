package blog.csdn.net.mchenys.common.photo;

import android.content.Intent;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;


/**
 * Created by mChenys on 2016/11/3.
 */
public class PhotoListActivity extends BaseActivity {
    private GridView mPhotoGv;
    private List<String> selectedPathList = new ArrayList<String>(); //存放用户从相册中选择的图片
    private List<PhotoAlbum.Photo> mPhotoList = new ArrayList<>();   //相册中的所有相片
    private int mCurrPhotoCount;
    private int mMaxPhotoCount;


    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setCenterTv("相机胶卷");
        titleBar.setLeft(null, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putStringArrayListExtra("photoList", (ArrayList<String>) selectedPathList);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        titleBar.setRightTv("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putStringArrayListExtra("photoList", (ArrayList<String>) selectedPathList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }
    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_photo_list;
    }

    protected void initData() {
        super.initData();
        String albumName = getIntent().getStringExtra("albumName");
        selectedPathList = getIntent().getStringArrayListExtra("photoList");
        mCurrPhotoCount = getIntent().getIntExtra("currPhotoCount", 0);
        mMaxPhotoCount = getIntent().getIntExtra("maxPhotoCount", 0);
        mPhotoList = PhotoAlbum.getAlbumByName(albumName).photoList;
    }



    protected void initView() {
        super.initView();
        mPhotoGv =  findViewById(R.id.gridView);
        mPhotoGv.setAdapter(new PhotoAdapter(this, mPhotoList, selectedPathList,
                mCurrPhotoCount, mMaxPhotoCount));
    }
}
