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
package com.mercandalli.android.apps.files.file.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.util.StringUtils;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelCardAdapter;
import com.mercandalli.android.apps.files.file.FileModelCardHeaderItem;
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.file.FileTypeModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.precondition.Preconditions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * The adapter for {@link FileAudioModel} rows.
 */
public class FileImageAdapter extends RecyclerView.Adapter<FileImageAdapter.ViewHolder> {

    private List<FileModel> files;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private FileModelListener moreListener;

    private boolean mHasHeader;

    private final String mStringDirectory;
    private final String mStringFile;
    private final String mStringFiles;

    /**
     * The view type of the header.
     */
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ROW_CARDS_HEADER = 1;
    private static final int TYPE_ITEM = 2;

    /* Header */
    private List<FileModelCardHeaderItem> mHeaderIds;
    private FileModelCardAdapter.OnHeaderClickListener mOnHeaderClickListener;

    @Inject
    FileManager mFileManager;

    public FileImageAdapter(Context context, List<FileModel> files, FileModelListener moreListener) {
        this.files = new ArrayList<>();
        this.files.addAll(files);
        this.moreListener = moreListener;
        this.mHasHeader = false;

        mStringDirectory = context.getString(R.string.file_model_adapter_directory);
        mStringFile = context.getString(R.string.file_model_adapter_file);
        mStringFiles = context.getString(R.string.file_model_adapter_files);

        FileApp.get().getFileAppComponent().inject(this);
    }

