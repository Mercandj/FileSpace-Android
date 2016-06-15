package com.mercandalli.android.apps.files;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercandalli.android.apps.files.main.MainActivity;
import com.mercandalli.android.apps.files.splash.SplashActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static com.mercandalli.android.apps.files.TestApp.resetApp;
import static com.mercandalli.android.apps.files.UiAutomatorLib.finish;
import static com.mercandalli.android.apps.files.UiAutomatorLib.takeScreenShot;
import static com.mercandalli.android.apps.files.view.DrawerLayoutUtils.actionCloseDrawer;
import static com.mercandalli.android.apps.files.view.DrawerLayoutUtils.actionOpenDrawer;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NoteAndroidTest {

    /**
     * Has to be public.
     */
    @Rule
    @NonNull
    public final ActivityTestRule<SplashActivity> activityRule = new ActivityTestRule<SplashActivity>(SplashActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            resetApp(InstrumentationRegistry.getTargetContext());
        }
    };

    @Before
    public void closeNavDrawer() {
        onView(ViewMatchers.withId(R.id.activity_main_drawer_layout)).perform(actionCloseDrawer());
    }

    @Test
    public void showNotesThenShowFiles() {
        takeScreenShot("showNotesThenShowFiles-start");

        // Find obj.
        final ViewInteraction drawerLayout = onView(ViewMatchers.withId(R.id.activity_main_drawer_layout));

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