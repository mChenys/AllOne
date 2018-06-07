package blog.csdn.net.mchenys.module.terminal;

import android.view.View;

import blog.csdn.net.mchenys.common.base.BaseTerminalActivity;
import blog.csdn.net.mchenys.common.config.Urls;
import blog.csdn.net.mchenys.common.widget.view.TitleBar;

/**
 * Created by mChenys on 2018/6/7.
 */

public class LiveColumnTerminalActivity extends BaseTerminalActivity {

    @Override
    public void setTitleBar(TitleBar titleBar) {
        titleBar.setLeft(null, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setCenterTv("专栏详情");
    }

    @Override
    protected void initData() {
        super.initData();
        setLoadType(TYPE_URL);
        String columnId = getIntent().getStringExtra("columnId");
        url = Urls.URL_ARTICLE + columnId + "?picRule=2&deviceType=android";

    }


}
