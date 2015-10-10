/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.util.Swappable;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IModelFileListener;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.models.ModelFileTypeENUM;
import mercandalli.com.filespace.ui.activities.Application;

public class AdapterDragModelFile extends BaseAdapter implements Swappable {

    private Application app;
    public List<ModelFile> files;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    private IModelFileListener moreListener;

    public AdapterDragModelFile(Application app, List<ModelFile> files, IModelFileListener moreListener) {
        this.app = app;
        this.files = files;
        this.moreListener = moreListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = app.getLayoutInflater();
        convertView = inflater.inflate(R.layout.tab_file_drag, parent, false);

        if(position<files.size()) {
            final ModelFile file = files.get(position);

            ((TextView) convertView.findViewById(R.id.title)).setText(file.getAdapterTitle());
            ((TextView) convertView.findViewById(R.id.subtitle)).setText(file.getAdapterSubtitle());

            if (file.directory)
                ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.drawable.directory);
            else if(file.type!=null) {
                if (file.type.equals(ModelFileTypeENUM.AUDIO.type))
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.drawable.file_audio);
                else if (file.type.equals(ModelFileTypeENUM.PDF.type))
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.drawable.file_pdf);
                else if (file.type.equals(ModelFileTypeENUM.APK.type))
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.drawable.file_apk);
                else if (file.type.equals(ModelFileTypeENUM.ARCHIVE.type))
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.drawable.file_archive);
                else if (file.type.equals(ModelFileTypeENUM.FILESPACE.type))
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.drawable.file_jarvis);
                else
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.drawable.file_default);
            }
            else
                ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.drawable.file_default);

            if(file.bitmap!=null)
                ((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(file.bitmap);

            if(moreListener == null)
                convertView.findViewById(R.id.more).setVisibility(View.GONE);
            convertView.findViewById(R.id.more).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreListener != null)
                        moreListener.execute(file);
                }
            });

            convertView.findViewById(R.id.item).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener!=null)
                        mItemClickListener.onItemClick(v, position);
                }
            });
        }
        return convertView;
    }

    public void remplaceList(ArrayList<ModelFile> list) {
        files.clear();
        files.addAll(0, list);
        notifyDataSetChanged();
    }




    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return files.get(position).id;
    }

    @Override
    public int getItemViewType(int position) {
        if(position<files.size())
            return files.get(position).viewType;
        return 0;
    }

    @Override
    public void swapItems(int i, int i1) {
        ModelFile tmp = files.get(i);
        files.set(i, files.get(i1));
        files.set(i1, tmp);

    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClick(View view , int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
