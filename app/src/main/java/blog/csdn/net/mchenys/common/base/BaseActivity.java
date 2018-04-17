package blog.csdn.net.mchenys.common.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.utils.AppUtils;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
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
    private TitleBar mTitleBar;
    private FrameLayout mContentLayout;
    private View mNightMode;

    private boolean isPush;
    private long exitTime;
    protected Context mContext;

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
        mTitleBar = findViewById(R.id.title_bar);
        mContentLayout = findViewById(R.id.fl_content);
        mNightMode = findViewById(R.id.view_night);
        setTitleBar(mTitleBar);
        if (null != getLayoutResID()) {
            View.inflate(this, getLayoutResID(), mContentLayout);
        }
    }

    protected void initListener() {

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
        if (onJpushBackPress()) {
            return;
        }
        if (isHomePage()) {
            onHomePageBackPress();
        } else {
            this.finish();
        }
    }

    private boolean onJpushBackPress() {
        if (isPush) {
            isPush = false;
            if (!AppUtils.isRunning(this)) {//app未启动
                if (isHomePage()) { //打开的是主页,提示是否要退出App
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                        Toast.makeText(this, getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
                    }
                } else { //打开的是二级及以上的页面,返回需要启动MainActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    JumpUtils.toActivity(this, intent);
                    finish();
                }
                return true;
            }

        }
        return false;
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
}
