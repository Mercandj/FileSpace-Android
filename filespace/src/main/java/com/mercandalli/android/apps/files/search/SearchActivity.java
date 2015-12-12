package com.mercandalli.android.apps.files.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.animation.ScaleAnimationAdapter;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.common.view.divider.SpacesItemDecoration;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelAdapter;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.main.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SearchActivity extends AppCompatActivity implements FileModelAdapter.OnFileClickListener {

    @Inject
    FileManager mFileManager;

    String mRootPath;

    File mCurrentFile;

    private List<FileModel> mFileModelList;
    private RecyclerView mRecyclerView;

    public static void start(final Context context) {
        final Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FileApp.get(this).getFileAppComponent().inject(this);

        mFileModelList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_search_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(12, 2));

        mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mCurrentFile = new File(mRootPath);

        refreshList();
    }

    private void updateAdapter() {
        FileModelAdapter adapter = new FileModelAdapter(mFileModelList, null, this, null);
        ScaleAnimationAdapter scaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, adapter);
        scaleAnimationAdapter.setDuration(220);
        scaleAnimationAdapter.setOffsetDuration(32);

        mRecyclerView.setAdapter(scaleAnimationAdapter);
    }

    private void refreshList() {
        mFileManager.getFiles(
                new FileModel.FileModelBuilder().file(mCurrentFile).build(),
                Constants.SORT_ABC,
                new ResultCallback<List<FileModel>>() {
                    @Override
                    public void success(List<FileModel> result) {
                        mFileModelList.clear();
                        mFileModelList.addAll(result);
                        updateAdapter();
                    }

                    @Override
                    public void failure() {

                    }
                });
    }

    @Override
    public void onFileClick(View view, int position) {
        if (position < mFileModelList.size()) {
            FileModel file = mFileModelList.get(position);
            if (file.isDirectory()) {
                if (file.getCount() == 0) {
                    Toast.makeText(SearchActivity.this, "No files", Toast.LENGTH_SHORT).show();
                } else {
                    mCurrentFile = file.getFile();
                    refreshList();
                }
            } else {
                mFileManager.execute(SearchActivity.this, position, mFileModelList, view);
            }
        }
    }
}
