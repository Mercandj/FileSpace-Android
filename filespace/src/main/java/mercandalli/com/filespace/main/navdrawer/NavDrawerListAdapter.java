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
package mercandalli.com.filespace.main.navdrawer;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mercandalli.com.filespace.main.Constants;
import mercandalli.com.filespace.main.ApplicationActivity;
import mercandalli.com.filespace.common.util.FileUtils;
import mercandalli.com.filespace.common.util.FontUtils;

import java.util.ArrayList;

import mercandalli.com.filespace.R;

/**
 * Sliding Menu stuff
 *
 * @author Jonathan
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private ApplicationActivity app;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(ApplicationActivity app, ArrayList<NavDrawerItem> navDrawerItems) {
        this.app = app;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = app.getLayoutInflater();

        NavDrawerItem item = navDrawerItems.get(position);

        switch (navDrawerItems.get(position).viewType) {
            case Constants.TAB_VIEW_TYPE_PROFILE:
                convertView = inflater.inflate(R.layout.tab_navdrawer_profil, parent, false);

                ((TextView) convertView.findViewById(R.id.title)).setText(item.title);
                FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Medium.ttf");

                ((TextView) convertView.findViewById(R.id.subtitle)).setText(item.subtitle);
                FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.subtitle)), "fonts/Roboto-Light.ttf");

                Bitmap icon_profile_online = app.getConfig().getUserProfilePicture();
                if (icon_profile_online != null)
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(icon_profile_online);
                else if (navDrawerItems.get(position).containsImage)
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(item.icon);
                else
                    convertView.findViewById(R.id.icon).setVisibility(View.GONE);

                StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
                long blockSize = statFs.getBlockSize();
                long totalSize = statFs.getBlockCount() * blockSize;
                long availableSize = statFs.getAvailableBlocks() * blockSize;
                long freeSize = statFs.getFreeBlocks() * blockSize;

                if (app.isLogged())
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
                    FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Medium.ttf");
                else
                    FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Light.ttf");

                if (navDrawerItems.get(position).containsImage) {
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
                FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Regular.ttf");
                break;

            case Constants.TAB_VIEW_TYPE_SETTING:
            case Constants.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE:
                convertView = inflater.inflate(R.layout.tab_navdrawer_setting, parent, false);

                ((TextView) convertView.findViewById(R.id.title)).setText(item.title);
                if (item.isSelected)
                    FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAB.TTF");
                else
                    FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAM.TTF");

                if (navDrawerItems.get(position).containsImage)
                    ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(item.icon);
                else
                    convertView.findViewById(R.id.icon).setVisibility(View.GONE);

                break;
        }

        return convertView;
    }
}