    /**
     * Adapter with header.
     */
    public FileImageAdapter(
            final List<FileModelCardHeaderItem> headerIds,
            final FileModelCardAdapter.OnHeaderClickListener onHeaderClickListener,
            Activity activity,
            List<FileModel> files,
            FileModelListener moreListener) {
        this(activity, files, moreListener);
        this.mHasHeader = true;
        mHeaderIds = new ArrayList<>();
        mHeaderIds.addAll(headerIds);
        mOnHeaderClickListener = onHeaderClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHasHeader && isHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_file_header_image, parent, false),
                    mHeaderIds,
                    mOnHeaderClickListener
            );
        } else if (viewType == TYPE_ROW_CARDS_HEADER) {
            return new RowCardsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file_row_cards, parent, false));
        }
        return new FileViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_file, parent, false),
                mHasHeader,
                mItemClickListener,
                mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            headerViewHolder.setFileModelCardHeaderItems(mHeaderIds);
        } else if (position < files.size() + (mHasHeader ? 1 : 0)) {
            final FileViewHolder fileViewHolder = (FileViewHolder) viewHolder;
            final FileModel fileModel = files.get(position - (mHasHeader ? 1 : 0));

            fileViewHolder.mTitle.setText(getAdapterTitle(fileModel));
            fileViewHolder.mSubtitle.setText(getAdapterSubtitle(fileModel));

            final ImageView icon = fileViewHolder.mIcon;
            if (fileModel.isDirectory()) {
                icon.setImageResource(R.drawable.directory);
            } else if (fileModel.getType() != null) {
                final FileTypeModel type = fileModel.getType();

                if (type.equals(FileTypeModelENUM.AUDIO.type)) {
                    icon.setImageResource(R.drawable.file_audio);
                } else if (type.equals(FileTypeModelENUM.PDF.type)) {
                    icon.setImageResource(R.drawable.file_pdf);
                } else if (type.equals(FileTypeModelENUM.APK.type)) {
                    icon.setImageResource(R.drawable.file_apk);
                } else if (type.equals(FileTypeModelENUM.ARCHIVE.type)) {
                    icon.setImageResource(R.drawable.file_archive);
                } else if (type.equals(FileTypeModelENUM.FILESPACE.type)) {
                    icon.setImageResource(R.drawable.file_space);
                } else if (type.equals(FileTypeModelENUM.IMAGE.type)) {
                    icon.setBackgroundColor(Color.parseColor("#0d84c8"));
                    Picasso.with(fileViewHolder.mItem.getContext())
                            .load(fileModel.getFile())
                            .placeholder(R.drawable.file_default)
                            .into(icon);
                } else {
                    icon.setBackgroundColor(Color.parseColor("#0d84c8"));
                    icon.setImageResource(R.drawable.file_default);
                }
            } else {
                icon.setBackgroundColor(Color.parseColor("#0d84c8"));
                icon.setImageResource(R.drawable.file_default);
            }

            //mFileManager.getCover(mActivity, file, fileViewHolder.icon);

            if (moreListener == null) {
                fileViewHolder.more.setVisibility(View.GONE);
            }
            fileViewHolder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreListener != null) {
                        moreListener.executeFileModel(fileModel, v);
                    }
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class FileViewHolder extends ViewHolder implements OnClickListener, View.OnLongClickListener {
        public TextView mTitle, mSubtitle;
        public ImageView mIcon;
        public View mItem;
        public View more;
        private boolean mHasHeader;
        private OnItemClickListener mItemClickListener;
        private OnItemLongClickListener mItemLongClickListener;

        public FileViewHolder(View itemLayoutView, boolean hasHeader, OnItemClickListener itemClickListener, OnItemLongClickListener itemLongClickListener) {
            super(itemLayoutView);
            mHasHeader = hasHeader;
            mItemClickListener = itemClickListener;
            mItemLongClickListener = itemLongClickListener;
            mItem = itemLayoutView.findViewById(R.id.card_file_item);
            mTitle = (TextView) itemLayoutView.findViewById(R.id.card_file_title);
            mSubtitle = (TextView) itemLayoutView.findViewById(R.id.card_file_subtitle);
            mIcon = (ImageView) itemLayoutView.findViewById(R.id.card_file_icon);
            more = itemLayoutView.findViewById(R.id.card_file_more);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(mIcon, getAdapterPosition() - (mHasHeader ? 1 : 0));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mItemLongClickListener != null && mItemLongClickListener.onItemLongClick(v, getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return files.size() + (mHasHeader ? 1 : 0);
    }

    public void addItem(FileModel name, int position) {
        this.files.add(position, name);
        this.notifyItemInserted(position);
    }

    public void removeAll() {
        int size = files.size();
        if (size > 0) {
            files = new ArrayList<>();
            this.notifyItemRangeInserted(0, size - 1);
        }
    }

    public void setList(List<FileModel> list) {
        files.clear();
        files.addAll(list);
        notifyDataSetChanged();
    }

    public boolean isHeader(int position) {
        return position == 0 && mHasHeader;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    private String getAdapterTitle(final FileModel fileModel) {
        String adapterTitleStart = "";

        if (fileModel.getType() == null) {
            return adapterTitleStart + (fileModel.getName() == null ?
                    fileModel.getUrl() : fileModel.getFullName());
        } else if (fileModel.getType().equals(FileTypeModelENUM.FILESPACE.type) && fileModel.getContent() != null) {
            return adapterTitleStart + fileModel.getContent().getAdapterTitle();
        } else if (fileModel.getName() != null) {
            return adapterTitleStart + fileModel.getFullName();
        } else {
            return adapterTitleStart + fileModel.getUrl();
        }
    }

    public String getAdapterSubtitle(FileModel fileModel) {
        if (fileModel.isDirectory()) {
            return mStringDirectory + ": " + fileModel.getCount() + " " + (fileModel.getCount() > 1 ? mStringFiles : mStringFile);
        } else if (fileModel instanceof FileAudioModel) {
            final FileAudioModel fileAudioModel = (FileAudioModel) fileModel;
            if (!StringUtils.isNullOrEmpty(fileAudioModel.getAlbum()) && !StringUtils.isNullOrEmpty(fileAudioModel.getArtist())) {
                return fileAudioModel.getArtist() + " - " + fileAudioModel.getAlbum();
            }
            if (!StringUtils.isNullOrEmpty(fileAudioModel.getArtist())) {
                return fileAudioModel.getArtist();
            }
            if (!StringUtils.isNullOrEmpty(fileAudioModel.getAlbum())) {
                return fileAudioModel.getAlbum();
            }

            String adapterTitleStart = "";
            if (fileAudioModel.getType() == null) {
                if (fileAudioModel.getName() != null) {
                    return adapterTitleStart + fileAudioModel.getFullName();
                } else {
                    return adapterTitleStart + fileAudioModel.getUrl();
                }
            } else if (fileAudioModel.getType().equals(FileTypeModelENUM.FILESPACE.type) && fileAudioModel.getContent() != null) {
                return adapterTitleStart + fileAudioModel.getContent().getAdapterTitle();
            } else if (fileAudioModel.getName() != null) {
                return adapterTitleStart + fileAudioModel.getFullName();
            } else {
                return adapterTitleStart + fileAudioModel.getUrl();
            }
        }
        return FileUtils.humanReadableByteCount(fileModel.getSize());
    }

    private static class RowCardsViewHolder extends ViewHolder {

        public RowCardsViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class HeaderViewHolder extends ViewHolder implements OnClickListener {

        @ColorInt
        private final int mPrimaryColor;
        private final FileModelCardAdapter.OnHeaderClickListener mOnHeaderClickListener;
        private final List<FileModelCardHeaderItem> mFileModelCardHeaderItems;

        public HeaderViewHolder(View itemView, List<FileModelCardHeaderItem> headerIds, FileModelCardAdapter.OnHeaderClickListener onHeaderClickListener) {
            super(itemView);
            Preconditions.checkNotNull(onHeaderClickListener);
            mPrimaryColor = ContextCompat.getColor(itemView.getContext(), R.color.primary);
            mOnHeaderClickListener = onHeaderClickListener;
            mFileModelCardHeaderItems = new ArrayList<>();
            mFileModelCardHeaderItems.addAll(headerIds);
            updateView();
        }

        @Override
        public void onClick(View v) {
            final int viewId = v.getId();

            boolean isElementAlreadySelected = false;
            for (FileModelCardHeaderItem f : mFileModelCardHeaderItems) {
                if (f.getId() == viewId && f.isSelected()) {
                    isElementAlreadySelected = true;
                    break;
                }
            }
            if (isElementAlreadySelected) {
                return;
            }
            for (FileModelCardHeaderItem f : mFileModelCardHeaderItems) {
                f.setSelected(f.getId() == viewId);
            }
            mOnHeaderClickListener.onHeaderClick(v, mFileModelCardHeaderItems);
            updateView();
        }

        public void setFileModelCardHeaderItems(List<FileModelCardHeaderItem> fileModelCardHeaderItems) {
            mFileModelCardHeaderItems.clear();
            mFileModelCardHeaderItems.addAll(fileModelCardHeaderItems);
            updateView();
        }

        private void updateView() {
            for (FileModelCardHeaderItem f : mFileModelCardHeaderItems) {
                final TextView tv = (TextView) itemView.findViewById(f.getId());
                tv.setOnClickListener(this);
                if (f.isSelected()) {
                    tv.setTextColor(mPrimaryColor);
                    tv.setBackgroundResource(R.drawable.file_local_audio_rounded_bg_selected);
                } else {
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundResource(R.drawable.file_local_audio_rounded_bg);
                }
            }
        }
    }

    public void setHasHeader(boolean hasHeader) {
        mHasHeader = hasHeader;
    }
}
