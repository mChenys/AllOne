package blog.csdn.net.mchenys.common.photo;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 类似微信朋友圈,大图预览效果
 * Created by mChenys on 2018/7/27.
 */

public class PhotoPreviewActivity2 extends BaseActivity {
    private ViewPager viewPager;
    private TextView tvNum;
    private ArrayList<String> urlList;

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_photo_preview;
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        //需要加载的网络图片
        String[] urls = {
                "http://a.hiphotos.baidu.com/image/pic/item/00e93901213fb80e3b0a611d3fd12f2eb8389424.jpg",
                "http://b.hiphotos.baidu.com/image/pic/item/5243fbf2b2119313999ff97a6c380cd790238d1f.jpg",
                "http://f.hiphotos.baidu.com/image/pic/item/43a7d933c895d1430055e4e97af082025baf07dc.jpg"
        };

        urlList = new ArrayList<>();
        Collections.addAll(urlList, urls);
    }

    @Override
    protected void initView() {
        super.initView();
        viewPager = findViewById(R.id.viewpager);
        tvNum = findViewById(R.id.tv_num);
        viewPager.setAdapter(new PicPageAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tvNum.setText(String.valueOf(position + 1) + "/" + urlList.size());
            }

            @Override
            public void onPageSelected(int position) {
                // 当图片滑动到下一页后，遍历当前所有加载过的PhotoView，恢复所有图片的默认状态大小
                int childCount = viewPager.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewPager.getChildAt(i);
                    try {
                        if (childAt != null && childAt instanceof PhotoView) {
                            PhotoView photoView = (PhotoView) childAt;// 得到viewPager里面的页面
                            //重新设置属性之后，当从当前放大页面滑动到下一页的时候，前面的放大页面就会重新成为设置的属性大小
                            new PhotoViewAttacher(photoView).setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
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
