package com.mercandalli.android.apps.files;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercandalli.android.apps.files.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.mercandalli.android.apps.files.TestUtils.actionOpenDrawer;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void navigateIntoDrawer() {
        onView(withId(R.id.activity_main_drawer_layout)).perform(actionOpenDrawer());
        onView(withId(R.id.view_nav_drawer_workspace)).perform(click());
        onView(withId(R.id.activity_main_drawer_layout)).perform(actionOpenDrawer());
        onView(withId(R.id.view_nav_drawer_files)).perform(click());
    }
}
