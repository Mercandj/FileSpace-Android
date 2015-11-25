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
package com.mercandalli.android.filespace.main.navdrawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.util.FileUtils;
import com.mercandalli.android.filespace.common.util.FontUtils;
import com.mercandalli.android.filespace.main.ApplicationCallback;
import com.mercandalli.android.filespace.main.Constants;

import java.util.ArrayList;

/**
 * Sliding Menu stuff
 *
 * @author Jonathan
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private final Context mContext;
    private final ApplicationCallback mApplicationCallback;
    private final ArrayList<NavDrawerItem> mNavDrawerItems;

    public NavDrawerListAdapter(Context context, ApplicationCallback applicationCallback, ArrayList<NavDrawerItem> navDrawerItems) {
        this.mContext = context;
        this.mApplicationCallback = applicationCallback;
        this.mNavDrawerItems = new ArrayList<>();
        this.mNavDrawerItems.addAll(navDrawerItems);
    }

    @Override
    public int getCount() {
        return mNavDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        NavDrawerItem item = mNavDrawerItems.get(position);

        switch (mNavDrawerItems.get(position).viewType) {
            case Constants.TAB_VIEW_TYPE_PROFILE:
                convertView = inflater.inflate(R.layout.tab_navdrawer_profil, parent, false);

                ((TextView) convertView.findViewById(R.id.title)).setText(item.title);
                FontUtils.applyFont(mContext, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Medium.ttf");

                ((TextView) convertView.findViewById(R.id.subtitle)).setText(item.subtitle);
                FontUtils.applyFont(mContext, ((TextView) convertView.findViewById(R.id.subtitle)), "fonts/Roboto-Light.ttf");

                Bitmap icon_profile_online = mApplicationCallback.getConfig().getUserProfilePicture();
                if (icon_profile_online != null)
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(icon_profile_online);
                else if (mNavDrawerItems.get(position).containsImage)
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(item.icon);
                else
                    convertView.findViewById(R.id.icon).setVisibility(View.GONE);

                StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
                long blockSize = statFs.getBlockSize();
                long totalSize = statFs.getBlockCount() * blockSize;
                long availableSize = statFs.getAvailableBlocks() * blockSize;
                long freeSize = statFs.getFreeBlocks() * blockSize;

                if (mApplicationCallback.isLogged())
                    convertView.findViewById(R.id.imageStorage).setVisibility(View.GONE);
                else {
                    convertView.findViewById(R.id.icon).setVisibility(View.INVISIBLE);
                    ((TextView) convertView.findViewById(R.id.title)).setText(((int) (((totalSize - availableSize) * 100.0 / totalSize))) + "% Full");
                }

                ((TextView) convertView.findViewById(R.id.subtitle)).setText("Using " + FileUtils.humanReadableByteCount(totalSize - availableSize) + " of " + FileUtils.humanReadableByteCount(totalSize));
                convertView.findViewById(R.id.root).setBackgroundResource(item.idBackgroundColor);

                break;

            case Constants.TAB_VIEW_TYPE_NORMAL:
                convertView = inflater.inflate(R.layout.tab_navdrawer, parent, false);

                ((TextView) convertView.findViewById(R.id.title)).setText(item.title);
                if (item.isSelected)
                    FontUtils.applyFont(mContext, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Medium.ttf");
                else
                    FontUtils.applyFont(mContext, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Light.ttf");

                if (mNavDrawerItems.get(position).containsImage) {
                    if (item.isSelected && item.icon_pressed != -1)
                        ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(item.icon_pressed);
                    else
                        ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(item.icon);
                } else
                    convertView.findViewById(R.id.icon).setVisibility(View.GONE);

                break;

            case Constants.TAB_VIEW_TYPE_SECTION:
                convertView = inflater.inflate(R.layout.tab_navdrawer_section, parent, false);
                break;

            case Constants.TAB_VIEW_TYPE_SECTION_TITLE:
                convertView = inflater.inflate(R.layout.tab_navdrawer_section_title, parent, false);
                ((TextView) convertView.findViewById(R.id.title)).setText(item.title);
                FontUtils.applyFont(mContext, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Regular.ttf");
                break;

            case Constants.TAB_VIEW_TYPE_SETTING:
            case Constants.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE:
                convertView = inflater.inflate(R.layout.tab_navdrawer_setting, parent, false);

                ((TextView) convertView.findViewById(R.id.title)).setText(item.title);
                if (item.isSelected)
                    FontUtils.applyFont(mContext, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAB.TTF");
                else
                    FontUtils.applyFont(mContext, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAM.TTF");

                if (mNavDrawerItems.get(position).containsImage)
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(item.icon);
                else
                    convertView.findViewById(R.id.icon).setVisibility(View.GONE);

                break;
        }

        return convertView;
    }
}
