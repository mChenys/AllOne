package blog.csdn.net.mchenys;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 自动添加微信好友
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private Instrumentation instrumentation;
    private UiDevice uiDevice;
    private String PKG_NAME = "com.tencent.mm";
    private int swipeCount = 0;

    @Test
    public void useAppContext() {
        int pid = android.os.Process.myPid();
        System.out.println("xxyy testcase 0 "+pid);
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance(instrumentation);

        while (true){
            System.out.println("xxyy running ");
            if(uiDevice.getCurrentPackageName() == null || !uiDevice.getCurrentPackageName().equals(PKG_NAME)){

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Context of the app under test.
                Context appContext = InstrumentationRegistry.getTargetContext();

                assertEquals("cn.com.pconline.autowechat", appContext.getPackageName());

                Context context = InstrumentationRegistry.getContext();
                Intent intent = context.getPackageManager()
                        .getLaunchIntentForPackage(PKG_NAME);
                // Clear out any previous instances
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

                uiDevice.wait(Until.hasObject(By.pkg(PKG_NAME).depth(0)),5000);
            }

            addNewFriend();

//            try {
//                uiDevice.dumpWindowHierarchy(new File("/storage/emulated/0/Pictures/n.xml"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

    }

    private void addNewFriend(){
        try {
            findTextAndClick("通讯录");
            Thread.sleep(1000);
            findTextAndClick("新的朋友");
            Thread.sleep(1000);

            UiObject2 uiObject1  = uiDevice.findObject(By.text("新的朋友"));
            if(uiObject1 != null){
                boolean isDone = false;
                int index = 0;
                List<UiObject2> list  = uiDevice.findObjects(By.text("接受"));
                for(UiObject2 uiObject2 : list){
                    UiObject2 uiObject21 = uiObject2.getParent().getParent().getChildren().get(0).getChildren().get(0); //昵称
                    String nickName = uiObject21.getText();
                    uiObject2.getParent().getParent().getChildren().get(0).click();
                    Thread.sleep(1000);
                    UiObject uiObject6  = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/b38")); //地区
                    String location = uiObject6.getText().replace("地区:","").replaceAll(" ","");
                    UiObject uiObject7  = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/k5"));
                    uiObject7.click();
                    Thread.sleep(1000);

                    //先判断是否加好友
//                    Request.Builder request = new Request.Builder();
//                    request.url("http://www.baidu.com");
//                    try {
//                        Response response = okHttpClient.newCall(request.build()).execute();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    uiObject2.click();
                    Thread.sleep(1000);
                    UiObject2 uiObject3  = uiDevice.findObject(By.text("朋友验证"));
                    if(uiObject3 != null){
                        UiObject2 uiObject4  = uiDevice.findObject(By.text("完成"));
                        if(uiObject4 != null){
                            uiObject4.click();
                        }
                    }
                    Thread.sleep(3000);
                    UiObject uiObject8  = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/k5"));
                    uiObject8.click();
                    Thread.sleep(1000);
                    index++;
                }
                if(list == null || list.size() == index){
                    uiDevice.swipe(200,200,100,100,5);
                    swipeCount++;
                    if(swipeCount > 100){
                        swipeCount = 0;
                        for (int i = 0; i<100;i++){
                            uiDevice.swipe(100,100,200,200,5);
                        }
                    }
                }
            }else {
                UiObject uiObject5  = uiDevice.findObject(new UiSelector().resourceId("com.tencent.mm:id/k5"));
                if(uiObject5 != null){
                    uiObject5.click();
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            System.out.println("xxyy err "+e.getMessage());
            e.printStackTrace();
        }

    }

    private void findTextAndClick(String text){
        UiObject2 uiObject2  = uiDevice.findObject(By.text(text));
        if(uiObject2 != null){
            uiObject2.click();
        }
    }

    private void findIDAndClick(String id){
        try {
            UiObject uiObject2  = uiDevice.findObject(new UiSelector().resourceId(id).instance(1));
            if(uiObject2 != null){
                uiObject2.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

    }
}
