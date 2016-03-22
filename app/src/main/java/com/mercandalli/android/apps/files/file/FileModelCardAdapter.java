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
import android.support.annotation.ColorInt;
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
import com.mercandalli.android.apps.files.file.filespace.FileSpaceModel;
import com.mercandalli.android.apps.files.precondition.Preconditions;
import com.mercandalli.android.apps.files.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link FileModel} card {@link android.support.v7.widget.RecyclerView.Adapter}.
 */
public class FileModelCardAdapter extends RecyclerView.Adapter<FileModelCardAdapter.ViewHolder> {

    /**
     * The view type of the header.
     */
    public static final int TYPE_HEADER_AUDIO = 0;

    /**
     * The view type of the header.
     */
    public static final int TYPE_HEADER_IMAGE = 1;

    /**
     * The view type of the card.
     */
    private static final int TYPE_CARD_ITEM = 2;

    /**
     * The application {@link Context}.
     */
    private final Context mContextApp;
    private final List<FileModel> mFiles;
    private final OnFileClickListener mOnFileClickListener;
    private final OnFileLongClickListener mOnFileLongClickListener;
    private FileModelListener mMoreListener;
    private OnFileSubtitleAdapter mOnFileSubtitleAdapter;

    private final String mStringDirectory;
    private final String mStringFile;
    private final String mStringFiles;

    /* Header */
    private List<FileModelCardHeaderItem> mHeaderIds;
    private OnHeaderClickListener mOnHeaderClickListener;

    private int mHeaderType = TYPE_HEADER_AUDIO;

