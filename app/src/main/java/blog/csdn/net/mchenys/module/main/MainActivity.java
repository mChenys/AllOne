package blog.csdn.net.mchenys.module.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.base.BaseFragment;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.utils.PermissionUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.module.circle.CircleFragment;
import blog.csdn.net.mchenys.module.personal.PersonalFragment;
import blog.csdn.net.mchenys.module.special.SpecialFragment;


/**
 * 主界面
 * Created by mChenys on 2017/12/27.
 */

public class MainActivity extends BaseActivity {
    //tab选择器
    private int[] mTabImage = {R.drawable.selector_wish_tab, R.drawable.selector_recommend_tab,
            R.drawable.selector_find_tab, R.drawable.selector_personal_tab,
    };

    //tab选项卡的文字
    private String[] mTabTitle = {"首页", "专家", "圈子", "我的"};

    //每个tab对应的Fragment
    private Class[] fragmentArray = {HomeFragment.class, SpecialFragment.class, CircleFragment.class,
            PersonalFragment.class};

    private int position;//首次打开的tab位置
    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();
        position = getIntent().getIntExtra(Constant.KEY_POSITION, 0);
        //批量申请权限
        PermissionUtils.requestMultiPermission(this, new int[]{
                PermissionUtils.CODE_READ_EXTERNAL_STORAGE,
                PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.CODE_READ_PHONE_STATE,
        }, mPermissionGrant);
        //单个权限申请
        //   PermissionUtils.requestPermission(this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
    }

    @Override
    protected void initView() {
        super.initView();
        FragmentTabHost tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.real_tabcontent);
        tabHost.getTabWidget().setDividerDrawable(null); //去掉分割线
        for (int i = 0; i < fragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(String.valueOf(i)).setIndicator(getTabItemView(i));
            Bundle bundle = new Bundle();
            bundle.putString(Constant.KEY_TITLE, mTabTitle[i]);
            bundle.putInt(Constant.KEY_POSITION, i);
            tabHost.addTab(tabSpec, fragmentArray[i], bundle); //添加tab和关联对应的fragment

            //设置tabView相关
            View tabView = tabHost.getTabWidget().getChildAt(i);
            tabView.setId(i);
            tabView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tabView.setOnTouchListener(mTabTouchListener);
        }
        tabHost.setCurrentTab(position);  //默认选中第一个tab
        //设置tab的切换监听
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                position = Integer.valueOf(tabId);
            }
        });
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_tab, null);
        ImageView imageView = view.findViewById(R.id.iv_tab);
        TextView textView = view.findViewById(R.id.tv_tab);
        imageView.setImageResource(mTabImage[index]);
        textView.setTextColor(getResources().getColorStateList(R.color.selector_tab_text_color));
        if (TextUtils.isEmpty(mTabTitle[index])) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mTabTitle[index]);
        }
        return view;
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_RECORD_AUDIO:
                    Toast.makeText(mContext, "录音权限已开启", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_GET_ACCOUNTS:
                    Toast.makeText(mContext, "获取联系人信息权限已开启", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_PHONE_STATE:
                    Toast.makeText(mContext, "获取手机态权限权限已开启", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CALL_PHONE:
                    Toast.makeText(mContext, "拨打电话权限已开启", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CAMERA:
                    Toast.makeText(mContext, "访问摄像头权限已开启", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
                    Toast.makeText(mContext, "GPS定位权限已开启", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_COARSE_LOCATION:
                    Toast.makeText(mContext, "网络定位权限已开启", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    Toast.makeText(mContext, "本地文件读取权限已开启", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    Toast.makeText(mContext, "文本文件写入权限已开启", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_MUlTI_PERMISSION:
                    //Toast.makeText(mContext, "相关权限已开启成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);

    }

    View.OnTouchListener mTabTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int id = v.getId();
            if (position == id) {
                //重复点击的处理
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(String.valueOf(id));
                    if (null != fragment) {
                        fragment.autoRefresh(new Bundle());
                    }
                }
                return true;
            } else {//切换点击
                if (event.getAction() == MotionEvent.ACTION_DOWN) {}
            }
            return false;
        }
    };
}
