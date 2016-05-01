/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.file.local;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.file.FileAddDialog;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.file.audio.FileAudioLocalFragment;
import com.mercandalli.android.apps.files.file.image.FileImageLocalFragment;
import com.mercandalli.android.apps.files.file.local.fab.FileLocalFabManager;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.library.baselibrary.view.RtlViewPager;

import static com.mercandalli.android.library.baselibrary.view.StatusBarUtils.setStatusBarColor;

public class FileLocalPagerFragment extends BackFragment implements
        ViewPager.OnPageChangeListener,
        TabLayout.OnTabSelectedListener,
        FileLocalFabManager.FabContainer,
        View.OnClickListener {

    private static final int NB_FRAGMENT = 3;
    private static final int INIT_FRAGMENT = 0;

    /**
     * Instantiate this {@link FileLocalPagerFragment}.
     *
     * @return The instance of this {@link Fragment}.
     */
    public static FileLocalPagerFragment newInstance() {
        return new FileLocalPagerFragment();
    }

    //region Views
    private TabLayout mTabLayout;
    private RtlViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private FloatingActionButton mFab1;
    private FloatingActionButton mFab2;
    //endregion Views

    private FileLocalFabManager mFileLocalFabManager;

    private SetToolbarCallback mSetToolbarCallback;

    private final int[] mImageResId = {
            R.drawable.ic_folder_open_white_24dp,
            R.drawable.ic_sd_storage_white_24dp,
            R.drawable.ic_music_note_white_24dp,
            R.drawable.ic_photo_white_24dp,
            R.drawable.ic_video_library_white_24dp
    };

    private final int[] mTitleIds = {
            R.string.tab_files,
            R.string.tab_sdcard,
            R.string.tab_musics,
            R.string.tab_photos,
            R.string.tab_videos
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SetToolbarCallback) {
            mSetToolbarCallback = (SetToolbarCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a HomeActivity. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        mSetToolbarCallback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileLocalFabManager = FileApp.get().getFileAppComponent().provideFileLocalFabManager();
        mFileLocalFabManager.setFabContainer(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file, container, false);
        findViews(rootView);
        initToolbar(rootView);
        initViews(savedInstanceState);
        onPageSelected(mViewPager.getCurrentItem());
        return rootView;
    }

    @Override
    public boolean back() {
        final Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (!(fragment instanceof BackFragment)) {
            return false;
        }
        return ((BackFragment) fragment).back();
    }

    //region Override - ViewPager
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        final Context context = getContext();
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).invalidateOptionsMenu();
        }
        mFileLocalFabManager.onCurrentViewPagerPageChange(position);
        syncTabLayout();
    }
    //endregion Override - ViewPager

    @Override
    public void updateFabs(final FileLocalFabManager.FabState[] fabStates) {
        for (int i = 0; i < fabStates.length; i++) {
            final FileLocalFabManager.FabState fabState = fabStates[i];
            if (fabState.fabVisible) {
                showFab(i);
            } else {
                hideFab(i);
            }
            int imageResource = fabState.fabImageResource;
            if (imageResource == -1) {
                imageResource = android.R.drawable.ic_input_add;
            }
            if (i == 0) {
                mFab1.setImageResource(imageResource);
            } else {
                mFab2.setImageResource(imageResource);
            }
        }
    }

    private void hideFab(
            final @IntRange(from = 0, to = FileLocalFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition) {
        switch (fabPosition) {
            case 0:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    mFab1.hide();
                } else {
                    mFab1.setVisibility(View.GONE);
                }
                break;
            case 1:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    mFab2.hide();
                } else {
                    mFab2.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void showFab(
            final @IntRange(from = 0, to = FileLocalFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition) {
        switch (fabPosition) {
            case 0:
                mFab1.show();
                break;
            case 1:
                mFab2.show();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_home).setVisible(false);

        final Fragment fragment = getCurrentFragment();
        if (fragment instanceof HomeIconVisible) {
            menu.findItem(R.id.action_home).setVisible(((HomeIconVisible) fragment).isHomeVisible());
        } else {
            menu.findItem(R.id.action_home).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                add();
                return true;
            case R.id.action_home:
                goHome();
                return true;
            case R.id.action_search:
                SearchActivity.start(getContext());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        final int currentItemPosition = mViewPager.getCurrentItem();
        if (tab.getPosition() == currentItemPosition) {
            final Fragment fragment = getCurrentFragment();
            if (fragment != null && fragment instanceof ScrollTop) {
                ((ScrollTop) fragment).scrollTop();
            }
        }
    }

    public int getCurrentFragmentIndex() {
        return mViewPager.getCurrentItem();
    }

    @Nullable
    public Fragment getCurrentFragment() {
        return getChildFragmentManager().findFragmentByTag("android:switcher:" +
                R.id.fragment_file_view_pager + ":" + mPagerAdapter.getItemId(getCurrentFragmentIndex()));
    }

    public void refreshListServer() {
        final Fragment fragment = getCurrentFragment();
        if (fragment != null && fragment instanceof ListController) {
            ((ListController) fragment).refreshCurrentList();
        }
    }

    public void add() {
        new FileAddDialog(getActivity(), -1, new IListener() {
            @Override
            public void execute() {
                refreshListServer();
            }
        }, null);
    }

    public void goHome() {
        final Fragment fragment = getCurrentFragment();
        if (fragment != null && fragment instanceof FileLocalFragment) {
            ((FileLocalFragment) fragment).goHome();
        }
    }

    private void findViews(final View rootView) {
        mViewPager = (RtlViewPager) rootView.findViewById(R.id.fragment_file_view_pager);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.fragment_file_tab_layout);
        mFab1 = (FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_1);
        mFab2 = (FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_2);
    }

    private void initToolbar(View rootView) {
        mSetToolbarCallback.setToolbar((Toolbar) rootView.findViewById(R.id.fragment_file_toolbar));
        setStatusBarColor(getActivity(), R.color.status_bar);
        setHasOptionsMenu(true);
    }

    private void initViews(@Nullable Bundle savedInstanceState) {
        mPagerAdapter = new FileManagerFragmentPagerAdapter(
                getChildFragmentManager(), isSdCardFragmentVisible());

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        if (savedInstanceState == null) {
            mViewPager.setOffscreenPageLimit(getCount() - 1);
            mViewPager.setCurrentItem(INIT_FRAGMENT);
        }

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(this);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            mTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        }
        syncTabLayout();

        mFab1.setVisibility(View.GONE);
        mFab2.setVisibility(View.GONE);

        mFab1.setOnClickListener(this);
        mFab2.setOnClickListener(this);
    }

    private void syncTabLayout() {
        final int position = mViewPager.getCurrentItem();
        mSetToolbarCallback.setTitleToolbar(getTitleRes(position));
        for (int i = 0; i < getCount(); i++) {
            final TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(getImageRes(i));
                final Drawable drawable = tab.getIcon();
                if (drawable != null) {
                    drawable.setColorFilter(i == position ? Color.WHITE : Color.parseColor("#85455A64"),
                            PorterDuff.Mode.SRC_ATOP);
                }
            }
        }
    }

    private boolean isSdCardFragmentVisible() {
        return FileUtils.isSdCardAvailable() && FileUtils.isSdCardAvailable();
    }

    @DrawableRes
    private int getImageRes(int pagerPosition) {
        int realPosition = pagerPosition;
        if (isSdCardFragmentVisible()) {
            return mImageResId[pagerPosition];
        } else if (pagerPosition != 0) {
            realPosition++;
        }
        return mImageResId[realPosition];
    }

    @StringRes
    private int getTitleRes(int pagerPosition) {
        int realPosition = pagerPosition;
        if (isSdCardFragmentVisible()) {
            return mTitleIds[pagerPosition];
        } else if (pagerPosition != 0) {
            realPosition++;
        }
        return mTitleIds[realPosition];
    }

    private int getCount() {
        return NB_FRAGMENT + (isSdCardFragmentVisible() ? 1 : 0);
    }

    @Override
    public void onClick(final View v) {
        if (v == mFab1) {
            mFileLocalFabManager.onFabClick(0, mFab1);
        } else if (v == mFab2) {
            mFileLocalFabManager.onFabClick(1, mFab2);
        }
    }

    //region Inner class and interface

    /**
     * A simple {@link FragmentPagerAdapter}.
     */
    private static class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
        private final boolean mIsSdcardVisible;

        public FileManagerFragmentPagerAdapter(
                final FragmentManager fm,
                final boolean isSdcardVisible) {
            super(fm);
            mIsSdcardVisible = isSdcardVisible;
        }

        @Override
        public Fragment getItem(int position) {
            if (mIsSdcardVisible) {
                switch (position) {
                    case 0:
                        return FileLocalFragment.newInstance(position);
                    case 1:
                        return FileLocalSdFragment.newInstance(position);
                    case 2:
                        return FileAudioLocalFragment.newInstance(position);
                    case 3:
                        return FileImageLocalFragment.newInstance(position);
                    default:
                        return FileLocalFragment.newInstance(position);
                }
            } else {
                switch (position) {
                    case 0:
                        return FileLocalFragment.newInstance(position);
                    case 1:
                        return FileAudioLocalFragment.newInstance(position);
                    case 2:
                        return FileImageLocalFragment.newInstance(position);
                    default:
                        return FileLocalFragment.newInstance(position);
                }
            }
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT + (mIsSdcardVisible ? 1 : 0);
        }
    }

    /**
     * An interface to manager {@link java.util.List} and {@link android.support.v7.widget.RecyclerView}.
     */
    public interface ListController {

        /**
         * Refresh the visible {@link java.util.List} and {@link android.support.v7.widget.RecyclerView}.
         */
        void refreshCurrentList();

        /**
         * Update the {@link android.support.v7.widget.RecyclerView} adapter.
         */
        void updateAdapter();
    }

    interface HomeIconVisible {
        boolean isHomeVisible();
    }

    public interface ScrollTop {

        /**
         * Scroll to the top of the {@link android.support.v7.widget.RecyclerView}.
         */
        void scrollTop();
    }
    //endregion Inner class and interface
}