    /**
     * Adapter without header.
     */
    public FileModelCardAdapter(
            final Context context,
            final List<FileModel> files,
            final FileModelListener moreListener,
            final OnFileClickListener onFileClickListener,
            final OnFileLongClickListener onFileLongClickListener) {
        mContextApp = context.getApplicationContext();
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
        if (viewType == TYPE_HEADER_AUDIO) {
            return new HeaderViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.view_file_header_audio, parent, false),
                    mHeaderIds,
                    mOnHeaderClickListener
            );
        } else if (viewType == TYPE_HEADER_IMAGE) {
            return new HeaderViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.view_file_header_image, parent, false),
                    mHeaderIds,
                    mOnHeaderClickListener
            );
        } else if (viewType == TYPE_CARD_ITEM) {
            return new CardViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.card_file, parent, false),
                    hasHeader(),
                    mOnFileClickListener,
                    mOnFileLongClickListener
            );
        }
        throw new RuntimeException("There is no type that matches the type " + viewType +
                " + make sure your using types correctly.");
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof HeaderViewHolder) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            headerViewHolder.setFileModelCardHeaderItems(mHeaderIds);
            return;
        }

        final FileModel fileModel = getFileModel(position);
        if (fileModel == null || !(viewHolder instanceof CardViewHolder)) {
            return;
        }

        final CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;
        cardViewHolder.mTitle.setText(getAdapterTitle(fileModel));
        cardViewHolder.subtitle.setText(getAdapterSubtitle(fileModel));

        final FileTypeModel type = fileModel.getType();
        if (fileModel.isDirectory()) {
            cardViewHolder.icon.setImageResource(R.drawable.directory);
        } else if (FileTypeModelENUM.AUDIO.type.equals(type)) {
            cardViewHolder.icon.setImageResource(R.drawable.file_audio);
        } else if (FileTypeModelENUM.PDF.type.equals(type)) {
            cardViewHolder.icon.setImageResource(R.drawable.file_pdf);
        } else if (FileTypeModelENUM.APK.type.equals(type)) {
            cardViewHolder.icon.setImageResource(R.drawable.file_apk);
        } else if (FileTypeModelENUM.ARCHIVE.type.equals(type)) {
            cardViewHolder.icon.setImageResource(R.drawable.file_archive);
        } else if (FileTypeModelENUM.FILESPACE.type.equals(type)) {
            cardViewHolder.icon.setImageResource(R.drawable.file_space);
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
                    mMoreListener.executeFileModel(fileModel, v);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? mHeaderType : TYPE_CARD_ITEM;
    }

    @Override
    public int getItemCount() {
        return mFiles.size() + (hasHeader() ? 1 : 0);
    }

    public void setList(final List<FileModel> list) {
        Preconditions.checkNotNull(list);
        mFiles.clear();
        mFiles.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnFileSubtitleAdapter(final OnFileSubtitleAdapter onFileSubtitleAdapter) {
        mOnFileSubtitleAdapter = onFileSubtitleAdapter;
    }

    public void setHeaderType(int headerType) {
        mHeaderType = headerType;
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
        } else if (FileTypeModelENUM.FILESPACE.type.equals(fileModel.getType()) &&
                fileModel.getContent() != null) {
            return adapterTitleStart + fileModel.getContent().getAdapterTitle();
        } else if (fileModel.getName() != null) {
            return adapterTitleStart + fileModel.getFullName();
        } else {
            return adapterTitleStart + fileModel.getUrl();
        }
    }

    private String getAdapterSubtitle(final FileModel fileModel) {
        String result;
        if (mOnFileSubtitleAdapter != null && (result =
                mOnFileSubtitleAdapter.onFileSubtitleModify(fileModel)) != null) {
            return result;
        }
        if (fileModel.isDirectory() && fileModel.getCount() != 0) {
            return mStringDirectory + ": " + StringUtils.longToShortString(fileModel.getCount()) +
                    " " + (fileModel.getCount() > 1 ? mStringFiles : mStringFile);
        }
        if (fileModel.isDirectory()) {
            return mStringDirectory;
        }

        final FileSpaceModel content;
        if (FileTypeModelENUM.FILESPACE.type.equals(fileModel.getType()) &&
                (content = fileModel.getContent()) != null) {
            return fileModel.getType().getTitle(mContextApp) + " " +
                    StringUtils.capitalize(content.getType().toString());
        }

        if (fileModel.getType() != null) {
            return fileModel.getType().getTitle(mContextApp);
        }
        return "";
    }

    @Nullable
    private FileModel getFileModel(final int adapterPosition) {
        if (isHeader(adapterPosition)) {
            return null;
        }
        final int filesPosition = adapterPosition - (hasHeader() ? 1 : 0);
        if (filesPosition >= mFiles.size()) {
            return null;
        }
        return mFiles.get(filesPosition);
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

        @ColorInt
        private final int mPrimaryColor;
        private final OnHeaderClickListener mOnHeaderClickListener;
        private final List<FileModelCardHeaderItem> mFileModelCardHeaderItems;

        public HeaderViewHolder(
                final View itemView,
                final List<FileModelCardHeaderItem> headerIds,
                final OnHeaderClickListener onHeaderClickListener) {
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

    private static class CardViewHolder extends ViewHolder implements
            OnClickListener, View.OnLongClickListener {
        public final TextView mTitle;
        public final TextView subtitle;
        public final ImageView icon;
        public final View mItem;
        public final View more;
        private final OnFileClickListener mOnFileClickListener;
        private final OnFileLongClickListener mOnFileLongClickListener;
        private final boolean mHasHeader;

        public CardViewHolder(
                final View itemLayoutView,
                final boolean hasHeader,
                final OnFileClickListener onFileClickListener,
                final OnFileLongClickListener onFileLongClickListener) {
            super(itemLayoutView);
            mHasHeader = hasHeader;
            mItem = itemLayoutView.findViewById(R.id.card_file_item);
            mTitle = (TextView) itemLayoutView.findViewById(R.id.card_file_title);
            subtitle = (TextView) itemLayoutView.findViewById(R.id.card_file_subtitle);
            icon = (ImageView) itemLayoutView.findViewById(R.id.card_file_icon);
            more = itemLayoutView.findViewById(R.id.card_file_more);
            mOnFileClickListener = onFileClickListener;
            mOnFileLongClickListener = onFileLongClickListener;
            mItem.setOnClickListener(this);
            mItem.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnFileClickListener != null) {
                mOnFileClickListener.onFileCardClick(icon, getAdapterPosition() - (mHasHeader ? 1 : 0));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mOnFileLongClickListener != null && mOnFileLongClickListener.onFileCardLongClick(v,
                    getAdapterPosition() - (mHasHeader ? 1 : 0));
        }
    }
}
