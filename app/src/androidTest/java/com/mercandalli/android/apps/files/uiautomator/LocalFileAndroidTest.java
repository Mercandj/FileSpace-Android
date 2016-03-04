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
        // Select the first file
        mDevice.findObject(new UiSelector()
                .resourceId(mResources.getResourceName(R.id.fragment_file_files_recycler_view)))
                .getChild(new UiSelector().clickable(true).index(0))
                .clickAndWaitForNewWindow(400);

        // Go up.
        clickWaitNewWindow(R.id.fragment_file_fab_2);

        // Swipe up.
        swipeUpById(R.id.fragment_file_files_recycler_view, 20);

        // Swipe up.
        swipeDownById(R.id.fragment_file_files_recycler_view, 20);

        // Select the second file
        mDevice.findObject(new UiSelector()
                .resourceId(mResources.getResourceName(R.id.fragment_file_files_recycler_view)))
                .getChild(new UiSelector().clickable(true).index(2))
                .clickAndWaitForNewWindow(400);
    }

    @Test
    public void swipeViewPager() throws UiObjectNotFoundException {
        // Swipe ViewPager
        swipeLeftById(R.id.fragment_file_view_pager, 20);
        swipeLeftById(R.id.fragment_file_view_pager, 20);
        // swipeRightById(R.id.fragment_file_view_pager, 20);
        // swipeRightById(R.id.fragment_file_view_pager, 20);
    }

    @Test
    public void createAndDeleteFolder() throws UiObjectNotFoundException {
        final String folderName = "_" + getCurrentDateString();

        // Create folder.
        clickWaitNewWindow(R.id.fragment_file_fab_1);
        clickWaitNewWindow("android:id/text1");
        mDevice.findObject(new UiSelector().className(EditText.class)).setText(folderName);
        clickWaitNewWindow("android:id/button1");

        // Delete
        mDevice.findObject(new UiSelector().descriptionContains("overflow#" + folderName)).click();
        clickWaitNewWindowContainsText(R.string.delete);
        clickWaitNewWindow("android:id/button1");
    }
}