package com.mercandalli.android.apps.files.uiautomator;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.widget.EditText;

import com.mercandalli.android.apps.files.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class LocalFileAndroidTest extends AbstractAndroidTest {

    @Test
    public void openFolderAndGoUp() throws UiObjectNotFoundException {

        takeScreenShot("openFolderAndGoUp-1");

        // Select the first file
        mDevice.findObject(new UiSelector()
                .resourceId(mResources.getResourceName(R.id.fragment_file_files_recycler_view)))
                .getChild(new UiSelector().clickable(true).index(0))
                .clickAndWaitForNewWindow(400);
        takeScreenShot("openFolderAndGoUp-2");

        // Go up.
        clickWaitNewWindow(R.id.fragment_file_fab_2);
        takeScreenShot("openFolderAndGoUp-3");

        // Swipe up.
        swipeUpById(R.id.fragment_file_files_recycler_view, 20);
        takeScreenShot("openFolderAndGoUp-4");

        // Swipe up.
        swipeDownById(R.id.fragment_file_files_recycler_view, 20);
        takeScreenShot("openFolderAndGoUp-5");

        // Select the second file
        mDevice.findObject(new UiSelector()
                .resourceId(mResources.getResourceName(R.id.fragment_file_files_recycler_view)))
                .getChild(new UiSelector().clickable(true).index(2))
                .clickAndWaitForNewWindow(400);
        takeScreenShot("openFolderAndGoUp-6");
    }

    @Test
    public void swipeViewPager() throws UiObjectNotFoundException {
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
    }

    @Test
    public void createAndDeleteFolder() throws UiObjectNotFoundException {
        final String folderName = "_" + getCurrentDateString();
        takeScreenShot("createAndDeleteFolder-1");

        // Create folder.
        clickWaitNewWindow(R.id.fragment_file_fab_1);
        clickWaitNewWindow("android:id/text1");
        mDevice.findObject(new UiSelector().className(EditText.class)).setText(folderName);
        clickWaitNewWindow("android:id/button1");
        takeScreenShot("createAndDeleteFolder-2");

        // Delete
        mDevice.findObject(new UiSelector().descriptionContains("overflow#" + folderName)).click();
        clickWaitNewWindowContainsText(R.string.delete);
        clickWaitNewWindow("android:id/button1");
        takeScreenShot("createAndDeleteFolder-3");
    }
}