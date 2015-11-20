/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.file.audio;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.main.App;
import mercandalli.com.filespace.file.FileModelListener;
import mercandalli.com.filespace.file.FileManager;
import mercandalli.com.filespace.file.FileModel;
import mercandalli.com.filespace.file.FileTypeModel;
import mercandalli.com.filespace.file.FileTypeModelENUM;
import mercandalli.com.filespace.common.util.FileUtils;
import mercandalli.com.filespace.common.util.StringUtils;

public class FileAudioDragAdapter extends RecyclerView.Adapter<FileAudioDragAdapter.ViewHolder> {

    private Activity mActivity;
    private List<FileAudioModel> files;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private FileModelListener moreListener;

    private boolean mShowSize;
    private boolean mHasHeader;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    @Inject
    FileManager mFileManager;

    public FileAudioDragAdapter(Activity activity, List<FileAudioModel> files, boolean hasHeader, FileModelListener moreListener) {
        this.mActivity = activity;
        this.files = new ArrayList<>();
        this.files.addAll(files);
        this.moreListener = moreListener;
        this.mHasHeader = hasHeader;

        App.get(mActivity).getAppComponent().inject(this);
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
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file_header, parent, false), viewType);
        }
        return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file_drag, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (mHasHeader && position == 0) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;

        } else if (position < files.size() + (mHasHeader ? 1 : 0)) {
            final FileViewHolder fileViewHolder = (FileViewHolder) viewHolder;
            final FileAudioModel file = files.get(position - (mHasHeader ? 1 : 0));

            fileViewHolder.title.setText(getAdapterTitle(file));
            fileViewHolder.subtitle.setText(getAdapterSubtitle(file));

            if (file.isDirectory())
                fileViewHolder.icon.setImageResource(R.drawable.directory);
            else if (file.getType() != null) {
                FileTypeModel type = file.getType();
                if (type.equals(FileTypeModelENUM.AUDIO.type))
                    fileViewHolder.icon.setImageResource(R.drawable.file_audio);
                else if (type.equals(FileTypeModelENUM.PDF.type))
                    fileViewHolder.icon.setImageResource(R.drawable.file_pdf);
                else if (type.equals(FileTypeModelENUM.APK.type))
                    fileViewHolder.icon.setImageResource(R.drawable.file_apk);
                else if (type.equals(FileTypeModelENUM.ARCHIVE.type))
                    fileViewHolder.icon.setImageResource(R.drawable.file_archive);
                else if (type.equals(FileTypeModelENUM.FILESPACE.type))
                    fileViewHolder.icon.setImageResource(R.drawable.file_space);
                else
                    fileViewHolder.icon.setImageResource(R.drawable.file_default);
            } else {
                fileViewHolder.icon.setImageResource(R.drawable.file_default);
            }

            //mFileManager.getCover(mActivity, file, fileViewHolder.icon);

            if (moreListener == null)
                fileViewHolder.more.setVisibility(View.GONE);
            fileViewHolder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreListener != null)
                        moreListener.executeFileModel(file);
                }
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class HeaderViewHolder extends ViewHolder {
        public HeaderViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);
        }
    }

    public class FileViewHolder extends ViewHolder implements OnClickListener, View.OnLongClickListener {
        public TextView title, subtitle;
        public ImageView icon;
        public View item;
        public View more;

        public FileViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);
            item = itemLayoutView.findViewById(R.id.item);
            title = (TextView) itemLayoutView.findViewById(R.id.title);
            subtitle = (TextView) itemLayoutView.findViewById(R.id.subtitle);
            icon = (ImageView) itemLayoutView.findViewById(R.id.icon);
            more = itemLayoutView.findViewById(R.id.more);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition());
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


    public void remplaceList(ArrayList<FileAudioModel> list) {
        files.clear();
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addFirst(ArrayList<FileAudioModel> list) {
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(ArrayList<FileAudioModel> list) {
        files.addAll(files.size(), list);
        notifyDataSetChanged();
    }

    public void addItem(FileAudioModel name, int position) {
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

    public void setList(List<FileAudioModel> list) {
        files.clear();
        files.addAll(list);
        notifyDataSetChanged();
    }

    public boolean isHeader(int position) {
        return position == 0;
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

    public String getAdapterSubtitle(FileAudioModel fileAudioModel) {
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
            if (fileAudioModel.getName() != null)
                return adapterTitleStart + fileAudioModel.getFullName();
            else
                return adapterTitleStart + fileAudioModel.getUrl();
        } else if (fileAudioModel.getType().equals(FileTypeModelENUM.FILESPACE.type) && fileAudioModel.getContent() != null) {
            return adapterTitleStart + fileAudioModel.getContent().getAdapterTitle();
        } else if (fileAudioModel.getName() != null)
            return adapterTitleStart + fileAudioModel.getFullName();
        else
            return adapterTitleStart + fileAudioModel.getUrl();
    }
}
