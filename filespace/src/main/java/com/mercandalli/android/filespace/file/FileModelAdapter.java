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
package com.mercandalli.android.filespace.file;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.Preconditions;
import com.mercandalli.android.filespace.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FileModelAdapter extends RecyclerView.Adapter<FileModelAdapter.ViewHolder> {

    private final List<FileModel> mFiles;
    private final OnFileClickListener mOnFileClickListener;
    private final OnFileLongClickListener mOnFileLongClickListener;
    private FileModelListener mMoreListener;

    public FileModelAdapter(
            final List<FileModel> files,
            final FileModelListener moreListener,
            final OnFileClickListener onFileClickListener,
            final OnFileLongClickListener onFileLongClickListener) {
        mFiles = new ArrayList<>();
        mFiles.addAll(files);
        mMoreListener = moreListener;
        mOnFileClickListener = onFileClickListener;
        mOnFileLongClickListener = onFileLongClickListener;
    }

    @Override
    public FileModelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file_card, parent, false), mOnFileClickListener, mOnFileLongClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (position < mFiles.size()) {
            final FileModel fileModel = mFiles.get(position);

            viewHolder.mTitleTextView.setText(getAdapterTitle(fileModel));
            viewHolder.mSubtitleTextView.setText(getAdapterSubtitle(fileModel));

            if (fileModel.isDirectory()) {
                viewHolder.mIconImageView.setImageResource(R.drawable.directory);
            } else if (fileModel.getType() != null) {
                FileTypeModel type = fileModel.getType();
                if (type.equals(FileTypeModelENUM.AUDIO.type)) {
                    viewHolder.mIconImageView.setImageResource(R.drawable.file_audio);
                } else if (type.equals(FileTypeModelENUM.PDF.type))
                    viewHolder.mIconImageView.setImageResource(R.drawable.file_pdf);
                else if (type.equals(FileTypeModelENUM.APK.type)) {
                    viewHolder.mIconImageView.setImageResource(R.drawable.file_apk);
                } else if (type.equals(FileTypeModelENUM.ARCHIVE.type)) {
                    viewHolder.mIconImageView.setImageResource(R.drawable.file_archive);
                } else if (type.equals(FileTypeModelENUM.FILESPACE.type)) {
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
                        mMoreListener.executeFileModel(fileModel);
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
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }


    public void replaceList(List<FileModel> list) {
        mFiles.clear();
        mFiles.addAll(list);
        notifyDataSetChanged();
    }

    public void addFirst(List<FileModel> list) {
        mFiles.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(List<FileModel> list) {
        mFiles.addAll(mFiles.size(), list);
        notifyDataSetChanged();
    }

    public void addItem(FileModel name, int position) {
        this.mFiles.add(position, name);
        this.notifyItemInserted(position);
    }

    public void removeAll() {
        int size = mFiles.size();
        if (size > 0) {
            mFiles.clear();
            this.notifyItemRangeInserted(0, size - 1);
        }
    }

    private String getAdapterTitle(FileModel fileModel) {
        String adapterTitleStart = "";

        if (fileModel.getType() == null) {
            if (fileModel.getName() != null)
                return adapterTitleStart + fileModel.getFullName();
            else
                return adapterTitleStart + fileModel.getUrl();
        } else if (fileModel.getType().equals(FileTypeModelENUM.FILESPACE.type) && fileModel.getContent() != null) {
            return adapterTitleStart + fileModel.getContent().getAdapterTitle();
        } else if (fileModel.getName() != null)
            return adapterTitleStart + fileModel.getFullName();
        else
            return adapterTitleStart + fileModel.getUrl();
    }

    public String getAdapterSubtitle(FileModel fileModel) {
        if (fileModel.isDirectory() && fileModel.getCount() != 0)
            return "Directory: " + StringUtils.longToShortString(fileModel.getCount()) + " file" + (fileModel.getCount() > 1 ? "s" : "");
        if (fileModel.isDirectory())
            return "Directory";

        if (FileTypeModelENUM.FILESPACE.type.equals(fileModel.getType()) && fileModel.getContent() != null)
            return fileModel.getType().getTitle() + " " + StringUtils.capitalize(fileModel.getContent().getType().toString());

        if (fileModel.getType() != null)
            return fileModel.getType().getTitle();
        return "";
    }

    public void setList(List<FileModel> list) {
        Preconditions.checkNotNull(list);
        mFiles.clear();
        mFiles.addAll(list);
        notifyDataSetChanged();
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
