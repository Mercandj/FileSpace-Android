package com.mercandalli.android.apps.files;

import android.support.annotation.NonNull;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static com.mercandalli.android.apps.files.TestApp.resetApp;
import static com.mercandalli.android.apps.files.UiAutomatorLib.finish;
import static com.mercandalli.android.apps.files.UiAutomatorLib.swipeLeftById;
import static com.mercandalli.android.apps.files.UiAutomatorLib.takeScreenShot;
import static com.mercandalli.android.apps.files.view.DrawerLayoutUtils.actionCloseDrawer;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public final class LocalAudioFileAndroidTest {

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
        onView(withId(R.id.activity_main_drawer_layout)).perform(actionCloseDrawer());
    }

    @Test
    public void playSongViaMusicFolder() throws UiObjectNotFoundException {
        takeScreenShot("playSongViaFirstMusicFolder-start");

        goLocalMusicFromHome();

        takeScreenShot("playSongViaFirstMusicFolder-end");
        finish(MainActivity.class);
    }

    @Test
    public void playSongViaMusicAll() throws UiObjectNotFoundException {
        takeScreenShot("playSongViaMusicAll-start");

        goLocalMusicFromHome();
        takeScreenShot("playSongViaMusicAll-1");

        onView(allOf(
                withId(R.id.view_file_header_audio_all),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withParent(withParent(withParent(withId(R.id.fragment_file_audio_local_recycler_view)))))
        ).perform(click());

        takeScreenShot("playSongViaMusicAll-end");
        finish(MainActivity.class);
    }

    public static void goLocalMusicFromHome() throws UiObjectNotFoundException {
        swipeLeftById(R.id.fragment_file_view_pager, 20);
    }
}