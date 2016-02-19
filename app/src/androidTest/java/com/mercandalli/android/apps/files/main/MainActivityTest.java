package com.mercandalli.android.apps.files.main;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.mercandalli.android.apps.files.R;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SuppressWarnings("PMD")
@LargeTest
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void swipeViewPager_shouldDisplayTabs() {
        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.SECONDS);
        final ViewInteraction viewInteraction = onView(withId(R.id.fragment_file_view_pager));
        final ViewAction swipeLeft = withCustomConstraints(swipeLeft(), isDisplayingAtLeast(85));
        final ViewAction swipeRight = withCustomConstraints(swipeRight(), isDisplayingAtLeast(85));

        //SwipeLeft
        viewInteraction.perform(swipeLeft);
        viewInteraction.perform(swipeLeft);
        //SwipeRight
        viewInteraction.perform(swipeRight);
        viewInteraction.perform(swipeRight);
    }

    /**
     * http://stackoverflow.com/questions/33505953/espresso-how-to-test-swiperefreshlayout
     */
    public static ViewAction withCustomConstraints(final ViewAction action, final Matcher<View> constraints) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return constraints;
            }

            @Override
            public String getDescription() {
                return action.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view) {
                action.perform(uiController, view);
            }
        };
    }
}