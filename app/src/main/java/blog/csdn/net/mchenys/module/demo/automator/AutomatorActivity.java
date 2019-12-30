package blog.csdn.net.mchenys.module.demo.automator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.CmdUtils;

/**
 * 自动化测试,开启微信朋友圈自动刷新
 * Created by mChenys on 2019/1/30.
 */

public class AutomatorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automator);
        initListener();
    }
    private void initListener() {
        findViewById(R.id.openBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * as 执行测试用例后控制台输入的命令如下:
                         * adb shell am instrument -w -r   -e debug false -e class blog.csdn.net.mchenys.TestWeChat#start blog.csdn.net.mchenys.test/android.support.test.runner.AndroidJUnitRunner
                         *
                         * 其中#start中的start是测试方法的名称,可省略不写,#是连接符,表示某个类下面的意思
                         */
                        //模仿as的命令来执行cmd命令启动测试用例
//                        CmdUtils.execRootCmdSilent("am instrument -w -r   -e debug false -e class blog.csdn.net.mchenys.TestWeChat#start blog.csdn.net.mchenys.test/android.support.test.runner.AndroidJUnitRunner");
                        //或者
                        CmdUtils.execRootCmdSilent("am instrument -w -r   -e debug false -e class blog.csdn.net.mchenys.TestWeChat blog.csdn.net.mchenys.test/android.support.test.runner.AndroidJUnitRunner");

                        //使用系统自带方法启动测试用例,无效方法
                        //getApplication().startInstrumentation(new ComponentName("blog.csdn.net.mchenys", "android.test.TestWeChat"), null, null);

                    }
                }).start();
            }
        });
        findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CmdUtils.execRootCmdSilent("am force-stop blog.csdn.net.mchenys");
                    }
                }).start();
            }
        });

    }
}
