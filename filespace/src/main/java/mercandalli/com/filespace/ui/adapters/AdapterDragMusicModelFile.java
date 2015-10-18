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
package mercandalli.com.filespace.ui.adapters;

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
import mercandalli.com.filespace.listeners.IModelFileListener;
import mercandalli.com.filespace.models.ModelFileType;
import mercandalli.com.filespace.models.ModelFileTypeENUM;
import mercandalli.com.filespace.models.MusicModelFile;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;

public class AdapterDragMusicModelFile extends RecyclerView.Adapter<AdapterDragMusicModelFile.ViewHolder> {

    private ApplicationActivity app;
    public List<MusicModelFile> files;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    private IModelFileListener moreListener;

    public AdapterDragMusicModelFile(ApplicationActivity app, List<MusicModelFile> files, IModelFileListener moreListener) {
        this.app = app;
        this.files = files;
        this.moreListener = moreListener;
    }

    @Override
    public AdapterDragMusicModelFile.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file_drag, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (position < files.size()) {
            final MusicModelFile file = files.get(position);

            viewHolder.title.setText(file.getAdapterTitle());
            viewHolder.subtitle.setText(file.getAdapterSubtitle());

            if (file.directory)
                viewHolder.icon.setImageResource(R.drawable.directory);
            else if (file.type != null) {
                ModelFileType type = file.type;
                if (type.equals(ModelFileTypeENUM.AUDIO.type))
                    viewHolder.icon.setImageResource(R.drawable.file_audio);
                else if (type.equals(ModelFileTypeENUM.PDF.type))
                    viewHolder.icon.setImageResource(R.drawable.file_pdf);
                else if (type.equals(ModelFileTypeENUM.APK.type))
                    viewHolder.icon.setImageResource(R.drawable.file_apk);
                else if (type.equals(ModelFileTypeENUM.ARCHIVE.type))
                    viewHolder.icon.setImageResource(R.drawable.file_archive);
                else if (type.equals(ModelFileTypeENUM.FILESPACE.type))
                    viewHolder.icon.setImageResource(R.drawable.file_space);
                else
                    viewHolder.icon.setImageResource(R.drawable.file_default);
            } else
                viewHolder.icon.setImageResource(R.drawable.file_default);

            if (file.bitmap != null)
                viewHolder.icon.setImageBitmap(file.bitmap);

            if (moreListener == null)
                viewHolder.more.setVisibility(View.GONE);
            viewHolder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreListener != null)
                        moreListener.executeModelFile(file);
                }
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
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


    public void remplaceList(ArrayList<MusicModelFile> list) {
        files.clear();
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addFirst(ArrayList<MusicModelFile> list) {
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(ArrayList<MusicModelFile> list) {
        files.addAll(files.size(), list);
        notifyDataSetChanged();
    }

    public void addItem(MusicModelFile name, int position) {
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
        if (position < files.size())
            return files.get(position).viewType;
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
}
