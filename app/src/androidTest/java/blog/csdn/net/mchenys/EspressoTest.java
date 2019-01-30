package blog.csdn.net.mchenys;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import blog.csdn.net.mchenys.module.demo.espresso.EspressoActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

/**
 * espresso 只能操作本应用的UI,需要指定Activity
 * Created by mChenys on 2019/1/28.
 */
@RunWith(AndroidJUnit4.class)
public class EspressoTest {

    //指定Activity
    @Rule
    public ActivityTestRule<EspressoActivity> mActivityRule = new ActivityTestRule<>(EspressoActivity.class);

    @Test
    public void testHelloWorldTextView() {
        onView(allOf(withText("Hello World!"), instanceOf(TextView.class))).check(matches(isDisplayed()));
    }

    @Test
    public void test() {
        //通过id找到edittext，在里面输入2并关闭输入法
        onView(ViewMatchers.withId(R.id.editText)).perform(ViewActions.typeText("2"), ViewActions.closeSoftKeyboard());
        //通过id找到edittext，在里面输入5并关闭输入法
        onView(ViewMatchers.withId(R.id.editText2)).perform(ViewActions.typeText("5"), ViewActions.closeSoftKeyboard());
        //通过id找到button，执行点击事件
        onView(ViewMatchers.withId(R.id.button)).perform(click());
        //通过id找到textview，并判断是否与文本匹配
        onView(ViewMatchers.withId(R.id.textView)).check(matches(withText("计算结果：7")));
    }



    /**
     * 根据 id 字符串获取真实 id
     * @param idStr
     * @return
     */
    private static int getResourceId(String idStr,String packageName) {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        return targetContext.getResources().getIdentifier(idStr, "id", packageName);
    }
}


