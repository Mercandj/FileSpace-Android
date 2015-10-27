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
package mercandalli.com.filespace.ui.adapter.file;

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

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IFileModelListener;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.model.file.FileTypeModel;
import mercandalli.com.filespace.model.file.FileTypeModelENUM;
import mercandalli.com.filespace.model.file.MusicFileModel;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.StringUtils;

public class AdapterDragMusicFileModel extends RecyclerView.Adapter<AdapterDragMusicFileModel.ViewHolder> {

    private Activity mActivity;
    private List<MusicFileModel> files;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private IFileModelListener moreListener;

    private boolean mShowSize;

    public AdapterDragMusicFileModel(Activity activity, List<MusicFileModel> files, IFileModelListener moreListener) {
        this.mActivity = activity;
        this.files = files;
        this.moreListener = moreListener;
    }

    @Override
    public AdapterDragMusicFileModel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file_drag, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (position < files.size()) {
            final MusicFileModel file = files.get(position);

            viewHolder.title.setText(getAdapterTitle(file));
            viewHolder.subtitle.setText(getAdapterSubtitle(file));

            if (file.isDirectory())
                viewHolder.icon.setImageResource(R.drawable.directory);
            else if (file.getType() != null) {
                FileTypeModel type = file.getType();
                if (type.equals(FileTypeModelENUM.AUDIO.type))
                    viewHolder.icon.setImageResource(R.drawable.file_audio);
                else if (type.equals(FileTypeModelENUM.PDF.type))
                    viewHolder.icon.setImageResource(R.drawable.file_pdf);
                else if (type.equals(FileTypeModelENUM.APK.type))
                    viewHolder.icon.setImageResource(R.drawable.file_apk);
                else if (type.equals(FileTypeModelENUM.ARCHIVE.type))
                    viewHolder.icon.setImageResource(R.drawable.file_archive);
                else if (type.equals(FileTypeModelENUM.FILESPACE.type))
                    viewHolder.icon.setImageResource(R.drawable.file_space);
                else
                    viewHolder.icon.setImageResource(R.drawable.file_default);
            } else
                viewHolder.icon.setImageResource(R.drawable.file_default);


            if (moreListener == null)
                viewHolder.more.setVisibility(View.GONE);
            viewHolder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreListener != null)
                        moreListener.executeFileModel(file);
                }
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        public TextView title, subtitle;
        public ImageView icon;
        public View item;
        public View more;

        public ViewHolder(View itemLayoutView, int viewType) {
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
        return files.size();
    }


    public void remplaceList(ArrayList<MusicFileModel> list) {
        files.clear();
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addFirst(ArrayList<MusicFileModel> list) {
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(ArrayList<MusicFileModel> list) {
        files.addAll(files.size(), list);
        notifyDataSetChanged();
    }

    public void addItem(MusicFileModel name, int position) {
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

    @Override
    public int getItemViewType(int position) {
        return 0;
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

    public String getAdapterSubtitle(MusicFileModel musicFileModel) {
        if (!StringUtils.isNullOrEmpty(musicFileModel.getAlbum()) && !StringUtils.isNullOrEmpty(musicFileModel.getArtist())) {
            return musicFileModel.getArtist() + " - " + musicFileModel.getAlbum();
        }
        if (!StringUtils.isNullOrEmpty(musicFileModel.getArtist())) {
            return musicFileModel.getArtist();
        }
        if (!StringUtils.isNullOrEmpty(musicFileModel.getAlbum())) {
            return musicFileModel.getAlbum();
        }

        String adapterTitleStart = "";
        if (mShowSize) {
            adapterTitleStart = FileUtils.humanReadableByteCount(musicFileModel.getSize()) + " - ";
        }

        if (musicFileModel.getType() == null) {
            if (musicFileModel.getName() != null)
                return adapterTitleStart + musicFileModel.getFullName();
            else
                return adapterTitleStart + musicFileModel.getUrl();
        } else if (musicFileModel.getType().equals(FileTypeModelENUM.FILESPACE.type) && musicFileModel.getContent() != null) {
            return adapterTitleStart + musicFileModel.getContent().getAdapterTitle();
        } else if (musicFileModel.getName() != null)
            return adapterTitleStart + musicFileModel.getFullName();
        else
            return adapterTitleStart + musicFileModel.getUrl();
    }
}
