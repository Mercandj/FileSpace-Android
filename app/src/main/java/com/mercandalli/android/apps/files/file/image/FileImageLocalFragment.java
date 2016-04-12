package com.mercandalli.android.apps.files.file.image;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.animation.ScaleAnimationAdapter;
import com.mercandalli.android.apps.files.common.fragment.InjectedFabFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.IStringListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelCardAdapter;
import com.mercandalli.android.apps.files.file.FileModelCardHeaderItem;
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.apps.files.file.local.FileLocalPagerFragment;
import com.mercandalli.android.apps.files.file.local.fab.FileLocalFabManager;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.FileAppComponent;
import com.mercandalli.android.library.baselibrary.java.StringUtils;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A {@link android.support.v4.app.Fragment} that displays the local {@link FileAudioModel}s.
 */
public class FileImageLocalFragment extends InjectedFabFragment implements
        FileModelCardAdapter.OnFileSubtitleAdapter,
        FileModelCardAdapter.OnHeaderClickListener,
        FileImageManager.GetAllLocalImageListener,
        FileImageManager.GetLocalImageFoldersListener,
        FileImageManager.GetLocalImageListener,
        FileLocalPagerFragment.ListController,
        ScaleAnimationAdapter.NoAnimatedPosition,
        SwipeRefreshLayout.OnRefreshListener,
        FileLocalPagerFragment.ScrollTop,
        FileLocalFabManager.FabController {

    /**
     * A key for the view pager position.
     */
    private static final String ARG_POSITION_IN_VIEW_PAGER = "FileLocalFragment.Args.ARG_POSITION_IN_VIEW_PAGER";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PAGE_FOLDERS,
            PAGE_FOLDER_INSIDE,
            PAGE_ALL})
    public @interface CurrentPage {
    }

    private static final int PAGE_FOLDERS = 0;
    private static final int PAGE_FOLDER_INSIDE = 1;
    private static final int PAGE_ALL = 2;

    @CurrentPage
    private int mCurrentPage = PAGE_FOLDERS;

    private RecyclerView mRecyclerView;
    private List<FileModel> mFileModels;
    private TextView mMessageTextView;

    private String mStringDirectory;
    private String mStringImage;
    private String mStringImages;

    private List<FileModelCardHeaderItem> mHeaderIds;

    /**
     * A simple {@link ProgressBar}. Call {@link #showProgressBar()} or {@link #hideProgressBar()}.
     */
    private ProgressBar mProgressBar;

    private FileImageAdapter mFileImageAdapter;
    private FileModelCardAdapter mFileModelCardAdapter;

    private final IListener mRefreshActivityAdapterListener;

    private ScaleAnimationAdapter mScaleAnimationAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * A simple {@link Handler}. Called by {@link #showProgressBar()} or {@link #hideProgressBar()}.
     */
    private final Handler mProgressBarActivationHandler;

    /**
     * A simple {@link Runnable}. Called by {@link #showProgressBar()} or {@link #hideProgressBar()}.
     */
    private final Runnable mProgressBarActivationRunnable;

    private FileModel mCurrentFolder;

    private int mPositionInViewPager;

    @Inject
    FileLocalFabManager mFileLocalFabManager;

    @Inject
    FileManager mFileManager;

    @Inject
    FileImageManager mFileImageManager;

    public static FileImageLocalFragment newInstance(final int positionInViewPager) {
        final FileImageLocalFragment fileAudioLocalFragment = new FileImageLocalFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_POSITION_IN_VIEW_PAGER, positionInViewPager);
        fileAudioLocalFragment.setArguments(args);
        return fileAudioLocalFragment;
    }

    /**
     * Do not use this constructor. Call {@link #newInstance(int)} instead.
     */
    public FileImageLocalFragment() {
        mRefreshActivityAdapterListener = new IListener() {
            @Override
            public void execute() {
                if (mApplicationCallback != null) {
                    mApplicationCallback.refreshData();
                }
            }
        };
        mProgressBarActivationHandler = new Handler();
        mProgressBarActivationRunnable = new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        };
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (!args.containsKey(ARG_POSITION_IN_VIEW_PAGER)) {
            throw new IllegalStateException("Missing args. Please use newInstance()");
        }
        mPositionInViewPager = args.getInt(ARG_POSITION_IN_VIEW_PAGER);
        mFileLocalFabManager.addFabContainer(mPositionInViewPager, this);
    }

    @Override
    public void onDestroy() {
        mFileLocalFabManager.removeFabContainer(mPositionInViewPager);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file_image_local, container, false);
        final Context context = getContext();

        mStringDirectory = context.getString(R.string.file_image_model_directory);
        mStringImage = context.getString(R.string.file_image_model_image);
        mStringImages = context.getString(R.string.file_image_model_images);

        mFileImageManager.registerAllLocalImageListener(this);
        mFileImageManager.registerLocalImageFoldersListener(this);
        mFileImageManager.registerLocalImageListener(this);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.fragment_file_image_local_progress_bar);
        mProgressBar.setVisibility(View.GONE);
        mMessageTextView = (TextView) rootView.findViewById(R.id.fragment_file_image_local_message);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_file_image_local_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_file_image_local_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        updateGridLayoutManager();

        mFileModels = new ArrayList<>();

        mHeaderIds = new ArrayList<>();
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_image_folder, true));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_image_recent, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_image_artist, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_image_album, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_image_all, false));

        mFileImageAdapter = new FileImageAdapter(mHeaderIds, this, getActivity(), mFileModels, new FileModelListener() {
            @Override
            public void executeFileModel(final FileModel fileModel, final View view) {
                final AlertDialog.Builder menuAlert = new AlertDialog.Builder(getContext());
                String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
                if (mApplicationCallback.isLogged()) {
                    menuList = new String[]{getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
                }
                menuAlert.setTitle("Action");
                menuAlert.setItems(menuList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (!mApplicationCallback.isLogged()) {
                                    item += 2;
                                }
                                switch (item) {
                                    case 0:
                                        if (fileModel.isDirectory()) {
                                            Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                                        } else {
                                            DialogUtils.alert(getActivity(), getString(R.string.upload), "Upload file " + fileModel.getName(), getString(R.string.upload), new IListener() {
                                                @Override
                                                public void execute() {
                                                    if (fileModel.getFile() != null) {
                                                        List<StringPair> parameters = mFileManager.getForUpload(fileModel);
                                                        (new TaskPost(getActivity(), mApplicationCallback, Constants.URL_DOMAIN + Config.ROUTE_FILE, new IPostExecuteListener() {
                                                            @Override
                                                            public void onPostExecute(JSONObject json, String body) {

                                                            }
                                                        }, parameters, fileModel.getFile())).execute();
                                                    }
                                                }
                                            }, getString(android.R.string.cancel), null);
                                        }
                                        break;
                                    case 1:
                                        mFileManager.openLocalAs(getActivity(), fileModel);
                                        break;
                                    case 2:
                                        DialogUtils.prompt(getActivity(), "Rename", "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
                                            @Override
                                            public void execute(String text) {
                                                mFileManager.rename(fileModel, text, mRefreshActivityAdapterListener);
                                            }
                                        }, "Cancel", null, fileModel.getFullName());
                                        break;
                                    case 3:
                                        DialogUtils.alert(getActivity(), "Delete", "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Yes", new IListener() {
                                            @Override
                                            public void execute() {
                                                mFileManager.delete(fileModel, mRefreshActivityAdapterListener);
                                            }
                                        }, "No", null);
                                        break;
                                    case 4:
                                        DialogUtils.alert(getActivity(),
                                                getString(R.string.properties) + " : " + fileModel.getName(),
                                                mFileManager.toSpanned(getContext(), fileModel),
                                                "OK",
                                                null,
                                                null,
                                                null);
                                        break;
                                }
                            }
                        });
                AlertDialog menuDrop = menuAlert.create();
                menuDrop.show();
            }
        });
        mFileImageAdapter.setOnItemClickListener(new FileImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mFileModels.get(position).isDirectory()) {
                    refreshListFoldersInside(mFileModels.get(position));
                } else {
                    mFileManager.execute(getActivity(), position, mFileModels, view);
                }
            }
        });

        mFileModelCardAdapter = new FileModelCardAdapter(context, mHeaderIds, this, mFileModels, null, new FileModelCardAdapter.OnFileClickListener() {
            @Override
            public void onFileCardClick(View view, int position) {
                refreshListFoldersInside(mFileModels.get(position));
            }
        }, null);
        mFileModelCardAdapter.setOnFileSubtitleAdapter(this);
        mFileModelCardAdapter.setHeaderType(FileModelCardAdapter.TYPE_HEADER_IMAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelCardAdapter);
            mScaleAnimationAdapter.setDuration(220);
            mScaleAnimationAdapter.setOffsetDuration(32);
            mScaleAnimationAdapter.setNoAnimatedPosition(FileImageLocalFragment.this);
            mRecyclerView.setAdapter(mScaleAnimationAdapter);
        } else {
            mRecyclerView.setAdapter(mFileModelCardAdapter);
        }

        refreshListFolders();

        mApplicationCallback.invalidateMenu();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        mFileImageManager.unregisterAllLocalImageListener(this);
        mFileImageManager.unregisterLocalImageFoldersListener(this);
        mFileImageManager.unregisterLocalImageListener(this);
        super.onDestroyView();
    }

    @Override
    public boolean back() {
        if (mCurrentPage == PAGE_FOLDER_INSIDE) {
            refreshListFolders();
            return true;
        }
        return false;
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onFabClick(int fabId, FloatingActionButton fab) {
        if (fabId == 0) {
            refreshListFolders();
        }
    }

    @Override
    public boolean isFabVisible(int fabId) {
        return fabId == 0 && mCurrentPage == PAGE_FOLDER_INSIDE;
    }

    @Override
    public int getFabImageResource(int fabId) {
        return R.drawable.arrow_up;
    }

    @Override
    protected void inject(FileAppComponent fileAppComponent) {
        fileAppComponent.inject(this);
    }

    @Nullable
    @Override
    public String onFileSubtitleModify(FileModel fileModel) {
        if (fileModel != null && fileModel.isDirectory() && fileModel.getCountAudio() != 0) {
            return mStringDirectory + ": " + StringUtils.longToShortString(fileModel.getCountAudio()) + " " + (fileModel.getCountAudio() > 1 ? mStringImages : mStringImage);
        }
        return null;
    }

    @Override
    public boolean onHeaderClick(View v, List<FileModelCardHeaderItem> fileModelCardHeaderItems) {
        mHeaderIds.clear();
        mHeaderIds.addAll(fileModelCardHeaderItems);
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.view_file_header_image_folder:
                refreshListFolders();
                break;
            case R.id.view_file_header_image_recent:
                //TODO
                break;
            case R.id.view_file_header_image_artist:
                //TODO
                break;
            case R.id.view_file_header_image_album:
                //TODO
                break;
            case R.id.view_file_header_image_all:
                refreshListAllMusic();
                break;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAllLocalImageSucceeded(List<FileModel> fileModels) {
        hideProgressBar();
        if (mFileModels == null) {
            mFileModels = new ArrayList<>();
        } else {
            mFileModels.clear();
        }
        mFileModels.addAll(fileModels);
        mFileImageAdapter.setHasHeader(true);

        mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileImageAdapter);
        mScaleAnimationAdapter.setDuration(220);
        mScaleAnimationAdapter.setOffsetDuration(32);
        mScaleAnimationAdapter.setNoAnimatedPosition(FileImageLocalFragment.this);
        mRecyclerView.setAdapter(mScaleAnimationAdapter);
        updateAdapter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAllLocalImageFailed() {
        updateAdapter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocalImageFoldersSucceeded(List<FileModel> fileModels) {
        hideProgressBar();
        if (mFileModels == null) {
            mFileModels = new ArrayList<>();
        } else {
            mFileModels.clear();
        }
        mFileModels.addAll(fileModels);

        mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelCardAdapter);
        mScaleAnimationAdapter.setDuration(220);
        mScaleAnimationAdapter.setOffsetDuration(32);
        mScaleAnimationAdapter.setNoAnimatedPosition(FileImageLocalFragment.this);
        mRecyclerView.setAdapter(mScaleAnimationAdapter);
        updateAdapter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocalImageFoldersFailed() {
        hideProgressBar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocalImageSucceeded(List<FileModel> fileModels) {
        mFileModels.clear();
        mFileModels.addAll(fileModels);
        mFileImageAdapter.setHasHeader(false);

        mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileImageAdapter);
        mScaleAnimationAdapter.setDuration(220);
        mScaleAnimationAdapter.setOffsetDuration(32);
        mScaleAnimationAdapter.setNoAnimatedPosition(FileImageLocalFragment.this);
        mRecyclerView.setAdapter(mScaleAnimationAdapter);
        updateAdapter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocalImageFailed() {
        updateAdapter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnimatedItem(int position) {
        return mCurrentPage == PAGE_FOLDER_INSIDE || position != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshCurrentList() {
        switch (mCurrentPage) {
            case PAGE_ALL:
                mFileImageManager.getAllLocalImage();
                break;
            case PAGE_FOLDERS:
                mFileImageManager.getLocalImageFolders();
                break;
            case PAGE_FOLDER_INSIDE:
                mFileImageManager.getLocalImage(mCurrentFolder);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scrollTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    public void refreshListFolders() {
        mCurrentFolder = null;
        mCurrentPage = PAGE_FOLDERS;
        if (mFileManager == null) {
            return;
        }

        showProgressBar();
        mFileImageManager.getLocalImageFolders();
    }

    public void refreshListAllMusic() {
        mCurrentPage = PAGE_ALL;
        if (mFileManager == null) {
            return;
        }

        showProgressBar();
        mFileImageManager.getAllLocalImage();
    }

    public void refreshListFoldersInside(final FileModel fileModel) {
        mCurrentFolder = fileModel;
        mCurrentPage = PAGE_FOLDER_INSIDE;
        mFileModels.clear();
        mFileImageManager.getLocalImage(fileModel);
    }

    public void updateAdapter() {
        if (mRecyclerView != null && mFileModels != null && isAdded()) {
            mFileLocalFabManager.updateFabButtons();

            if (mFileModels.size() == 0) {
                mMessageTextView.setText(getString(R.string.no_image));
                mMessageTextView.setVisibility(View.VISIBLE);
            } else {
                mMessageTextView.setVisibility(View.GONE);
            }

            if (mCurrentPage == PAGE_FOLDERS) {
                updateGridLayoutManager();
                mFileModelCardAdapter.setList(mFileModels);
            } else {
                updateStaggeredGridLayoutManager();
                mFileImageAdapter.setList(mFileModels);
            }
        }
    }

    private void showProgressBar() {
        mProgressBarActivationHandler.postDelayed(mProgressBarActivationRunnable, 200);
    }

    private void hideProgressBar() {
        mProgressBarActivationHandler.removeCallbacks(mProgressBarActivationRunnable);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void updateGridLayoutManager() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.column_number_small_card));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mFileModelCardAdapter.isHeader(position) && mCurrentPage != PAGE_FOLDER_INSIDE ?
                        gridLayoutManager.getSpanCount() : 1;
            }
        });
    }

    private void updateStaggeredGridLayoutManager() {
        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.column_number_small_card), StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
    }
}
