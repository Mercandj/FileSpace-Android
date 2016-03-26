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
package com.mercandalli.android.apps.files.file;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.precondition.Preconditions;
import com.mercandalli.android.apps.files.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The main {@link FileModel} {@link RecyclerView.Adapter}.
 */
public class FileModelAdapter extends RecyclerView.Adapter<FileModelAdapter.ViewHolder> {

    private final Context mContext;
    private final List<FileModel> mFiles;
    private final OnFileClickListener mOnFileClickListener;
    private final OnFileLongClickListener mOnFileLongClickListener;
    private final FileModelListener mMoreListener;

    private final String mStringDirectory;
    private final String mStringFile;
    private final String mStringFiles;

    public FileModelAdapter(
            final Context context,
            final List<FileModel> files,
            final FileModelListener moreListener,
            final OnFileClickListener onFileClickListener,
            final OnFileLongClickListener onFileLongClickListener) {
        Preconditions.checkNotNull(files);
        mContext = context;
        mFiles = new ArrayList<>();
        mFiles.addAll(files);
        mMoreListener = moreListener;
        mOnFileClickListener = onFileClickListener;
        mOnFileLongClickListener = onFileLongClickListener;
        mStringDirectory = context.getString(R.string.file_model_adapter_directory);
        mStringFile = context.getString(R.string.file_model_adapter_file);
        mStringFiles = context.getString(R.string.file_model_adapter_files);
        setHasStableIds(true);
    }

    @Override
    public FileModelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.tab_file_card, parent, false), mOnFileClickListener, mOnFileLongClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final FileModel fileModel = getFileModel(position);
        if (fileModel == null) {
            return;
        }

        final String adapterTitle = getAdapterTitle(fileModel);
        viewHolder.mTitleTextView.setText(adapterTitle);
        viewHolder.mMoreView.setContentDescription("overflow#" + adapterTitle + "#" + fileModel.getId());
        viewHolder.mSubtitleTextView.setText(getAdapterSubtitle(fileModel));

        if (fileModel.isDirectory()) {
            viewHolder.mIconImageView.setImageResource(R.drawable.directory);
        } else if (fileModel.getType() != null) {
            final FileTypeModel type = fileModel.getType();
            if (FileTypeModelENUM.AUDIO.type.equals(type)) {
                viewHolder.mIconImageView.setImageResource(R.drawable.file_audio);
            } else if (FileTypeModelENUM.PDF.type.equals(type)) {
                viewHolder.mIconImageView.setImageResource(R.drawable.file_pdf);
            } else if (FileTypeModelENUM.APK.type.equals(type)) {
                viewHolder.mIconImageView.setImageResource(R.drawable.file_apk);
            } else if (FileTypeModelENUM.ARCHIVE.type.equals(type)) {
                viewHolder.mIconImageView.setImageResource(R.drawable.file_archive);
            } else if (FileTypeModelENUM.FILESPACE.type.equals(type)) {
                viewHolder.mIconImageView.setImageResource(R.drawable.file_space);
            } else {
                viewHolder.mIconImageView.setImageResource(R.drawable.file_default);
            }
        } else {
            viewHolder.mIconImageView.setImageResource(R.drawable.file_default);
        }

        /*
        if (file.bitmap != null)
            viewHolder.icon.setImageBitmap(file.bitmap);
        */

        if (mMoreListener == null) {
            viewHolder.mMoreView.setVisibility(View.GONE);
        }
        viewHolder.mMoreView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreListener != null) {
                    mMoreListener.executeFileModel(fileModel, v);
                }
            }
        });

        /*
        if (file.selected)
            viewHolder.item.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.tab_selected));
        else
            viewHolder.item.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.tab_file));
        */
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public void setList(final List<FileModel> list) {
        Preconditions.checkNotNull(list);
        mFiles.clear();
        mFiles.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(FileModel name, int position) {
        mFiles.add(position, name);
        notifyItemInserted(position);
    }

    /**
     * Return the file title.
     */
    private String getAdapterTitle(FileModel fileModel) {
        String adapterTitleStart = "";

        if (fileModel.getType() == null) {
            if (fileModel.getName() != null) {
                return adapterTitleStart + fileModel.getFullName();
            } else {
                return adapterTitleStart + fileModel.getUrl();
            }
        } else if (fileModel.getType().equals(FileTypeModelENUM.FILESPACE.type) && fileModel.getContent() != null) {
            return adapterTitleStart + fileModel.getContent().getAdapterTitle();
        } else if (fileModel.getName() != null) {
            return adapterTitleStart + fileModel.getFullName();
        } else {
            return adapterTitleStart + fileModel.getUrl();
        }
    }

    private String getAdapterSubtitle(FileModel fileModel) {
        if (fileModel.isDirectory() && fileModel.getCount() != 0) {
            return mStringDirectory + ": " + StringUtils.longToShortString(fileModel.getCount()) + " " + (fileModel.getCount() > 1 ? mStringFiles : mStringFile);
        }
        if (fileModel.isDirectory()) {
            return mStringDirectory;
        }

        if (FileTypeModelENUM.FILESPACE.type.equals(fileModel.getType()) && fileModel.getContent() != null) {
            return fileModel.getType().getTitle(mContext) + " " + StringUtils.capitalize(fileModel.getContent().getType().toString());
        }

        if (fileModel.getType() != null) {
            return fileModel.getType().getTitle(mContext);
        }
        return "";
    }

    @Nullable
    private FileModel getFileModel(final int adapterPosition) {
        if (adapterPosition >= mFiles.size()) {
            return null;
        }
        return mFiles.get(adapterPosition);
    }

    public interface OnFileClickListener {

        void onFileClick(View view, int position);
    }

    public interface OnFileLongClickListener {

        boolean onFileLongClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        public final TextView mTitleTextView;
        public final TextView mSubtitleTextView;
        public final ImageView mIconImageView;
        public final View mItemView;
        public final View mMoreView;
        private final OnFileClickListener mOnFileClickListener;
        private final OnFileLongClickListener mOnFileLongClickListener;

        public ViewHolder(View itemLayoutView, OnFileClickListener onFileClickListener, OnFileLongClickListener onFileLongClickListener) {
            super(itemLayoutView);
            mItemView = itemLayoutView.findViewById(R.id.tab_file_card_item);
            mTitleTextView = (TextView) itemLayoutView.findViewById(R.id.tab_file_card_title);
            mSubtitleTextView = (TextView) itemLayoutView.findViewById(R.id.tab_file_card_subtitle);
            mIconImageView = (ImageView) itemLayoutView.findViewById(R.id.tab_file_card_icon);
            mMoreView = itemLayoutView.findViewById(R.id.tab_file_card_more);
            mOnFileClickListener = onFileClickListener;
            mOnFileLongClickListener = onFileLongClickListener;
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnFileClickListener != null) {
                mOnFileClickListener.onFileClick(mIconImageView, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mOnFileLongClickListener != null && mOnFileLongClickListener.onFileLongClick(v, getAdapterPosition());
        }
    }
}
