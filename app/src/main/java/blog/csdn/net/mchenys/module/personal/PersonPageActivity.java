package blog.csdn.net.mchenys.module.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.sns.bean.SnsShareContent;
import blog.csdn.net.mchenys.common.utils.ImageLoadUtils;
import blog.csdn.net.mchenys.common.utils.ShareUtils;
import blog.csdn.net.mchenys.common.utils.ToastUtils;
import blog.csdn.net.mchenys.common.widget.focusimg.FragmentPagerAdapterCompat;
import blog.csdn.net.mchenys.common.widget.layout.PinkNestedScrollView2;
import blog.csdn.net.mchenys.common.widget.pageindicator.TabPageIndicator;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.UserInfo;
import blog.csdn.net.mchenys.module.designer.DesignerIdentityActivity;


/**
 * 用户主页(设计师和普通用户主页)
 * Created by mChenys on 2018/8/9.
 */

public class PersonPageActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager mVpContent;
    private FragmentPagerAdapterCompat mPagerAdapterCompat;
    private TabPageIndicator mTabIndicator;
    private PinkNestedScrollView2 mScrollParent2;
    private RelativeLayout mTitleRl;
    private ImageView mIvBack, mIvShare;
    private TextView mTitleTv;
    private String userId;
    private SnsShareContent mSnsShareContent;

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_personal_page;
    }

    public static void start(Context context, String userId) {
        Intent intent = new Intent(context, PersonPageActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    public static void start(Context context, String userId, boolean isPush) {
        Intent intent = new Intent(context, PersonPageActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isPush", isPush);
        context.startActivity(intent);
    }

    @Override
    protected void initData() {
        super.initData();
        userId = getIntent().getStringExtra("userId");
    }

    @Override
    protected void initView() {
        super.initView();
        //内容视图
        mVpContent = (ViewPager) findViewById(R.id.vp);
        mVpContent.setAdapter(mPagerAdapterCompat = new FragmentPagerAdapterCompat(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return position == 0 ? HouseCaseFragment.newInstance(userId) :
                        new UserInfoFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "整屋案例" : "个人资料";
            }
        });
        mTabIndicator = (TabPageIndicator) findViewById(R.id.tab_indicator);
        mTabIndicator.setDividerVisible(false);
        mTabIndicator.setViewPager(mVpContent);
        mTabIndicator.notifyDataSetChanged();
        mTabIndicator.setCurrentItem(0);

        mTitleRl = (RelativeLayout) findViewById(R.id.rl_title);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mScrollParent2 = (PinkNestedScrollView2) findViewById(R.id.mnsp2);

    }

    @Override
    protected void initListener() {
        super.initListener();
        mScrollParent2.setOnTopScrollListener(mOnTopScrollListener);

        mIvBack.setOnClickListener(this);
        mIvShare.setOnClickListener(this);
        findViewById(R.id.tv_book_designer).setOnClickListener(this);

    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    //设置头部信息和个人资料
    public void setUserInfo(UserInfo userInfo) {
        UserInfoFragment userInfoFragment = (UserInfoFragment) mPagerAdapterCompat.getFragment(1);
        userInfoFragment.setData(userInfo);
        setTopView(userInfo);
    }

    //设置分享数据
    public void setShareData(JSONObject shareData) {
        if (null != shareData) {
            String shareUrl = shareData.optString("shareUrl");
            String hideContent = "#PCHouse#" + shareUrl;
            String shareTitle = shareData.optString("shareTitle");
            String content = shareData.optString("sharePreview");
            String shareImg = shareData.optString("shareImg");
            mSnsShareContent = ShareUtils.wrapShareContent(shareTitle, content, content, hideContent, shareUrl, shareImg);


        }

    }

    private void setTopView(UserInfo userInfo) {
        ImageView headerIv = (ImageView) findViewById(R.id.iv_header);
        ImageLoadUtils.disPlay(userInfo.userFace, headerIv);


        TextView usernameTv = (TextView) findViewById(R.id.tv_username);
        usernameTv.setText(userInfo.nickName);
        mTitleTv.setText(userInfo.nickName);

        ImageView designerIv = (ImageView) findViewById(R.id.iv_designer);
        designerIv.setVisibility(userInfo.isDesigner == 1 ? View.VISIBLE : View.GONE);

        TextView fansTv = (TextView) findViewById(R.id.tv_fans);
        TextView focusTv = (TextView) findViewById(R.id.tv_focus);
        TextView caseTv = (TextView) findViewById(R.id.tv_case);
        fansTv.setText("粉丝 " + userInfo.fansCount);
        focusTv.setText("关注 " + userInfo.focusedCount);
        caseTv.setText("案例 " + userInfo.caseCount);
        fansTv.setOnClickListener(this);
        focusTv.setOnClickListener(this);
        caseTv.setOnClickListener(this);

        TextView addFocusTv = (TextView) findViewById(R.id.tv_add_focus);
        addFocusTv.setOnClickListener(this);

        TextView bookDesignerTv = (TextView) findViewById(R.id.tv_book_designer);
        bookDesignerTv.setOnClickListener(this);
    }


    PinkNestedScrollView2.OnTopScrollListener mOnTopScrollListener = new PinkNestedScrollView2.OnTopScrollListener() {
        @Override
        public void onScroll(float scrollY, float maxScrollY) {

            if (scrollY <= 0) {
                mIvBack.setImageResource(R.drawable.app_left_return_white);
                mIvShare.setImageResource(R.drawable.ic_share_white_new);
                mTitleTv.setVisibility(View.GONE);
                mTitleRl.setBackgroundColor(Color.argb(0, 255, 255, 255));
            } else if (scrollY > 0 && scrollY <= 250) {
                float scale = scrollY / 250;
                int alpha = (int) (255 * scale);
                mTitleRl.setBackgroundColor(Color.argb(alpha, 255, 255, 255));
                mTitleTv.setVisibility(alpha > 230 ? View.VISIBLE : View.GONE);
            } else {
                mTitleRl.setBackgroundColor(Color.argb(255, 255, 255, 255));
                mTitleTv.setVisibility(View.VISIBLE);
                mIvBack.setImageResource(R.drawable.app_left_return);
                mIvShare.setImageResource(R.drawable.ic_share_black_new);
            }
        }


    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_share:
                ShareUtils.share(this, mSnsShareContent, null);
                break;
            case R.id.tv_fans:
                ToastUtils.showShort(mContext, "粉丝");
                break;
            case R.id.tv_focus:
                ToastUtils.showShort(mContext, "关注");
                break;
            case R.id.tv_case:
                ToastUtils.showShort(mContext, "案例");
                break;
            case R.id.tv_add_focus:
                ToastUtils.showShort(mContext, "加关注");
                break;
            case R.id.tv_book_designer:
                 startActivity(new Intent(this, DesignerIdentityActivity.class));
                break;
        }
    }
}
