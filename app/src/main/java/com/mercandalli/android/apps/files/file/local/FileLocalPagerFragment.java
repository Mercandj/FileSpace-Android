/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p>
 * LICENSE:
 * <p>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.file.local;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.fragment.FabFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.fab.FabController;
import com.mercandalli.android.apps.files.file.FileAddDialog;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.file.audio.FileAudioLocalFragment;
import com.mercandalli.android.apps.files.file.image.FileImageLocalFragment;
import com.mercandalli.android.apps.files.main.ApplicationCallback;
import com.mercandalli.android.apps.files.main.Constants;

public class FileLocalPagerFragment extends BackFragment implements
        ViewPager.OnPageChangeListener,
        FabFragment.RefreshFabCallback {

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
    private ViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private FloatingActionButton mFab1;
    private FloatingActionButton mFab2;
    //endregion Views

    private SetToolbarCallback mSetToolbarCallback;

    private static final int[] mImageResId = {
            R.drawable.ic_folder_open_white_24dp,
            R.drawable.ic_sd_storage_white_24dp,
            R.drawable.ic_music_note_white_24dp,
            R.drawable.ic_photo_white_24dp,
            R.drawable.ic_video_library_white_24dp
    };

    private static final int[] mTitleIds = {
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
        super.onDetach();
        mSetToolbarCallback = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file, container, false);
        findViews(rootView);
        initToolbar(rootView);
        initViews(savedInstanceState);
        return rootView;
    }

    @Override
    public boolean back() {
        final Fragment fragment = getCurrentFragment();
        if (fragment == null || !(fragment instanceof FabFragment)) {
            return false;
        }
        final FabFragment fabFragment = (FabFragment) fragment;
        refreshFab(fabFragment);
        return fabFragment.back();
    }

    @Override
    public void onFocus() {
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
        mApplicationCallback.invalidateMenu();
        refreshFab(position);
        syncTabLayout();
    }
    //endregion Override - ViewPager

    @Override
    public void onRefreshFab() {
        refreshFab();
    }

    @Override
    public void hideFab(int fab_id) {
        switch (fab_id) {
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

    @Override
    public void showFab(int fab_id) {
        switch (fab_id) {
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

        SearchView mSearchView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        } else {
            return;
        }

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                if (query == null) {
                    return true;
                }
                if (query.replaceAll(" ", "").equals("")) {
                    refreshListServer();
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query == null || query.replaceAll(" ", "").equals("")) {
                    return false;
                }
                refreshListServer(query);
                return false;
            }
        };

        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(queryTextListener);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_home).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(true);

        if (mApplicationCallback != null) {
            final Fragment fragment = getCurrentFragment();
            if (fragment instanceof HomeIconVisible) {
                menu.findItem(R.id.action_home).setVisible(((HomeIconVisible) fragment).isHomeVisible());
            } else {
                menu.findItem(R.id.action_home).setVisible(true);
            }
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
            case R.id.action_sort:
                sort();
                return true;
            case R.id.action_search:
                SearchActivity.start(getContext());
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        refreshListServer(null);
    }

    public void refreshListServer(String search) {
        final Fragment fragment = getCurrentFragment();
        if (fragment != null && fragment instanceof ListController) {
            ((ListController) fragment).refreshCurrentList(search);
        }
    }

    public void updateAdapterListServer() {
        final Fragment fragment = getCurrentFragment();
        if (fragment != null && fragment instanceof ListController) {
            ((ListController) fragment).updateAdapter();
        }
    }

    public void refreshData() {
        final Fragment fragment = getCurrentFragment();
        if (fragment != null && fragment instanceof ListController) {
            ((ListController) fragment).refreshCurrentList();
        }
    }

    public void add() {
        new FileAddDialog(getActivity(), mApplicationCallback, -1, new IListener() {
            @Override
            public void execute() {
                refreshListServer();
            }
        }, null);
    }

    public void goHome() {
        final Fragment fragment = getCurrentFragment();
        if (fragment != null && fragment instanceof FileLocalFragment) {
            FileLocalFragment fragmentFileManagerFragment = (FileLocalFragment) fragment;
            fragmentFileManagerFragment.goHome();
        }
    }

    public void sort() {
        final Context context = getContext();
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(getContext());
        String[] menuList = {context.getString(R.string.sort_abc), context.getString(R.string.sort_size), context.getString(R.string.sort_date)};
        menuAlert.setTitle(getString(R.string.view));
        menuAlert.setItems(menuList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        final Fragment fragment = getCurrentFragment();
                        if (fragment != null) {
                            if (fragment instanceof ISortMode) {
                                ((ISortMode) fragment).setSortMode(item == 0 ? Constants.SORT_ABC : (item == 1 ? Constants.SORT_SIZE : Constants.SORT_DATE_MODIFICATION));
                            } else {
                                Toast.makeText(getContext(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog menuDrop = menuAlert.create();
        menuDrop.show();
    }

    private void findViews(final View rootView) {
        mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_file_view_pager);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.fragment_file_tab_layout);
        mFab1 = ((FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_1));
        mFab2 = ((FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_2));
    }

    private void initToolbar(View rootView) {
        mSetToolbarCallback.setToolbar((Toolbar) rootView.findViewById(R.id.fragment_file_toolbar));
        setStatusBarColor(getActivity(), R.color.status_bar);
        setHasOptionsMenu(true);
    }

    private void initViews(@Nullable Bundle savedInstanceState) {
        mPagerAdapter = new FileManagerFragmentPagerAdapter(getChildFragmentManager(), mApplicationCallback);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        if (savedInstanceState == null) {
            mViewPager.setOffscreenPageLimit(getCount() - 1);
            mViewPager.setCurrentItem(INIT_FRAGMENT);
        }

        mTabLayout.setupWithViewPager(mViewPager);
        syncTabLayout();

        mFab1.setVisibility(View.GONE);
        mFab2.setVisibility(View.GONE);
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

    //region Fab
    private void refreshFab() {
        refreshFab(getCurrentFragmentIndex());
    }

    private void refreshFab(int currentFragmentId) {
        if (currentFragmentId == -1) {
            return;
        }
        final Fragment fabFragment = getCurrentFragment();
        if (fabFragment == null || !(fabFragment instanceof FabController)) {
            return;
        }
        refreshFab((FabController) fabFragment);
    }

    private void refreshFab(final FabController fabController) {
        if (mFab1 == null) {
            return;
        }
        int imageResource;
        if (fabController.isFabVisible(0)) {
            showFab(0);
            imageResource = fabController.getFabImageResource(0);
            if (imageResource == -1) {
                imageResource = android.R.drawable.ic_input_add;
            }
            mFab1.setImageResource(imageResource);
            mFab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabController.onFabClick(0, mFab1);
                }
            });
        } else {
            hideFab(0);
        }

        if (mFab2 == null) {
            return;
        }
        if (fabController.isFabVisible(1)) {
            showFab(1);
            imageResource = fabController.getFabImageResource(1);
            if (imageResource == -1) {
                imageResource = android.R.drawable.ic_input_add;
            }
            mFab2.setImageResource(imageResource);
            mFab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabController.onFabClick(1, mFab2);
                }
            });
        } else {
            hideFab(1);
        }
    }
    //endregion Fab

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

    //region Inner class and interface

    /**
     * A simple {@link FragmentPagerAdapter}.
     */
    public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
        ApplicationCallback mApplicationCallback;

        public FileManagerFragmentPagerAdapter(FragmentManager fm, ApplicationCallback applicationCallback) {
            super(fm);
            mApplicationCallback = applicationCallback;
        }

        @Override
        public FabFragment getItem(int i) {
            if (isSdCardFragmentVisible()) {
                switch (i) {
                    case 0:
                        return FileLocalFragment.newInstance();
                    case 1:
                        return FileLocalSdFragment.newInstance();
                    case 2:
                        return FileAudioLocalFragment.newInstance();
                    case 3:
                        return FileImageLocalFragment.newInstance();
                    default:
                        return FileLocalFragment.newInstance();
                }
            } else {
                switch (i) {
                    case 0:
                        return FileLocalFragment.newInstance();
                    case 1:
                        return FileAudioLocalFragment.newInstance();
                    case 2:
                        return FileImageLocalFragment.newInstance();
                    default:
                        return FileLocalFragment.newInstance();
                }
            }
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT + (isSdCardFragmentVisible() ? 1 : 0);
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
         * Refresh the visible {@link java.util.List} and {@link android.support.v7.widget.RecyclerView}.
         *
         * @param search The current search.
         */
        void refreshCurrentList(String search);

        /**
         * Update the {@link android.support.v7.widget.RecyclerView} adapter.
         */
        void updateAdapter();
    }

    interface HomeIconVisible {
        boolean isHomeVisible();
    }
    //endregion Inner class and interface
}
