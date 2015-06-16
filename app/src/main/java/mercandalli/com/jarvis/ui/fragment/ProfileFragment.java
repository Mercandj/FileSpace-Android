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
package mercandalli.com.jarvis.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelUser;
import mercandalli.com.jarvis.net.TaskGet;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.util.StringPair;

import static mercandalli.com.jarvis.util.NetUtils.isInternetConnection;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class ProfileFragment extends Fragment {

    private Application app;
    private View rootView;
    private ProgressBar circularProgressBar;
    private ModelUser user;

    public ProfileFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        this.circularProgressBar = (ProgressBar) this.rootView.findViewById(R.id.circularProgressBar);
        this.circularProgressBar.setVisibility(View.VISIBLE);

        Bitmap icon_profile_online = app.getConfig().getUserProfilePicture();
        if(icon_profile_online!=null)
            ((ImageView) rootView.findViewById(R.id.icon)).setImageBitmap(icon_profile_online);

        refreshView();

        return rootView;
    }

    @Override
    public boolean back() {
        return false;
    }

    public void refreshView() {
        if(isInternetConnection(app) && app.isLogged()) {
            List<StringPair> parameters = null;
            new TaskGet(
                    app,
                    this.app.getConfig().getUser(),
                    this.app.getConfig().getUrlServer() + this.app.getConfig().routeUser + "/" + this.app.getConfig().getUserId(),
                    new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            if(!isAdded())
                                return;
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        user = new ModelUser(app, json.getJSONObject("result"));
                                    }
                                }
                                else
                                    Toast.makeText(app, app.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            updateView();
                        }
                    },
                    parameters
            ).execute();
        }
    }

    public void updateView() {
        this.circularProgressBar.setVisibility(View.GONE);
    }
}
