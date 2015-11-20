package mercandalli.com.filespace.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.main.App;
import mercandalli.com.filespace.main.Constants;
import mercandalli.com.filespace.common.listener.ResultCallback;
import mercandalli.com.filespace.file.FileManager;
import mercandalli.com.filespace.file.FileModel;
import mercandalli.com.filespace.common.animation.ScaleAnimationAdapter;
import mercandalli.com.filespace.file.FileModelAdapter;
import mercandalli.com.filespace.common.view.divider.SpacesItemDecoration;

public class SearchActivity extends AppCompatActivity {

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

        App.get(this).getAppComponent().inject(this);

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
        FileModelAdapter adapter = new FileModelAdapter(this, mFileModelList, R.layout.tab_file_light, null);
        ScaleAnimationAdapter scaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, adapter);
        scaleAnimationAdapter.setDuration(220);
        scaleAnimationAdapter.setOffsetDuration(32);

        mRecyclerView.setAdapter(scaleAnimationAdapter);
        adapter.setOnItemClickListener(new FileModelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View arg1, int position) {
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
                        mFileManager.execute(SearchActivity.this, position, mFileModelList, arg1);
                    }
                }
            }
        });
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
}
