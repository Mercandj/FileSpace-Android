package com.mercandalli.android.apps.files;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercandalli.android.apps.files.main.MainActivity;
import com.mercandalli.android.apps.files.splash.SplashActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static com.mercandalli.android.apps.files.TestApp.resetApp;
import static com.mercandalli.android.apps.files.UiAutomatorLib.finish;
import static com.mercandalli.android.apps.files.UiAutomatorLib.swipeLeftById;
import static com.mercandalli.android.apps.files.UiAutomatorLib.takeScreenShot;
import static com.mercandalli.android.apps.files.view.DrawerLayoutUtils.actionCloseDrawer;

@LargeTest
@RunWith(AndroidJUnit4.class)
public final class LocalImageFileAndroidTest {

    /**
     * Has to be public.
     */
    @Rule
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
    public void openImageViaImageFolder() throws UiObjectNotFoundException {
        takeScreenShot("openImageViaImageFolder-start");

        goLocalMusicFromHome();

        takeScreenShot("openImageViaImageFolder-end");
        finish(MainActivity.class);
    }

    public static void goLocalMusicFromHome() throws UiObjectNotFoundException {
        swipeLeftById(R.id.fragment_file_view_pager, 20);
        swipeLeftById(R.id.fragment_file_view_pager, 20);
    }
}