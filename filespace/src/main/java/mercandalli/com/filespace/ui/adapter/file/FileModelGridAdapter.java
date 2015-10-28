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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.model.file.FileTypeModelENUM;
import mercandalli.com.filespace.util.StringUtils;

public class FileModelGridAdapter extends BaseAdapter {

    private Activity mActivity;
    public List<FileModel> files;

    public FileModelGridAdapter(Activity activity, List<FileModel> files) {
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
        if (itemLayoutView == null) {
            itemLayoutView = inflater.inflate(R.layout.tab_file_images, parent, false);
        }

        RelativeLayout item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
        TextView title = (TextView) itemLayoutView.findViewById(R.id.title);
        TextView subtitle = (TextView) itemLayoutView.findViewById(R.id.subtitle);
        ImageView icon = (ImageView) itemLayoutView.findViewById(R.id.icon);
        ImageView more = (ImageView) itemLayoutView.findViewById(R.id.more);

        if (position < files.size()) {
            final FileModel fileModel = files.get(position);

            title.setText(getAdapterTitle(fileModel));
            subtitle.setText(getAdapterSubtitle(fileModel));

            if (fileModel.isDirectory())
                icon.setImageResource(R.drawable.directory);
            else if (fileModel.getType() != null) {
                if (fileModel.getType().equals(FileTypeModelENUM.AUDIO.type))
                    icon.setImageResource(R.drawable.file_audio);
                else if (fileModel.getType().equals(FileTypeModelENUM.PDF.type))
                    icon.setImageResource(R.drawable.file_pdf);
                else if (fileModel.getType().equals(FileTypeModelENUM.APK.type))
                    icon.setImageResource(R.drawable.file_apk);
                else if (fileModel.getType().equals(FileTypeModelENUM.ARCHIVE.type))
                    icon.setImageResource(R.drawable.file_archive);
                else if (fileModel.getType().equals(FileTypeModelENUM.FILESPACE.type))
                    icon.setImageResource(R.drawable.file_space);
                else
                    icon.setImageResource(R.drawable.file_default);
            } else
                icon.setImageResource(R.drawable.file_default);

            /*
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
            */
        }

        return itemLayoutView;
    }

    private String getAdapterTitle(FileModel fileModel) {
        if (fileModel.getType() == null) {
            if (fileModel.getName() != null)
                return fileModel.getFullName();
            else
                return fileModel.getUrl();
        }
        /*
        else if (fileModel.getType().equals(ModelFileTypeENUM.FILESPACE.type) && fileModel.content != null) {
            return adapterTitleStart + fileModel.content.getAdapterTitle();
        }
        */
        else if (fileModel.getName() != null)
            return fileModel.getFullName();
        else
            return fileModel.getUrl();
    }

    public String getAdapterSubtitle(FileModel fileModel) {
        if (fileModel.isDirectory() && fileModel.getCount() != 0)
            return "Directory: " + StringUtils.longToShortString(fileModel.getCount()) + " file" + (fileModel.getCount() > 1 ? "s" : "");
        if (fileModel.isDirectory())
            return "Directory";
        /*
        if (ModelFileTypeENUM.FILESPACE.type.equals(fileModel.getType()) && fileModel.content != null)
            return fileModel.getType().getTitle() + " " + StringUtils.capitalize(fileModel.content.type.type.toString());
        */
        if (fileModel.getType() != null)
            return fileModel.getType().getTitle();
        return "";
    }
}
