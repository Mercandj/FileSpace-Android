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
package mercandalli.com.filespace.ui.navdrawer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import java.util.ArrayList;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.ui.activity.ActivityRegisterLogin;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.util.FontUtils;

/**
 * Sliding Menu stuff
 * @author Jonathan
 *
 */
public class NavDrawerListAdapter extends BaseAdapter {
	
	private Application app;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	public NavDrawerListAdapter(Application app, ArrayList<NavDrawerItem> navDrawerItems){
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
		
		switch(navDrawerItems.get(position).viewType) {
		case Const.TAB_VIEW_TYPE_PROFIL:
			convertView = inflater.inflate(R.layout.tab_navdrawer_profil, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Medium.ttf");
			
			((TextView) convertView.findViewById(R.id.subtitle)).setText(item.subtitle);
			FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.subtitle)), "fonts/Roboto-Light.ttf");

			Bitmap icon_profile_online = app.getConfig().getUserProfilePicture();
			if(icon_profile_online!=null)
				((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(icon_profile_online);
			else if(navDrawerItems.get(position).containsImage)
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getResources().getDrawable(item.icon));
			else
				((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);

			SignInButton signInButton = ((SignInButton) convertView.findViewById(R.id.signInButton));
            Button signIn = ((Button) convertView.findViewById(R.id.signIn));
            if(app.isLogged()) {
                signInButton.setVisibility(View.GONE);
                signIn.setVisibility(View.GONE);
            }
            else {
                ((TextView) convertView.findViewById(R.id.title)).setVisibility(View.GONE);
                ((TextView) convertView.findViewById(R.id.subtitle)).setVisibility(View.GONE);
                ((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
                signIn.setVisibility(View.VISIBLE);
                signIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(app, ActivityRegisterLogin.class);
                        app.startActivity(intent);
                        app.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        app.finish();
                    }
                });
                FontUtils.applyFont(app, signIn, "fonts/Roboto-Medium.ttf");

            }
			
			break;		
		
		case Const.TAB_VIEW_TYPE_NORMAL:
			convertView = inflater.inflate(R.layout.tab_navdrawer, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			if(item.isSelected)
				FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Medium.ttf");
			else
				FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Light.ttf");

			if(navDrawerItems.get(position).containsImage) {
				if(item.isSelected && item.icon_pressed != -1)
					((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getResources().getDrawable(item.icon_pressed));
				else
					((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getResources().getDrawable(item.icon));
			}
			else
				((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);
			
			break;
			
		case Const.TAB_VIEW_TYPE_SECTION:
			convertView = inflater.inflate(R.layout.tab_navdrawer_section, parent, false);
			break;

        case Const.TAB_VIEW_TYPE_SECTION_TITLE:
            convertView = inflater.inflate(R.layout.tab_navdrawer_section_title, parent, false);
            ((TextView) convertView.findViewById(R.id.title)).setText(item.title);
            FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/Roboto-Regular.ttf");
            break;
			
		case Const.TAB_VIEW_TYPE_SETTING:
        case Const.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE:
			convertView = inflater.inflate(R.layout.tab_navdrawer_setting, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			if(item.isSelected)
				FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAB.TTF");
			else
				FontUtils.applyFont(app, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAM.TTF");
			
			if(navDrawerItems.get(position).containsImage)
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getResources().getDrawable(item.icon));
			else
				((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);
			
			break;
		}
        
        return convertView;
	}
}
