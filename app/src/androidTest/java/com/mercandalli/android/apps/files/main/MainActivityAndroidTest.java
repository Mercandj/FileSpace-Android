package com.mercandalli.android.apps.files.main;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercandalli.android.apps.files.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.mercandalli.android.apps.files.UtilsAndroidTest.actionOpenDrawer;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityAndroidTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void scrollLocalFilesAndClick() {
        // Find obj.
        final ViewInteraction viewInteraction = onView(withId(R.id.fragment_file_files_recycler_view));

        // SwipeLeft.
        viewInteraction.perform(swipeUp());
        viewInteraction.perform(swipeDown());

        onView(withId(R.id.fragment_file_files_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void showNotesThenShowFiles() {

        // Find obj.
        final ViewInteraction drawerLayout = onView(withId(R.id.activity_main_drawer_layout));

        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.SECONDS);

        //drawerLayout.perform(actionCloseDrawer());

        //IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.SECONDS);

        // Go to workspace.
        drawerLayout.perform(actionOpenDrawer());
        IdlingPolicies.setIdlingResourceTimeout(400, TimeUnit.MILLISECONDS);
        onView(withId(R.id.view_nav_drawer_workspace)).perform(click());
        //onView(withId(R.id.fragment_workspace_note_input)).check(matches(isDisplayed()));

        IdlingPolicies.setIdlingResourceTimeout(800, TimeUnit.MILLISECONDS);

        // Go to files.
        drawerLayout.perform(actionOpenDrawer());
        IdlingPolicies.setIdlingResourceTimeout(400, TimeUnit.MILLISECONDS);
        onView(withId(R.id.view_nav_drawer_files)).perform(click());
        //onView(withId(R.id.fragment_file_files_recycler_view)).check(matches(isDisplayed()));

        IdlingPolicies.setIdlingResourceTimeout(800, TimeUnit.MILLISECONDS);
    }
}