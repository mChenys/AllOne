package blog.csdn.net.mchenys;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import blog.csdn.net.mchenys.module.demo.espresso.EspressoActivity;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by mChenys on 2019/1/28.
 */
@RunWith(AndroidJUnit4.class)
public class TestRecycleView {
    @Rule
    public ActivityTestRule<EspressoActivity> mActivityRule = new ActivityTestRule<>(EspressoActivity.class);

    @Test
    public void testRecycleView() {
        //通过文本RecycleView找到按钮，并执行点击事件，跳转到RecycleviewActivity
        Espresso.onView(withText("RecycleView")).perform(click());
        //通过文本"Item 0"找到view，并检查是否显示，然后执行点击事件 ，此时会弹出对话框
        Espresso.onView(withText("Item 0")).check(matches(isDisplayed())).perform(click());
        //通过文本"确定"找到对话框上的确定按钮，执行点击事件，关闭对话框
        Espresso.onView(withText("确定")).perform(click());
        //通过文本"Item 2"找到view，并检查是否显示，然后执行点击事件，此时会弹出对话框
        Espresso.onView(withText("Item 2")).check(matches(isDisplayed())).perform(click());
        //执行点击返回按钮事件，关闭对话框
        Espresso.pressBack();
        //通过id找到recycleview，然后执行滑动事件，滑动到21项,要对Recycle进行操作，先要修改bulid.gradle引入一些包
        Espresso.onView(ViewMatchers.withId(R.id.recycleview)).perform(RecyclerViewActions.scrollToPosition(21));
        //通过文本"Item 20"找到view，并检查是否显示，然后执行点击事件，此时会弹出对话框
        Espresso.onView(withText("Item 20")).check(matches(isDisplayed())).perform(click());
        //通过文本"确定"找到对话框上的确定按钮，执行点击事件，关闭对话框
        Espresso.onView(withText("确定")).perform(click());
        //执行点击返回按钮事件，关闭跳转到RecycleviewActivity
        Espresso.pressBack();
    }
}
