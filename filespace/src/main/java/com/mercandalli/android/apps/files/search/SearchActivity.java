package com.mercandalli.android.apps.files.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.animation.ScaleAnimationAdapter;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelAdapter;
import com.mercandalli.android.apps.files.main.FileApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SearchActivity extends AppCompatActivity implements FileModelAdapter.OnFileClickListener, ResultCallback<List<FileModel>> {

    @Inject
    FileManager mFileManager;

    String mRootPath;

    File mCurrentFile;

    private EditText mSearchEditText;

    private List<FileModel> mFileModelList;
    private RecyclerView mRecyclerView;
    private FileModelAdapter mFileModelAdapter;

    private final Handler mSearchDelayHandler = new Handler();

    private Runnable mSearchDelayRunnable;

    public static void start(final Context context) {
        final Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FileApp.get(this).getFileAppComponent().inject(this);

        mSearchDelayRunnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(mSearchEditText.getText())) {
                            performSearch(mSearchEditText.getText().toString());
                        }
                    }
                });
            }
        };

        mFileModelList = new ArrayList<>();

        findViews();

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        mFileModelAdapter = new FileModelAdapter(mFileModelList, null, this, null);

        mRecyclerView.setHasFixedSize(true);
        final int nbColumn = getResources().getInteger(R.integer.column_number_card);
        if (nbColumn <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, nbColumn));
        }

        ScaleAnimationAdapter scaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelAdapter);
        scaleAnimationAdapter.setDuration(220);
        scaleAnimationAdapter.setOffsetDuration(32);
        mRecyclerView.setAdapter(scaleAnimationAdapter);

        mRecyclerView.setAdapter(mFileModelAdapter);

        mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mCurrentFile = new File(mRootPath);

        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !TextUtils.isEmpty(v.getText())) {
                    final String search = v.getText().toString();
                    mSearchDelayHandler.removeCallbacks(mSearchDelayRunnable);
                    performSearch(search);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!super.onOptionsItemSelected(item) && item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void performSearch(String search) {
        mFileManager.searchLocal(this, search, this);
    }

    /**
     * Find all the {@link View}s.
     */
    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_search_recycler_view);
        mSearchEditText = (EditText) findViewById(R.id.activity_search_edit_text);
    }

    @Override
    public void success(List<FileModel> result) {
        mFileModelAdapter.setList(result);
    }

    @Override
    public void failure() {

    }

    @Override
    public void onFileClick(View view, int position) {

    }
}
