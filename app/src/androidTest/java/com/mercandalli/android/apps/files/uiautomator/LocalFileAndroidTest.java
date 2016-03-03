package com.mercandalli.android.apps.files.uiautomator;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

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
        swipeUpById(R.id.fragment_file_files_recycler_view, 30);

        // Swipe up.
        swipeDownById(R.id.fragment_file_files_recycler_view, 30);

        // Select the second file
        mDevice.findObject(new UiSelector()
                .resourceId(mResources.getResourceName(R.id.fragment_file_files_recycler_view)))
                .getChild(new UiSelector().clickable(true).index(2))
                .clickAndWaitForNewWindow(400);
    }

    @Test
    public void swipeViewPager() throws UiObjectNotFoundException {
        // Swipe ViewPager
        swipeLeftById(R.id.fragment_file_files_recycler_view, 30);
    }
}