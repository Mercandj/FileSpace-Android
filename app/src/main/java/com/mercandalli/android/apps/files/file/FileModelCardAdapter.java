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
package com.mercandalli.android.apps.files.file;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
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
import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link FileModel} card {@link android.support.v7.widget.RecyclerView.Adapter}.
 */
public class FileModelCardAdapter extends RecyclerView.Adapter<FileModelCardAdapter.ViewHolder> {

    /**
     * The view type of the header.
     */
    private static final int TYPE_HEADER = 0;

    /**
     * The view type of the card.
     */
    private static final int TYPE_CARD_ITEM = 1;

    private final String mStringDirectory;
    private final String mStringFile;
    private final String mStringFiles;

    private final Context mContext;
    private final List<FileModel> mFiles;
    private final OnFileClickListener mOnFileClickListener;
    private final OnFileLongClickListener mOnFileLongClickListener;
    private FileModelListener mMoreListener;
    private OnFileSubtitleAdapter mOnFileSubtitleAdapter;

    /* Header */
    private List<FileModelCardHeaderItem> mHeaderIds;
    private OnHeaderClickListener mOnHeaderClickListener;

    /**
     * Adapter without header.
     */
    public FileModelCardAdapter(
            final Context context,
            final List<FileModel> files,
            final FileModelListener moreListener,
            final OnFileClickListener onFileClickListener,
            final OnFileLongClickListener onFileLongClickListener) {
        mContext = context;
        mFiles = new ArrayList<>();
        mFiles.addAll(files);
        mMoreListener = moreListener;
        mOnFileClickListener = onFileClickListener;
        mOnFileLongClickListener = onFileLongClickListener;

        mStringDirectory = context.getString(R.string.file_model_adapter_directory);
        mStringFile = context.getString(R.string.file_model_adapter_file);
        mStringFiles = context.getString(R.string.file_model_adapter_files);
    }

    /**
     * Adapter with header.
     */
    public FileModelCardAdapter(
            final Context context,
            final List<FileModelCardHeaderItem> headerIds,
            final OnHeaderClickListener onHeaderClickListener,
            final List<FileModel> files,
            final FileModelListener moreListener,
            final OnFileClickListener onFileClickListener,
            final OnFileLongClickListener onFileLongClickListener) {
        this(context, files, moreListener, onFileClickListener, onFileLongClickListener);
        mHeaderIds = new ArrayList<>();
        mHeaderIds.addAll(headerIds);
        mOnHeaderClickListener = onHeaderClickListener;
    }

