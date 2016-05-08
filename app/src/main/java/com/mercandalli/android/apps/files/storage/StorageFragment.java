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
package com.mercandalli.android.apps.files.storage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;

import static com.mercandalli.android.library.baselibrary.view.StatusBarUtils.setStatusBarColor;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class StorageFragment extends Fragment {

    @Nullable
    private SetToolbarCallback mSetToolbarCallback;

    @Nullable
    private StorageProgressBarWrapper mMainProgressBar;

    public static StorageFragment newInstance() {
        return new StorageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_storage, container, false);

        mMainProgressBar = (StorageProgressBarWrapper) rootView.findViewById(R.id.fragment_storage_progress_bar);
        mMainProgressBar.setDuration(800);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_storage_toolbar);
        if (mSetToolbarCallback != null && toolbar != null) {
            toolbar.setTitle("Storage");
            mSetToolbarCallback.setToolbar(toolbar);
        }
        setStatusBarColor(activity, R.color.status_bar);

        updateView(StorageManager.getInstance().getStorage());

        return rootView;
    }

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

    private void updateView(@NonNull final Storage storage) {
        if (mMainProgressBar == null) {
            return;
        }
        final long totalSize = storage.getTotalSize();
        mMainProgressBar.setProgress(
                (int) ((100f * (totalSize - storage.getAvailableSize())) / totalSize));
    }
}
