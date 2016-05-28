package com.mercandalli.android.apps.files;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.library.base.view.ViewUtils;

import java.io.File;

/**
 * A {@link WearableActivity} used to control explore local files.
 */
public class FileLocalActivity extends WearableActivity {

    @Nullable
    private File mCurrentFolder;

    @Nullable
    private TextView mPathTextView;

    @Nullable
    private WearableListView mWearableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_file);
        setAmbientEnabled();
        findViews();

        mCurrentFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator);
        if (!mCurrentFolder.exists()) {
            ViewUtils.setTextViewText(mPathTextView, "Folder not found");
        } else {
            ViewUtils.setTextViewText(mPathTextView, mCurrentFolder.getAbsolutePath());

            final String[] list = mCurrentFolder.list();
            if (list == null || list.length == 0) {
                Toast.makeText(this, "Empty folder", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void findViews() {
        mPathTextView = (TextView) findViewById(R.id.activity_local_file_path);
        mWearableListView = (WearableListView) findViewById(R.id.activity_local_file_wearable_list);
    }
}
