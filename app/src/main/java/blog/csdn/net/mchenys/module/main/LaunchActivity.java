package blog.csdn.net.mchenys.module.main;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.base.BaseActivity;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.HttpUtils;
import blog.csdn.net.mchenys.common.utils.JumpUtils;
import blog.csdn.net.mchenys.common.utils.PreferencesUtils;
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
        //获取省市数据
        loadAllProvinceCities();
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

    @Override
    protected void initView() {
        super.initView();
        transStatusBar(false);
    }

    private void loadAllProvinceCities() {
        HttpUtils.getJSON(true, Urls.GET_ALL_PROVINCE_CITIES, null, null, new HttpUtils.JSONCallback() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onSuccess(JSONObject jsonObject, OkResponse okResponse) {
                int status = jsonObject.optInt("status");
                if (status == 1) {
                    try {
                        String provinceCities = jsonObject.getJSONArray("data").toString();
                        PreferencesUtils.setPreferences(getApplicationContext(), "pre_province_cities", "key_province_cities", provinceCities);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
