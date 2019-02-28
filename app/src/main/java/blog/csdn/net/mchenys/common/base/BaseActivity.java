package blog.csdn.net.mchenys.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.utils.ScreenShotListenManager;
import blog.csdn.net.mchenys.common.utils.UIUtils;
import blog.csdn.net.mchenys.common.widget.dialog.ScreenShotDialog;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.module.main.MainActivity;


/**
 * 基类
 * Useage:<p>
 * getLayoutResID():设置布局id<p>
 * initData():初始化数据<p>
 * initView():初始化View<p>
 * initListener():初始化监听<p>
 * loadData():加载数据<p>
 * <p>
 * Created by mChenys on 2017/12/27.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected View mRootView;
    private TitleBar mTitleBar;
    private FrameLayout mContentLayout;
    private View mNightMode;

    private boolean isPush;
    private long exitTime;
    protected Activity mContext;
    //默认开启点击软键盘以外的区域收起软键盘
    private boolean enableAutoCloseSoftInput = true;

    //截屏监听
    private ScreenShotListenManager mScreenShotListenManager;
    private ScreenShotDialog mShotDialog;

    /**
     * 子类别重写,可以重写initData initView initListener
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        initData();
        initView();
        initListener();
        loadData();
    }


    protected abstract Integer getLayoutResID();

    protected void initData() {
        initPush();
    }

    protected void initView() {
        mRootView = findViewById(R.id.fl_root_layout);
        mTitleBar = findViewById(R.id.title_bar);
        mContentLayout = findViewById(R.id.fl_content);
        mNightMode = findViewById(R.id.view_night);
        setTitleBar(mTitleBar);
        boolean needTrans = mTitleBar.getVisibility() == View.GONE;
        transStatusBar(needTrans);//修改状态栏是否透明,默认如果TitleBar不可见则透明
        if (null != getLayoutResID()) {
            View.inflate(this, getLayoutResID(), mContentLayout);
        }
        mShotDialog = new ScreenShotDialog(this);
        mScreenShotListenManager = ScreenShotListenManager.newInstance(this);
    }

    protected void initListener() {

        mScreenShotListenManager.setListener(
                new ScreenShotListenManager.OnScreenShotListener() {
                    public void onShot(String imagePath) {
                        Log.e("cys", "截屏路径imagePath:" + imagePath);
                        mShotDialog.show(imagePath);
                    }
                }
        );
        mScreenShotListenManager.startListen();
    }

    protected void loadData() {

    }

    private void initPush() {
        Intent intent = this.getIntent();
        if (intent != null) {
            isPush = intent.getBooleanExtra(Constant.KEY_PUSH, false);
            if (isPush) {
                Bundle bundle = intent.getExtras();
                String geTastId = bundle.getString(Constant.GE_TASKID, "");
                String geMsgId = bundle.getString(Constant.GE_MSGID, "");
                //  PushManager.getInstance().sendFeedbackMessage(this, geTastId, geMsgId, 90001);
            }
        }
    }

    /**
     * 是否要透明状态栏,如果透明,则布局会延伸到状态栏下
     *
     * @param trans 是否透明
     */
    public void transStatusBar(boolean trans) {
        if (!trans) {
            mRootView.setFitsSystemWindows(true);
            UIUtils.setStatusBarColor(this, R.color.white);
            UIUtils.setLightStatusBar(this, true);
        } else {
            UIUtils.setStatusBarTrans(this);
            mRootView.setFitsSystemWindows(false);
        }
    }

    /**
     * 用于添加蒙层,或者其他view覆盖在主视图之上
     *
     * @param view
     */
    public void addContentView(View view) {
        mContentLayout.addView(view);
    }

    /**
     * 移除主视图之上的其他view
     *
     * @param view
     */
    public void removeContentView(View view) {
        mContentLayout.removeView(view);
    }

    /**
     * 移除主视图之上的第几个view
     *
     * @param index
     */
    public void removeContentViewAt(int index) {
        mContentLayout.removeViewAt(index);
    }

    /**
     * 夜间模式切换
     *
     * @param on true 打开
     */
    public void toggleNightMode(boolean on) {
        mNightMode.setVisibility(on ? View.VISIBLE : View.GONE);
    }

    public abstract void setTitleBar(TitleBar titleBar);

    @Override
    public void onBackPressed() {
        if (isHomePage()) {
            onHomePageBackPress();
        } else if (isPush) {
            isPush = false;
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            JumpUtils.toActivity(this, intent);
            finish();
        } else {
            finish();
        }
    }

    /**
     * 判断是否是一级页面
     *
     * @return
     */
    private boolean isHomePage() {
        return this.getClass().getSimpleName().equals("MainActivity");
    }

    /**
     * 如果是一级页面,2s内按下back键2次退出app
     */
    private void onHomePageBackPress() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            exitTime = System.currentTimeMillis();
            Toast.makeText(this, getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
        } else {
            finish();
            System.exit(0);
        }
    }

    public void autoRefresh(Bundle bundle) {
        //empty
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScreenShotListenManager.stopListen();
    }

    //点击输入框以外的区域隐藏软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                if (enableAutoCloseSoftInput) {
                    if (hideInputMethod(this, v)) {
                        return true; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static Boolean hideInputMethod(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

}
