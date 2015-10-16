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
package mercandalli.com.filespace.ui.navdrawer;

import java.util.ArrayList;

/**
 * Sliding Menu stuff
 * @author Jonathan
 *
 */
public class NavDrawerItemList {

    private ArrayList<NavDrawerItem> liste = new ArrayList<NavDrawerItem>();

    public void add(NavDrawerItem o) {
        if (o == null) return;
        liste.add(o);
    }

    public NavDrawerItem get(int o) {
        return liste.get(o);
    }

    public int size() {
        return liste.size();
    }

    public ArrayList<NavDrawerItem> getListe() {
        return liste;
    }

    public int getIndice(NavDrawerItem o) {
        for (int i = 0; i < liste.size(); i++)
            if (get(i).equals(o))
                return i;
        return -1;
    }
}
