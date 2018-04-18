package blog.csdn.net.mchenys.common.photo;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;


/**
 * 相册列表
 * 调用方式
 * Intent intent = new Intent(this, AlbumListActivity.class);
 * intent.putExtra("currPhotoCount", imageList.size());
 * intent.putExtra("maxPhotoCount", mMaxPhotoCount);
 * startActivityForResult(intent, ConstantsModern.REQ_ALBUM_CODE);
 *
 * Created by mChenys on 2016/11/3.
 */
public class AlbumListActivity extends BaseActivity {
    private static final String TAG = "AlbumListActivity";
    private ListView mListView;
    private ProgressBar mProgressBar;
    private AlbumAdapter mAdapter;
    private List<PhotoAlbum> mPhotoAlbumList = new ArrayList<>();//相册集合
    private List<String> selectedPathList = new ArrayList<String>(); //存放用户从相册中选择的图片
    private int mCurrPhotoCount;
    private int mMaxPhotoCount;

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setLeft(null, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, getIntent());
                finish();
            }
        });
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_album_list;
    }

    protected void initData() {
        super.initData();
        mCurrPhotoCount = getIntent().getIntExtra("currPhotoCount", 0);
        mMaxPhotoCount = getIntent().getIntExtra("maxPhotoCount", 0);

    }

    protected void initView() {
        super.initView();
        mListView = findViewById(R.id.bbs_posts_list);
        mProgressBar = findViewById(R.id.pb_loading);
        mAdapter = new AlbumAdapter(this, mPhotoAlbumList);
        mListView.setAdapter(mAdapter);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void loadData() {
        super.loadData();
        PhotoAlbum.queryAlbum(this, new PhotoAlbum.Callback() {

            @Override
            public void onComplete(List<PhotoAlbum> albumList) {
                mPhotoAlbumList.clear();
                mPhotoAlbumList.addAll(albumList);
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    protected void initListener() {
        super.initListener();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AlbumListActivity.this, PhotoListActivity.class);
                intent.putExtra("albumName", mPhotoAlbumList.get(position).albumName);//当前选中的相册名字
                intent.putStringArrayListExtra("photoList", (ArrayList<String>) selectedPathList);//已选择的相片
                intent.putExtra("currPhotoCount", mCurrPhotoCount);
                intent.putExtra("maxPhotoCount", mMaxPhotoCount);
                startActivityForResult(intent, 200);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                // 相册选择结果返回
                setResult(RESULT_OK, data);
                finish();
                break;
            case RESULT_CANCELED: //取消不作处理
//                selectedPathList = data.getStringArrayListExtra("photoList");
                break;
        }

    }
}
