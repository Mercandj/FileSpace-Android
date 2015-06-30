/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.ui.activity.Application;

/**
 * Created by Jonathan on 30/06/15.
 */
public class EmptyFragment extends Fragment {

    private Application app;
    private View rootView;

    public EmptyFragment() {
        this.app = app;
    }

    public EmptyFragment(Application app) {
        this.app = app;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (Application) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_empty, container, false);

        return this.rootView;
    }

    @Override
    public boolean back() {
        return false;
    }
}
