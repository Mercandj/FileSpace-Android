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
package mercandalli.com.filespace.ui.fragments;

import android.app.Fragment;

import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.ui.activities.ApplicationDrawerActivity;

/**
 * Created by Jonathan on 17/04/2015.
 */
public abstract class BackFragment extends Fragment {

    protected ApplicationDrawerActivity app;

    public void setApp(ApplicationDrawerActivity app) {
        this.app = app;
    }

    /**
     * Activity follow back to fragment.
     * @return true if action
     */
    public abstract boolean back();

    /**
     * Get the focus.
     * For example: on a ViewPager.
     */
    public abstract void onFocus();

    public interface IListViewMode {
        /**
         * Define if the list or grid.
         * @param viewMode {@link Const#MODE_LIST} or {@link Const#MODE_GRID}
         */
        void setViewMode(int viewMode);
    }

    public interface ISortMode {
        /**
         * Define the list sort.
         * @param sortMode {@link Const#SORT_ABC}, {@link Const#SORT_SIZE} or {@link Const#SORT_DATE_MODIFICATION}
         */
        void setSortMode(int sortMode);
    }
}
