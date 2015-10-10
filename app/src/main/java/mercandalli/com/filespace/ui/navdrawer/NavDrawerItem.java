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

import mercandalli.com.filespace.listeners.IListener;

/**
 * Sliding Menu stuff
 * @author Jonathan
 *
 */
public class NavDrawerItem {
	
	public String title;
	public String subtitle;
	public int icon, icon_pressed = -1;
	public int viewType;
	public boolean containsImage;
	public boolean isSelected = false;
	public IListener listenerClick = null;

	public int idBackgroundColor;
	
	public boolean initChecked = false;
	//public OnCheckedChangeListener onCheckedChangeListener = null;
	
	public NavDrawerItem(String title, int icon, int viewType) {
		super();
		this.title = title;
		this.icon = icon;
		this.viewType = viewType;
		this.containsImage = true;
	}
	
	public NavDrawerItem(String title, String subtitle, int icon, int viewType) {
		super();
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
		this.viewType = viewType;
		this.containsImage = true;
	}

	public NavDrawerItem(String title, String subtitle, IListener listenerClick, int icon, int viewType) {
		super();
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
		this.viewType = viewType;
		this.containsImage = true;
		this.listenerClick = listenerClick;
	}

    public NavDrawerItem(String title, String subtitle, IListener listenerClick, int icon, int idBackgroundColor, int viewType) {
        super();
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.viewType = viewType;
        this.containsImage = true;
        this.listenerClick = listenerClick;
        this.idBackgroundColor = idBackgroundColor;
    }
	
	public NavDrawerItem(String title, IListener listenerClick, int icon, int viewType) {
		super();
		this.title = title;
		this.icon = icon;
		this.viewType = viewType;
		this.containsImage = true;
		this.listenerClick = listenerClick;
	}

	public NavDrawerItem(String title, IListener listenerClick, int icon, int icon_pressed, int viewType) {
		super();
		this.title = title;
		this.icon = icon;
		this.icon_pressed = icon_pressed;
		this.viewType = viewType;
		this.containsImage = true;
		this.listenerClick = listenerClick;
	}

	public NavDrawerItem(String title, int viewType) {
		super();
		this.title = title;
		this.viewType = viewType;
		this.containsImage = false;
	}
	
	public NavDrawerItem(String title, IListener listenerClick, int SLIDING_MENU_TAB) {
		super();
		this.title = title;
		this.viewType = SLIDING_MENU_TAB;
		this.containsImage = false;
		this.listenerClick = listenerClick;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof NavDrawerItem)) return false;
		return this.title.equals(((NavDrawerItem)o).title) && this.icon==((NavDrawerItem)o).icon;
	}
}
