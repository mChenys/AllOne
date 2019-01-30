package blog.csdn.net.mchenys;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import blog.csdn.net.mchenys.module.demo.espresso.EspressoActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by mChenys on 2019/1/28.
 */
@RunWith(AndroidJUnit4.class)
public class TestIntent  {
    @Rule
    public IntentsTestRule<EspressoActivity> mIntentTestRule =
            new IntentsTestRule<EspressoActivity>(EspressoActivity.class);

    @Test
    public void triggerIntentTest() {
        onView(withId(R.id.switchActivity)).perform(click());
        intended(allOf(
                hasAction(Intent.ACTION_CALL),
                hasData("123456789"),
                toPackage(InstrumentationRegistry.getTargetContext().getPackageName()),
                hasExtra("input", "Test")
        ));
    }
}
