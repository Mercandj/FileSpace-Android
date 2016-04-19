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
package com.mercandalli.android.apps.files.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.user.UserModel;
import com.mercandalli.android.library.baselibrary.java.HashUtils;
import com.mercandalli.android.library.baselibrary.java.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserAddFragment extends BackFragment {

    private View rootView;
    private TextView username, password;
    private ImageButton circle;

    private UserModel newUser;
    private boolean requestLaunched = false;

    public static UserAddFragment newInstance() {
        return new UserAddFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_admin_add_user, container, false);

        this.circle = (ImageButton) this.rootView.findViewById(R.id.circle);
        this.circle.setVisibility(View.GONE);

        this.username = (TextView) this.rootView.findViewById(R.id.username);
        this.password = (TextView) this.rootView.findViewById(R.id.password);

        this.newUser = new UserModel();

        this.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtils.isNullOrEmpty(s.toString()) && !StringUtils.isNullOrEmpty(password.getText().toString())) {
                    circle.setVisibility(View.VISIBLE);
                } else {
                    circle.setVisibility(View.GONE);
                }
                newUser.username = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        this.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtils.isNullOrEmpty(s.toString()) && !StringUtils.isNullOrEmpty(username.getText().toString())) {
                    circle.setVisibility(View.VISIBLE);
                } else {
                    circle.setVisibility(View.GONE);
                }
                newUser.password = HashUtils.sha1(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        this.circle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Register : POST /user
                List<StringPair> parameters = new ArrayList<>();
                parameters.add(new StringPair("username", "" + newUser.username));
                parameters.add(new StringPair("password", "" + newUser.password));

                if (NetUtils.isInternetConnection(getContext()) && !StringUtils.isNullOrEmpty(newUser.username) && !StringUtils.isNullOrEmpty(newUser.password)) {
                    requestLaunched = true;
                    (new TaskPost(getActivity(),  Constants.URL_DOMAIN + Config.ROUTE_USER, new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            try {
                                if (json != null) {
                                    if (json.has("succeed") && json.getBoolean("succeed")) {
                                        Toast.makeText(getActivity(), "User added", Toast.LENGTH_SHORT).show();
                                    }
                                    if (json.has("user")) {
                                        JSONObject user = json.getJSONObject("user");
                                        if (user.has("id")) {
                                            Config.setUserId(getActivity(), user.getInt("id"));
                                        }
                                    }
                                } else {
                                    Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e(getClass().getName(), "Failed to convert Json", e);
                            }
                            requestLaunched = false;
                        }
                    }, parameters)).execute();
                } else {
                    requestLaunched = false;
                    Toast.makeText(getActivity(), "Request not sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return this.rootView;
    }

    @Override
    public boolean back() {
        return false;
    }
}
