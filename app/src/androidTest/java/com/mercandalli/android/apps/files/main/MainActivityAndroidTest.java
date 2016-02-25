package com.mercandalli.android.apps.files.main;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
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
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.mercandalli.android.apps.files.UtilsAndroidTest.actionCloseDrawer;
import static com.mercandalli.android.apps.files.UtilsAndroidTest.actionOpenDrawer;
import static com.mercandalli.android.apps.files.UtilsAndroidTest.withCustomConstraints;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityAndroidTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void swipeViewPager_shouldDisplayTabs() {

        // Find obj.
        final ViewInteraction viewInteraction = onView(withId(R.id.fragment_file_view_pager));
        final ViewInteraction drawerLayout = onView(withId(R.id.activity_main_drawer_layout));
        final ViewAction swipeLeft = withCustomConstraints(swipeLeft(), isDisplayingAtLeast(85));
        final ViewAction swipeRight = withCustomConstraints(swipeRight(), isDisplayingAtLeast(85));

        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.SECONDS);

        drawerLayout.perform(actionCloseDrawer());

        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.SECONDS);

        // SwipeLeft.
        viewInteraction.perform(swipeLeft);
        viewInteraction.perform(swipeLeft);

        IdlingPolicies.setIdlingResourceTimeout(800, TimeUnit.MILLISECONDS);

        // SwipeRight.
        viewInteraction.perform(swipeRight);
        viewInteraction.perform(swipeRight);

        IdlingPolicies.setIdlingResourceTimeout(800, TimeUnit.MILLISECONDS);
    }

    @Test
    public void navigateIntoDrawer_shouldDisplayDrawer() {

        // Find obj.
        final ViewInteraction drawerLayout = onView(withId(R.id.activity_main_drawer_layout));

        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.SECONDS);

        drawerLayout.perform(actionCloseDrawer());

        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.SECONDS);

        // Go to workspace.
        drawerLayout.perform(actionOpenDrawer());
        IdlingPolicies.setIdlingResourceTimeout(400, TimeUnit.MILLISECONDS);
        onView(withId(R.id.view_nav_drawer_workspace)).perform(click());

        IdlingPolicies.setIdlingResourceTimeout(800, TimeUnit.MILLISECONDS);

        // Go to files.
        drawerLayout.perform(actionOpenDrawer());
        IdlingPolicies.setIdlingResourceTimeout(400, TimeUnit.MILLISECONDS);
        onView(withId(R.id.view_nav_drawer_files)).perform(click());

        IdlingPolicies.setIdlingResourceTimeout(800, TimeUnit.MILLISECONDS);
    }
}