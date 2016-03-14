package com.mercandalli.android.apps.files.test;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static com.mercandalli.android.apps.files.lib.AbstractAndroidTest.finish;
import static com.mercandalli.android.apps.files.lib.AbstractAndroidTest.takeScreenShot;
import static com.mercandalli.android.apps.files.lib.view.DrawerLayoutUtils.actionOpenDrawer;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityAndroidTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void scrollLocalFilesAndClick() {
        takeScreenShot("scrollLocalFilesAndClick-start");

        // Find obj.
        final ViewInteraction viewInteraction = onView(ViewMatchers.withId(R.id.fragment_file_files_recycler_view));

        // Scroll recyclerView.
        viewInteraction.perform(swipeUp());
        takeScreenShot("scrollLocalFilesAndClick-1");
        viewInteraction.perform(swipeDown());
        takeScreenShot("scrollLocalFilesAndClick-2");

        onView(ViewMatchers.withId(R.id.fragment_file_files_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        takeScreenShot("scrollLocalFilesAndClick-end");
        finish(MainActivity.class);
    }

    @Test
    public void showNotesThenShowFiles() {
        takeScreenShot("showNotesThenShowFiles-start");

        // Find obj.
        final ViewInteraction drawerLayout = onView(ViewMatchers.withId(R.id.activity_main_drawer_layout));

        //drawerLayout.perform(actionCloseDrawer());

        //IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.SECONDS);

        // Go to workspace.
        drawerLayout.perform(actionOpenDrawer());
        onView(ViewMatchers.withId(R.id.view_nav_drawer_workspace)).perform(click());
        //onView(withId(R.id.fragment_workspace_note_input)).check(matches(isDisplayed()));
        takeScreenShot("showNotesThenShowFiles-1");

        // Go to files.
        drawerLayout.perform(actionOpenDrawer());
        onView(ViewMatchers.withId(R.id.view_nav_drawer_files)).perform(click());
        //onView(withId(R.id.fragment_file_files_recycler_view)).check(matches(isDisplayed()));

        takeScreenShot("showNotesThenShowFiles-end");
        finish(MainActivity.class);
    }
}