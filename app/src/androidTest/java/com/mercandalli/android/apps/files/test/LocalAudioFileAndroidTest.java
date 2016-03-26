package com.mercandalli.android.apps.files.test;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static com.mercandalli.android.apps.files.core.TestApp.resetApp;
import static com.mercandalli.android.apps.files.core.UiAutomatorLib.finish;
import static com.mercandalli.android.apps.files.core.UiAutomatorLib.swipeLeftById;
import static com.mercandalli.android.apps.files.core.UiAutomatorLib.takeScreenShot;
import static com.mercandalli.android.apps.files.core.view.DrawerLayoutUtils.actionCloseDrawer;

@LargeTest
@RunWith(AndroidJUnit4.class)
public final class LocalAudioFileAndroidTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
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
    public void playSongViaMusicFolder() throws UiObjectNotFoundException {
        takeScreenShot("playSongViaFirstMusicFolder-start");

        goLocalMusicFromHome();

        takeScreenShot("playSongViaFirstMusicFolder-end");
        finish(MainActivity.class);
    }

    public static void goLocalMusicFromHome() throws UiObjectNotFoundException {
        swipeLeftById(R.id.fragment_file_view_pager, 20);
    }
}