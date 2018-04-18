package blog.csdn.net.mchenys.common.photo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.utils.BitmapUtils;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;


/**
 * 大图查看
 * Created by mChenys on 2016/11/4.
 */
public class PhotoViewActivity extends BaseActivity {

    @Override
    protected Integer getLayoutResID() {
        return null;
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected void initView() {
        super.initView();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        ImageView imageView = new ImageView(PhotoViewActivity.this);
        setContentView(imageView);
        String photoPath = getIntent().getStringExtra("photo");
        showBitImage(photoPath, imageView);
    }

    private void showBitImage(String photoPath, final ImageView imageView) {
        if (URLUtil.isNetworkUrl(photoPath)) {
            ImageLoadUtils.disPlay(photoPath, imageView);
        } else if (!StringUtils.isEmpty(photoPath)) {
            imageView.setImageBitmap(BitmapUtils.compressAndResize(photoPath, 0, 0, 1 * 1024));
        } else {
            imageView.setImageResource(R.drawable.bg_loading);
        }
    }
}
