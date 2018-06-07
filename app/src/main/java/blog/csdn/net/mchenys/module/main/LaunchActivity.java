package blog.csdn.net.mchenys.module.main;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;
import blog.csdn.net.mchenys.model.SubColumn;


/**
 * 启动界面
 * Created by mChenys on 2017/12/27.
 */

public class LaunchActivity extends BaseActivity {

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_launch;
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        SubColumn.requestData(this);
        //解决下载安装app并打开,HOME键回到桌面,点击桌面图标打开APP时,APP重新打开启动页问题
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        //模拟 2s跳转
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JumpUtils.startActivity(LaunchActivity.this, AppGuideActivity.class);
                LaunchActivity.this.finish();
            }
        }, 2000);
    }


}
