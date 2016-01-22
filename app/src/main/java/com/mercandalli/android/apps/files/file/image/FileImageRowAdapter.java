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
package com.mercandalli.android.apps.files.file.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * The adapter for {@link FileAudioModel} rows.
 */
public class FileImageRowAdapter extends RecyclerView.Adapter<FileImageRowAdapter.ViewHolder> {

    private List<FileModel> files;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private FileModelListener moreListener;

    private boolean mShowSize;
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

    public FileImageRowAdapter(Context context, List<FileModel> files, FileModelListener moreListener) {
        this.files = new ArrayList<>();
        this.files.addAll(files);
        this.moreListener = moreListener;
        this.mHasHeader = false;

        mStringDirectory = context.getString(R.string.file_model_adapter_directory);
        mStringFile = context.getString(R.string.file_model_adapter_file);
        mStringFiles = context.getString(R.string.file_model_adapter_files);

        FileApp.get(context).getFileAppComponent().inject(this);
    }

    /**
     * Adapter with header.
     */
    public FileImageRowAdapter(
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
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_file_header_audio, parent, false),
                    mHeaderIds,
                    mOnHeaderClickListener
            );
        } else if (viewType == TYPE_ROW_CARDS_HEADER) {
            return new RowCardsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file_row_cards, parent, false));
        }
        return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file_card_drag_drop, parent, false), mItemClickListener, mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            headerViewHolder.setFileModelCardHeaderItems(mHeaderIds);
        } else if (position < files.size() + (mHasHeader ? 1 : 0)) {
            final FileViewHolder fileViewHolder = (FileViewHolder) viewHolder;
            final FileModel file = files.get(position - (mHasHeader ? 1 : 0));

            fileViewHolder.title.setText(getAdapterTitle(file));
            fileViewHolder.subtitle.setText(getAdapterSubtitle(file));

            if (file.isDirectory()) {
                fileViewHolder.icon.setImageResource(R.drawable.directory);
            } else if (file.getType() != null) {
                FileTypeModel type = file.getType();
                if (type.equals(FileTypeModelENUM.AUDIO.type)) {
                    fileViewHolder.icon.setImageResource(R.drawable.file_audio);
                } else if (type.equals(FileTypeModelENUM.PDF.type)) {
                    fileViewHolder.icon.setImageResource(R.drawable.file_pdf);
                } else if (type.equals(FileTypeModelENUM.APK.type)) {
                    fileViewHolder.icon.setImageResource(R.drawable.file_apk);
                } else if (type.equals(FileTypeModelENUM.ARCHIVE.type)) {
                    fileViewHolder.icon.setImageResource(R.drawable.file_archive);
                } else if (type.equals(FileTypeModelENUM.FILESPACE.type)) {
                    fileViewHolder.icon.setImageResource(R.drawable.file_space);
                } else {
                    fileViewHolder.icon.setImageResource(R.drawable.file_default);
                }
            } else {
                fileViewHolder.icon.setImageResource(R.drawable.file_default);
            }

            //mFileManager.getCover(mActivity, file, fileViewHolder.icon);

            if (moreListener == null) {
                fileViewHolder.more.setVisibility(View.GONE);
            }
            fileViewHolder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreListener != null) {
                        moreListener.executeFileModel(file);
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
        public TextView title, subtitle;
        public ImageView icon;
        public View item;
        public View more;
        OnItemClickListener mItemClickListener;
        OnItemLongClickListener mItemLongClickListener;

        public FileViewHolder(View itemLayoutView, OnItemClickListener itemClickListener, OnItemLongClickListener itemLongClickListener) {
            super(itemLayoutView);
            mItemClickListener = itemClickListener;
            mItemLongClickListener = itemLongClickListener;
            item = itemLayoutView.findViewById(R.id.tab_file_card_drag_drop_item);
            title = (TextView) itemLayoutView.findViewById(R.id.tab_file_card_drag_drop_title);
            subtitle = (TextView) itemLayoutView.findViewById(R.id.tab_file_card_drag_drop_subtitle);
            icon = (ImageView) itemLayoutView.findViewById(R.id.tab_file_card_drag_drop_icon);
            more = itemLayoutView.findViewById(R.id.tab_file_card_drag_drop_more);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(icon, getAdapterPosition());
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


    public void remplaceList(ArrayList<FileModel> list) {
        files.clear();
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addFirst(ArrayList<FileModel> list) {
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(ArrayList<FileModel> list) {
        files.addAll(files.size(), list);
        notifyDataSetChanged();
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

    private String getAdapterTitle(FileModel fileModel) {
        String adapterTitleStart = "";
        if (mShowSize) {
            adapterTitleStart = FileUtils.humanReadableByteCount(fileModel.getSize()) + " - ";
        }

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
            if (mShowSize) {
                adapterTitleStart = FileUtils.humanReadableByteCount(fileAudioModel.getSize()) + " - ";
            }

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

        private final FileModelCardAdapter.OnHeaderClickListener mOnHeaderClickListener;
        private final List<FileModelCardHeaderItem> mFileModelCardHeaderItems;

        public HeaderViewHolder(View itemView, List<FileModelCardHeaderItem> headerIds, FileModelCardAdapter.OnHeaderClickListener onHeaderClickListener) {
            super(itemView);
            Preconditions.checkNotNull(onHeaderClickListener);
            mOnHeaderClickListener = onHeaderClickListener;
            mFileModelCardHeaderItems = new ArrayList<>();
            mFileModelCardHeaderItems.addAll(headerIds);
            updateView();
        }

        public void setFileModelCardHeaderItems(List<FileModelCardHeaderItem> fileModelCardHeaderItems) {
            mFileModelCardHeaderItems.clear();
            mFileModelCardHeaderItems.addAll(fileModelCardHeaderItems);
            updateView();
        }

        private void updateView() {
            final Context context = itemView.getContext();
            for (FileModelCardHeaderItem i : mFileModelCardHeaderItems) {
                final TextView tv = (TextView) itemView.findViewById(i.getId());
                tv.setOnClickListener(this);
                if (i.isSelected()) {
                    tv.setTextColor(ContextCompat.getColor(context, R.color.primary));
                    tv.setBackgroundResource(R.drawable.file_local_audio_rounded_bg_selected);
                } else {
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundResource(R.drawable.file_local_audio_rounded_bg);
                }
            }
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
                if (f.getId() == viewId) {
                    f.setSelected(true);
                } else {
                    f.setSelected(false);
                }
            }
            mOnHeaderClickListener.onHeaderClick(v, mFileModelCardHeaderItems);
            updateView();
        }
    }

    public void setHasHeader(boolean hasHeader) {
        mHasHeader = hasHeader;
    }
}
