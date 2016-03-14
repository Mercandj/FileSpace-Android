package com.mercandalli.android.apps.files.test;

import android.app.Activity;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.mercandalli.android.apps.files.lib.AbstractAndroidTest;
import com.mercandalli.android.apps.files.main.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class LocalFileAndroidTest extends AbstractAndroidTest {

    @Test
    public void openFolderAndGoUp() throws UiObjectNotFoundException {
        final Activity activity = getCurrentActivity();

        takeScreenShot(activity, "openFolderAndGoUp-start");

        /*
        // Select the first file
        mDevice.findObject(new UiSelector()
                .resourceId(mResources.getResourceName(R.id.fragment_file_files_recycler_view)))
                .getChild(new UiSelector().clickable(true).index(0))
                .clickAndWaitForNewWindow(400);
        takeScreenShot(activity, "openFolderAndGoUp-1");

        // Go up.
        clickWaitNewWindow(R.id.fragment_file_fab_2);
        takeScreenShot(activity, "openFolderAndGoUp-2");

        // Swipe up.
        swipeUpById(R.id.fragment_file_files_recycler_view, 20);
        takeScreenShot(activity, "openFolderAndGoUp-3");

        // Swipe up.
        swipeDownById(R.id.fragment_file_files_recycler_view, 20);
        takeScreenShot(activity, "openFolderAndGoUp-4");

        // Select the second file
        mDevice.findObject(new UiSelector()
                .resourceId(mResources.getResourceName(R.id.fragment_file_files_recycler_view)))
                .getChild(new UiSelector().clickable(true).index(2))
                .clickAndWaitForNewWindow(400);
        */
        takeScreenShot(activity, "openFolderAndGoUp-end");
        finish(MainActivity.class);
    }

    @Test
    public void swipeViewPager() throws UiObjectNotFoundException {
        takeScreenShot("swipeViewPager-start");
        /*
        // Swipe ViewPager
        takeScreenShot("swipeViewPager-1");
        swipeLeftById(R.id.fragment_file_view_pager, 20);
        takeScreenShot("swipeViewPager-2");
        swipeLeftById(R.id.fragment_file_view_pager, 20);
        takeScreenShot("swipeViewPager-3");
        // swipeRightById(R.id.fragment_file_view_pager, 20);
        // takeScreenShot("swipeViewPager-4");
        // swipeRightById(R.id.fragment_file_view_pager, 20);
        // takeScreenShot("swipeViewPager-5");
        */
        takeScreenShot("swipeViewPager-end");
        finish(MainActivity.class);
    }

    @Test
    public void createAndDeleteFolder() throws UiObjectNotFoundException {
        takeScreenShot("createAndDeleteFolder-start");
        /*
        final Activity activity = getCurrentActivity();
        final String folderName = "_" + getCurrentDateString();
        takeScreenShot(activity, "createAndDeleteFolder-1");

        // Create folder.
        clickWaitNewWindow(R.id.fragment_file_fab_1);
        clickWaitNewWindow("android:id/text1");
        mDevice.findObject(new UiSelector().className(EditText.class)).setText(folderName);
        clickWaitNewWindow("android:id/button1");
        takeScreenShot(activity, "createAndDeleteFolder-2");

        // Delete
        mDevice.findObject(new UiSelector().descriptionContains("overflow#" + folderName)).click();
        clickWaitNewWindowContainsText(R.string.delete);
        clickWaitNewWindow("android:id/button1");
        */
        takeScreenShot("createAndDeleteFolder-end");
        finish(MainActivity.class);
    }
}