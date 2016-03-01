package com.mercandalli.android.apps.files.uiautomator;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Intent.CATEGORY_LAUNCHER;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

@RunWith(AndroidJUnit4.class)
public final class UiAutomatorTest {

    private static final String TARGET_PACKAGE = InstrumentationRegistry.getTargetContext().getPackageName();
    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private final Instrumentation mInstrumentation = InstrumentationRegistry.getInstrumentation();
    private final UiDevice mDevice = UiDevice.getInstance(mInstrumentation);

    private static final int MAX_LAUNCH_TIMEOUT = 14_000;

    @Before
    public void launchSample() {

        // Start from the home screen
        mDevice.pressHome();

        final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(TARGET_PACKAGE);
        if (intent == null) {
            throw new AssertionError("YouSing app not installed. Intent is null. Package = " + TARGET_PACKAGE);
        }
        intent.addCategory(CATEGORY_LAUNCHER);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK); // Clear out any previous instances
        mContext.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(TARGET_PACKAGE).depth(0)), MAX_LAUNCH_TIMEOUT);
    }

    @Test
    public void openFolderAndGoUp() throws UiObjectNotFoundException {
        // Select the first file
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_files_recycler_view"))
                .getChild(new UiSelector().clickable(true).index(0))
                .click();

        // Go up.
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_fab_2"))
                .click();

        // Swipe up.
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_files_recycler_view"))
                .swipeUp(3);

        // Swipe up.
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_files_recycler_view"))
                .swipeDown(3);

        // Select the second file
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_files_recycler_view"))
                .getChild(new UiSelector().clickable(true).index(2))
                .click();
    }

    @Test
    public void swipeViewPager() throws UiObjectNotFoundException {
        // Select the first file
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_files_recycler_view"))
                .getChild(new UiSelector().clickable(true).index(0))
                .click();

        // Go up.
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_fab_2"))
                .click();

        // Swipe up.
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_files_recycler_view"))
                .swipeUp(3);

        // Swipe up.
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_files_recycler_view"))
                .swipeDown(3);

        // Select the second file
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_files_recycler_view"))
                .getChild(new UiSelector().clickable(true).index(2))
                .click();

        // Select the second file
        mDevice.findObject(new UiSelector()
                .resourceId("com.mercandalli.android.apps.files:id/fragment_file_view_pager"))
                .swipeLeft(4);
    }
}