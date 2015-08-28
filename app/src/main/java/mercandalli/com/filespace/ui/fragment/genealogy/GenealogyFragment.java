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
package mercandalli.com.filespace.ui.fragment.genealogy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.json.JSONObject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.dialog.DialogAddGenealogyUser;
import mercandalli.com.filespace.ui.fragment.Fragment;

/**
 * Created by Jonathan on 28/08/2015.
 */
public class GenealogyFragment extends Fragment {


    private Application app;
    private View rootView;

    public GenealogyFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_genealogy, container, false);

        ((ImageButton) this.rootView.findViewById(R.id.circle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });

        return rootView;
    }

    @Override
    public boolean back() {
        return false;
    }

    public void add() {
        app.dialog = new DialogAddGenealogyUser(app,new IPostExecuteListener() {
            @Override
            public void execute(JSONObject json, String body) {
                // TODO
            }
        });
    }
}
