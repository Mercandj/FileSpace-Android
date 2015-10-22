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

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IModelFileListener;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.models.ModelFileTypeENUM;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;

public class AdapterGridModelFile extends BaseAdapter {

    private Activity mActivity;
    public List<ModelFile> files;
    private IModelFileListener moreListener;

    public AdapterGridModelFile(Activity activity, List<ModelFile> files) {
        this.mActivity = activity;
        this.files = files;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View itemLayoutView, ViewGroup parent) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        itemLayoutView = inflater.inflate(R.layout.tab_file_images, parent, false);

        RelativeLayout item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
        TextView title = (TextView) itemLayoutView.findViewById(R.id.title);
        TextView subtitle = (TextView) itemLayoutView.findViewById(R.id.subtitle);
        ImageView icon = (ImageView) itemLayoutView.findViewById(R.id.icon);
        ImageView more = (ImageView) itemLayoutView.findViewById(R.id.more);

        if (position < files.size()) {
            final ModelFile file = files.get(position);

            title.setText(file.getAdapterTitle());
            subtitle.setText(file.getAdapterSubtitle());

            if (file.directory)
                icon.setImageResource(R.drawable.directory);
            else if (file.type != null) {
                if (file.type.equals(ModelFileTypeENUM.AUDIO.type))
                    icon.setImageResource(R.drawable.file_audio);
                else if (file.type.equals(ModelFileTypeENUM.PDF.type))
                    icon.setImageResource(R.drawable.file_pdf);
                else if (file.type.equals(ModelFileTypeENUM.APK.type))
                    icon.setImageResource(R.drawable.file_apk);
                else if (file.type.equals(ModelFileTypeENUM.ARCHIVE.type))
                    icon.setImageResource(R.drawable.file_archive);
                else if (file.type.equals(ModelFileTypeENUM.FILESPACE.type))
                    icon.setImageResource(R.drawable.file_space);
                else
                    icon.setImageResource(R.drawable.file_default);
            } else
                icon.setImageResource(R.drawable.file_default);

            if (file.bitmap != null)
                icon.setImageBitmap(file.bitmap);

            more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreListener != null)
                        moreListener.executeModelFile(file);
                }
            });

            if (file.selected) {
                item.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.tab_selected));
            } else {
                item.setBackground(null);
            }

            if (file.type.equals(ModelFileTypeENUM.FILESPACE.type)) {
                /*
                final Handler timerHandler = new Handler();

                Runnable timerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(v)
                        viewHolder.title.notify();
                        timerHandler.postDelayed(this, 1000); // run every s
                    }
                };
                timerRunnable.run();
                */
            }
        }

        return itemLayoutView;
    }
}
