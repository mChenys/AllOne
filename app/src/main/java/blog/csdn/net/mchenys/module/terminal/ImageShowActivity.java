package blog.csdn.net.mchenys.module.terminal;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.photoview.OnOutsidePhotoTapListener;
import blog.csdn.net.mchenys.common.widget.photoview.OnPhotoTapListener;
import blog.csdn.net.mchenys.common.widget.photoview.PhotoView;

/**
 * 大图预览
 * Created by mChenys on 2019/5/5.
 */

public class ImageShowActivity extends BaseActivity {
    private ViewPager mViewPager;
    private TextView tvNum;
    private ArrayList<String> urlList;
    private int mCurrentIndex;

    @Override
    protected void initIntent() {
        super.initIntent();
        urlList = getIntent().getStringArrayListExtra(Constant.KEY_IMAGES);
        mCurrentIndex = getIntent().getIntExtra(Constant.KEY_POSITION, 0);
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_photo_preview;
    }

    @Override
    protected void initView() {
        super.initView();
        tvNum = findViewById(R.id.tv_num);
        tvNum.setText(String.valueOf(mCurrentIndex + 1) + "/" + urlList.size());
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new PicPageAdapter());
        mViewPager.setCurrentItem(mCurrentIndex);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tvNum.setText(String.valueOf(position + 1) + "/" + urlList.size());
            }
        });
    }

    class PicPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return urlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = (PhotoView) View.inflate(mContext, R.layout.layout_photo_view, null);
            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ToastUtils.showShort("长按事件");
                    return true;
                }
            });
            photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    //点击事件
                    finish();
                }
            });
            photoView.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
                @Override
                public void onOutsidePhotoTap(ImageView imageView) {
                    //点击事件
                    finish();
                }
            });
            ImageLoadUtils.disPlayWithFitCenter(urlList.get(position), photoView);
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
