package blog.csdn.net.mchenys.module.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.LogUtils;

/**
 * Created by mChenys on 2019/4/19.
 */

public class ParseDeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_deep_link);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        LogUtils.e("cys", "appLinkData:" + appLinkData);

        TextView textView = findViewById(R.id.tv_link);
        textView.setText("appLinkData:" + appLinkData);
    }
}
