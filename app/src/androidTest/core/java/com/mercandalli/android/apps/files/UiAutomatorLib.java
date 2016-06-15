package com.mercandalli.android.apps.files;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.squareup.spoon.Spoon;

import junit.framework.Assert;

import java.text.DateFormat;
import java.util.Date;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

/**
 * An abstract test that launch the app and provide useful test methods.
 */
@SuppressWarnings("unused")
public class UiAutomatorLib {

    /**
     * A simple thread sleep.
     *
     * @param timeMillis The time in millis.
     */
    public static void sleep(final int timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException ignored) {
        }
    }

    public static String getCurrentDateString() {
        return DateFormat.getDateTimeInstance().format(new Date())
                .replaceAll("\\s", "").replaceAll(":", "").replaceAll(",", "").trim();
    }

    public static Activity getActivity() {
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

    public static Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    public static Resources getResources() {
        return InstrumentationRegistry.getTargetContext().getResources();
    }

    //region UI AUTOMATOR base
    public static UiDevice getDevice() {
        return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    public static void pressBack() {
        getDevice().pressBack();
    }
    //endregion UI AUTOMATOR base

    //region - find object and determine object stats

    /**
     * Find an {@link UiObject} with the resource id.
     *
     * @param id The resource id (e.g. R.id.toolbar).
     * @return The UiObject.
     */
    @NonNull
    public static UiObject findObjectById(@IdRes final int id) {
        return findObjectById(getResources().getResourceName(id));
    }

    /**
     * Find an {@link UiObject} with the resource id.
     *
     * @param id The resource id (e.g. the_package_name:id/the_id).
     * @return The UiObject.
     */
    @NonNull
    public static UiObject findObjectById(final String id) {
        return getDevice().findObject(new UiSelector().resourceId(id));
    }

    /**
     * Find an {@link UiObject} with a specific {@link String} displayed.
     *
     * @param text A text displayed.
     * @return The UiObject.
     */
    @NonNull
    public static UiObject findObjectContainsText(final String text) {
        return getDevice().findObject(new UiSelector().textContains(text));
    }

    /**
     * Find an {@link UiObject} with a specific {@link String} displayed.
     *
     * @param containsTextId The string resource id (e.g. R.string.the_id).
     * @return The UiObject.
     */
    @NonNull
    public static UiObject findObjectContainsText(@StringRes final int containsTextId) {
        return getDevice().findObject(new UiSelector().textContains(getResources().getString(containsTextId)));
    }

    /**
     * Is this {@link UiObject} clickable.
     *
     * @param uiObject A {@link UiObject}.
     * @return True if clickable.
     * @throws UiObjectNotFoundException Throw an exception if not found.
     */
    public static boolean isObjectClickable(final UiObject uiObject) throws UiObjectNotFoundException {
        return uiObject != null && uiObject.exists() && uiObject.isClickable();
    }
    //endregion - find object and determine object stats

    //region - click & swipe

    /**
     * Call clickAndWaitForNewWindow.
     *
     * @param id The view id.
     * @throws UiObjectNotFoundException
     */
    public static boolean click(
            @IdRes final int id) throws UiObjectNotFoundException {
        return findObjectById(id).click();
    }

    /**
     * Call clickAndWaitForNewWindow.
     *
     * @param id The view id.
     * @throws UiObjectNotFoundException
     */
    public static boolean clickWaitNewWindow(
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
    public static boolean clickWaitNewWindow(
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
    public static boolean clickWaitNewWindow(
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
    public static boolean clickWaitNewWindowContainsText(
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
    public static boolean clickWaitNewWindowContainsText(
            @StringRes final int containsTextId) throws UiObjectNotFoundException {
        final UiObject uiObject = findObjectContainsText(containsTextId);
        Assert.assertTrue(uiObject.exists());
        return uiObject.clickAndWaitForNewWindow(5_500);
    }

    public static boolean swipeUpById(
            @IdRes final int id,
            final int speedSteps) throws UiObjectNotFoundException {
        return findObjectById(id).swipeUp(speedSteps);
    }

    public static boolean swipeDownById(
            @IdRes final int id,
            final int speedSteps) throws UiObjectNotFoundException {
        return findObjectById(id).swipeDown(speedSteps);
    }

    public static boolean swipeLeftById(
            @IdRes final int id,
            final int speedSteps) throws UiObjectNotFoundException {
        return findObjectById(id).swipeLeft(speedSteps);
    }

    public static boolean swipeRightById(
            @IdRes final int id,
            final int speedSteps) throws UiObjectNotFoundException {
        return findObjectById(id).swipeRight(speedSteps);
    }
    //endregion - click & swipe

    //region - setText
    public static void setText(
            final String id,
            final String text) throws UiObjectNotFoundException {
        final UiObject objectById = findObjectById(id);
        objectById.clickAndWaitForNewWindow(100);
        objectById.setText(text);
    }

    public static void setText(
            @IdRes final int id,
            final String text) throws UiObjectNotFoundException {
        setText(getResources().getResourceName(id), text);
    }
    //endregion - setText

    //region - assert
    public static void assertExists(@IdRes final int id) throws UiObjectNotFoundException {
        Assert.assertTrue(isObjectClickable(id));
    }

    public static void assertExistsContains(final String textContains) {
        Assert.assertTrue(isObjectExistsContains(textContains));
    }

    public static boolean isObjectClickable(@IdRes final int id) throws UiObjectNotFoundException {
        return findObjectById(id).exists();
    }

    public static boolean isObjectExistsContains(final String textContains) {
        return getDevice().findObject(new UiSelector()
                .textContains(textContains))
                .exists();
    }
    //endregion - assert

    //region - device actions
    public static void orientationLeft() {
        try {
            getDevice().setOrientationRight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        sleep(100);
    }

    public static void orientationRight() {
        try {
            getDevice().setOrientationLeft();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        sleep(100);
    }

    public static void orientationPortrait() {
        try {
            getDevice().setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        sleep(100);
    }
    //endregion - device actions

    public static void takeScreenShot(final String title) {
        takeScreenShot(getActivity(), title);
    }

    public static void takeScreenShot(final Activity activity, final String title) {
        Spoon.screenshot(activity, title);
    }

    /**
     * Assert that the current activity is an instance of a given class and finish it.
     *
     * @param activityClass the {@link Class}
     * @throws Throwable
     */
    public static void finish(Class<? extends Activity> activityClass) {
        sleep(800);
        final Activity currentActivity = getActivity();
        assertThat(currentActivity, instanceOf(activityClass));
        currentActivity.finish();
        sleep(800);
    }
}