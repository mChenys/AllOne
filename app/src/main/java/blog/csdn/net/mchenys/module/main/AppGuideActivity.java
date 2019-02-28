package blog.csdn.net.mchenys.module.main;

import android.view.View;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;


/**
 * 欢迎界面
 * Created by mChenys on 2017/12/27.
 */

public class AppGuideActivity extends BaseActivity {
    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setVisibility(View.GONE);
    }

    @Override
    protected Integer getLayoutResID() {
        return R.layout.activity_app_guide;
    }

    @Override
    protected void initView() {
        super.initView();
        transStatusBar(false);
    }

    public void toMainActivity(View view) {
        JumpUtils.startActivity(this, MainActivity.class);
        finish();
    }
}
