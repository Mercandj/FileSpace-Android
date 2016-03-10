package com.mercandalli.android.apps.files.lib;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import com.squareup.spoon.Spoon;

import junit.framework.Assert;

import org.junit.Before;

import java.text.DateFormat;
import java.util.Date;

import static android.content.Intent.CATEGORY_LAUNCHER;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;

/**
 * An abstract test that launch the app and provide useful test methods.
 */
@SuppressWarnings("unused")
public abstract class AbstractAndroidTest {

    protected final String mTargetPackage = InstrumentationRegistry.getTargetContext().getPackageName();
    protected final Context mContext = InstrumentationRegistry.getTargetContext();
    protected final Resources mResources = mContext.getResources();
    protected final Instrumentation mInstrumentation = InstrumentationRegistry.getInstrumentation();
    protected final UiDevice mDevice = UiDevice.getInstance(mInstrumentation);

    private static final int MAX_LAUNCH_TIMEOUT = 14_000;

    @Before
    public void launchSample() {

        // Start from the home screen
        mDevice.pressHome();

        final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mTargetPackage);
        if (intent == null) {
            throw new AssertionError("FileSpace app not installed. Intent is null. Package = " + mTargetPackage);
        }
        intent.addCategory(CATEGORY_LAUNCHER);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK); // Clear out any previous instances
        mContext.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(mTargetPackage).depth(0)), MAX_LAUNCH_TIMEOUT);

        // Close navDrawer if open.
        /*final UiObject navDrawer = mDevice.findObject(new UiSelector()
                .resourceId(mResources.getResourceName(R.id.activity_main_navigation_view)));
        try {
            if (navDrawer != null && navDrawer.isEnabled()) {
                navDrawer.swipeLeft(10);
            }
        } catch (UiObjectNotFoundException ignored) {
        }*/
    }

    /**
     * A simple thread sleep.
     *
     * @param timeMillis The time in millis.
     */
    protected void sleep(final int timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException ignored) {
        }
    }

    protected String getCurrentDateString() {
        return DateFormat.getDateTimeInstance().format(new Date())
                .replaceAll("\\s", "").replaceAll(":", "").replaceAll(",", "").trim();
    }

    //region - find object and determine object stats

    /**
     * Find an {@link UiObject} with the resource id.
     *
     * @param id The resource id (e.g. R.id.toolbar).
     * @return The UiObject.
     */
    @NonNull
    protected UiObject findObjectById(@IdRes final int id) {
        return findObjectById(mResources.getResourceName(id));
    }

    /**
     * Find an {@link UiObject} with the resource id.
     *
     * @param id The resource id (e.g. the_package_name:id/the_id).
     * @return The UiObject.
     */
    @NonNull
    protected UiObject findObjectById(final String id) {
        return mDevice.findObject(new UiSelector().resourceId(id));
    }

    /**
     * Find an {@link UiObject} with a specific {@link String} displayed.
     *
     * @param text A text displayed.
     * @return The UiObject.
     */
    @NonNull
    protected UiObject findObjectContainsText(final String text) {
        return mDevice.findObject(new UiSelector().textContains(text));
    }

    /**
     * Find an {@link UiObject} with a specific {@link String} displayed.
     *
     * @param containsTextId The string resource id (e.g. R.string.the_id).
     * @return The UiObject.
     */
    @NonNull
    protected UiObject findObjectContainsText(@StringRes final int containsTextId) {
        return mDevice.findObject(new UiSelector().textContains(mResources.getString(containsTextId)));
    }

    /**
     * Is this {@link UiObject} clickable.
     *
     * @param uiObject A {@link UiObject}.
     * @return True if clickable.
     * @throws UiObjectNotFoundException Throw an exception if not found.
     */
    protected boolean isObjectClickable(final UiObject uiObject) throws UiObjectNotFoundException {
        return uiObject != null && uiObject.exists() && uiObject.isClickable();
    }
    //endregion - find object and determine object stats

    //region - click & swipe

    /**
     * Call clickAndWaitForNewWindow.*
     *
     * @param id The view id.
     * @throws UiObjectNotFoundException
     */
    protected boolean clickWaitNewWindow(
            @IdRes final int id) throws UiObjectNotFoundException {
        return clickWaitNewWindow(id, 5_500);
    }

    /**
     * Call clickAndWaitForNewWindow.
     *
     * @param id      The view id.
     * @param timeout timeout before giving up on waiting for a new window
     * @throws UiObjectNotFoundException Throw an exception if not found.
     */
    protected boolean clickWaitNewWindow(
            @IdRes final int id,
            final long timeout) throws UiObjectNotFoundException {
        return findObjectById(id).clickAndWaitForNewWindow(timeout);
    }

    /**
     * Call clickAndWaitForNewWindow. You can use this method to click on external app view
     * like a system popup or the launcher screen...
     *
     * @param id The full string id given by sdk/tools/uiautomatorviewer.bat.
     *           (e.g. the_package_name:id/the_id).
     * @return The clickAndWaitForNewWindow result.
     * @throws UiObjectNotFoundException Throw an exception if not found.
     */
    protected boolean clickWaitNewWindow(
            final String id) throws UiObjectNotFoundException {
        return findObjectById(id).clickAndWaitForNewWindow(5_500);
    }

    /**
     * Check if the object exists and click on it (wait new windows).
     *
     * @param containsText The text displayed.
     * @return The clickAndWaitForNewWindow result.
     * @throws UiObjectNotFoundException Throw an exception if not found.
     */
    protected boolean clickWaitNewWindowContainsText(
            final String containsText) throws UiObjectNotFoundException {
        final UiObject uiObject = findObjectContainsText(containsText);
        Assert.assertTrue(uiObject.exists());
        return uiObject.clickAndWaitForNewWindow(5_500);
    }

    /**
     * Check if the object exists and click on it (wait new windows).
     *
     * @param containsTextId The text displayed.
     * @return The clickAndWaitForNewWindow result.
     * @throws UiObjectNotFoundException Throw an exception if not found.
     */
    protected boolean clickWaitNewWindowContainsText(
            @StringRes final int containsTextId) throws UiObjectNotFoundException {
        final UiObject uiObject = findObjectContainsText(containsTextId);
        Assert.assertTrue(uiObject.exists());
        return uiObject.clickAndWaitForNewWindow(5_500);
    }

    protected boolean swipeUpById(
            @IdRes final int id,
            final int speedSteps) throws UiObjectNotFoundException {
        return findObjectById(id).swipeUp(speedSteps);
    }

    protected boolean swipeDownById(
            @IdRes final int id,
            final int speedSteps) throws UiObjectNotFoundException {
        return findObjectById(id).swipeDown(speedSteps);
    }

    protected boolean swipeLeftById(
            @IdRes final int id,
            final int speedSteps) throws UiObjectNotFoundException {
        return findObjectById(id).swipeLeft(speedSteps);
    }

    protected boolean swipeRightById(
            @IdRes final int id,
            final int speedSteps) throws UiObjectNotFoundException {
        return findObjectById(id).swipeRight(speedSteps);
    }
    //endregion - click & swipe

    //region - setText
    protected void setText(
            final String id,
            final String text) throws UiObjectNotFoundException {
        final UiObject objectById = findObjectById(id);
        objectById.clickAndWaitForNewWindow(100);
        objectById.setText(text);
    }

    protected void setText(
            @IdRes final int id,
            final String text) throws UiObjectNotFoundException {
        setText(mResources.getResourceName(id), text);
    }
    //endregion - setText

    //region - assert
    protected void assertExists(@IdRes final int id) throws UiObjectNotFoundException {
        Assert.assertTrue(isObjectClickable(id));
    }

    protected void assertExistsContains(final String textContains) {
        Assert.assertTrue(isObjectExistsContains(textContains));
    }

    protected boolean isObjectClickable(@IdRes final int id) throws UiObjectNotFoundException {
        return findObjectById(id).exists();
    }

    protected boolean isObjectExistsContains(final String textContains) {
        return mDevice.findObject(new UiSelector()
                .textContains(textContains))
                .exists();
    }
    //endregion - assert

    //region - device actions
    protected void orientationLeft() {
        try {
            mDevice.setOrientationRight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mDevice.waitForWindowUpdate(null, 100);
    }

    protected void orientationRight() {
        try {
            mDevice.setOrientationLeft();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mDevice.waitForWindowUpdate(null, 100);
    }

    protected void orientationPortrait() {
        try {
            mDevice.setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mDevice.waitForWindowUpdate(null, 100);
    }
    //endregion - device actions

    protected void takeScreenShot(final String title) {
        takeScreenShot(getCurrentActivity(), title);
    }

    protected void takeScreenShot(final Activity activity, final String title) {
        Spoon.screenshot(activity, title);
    }

    protected Activity getCurrentActivity() {
        final Activity[] activity = new Activity[1];
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity[0] = Iterables.getOnlyElement(ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(Stage.RESUMED));
            }
        });
        return activity[0];
    }
}