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
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;
import com.mercandalli.android.library.base.battery.BatteryUtils;

import java.util.List;

import static com.mercandalli.android.library.base.view.StatusBarUtils.setStatusBarColor;

public class StorageFragment extends Fragment implements
        FileLocalProviderManager.GetFilePathsListener,
        FileLocalProviderManager.GetFileAudioListener,
        FileLocalProviderManager.GetFileImageListener {

    @Nullable
    private SetToolbarCallback mSetToolbarCallback;

    @Nullable
    private TextView mNumberFiles;
    @Nullable
    private TextView mNumberMusics;
    @Nullable
    private TextView mNumberPhotos;

    public static StorageFragment newInstance() {
        return new StorageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_storage, container, false);

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_storage_toolbar);
        if (mSetToolbarCallback != null && toolbar != null) {
            toolbar.setTitle("Storage");
            mSetToolbarCallback.setToolbar(toolbar);
        }
        setStatusBarColor(activity, R.color.status_bar);

        updateView(
                (StorageProgressBarWrapper) rootView.findViewById(R.id.fragment_storage_progress_bar),
                1_000,
                StorageManager.getInstance().getStorageDisk());
        updateView(
                (StorageProgressBarWrapper) rootView.findViewById(R.id.fragment_storage_progress_bar_ram),
                1_000,
                StorageManager.getInstance().getRam(activity));
        updateView(
                (StorageProgressBarWrapper) rootView.findViewById(R.id.fragment_storage_progress_bar_battery),
                1_000,
                BatteryUtils.getBatteryPercent(activity));

        mNumberFiles = (TextView) rootView.findViewById(R.id.fragment_storage_number_files);
        mNumberMusics = (TextView) rootView.findViewById(R.id.fragment_storage_number_musics);
        mNumberPhotos = (TextView) rootView.findViewById(R.id.fragment_storage_number_photos);

        final FileLocalProviderManager fileLocalProviderManager =
                FileLocalProviderManager.getInstance(activity);
        fileLocalProviderManager.getFilePaths(this);
        fileLocalProviderManager.getFileAudioPaths(this);
        fileLocalProviderManager.getFileImagePaths(this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        final FileLocalProviderManager fileLocalProviderManager =
                FileLocalProviderManager.getInstance(getContext());
        fileLocalProviderManager.removeGetFilePathsListener(this);
        fileLocalProviderManager.removeGetFileAudioListener(this);
        fileLocalProviderManager.removeGetFileImageListener(this);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SetToolbarCallback) {
            mSetToolbarCallback = (SetToolbarCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a SetToolbarCallback. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        mSetToolbarCallback = null;
        super.onDetach();
    }

    private void updateView(
            final StorageProgressBarWrapper mainStorageProgressBarWrapper,
            final int animationDuration,
            @NonNull final Storage storage) {
        if (mainStorageProgressBarWrapper == null) {
            return;
        }
        final long totalSize = storage.getTotalSize();
        mainStorageProgressBarWrapper.setDuration(animationDuration);
        mainStorageProgressBarWrapper.setProgress(
                (int) ((100f * (totalSize - storage.getAvailableSize())) / totalSize));
    }

    private void updateView(
            final StorageProgressBarWrapper mainStorageProgressBarWrapper,
            final int animationDuration,
            final float percent) {
        if (mainStorageProgressBarWrapper == null) {
            return;
        }
        mainStorageProgressBarWrapper.setDuration(animationDuration);
        mainStorageProgressBarWrapper.setProgress((int) percent);
    }

    @Override
    public void onGetFile(@NonNull final List<String> filePaths) {
        if (mNumberFiles != null) {
            mNumberFiles.setText(filePaths.size() + " files");
        }
    }

    @Override
    public void onGetFileAudio(@NonNull final List<String> fileAudioPaths) {
        if (mNumberMusics != null) {
            mNumberMusics.setText(fileAudioPaths.size() + " musics");
        }
    }

    @Override
    public void onGetFileImage(@NonNull final List<String> fileImagePaths) {
        if (mNumberPhotos != null) {
            mNumberPhotos.setText(fileImagePaths.size() + " photos");
        }
    }
}
