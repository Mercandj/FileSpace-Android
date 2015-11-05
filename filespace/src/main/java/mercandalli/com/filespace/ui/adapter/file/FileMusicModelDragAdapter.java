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

import javax.inject.Inject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.App;
import mercandalli.com.filespace.listener.IFileModelListener;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.model.file.FileMusicModel;
import mercandalli.com.filespace.model.file.FileTypeModel;
import mercandalli.com.filespace.model.file.FileTypeModelENUM;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.StringUtils;

public class FileMusicModelDragAdapter extends RecyclerView.Adapter<FileMusicModelDragAdapter.ViewHolder> {

    private Activity mActivity;
    private List<FileMusicModel> files;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private IFileModelListener moreListener;

    private boolean mShowSize;

    @Inject
    FileManager mFileManager;

    public FileMusicModelDragAdapter(Activity activity, List<FileMusicModel> files, IFileModelListener moreListener) {
        this.mActivity = activity;
        this.files = new ArrayList<>();
        this.files.addAll(files);
        this.moreListener = moreListener;

        App.get(mActivity).getAppComponent().inject(this);
    }

    @Override
    public FileMusicModelDragAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file_drag, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (position < files.size()) {
            final FileMusicModel file = files.get(position);

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
            } else {
                viewHolder.icon.setImageResource(R.drawable.file_default);
            }

            //mFileManager.getCover(mActivity, file, viewHolder.icon);

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


    public void remplaceList(ArrayList<FileMusicModel> list) {
        files.clear();
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addFirst(ArrayList<FileMusicModel> list) {
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(ArrayList<FileMusicModel> list) {
        files.addAll(files.size(), list);
        notifyDataSetChanged();
    }

    public void addItem(FileMusicModel name, int position) {
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

    public void setList(List<FileMusicModel> list) {
        files.clear();
        files.addAll(list);
        notifyDataSetChanged();
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

    public String getAdapterSubtitle(FileMusicModel fileMusicModel) {
        if (!StringUtils.isNullOrEmpty(fileMusicModel.getAlbum()) && !StringUtils.isNullOrEmpty(fileMusicModel.getArtist())) {
            return fileMusicModel.getArtist() + " - " + fileMusicModel.getAlbum();
        }
        if (!StringUtils.isNullOrEmpty(fileMusicModel.getArtist())) {
            return fileMusicModel.getArtist();
        }
        if (!StringUtils.isNullOrEmpty(fileMusicModel.getAlbum())) {
            return fileMusicModel.getAlbum();
        }

        String adapterTitleStart = "";
        if (mShowSize) {
            adapterTitleStart = FileUtils.humanReadableByteCount(fileMusicModel.getSize()) + " - ";
        }

        if (fileMusicModel.getType() == null) {
            if (fileMusicModel.getName() != null)
                return adapterTitleStart + fileMusicModel.getFullName();
            else
                return adapterTitleStart + fileMusicModel.getUrl();
        } else if (fileMusicModel.getType().equals(FileTypeModelENUM.FILESPACE.type) && fileMusicModel.getContent() != null) {
            return adapterTitleStart + fileMusicModel.getContent().getAdapterTitle();
        } else if (fileMusicModel.getName() != null)
            return adapterTitleStart + fileMusicModel.getFullName();
        else
            return adapterTitleStart + fileMusicModel.getUrl();
    }
}