    @Override
    public FileModelCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_file_header_audio, parent, false),
                    mHeaderIds,
                    mOnHeaderClickListener
            );
        } else if (viewType == TYPE_CARD_ITEM) {
            return new CardViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_file, parent, false),
                    hasHeader(),
                    mOnFileClickListener,
                    mOnFileLongClickListener
            );
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly.");
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof HeaderViewHolder) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            headerViewHolder.setFileModelCardHeaderItems(mHeaderIds);
            return;
        }

        final int filesPosition = position - (hasHeader() ? 1 : 0);
        if (viewHolder instanceof CardViewHolder && filesPosition < mFiles.size()) {
            final CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;
            final FileModel fileModel = mFiles.get(filesPosition);

            cardViewHolder.mTitle.setText(getAdapterTitle(fileModel));
            cardViewHolder.subtitle.setText(getAdapterSubtitle(fileModel));

            if (fileModel.isDirectory()) {
                cardViewHolder.icon.setImageResource(R.drawable.directory);
            } else if (fileModel.getType() != null) {
                FileTypeModel type = fileModel.getType();
                if (type.equals(FileTypeModelENUM.AUDIO.type)) {
                    cardViewHolder.icon.setImageResource(R.drawable.file_audio);
                } else if (type.equals(FileTypeModelENUM.PDF.type)) {
                    cardViewHolder.icon.setImageResource(R.drawable.file_pdf);
                } else if (type.equals(FileTypeModelENUM.APK.type)) {
                    cardViewHolder.icon.setImageResource(R.drawable.file_apk);
                } else if (type.equals(FileTypeModelENUM.ARCHIVE.type)) {
                    cardViewHolder.icon.setImageResource(R.drawable.file_archive);
                } else if (type.equals(FileTypeModelENUM.FILESPACE.type)) {
                    cardViewHolder.icon.setImageResource(R.drawable.file_space);
                } else {
                    cardViewHolder.icon.setImageResource(R.drawable.file_default);
                }
            } else {
                cardViewHolder.icon.setImageResource(R.drawable.file_default);
            }

            if (mMoreListener == null) {
                cardViewHolder.more.setVisibility(View.GONE);
            }
            cardViewHolder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMoreListener != null) {
                        mMoreListener.executeFileModel(fileModel);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? TYPE_HEADER : TYPE_CARD_ITEM;
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public void setList(List<FileModel> list) {
        Preconditions.checkNotNull(list);
        mFiles.clear();
        mFiles.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnFileSubtitleAdapter(OnFileSubtitleAdapter onFileSubtitleAdapter) {
        mOnFileSubtitleAdapter = onFileSubtitleAdapter;
    }

    public boolean isHeader(final int position) {
        return hasHeader() && position == 0;
    }

    public boolean hasHeader() {
        return mOnHeaderClickListener != null;
    }

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
        String result;
        if (mOnFileSubtitleAdapter != null && (result = mOnFileSubtitleAdapter.onFileSubtitleModify(fileModel)) != null) {
            return result;
        }
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

    public interface OnFileClickListener {
        void onFileCardClick(View v, int position);
    }

    public interface OnFileLongClickListener {
        boolean onFileCardLongClick(View v, int position);
    }

    public interface OnHeaderClickListener {
        /**
         * The header is clicked.
         */
        boolean onHeaderClick(View v, List<FileModelCardHeaderItem> fileModelCardHeaderItems);
    }

    public interface OnFileSubtitleAdapter {

        @Nullable
        String onFileSubtitleModify(FileModel fileModel);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class HeaderViewHolder extends ViewHolder implements OnClickListener {

        private final OnHeaderClickListener mOnHeaderClickListener;
        private final List<FileModelCardHeaderItem> mFileModelCardHeaderItems;

        public HeaderViewHolder(View itemView, List<FileModelCardHeaderItem> headerIds, OnHeaderClickListener onHeaderClickListener) {
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

    private static class CardViewHolder extends ViewHolder implements OnClickListener, View.OnLongClickListener {
        public final TextView mTitle;
        public final TextView subtitle;
        public final ImageView icon;
        public final View item;
        public final View more;
        private final OnFileClickListener mOnFileClickListener;
        private final OnFileLongClickListener mOnFileLongClickListener;
        private final boolean mHasHeader;

        public CardViewHolder(View itemLayoutView, boolean hasHeader, OnFileClickListener onFileClickListener, OnFileLongClickListener onFileLongClickListener) {
            super(itemLayoutView);
            mHasHeader = hasHeader;
            item = itemLayoutView.findViewById(R.id.card_file_item);
            mTitle = (TextView) itemLayoutView.findViewById(R.id.card_file_title);
            subtitle = (TextView) itemLayoutView.findViewById(R.id.card_file_subtitle);
            icon = (ImageView) itemLayoutView.findViewById(R.id.card_file_icon);
            more = itemLayoutView.findViewById(R.id.card_file_more);
            mOnFileClickListener = onFileClickListener;
            mOnFileLongClickListener = onFileLongClickListener;
            item.setOnClickListener(this);
            item.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnFileClickListener != null) {
                mOnFileClickListener.onFileCardClick(icon, getAdapterPosition() - (mHasHeader ? 1 : 0));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mOnFileLongClickListener != null && mOnFileLongClickListener.onFileCardLongClick(v, getAdapterPosition() - (mHasHeader ? 1 : 0));
        }
    }
}
