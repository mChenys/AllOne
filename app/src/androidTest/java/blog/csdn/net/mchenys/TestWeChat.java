package blog.csdn.net.mchenys;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by mChenys on 2019/1/29.
 */
@RunWith(AndroidJUnit4.class)

public class TestWeChat {


    @Test
    public void start()throws Exception {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        while (true){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //测试登录
            testLogin(uiDevice);
            //等待登录
            uiDevice.wait(Until.hasObject(By.pkg("com.tencent.mm").depth(0)),5000);
            //测试朋友圈刷新
            testFriendCircleRefresh(uiDevice);

        }

    }

    private void testFriendCircleRefresh(UiDevice uiDevice) throws Exception {
        UiObject find = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/cw3").text("发现"));
        if(find.exists()&& find.click()){
            boolean open = uiDevice.findObject(new UiSelector().resourceId("android:id/title").text("朋友圈")).click();
            if (open) {
                UiScrollable listView = new UiScrollable(new UiSelector().resourceId("com.tencent.mm:id/e2s").className(ListView.class));
                if(listView.exists()){
                    int w = uiDevice.getDisplayWidth()/2;
                    //steps：是指分多少次完成这次动作。每次移动花费的时间是固定的，都为5ms。
                    if (uiDevice.drag(w, w, 0, 1000, 10)) {
                        uiDevice.wait(Until.hasObject(By.pkg("com.tencent.mm").depth(0)),1000);
                        uiDevice.pressBack();
                    }
                }
            }
        }
    }

    public void testLogin(UiDevice uiDevice) throws Exception {
        Context context = InstrumentationRegistry.getContext();
        //启动微信
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        //等待启动
        uiDevice.wait(Until.hasObject(By.pkg("com.tencent.mm").depth(0)),5000);
        //查找欢迎界面的登录按钮
        UiObject wellComeLogin = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/drq").text("登录"));
        if(wellComeLogin.exists()&& wellComeLogin.click()){
            //使用其他账号登录
            userOtherLogin(uiDevice);
        }else{
            UiObject accountName = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/ch4").className(TextView.class));
            if (accountName.exists() && accountName.getText().equals("130 2536 2851")) {
                UiObject passwordEdt = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/ji").className(EditText.class));
                if(passwordEdt.exists()){
                    passwordEdt.setText("cys911014..");
                    //登录
                    uiDevice.findObject(By.res("com.tencent.mm:id/ch7").text("登录")).click();
                }
            }else{
                UiObject more = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/chd").text("更多"));
                if(more.exists()&& more.click()){
                    UiObject otherLogin = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/cj").text("登录其他帐号"));
                    if (otherLogin.exists() && otherLogin.click()) {
                        userOtherLogin(uiDevice);
                    }
                }

            }

        }

    }

    private void userOtherLogin(UiDevice uiDevice) throws UiObjectNotFoundException {
        //查找账号登录
        UiObject loginByAccount = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/ch6").text("用微信号/QQ号/邮箱登录"));
        if(loginByAccount.exists()&& loginByAccount.click()){
            //账号输入框
            List<UiObject2> list = uiDevice.findObjects(By.res("com.tencent.mm:id/ji"));
            if(list.size()==2){
                list.get(0).setText("13025362851");
                list.get(1).setText("cys911014..");

                //点击登录
                UiObject aLogin = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/ch7").text("登录").className(Button.class));
                if(aLogin.exists()&&aLogin.click()){
                    uiDevice.wait(Until.hasObject(By.pkg("com.tencent.mm").depth(0)),1000);
                    UiObject yes = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/au_"));
                    if(yes.exists())
                        yes.click();
                }
            }
        }
    }
}
