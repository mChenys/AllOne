package blog.csdn.net.mchenys.module.main;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.PreferencesUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;
import blog.csdn.net.mchenys.common.widget.focusimg.FocusCircleView;
import blog.csdn.net.mchenys.common.widget.focusimg.FragmentPagerAdapterCompat;
import blog.csdn.net.mchenys.common.widget.focusimg.ImageViewPager;
import blog.csdn.net.mchenys.common.widget.layout.RefreshLayout;
import blog.csdn.net.mchenys.common.widget.pageindicator.TabPageIndicator;
import blog.csdn.net.mchenys.common.widget.view.UEView;
import blog.csdn.net.mchenys.model.HomeFocus;
import blog.csdn.net.mchenys.model.SubColumn;


/**
 * 首页
 * Created by mChenys on 2017/12/28.
 */

public class HomeFragment extends BaseFragment {
    private SubColumn mSubColumn;
    private TabPageIndicator mTabIndicator;
    private ViewPager mVpContent;
    private UEView mUeView;
    private List<HomeFocus> mFocusList = new ArrayList<>();
    private ImageViewPager mImageViewPager;
    private ImageViewPager.BasePagerAdapter mFocusAdapter;
    private FocusCircleView mFocusCircleView;

    private RefreshLayout mRefreshLayout;
    private FragmentPagerAdapterCompat mPagerAdapterCompat;

    private List<SubColumn.LivingColumnPojo> mSubColumnList = new ArrayList<>();


    protected void initParams() {
        super.initParams();
        String subColumnJsonStr = PreferencesUtils.getPreference(mContext, Constant.PREFERENCES_SUBCOLUMN, Constant.PREFERENCES_KEY_SUBCOLUMN,null);
        if (!StringUtils.isEmpty(subColumnJsonStr)) {
            Gson gson = new Gson();
            try {
                mSubColumn = gson.fromJson(subColumnJsonStr, SubColumn.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (mSubColumn != null) {
                mSubColumnList = mSubColumn.livingColumn;
            }
        }
    }
    @Override
    protected Integer getLayoutResID() {
        return R.layout.fragment_home;
    }


    protected void initView() {
        super.initView();
        //焦点图
        mImageViewPager =  findViewById(R.id.imageViewPager);
        mFocusCircleView = findViewById(R.id.FocusCircleView);
        mImageViewPager.setAdapter(mFocusAdapter = new ImageViewPager.BasePagerAdapter<HomeFocus>(mContext, mFocusList) {
            @Override
            public View getItemView(Context context, int position, HomeFocus focus) {
                View view = LayoutInflater.from(context).inflate(R.layout.focus_view_item, null);
                TextView adLabel =  view.findViewById(R.id.tv_ad);
                ImageView imageView =  view.findViewById(R.id.focus_view);
                adLabel.setVisibility(View.GONE);
                if (focus != null) {
                    ImageLoadUtils.disPlay(focus.image, imageView);
                }
                return view;
            }

        });
        mImageViewPager.setCurrentItem(Integer.MAX_VALUE / 2 - 3);

        //导航栏
        mTabIndicator = findViewById(R.id.tab_indicator);
        mTabIndicator.setDividerVisible(false);
        //异常页面
        mUeView = findViewById(R.id.UEView);

        //下拉刷新
        mRefreshLayout =findViewById(R.id.refreshLayout);
        //内容视图
        mVpContent =  findViewById(R.id.vp_content);
    }

    @Override
    protected void initListener() {
        super.initListener();
        //焦点图滑动
        mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mFocusList != null && !mFocusList.isEmpty()) {
                    position = position % mFocusList.size();
                    mFocusCircleView.setCurrentFocus(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //下拉刷新
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != mPagerAdapterCompat) {
                    LiveInstituteFragment fragment = (LiveInstituteFragment) mPagerAdapterCompat.getFragment(mVpContent.getCurrentItem());
                    if (null != fragment) {
                        fragment.startRefresh();
                    }
                }
            }
        });
    }

    public void finishRefresh(boolean isLoadMore) {
        Log.e("cys", "finishRefresh");
        if(!isLoadMore)
            mRefreshLayout.onComplete();
    }

    class BasePagerAdapter extends FragmentPagerAdapterCompat {

        public BasePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mSubColumnList == null ? 0 : mSubColumnList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mSubColumnList == null ? null : mSubColumnList.get(position).name;
        }

        @Override
        public Fragment getItem(int position) {
            return LiveInstituteFragment.newInstance(mSubColumnList.get(position).id);
        }
    }

    protected void loadData() {
        super.loadData();
        if (mSubColumnList != null && !mSubColumnList.isEmpty()) {
            mVpContent.setAdapter(mPagerAdapterCompat = new BasePagerAdapter(getChildFragmentManager()));
            mTabIndicator.setViewPager(mVpContent);
            mTabIndicator.notifyDataSetChanged();
            mTabIndicator.setCurrentItem(0);
        } else {
            mUeView.showNoData();
        }
    }


    public void parseFocusList(JSONObject object) {
       /* JSONArray array = object.optJSONArray("focusInfo");
        if (null != array) {
            List<HomeFocus> temp = HomeFocus.parseList(array);
            if (!temp.isEmpty()) {
                mFocusList.clear();
                mFocusList.addAll(temp);
                mFocusAdapter.notifyDataSetChanged();
                mFocusCircleView.setCount(mFocusAdapter.getDataCount());
                mFocusCircleView.setCurrentFocus(mImageViewPager.getCurrentItem() % mFocusList.size());
                findViewById(R.id.rl_focus).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.rl_focus).setVisibility(View.GONE);
            }
        }else{
            findViewById(R.id.rl_focus).setVisibility(View.GONE);
        }*/

       //test
        List<HomeFocus> temp = HomeFocus.getTest();
        mFocusList.clear();
        mFocusList.addAll(temp);
        mFocusAdapter.notifyDataSetChanged();
        mFocusCircleView.setCount(mFocusAdapter.getDataCount());
        mFocusCircleView.setCurrentFocus(mImageViewPager.getCurrentItem() % mFocusList.size());
        findViewById(R.id.rl_focus).setVisibility(View.VISIBLE);


    }

    @Override
    public void onResume() {
        super.onResume();
        mImageViewPager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageViewPager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageViewPager.onDestory();
    }
}
