package blog.csdn.net.mchenys.common.photo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.photoview.OnPhotoTapListener;
import blog.csdn.net.mchenys.common.widget.photoview.PhotoView;

/**
 * PhotoPreviewActivity中显示的图片
 */

public class PhotoFragment extends BaseFragment {

    private String url;
    private PhotoView mPhotoView;

    @Override
    protected Integer getLayoutResID() {
        return R.layout.layout_photo_view;
    }
    /**
     * 获取这个fragment需要展示图片的url
     *
     * @param url
     * @return
     */
    public static PhotoFragment newInstance(String url) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("url");
    }

    @Override
    protected void initView() {
        super.initView();
        mPhotoView = findViewById(R.id.photoview);
        //设置缩放类型，默认ScaleType.CENTER（可以不设置）
        mPhotoView.setScaleType(ImageView.ScaleType.CENTER);
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ToastUtils.showShort("长按事件");
                return true;
            }
        });
        mPhotoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                //点击事件
                ToastUtils.showShort("点击事件");
                mContext.finish();
            }
        });

        ImageLoadUtils.disPlayWithFitCenter(url,mPhotoView);

    }
}
