package com.mercandalli.android.apps.files.file.local;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.animation.ScaleAnimationAdapter;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelAdapter;
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.main.FileApp;

import java.util.ArrayList;
import java.util.List;

import static com.mercandalli.android.library.baselibrary.input.KeyboardUtils.hideSoftInput;

public class SearchActivity extends AppCompatActivity implements
        FileModelAdapter.OnFileClickListener,
        ResultCallback<List<FileModel>>,
        View.OnClickListener,
        FileModelListener,
        FileLocalOverflowActions.FileLocalActionCallback {

    private FileManager mFileManager;

    /**
     * The search {@link EditText}.
     */
    private EditText mSearchEditText;

    /**
     * A {@link List} of result.
     */
    private final List<FileModel> mFileModelList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private FileModelAdapter mFileModelAdapter;

    private final Handler mSearchDelayHandler = new Handler();

    private Runnable mSearchDelayRunnable;
    private ProgressBar mProgressBar;
    private ImageView mClearImageView;
    private TextView mEmptyTextView;

    private FileLocalOverflowActions mFileLocalOverflowActions;
    private String mLastSearch;

    public static void start(final Context context) {
        final Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mFileManager = FileApp.get().getFileAppComponent().provideFileManager();

        mFileLocalOverflowActions = new FileLocalOverflowActions(this, this);
        mFileLocalOverflowActions.setShowCopyCut(false);

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

        findViews();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_search_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        mFileModelAdapter = new FileModelAdapter(this, mFileModelList, this, this, null);

        mRecyclerView.setHasFixedSize(true);
        final int nbColumn = getResources().getInteger(R.integer.column_number_card);
        if (nbColumn <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, nbColumn));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ScaleAnimationAdapter scaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelAdapter);
            scaleAnimationAdapter.setDuration(220);
            scaleAnimationAdapter.setOffsetDuration(32);
            mRecyclerView.setAdapter(scaleAnimationAdapter);
        } else {
            mRecyclerView.setAdapter(mFileModelAdapter);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {
                    hideSoftInput(mSearchEditText);
                }
            }
        });

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
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing here.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchDelayHandler.removeCallbacks(mSearchDelayRunnable);
                if (TextUtils.isEmpty(mSearchEditText.getText())) {
                    mClearImageView.setVisibility(View.GONE);
                } else {
                    mClearImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchDelayHandler.removeCallbacks(mSearchDelayRunnable);
                mSearchDelayHandler.postDelayed(mSearchDelayRunnable, 400);
            }
        });

        mClearImageView.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!super.onOptionsItemSelected(item) && item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void success(List<FileModel> result) {
        mFileModelList.clear();
        mFileModelList.addAll(result);
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mFileModelAdapter.setList(result);

        if (result.isEmpty()) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void failure() {
        mFileModelList.clear();
        mFileModelAdapter.setList(mFileModelList);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFileClick(View view, int position) {
        if (mFileModelList.get(position).isDirectory()) {
            // TODO - Click folder search
        } else {
            mFileManager.execute(this, position, mFileModelList, view);
        }
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.activity_search_toolbar_btn_clear:
                clearSearch();
                break;
        }
    }

    @Override
    public void executeFileModel(FileModel fileModel, View view) {
        mFileLocalOverflowActions.show(fileModel, view, false);
    }

    @Override
    public void refreshFab() {

    }

    @Override
    public void refreshData() {
        performSearch(mLastSearch);
    }

    @Override
    public void addCopyFile(FileModel fileModel) {

    }

    @Override
    public void addCutFile(FileModel fileModel) {

    }

    @Override
    public boolean isFileToCut() {
        return false;
    }

    @Override
    public boolean isFileToCopy() {
        return false;
    }

    @Override
    public void clearFileToCut() {

    }

    @Override
    public void clearFileToCopy() {

    }

    /**
     * Find all the {@link View}s.
     */
    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_search_recycler_view);
        mSearchEditText = (EditText) findViewById(R.id.activity_search_edit_text);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_search_progress_bar);
        mClearImageView = (ImageView) findViewById(R.id.activity_search_toolbar_btn_clear);
        mEmptyTextView = (TextView) findViewById(R.id.activity_search_empty_view);
    }

    /**
     * Call the {@link #mFileManager} to perform the search.
     */
    private void performSearch(final String search) {
        mLastSearch = search;
        mRecyclerView.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mFileManager.searchLocal(this, search, this);
    }

    /**
     * Clear the search.
     */
    private void clearSearch() {
        mRecyclerView.setVisibility(View.GONE);
        mSearchEditText.setText("");
    }
}
