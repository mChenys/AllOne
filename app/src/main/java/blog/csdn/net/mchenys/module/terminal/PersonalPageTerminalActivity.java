package blog.csdn.net.mchenys.module.terminal;

import android.content.Context;
import android.content.Intent;

import blog.csdn.net.mchenys.common.base.BaseTerminalActivity;
import blog.csdn.net.mchenys.common.config.Urls;

/**
 * Created by mChenys on 2018/6/15.
 */
@Deprecated
public class PersonalPageTerminalActivity extends BaseTerminalActivity {
    private String userId;

    @Override
    protected void initData() {
        super.initData();
        if (getIntent() != null) {
            userId = getIntent().getStringExtra("userId");
        }
        url = Urls.DESIGNER_PERSONAL_PAGE + "?userId=" + userId + "&v2";
    }

    public static void start(Context context, String userId) {
        Intent intent = new Intent(context, PersonalPageTerminalActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }
}
