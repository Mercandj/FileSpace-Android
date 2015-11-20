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
package mercandalli.com.filespace.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.main.Config;
import mercandalli.com.filespace.common.listener.IPostExecuteListener;
import mercandalli.com.filespace.common.net.TaskPost;
import mercandalli.com.filespace.main.MainActivity;
import mercandalli.com.filespace.common.fragment.BackFragment;
import mercandalli.com.filespace.common.util.GpsUtils;
import mercandalli.com.filespace.common.util.HashUtils;
import mercandalli.com.filespace.common.util.NetUtils;
import mercandalli.com.filespace.common.util.StringPair;
import mercandalli.com.filespace.common.util.StringUtils;

public class RegistrationFragment extends BackFragment {

    private boolean mRequestLaunched = false; // Block the second task if one launch

    private EditText mUsername, mPassword;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    public RegistrationFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        this.mUsername = (EditText) rootView.findViewById(R.id.fragment_registration_username);
        this.mPassword = (EditText) rootView.findViewById(R.id.fragment_registration_password);

        ((CheckBox) rootView.findViewById(R.id.autoconnection)).setChecked(mApplicationCallback.getConfig().isAutoConncetion());
        ((CheckBox) rootView.findViewById(R.id.autoconnection)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mApplicationCallback.getConfig().setAutoConnection(isChecked);
            }
        });

        this.mUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    RegistrationFragment.this.mPassword.requestFocus();
                    return true;
                }
                return false;
            }
        });

        this.mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    inscription();
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    public void connectionSucceed() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        this.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
        getActivity().finish();
    }

    public void inscription() {
        ModelUser user = new ModelUser();

        if (!StringUtils.isNullOrEmpty(mUsername.getText().toString()))
            user.username = mUsername.getText().toString();

        if (!StringUtils.isNullOrEmpty(mPassword.getText().toString()))
            user.password = HashUtils.sha1(mPassword.getText().toString());

        inscription(user);
    }

    public void inscription(ModelUser user) {
        if (mRequestLaunched) {
            return;
        }
        mRequestLaunched = true;

        if (!StringUtils.isNullOrEmpty(user.username)) {
            mApplicationCallback.getConfig().setUserUsername(user.username);
        } else {
            user.username = mApplicationCallback.getConfig().getUserUsername();
        }

        if (!StringUtils.isNullOrEmpty(user.password)) {
            mApplicationCallback.getConfig().setUserPassword(user.password);
        } else {
            user.password = mApplicationCallback.getConfig().getUserPassword();
        }

        if (StringUtils.isNullOrEmpty(mApplicationCallback.getConfig().getUrlServer())) {
            mRequestLaunched = false;
            return;
        }

        // Register : POST /user
        List<StringPair> parameters = new ArrayList<>();
        parameters.add(new StringPair("username", "" + user.username));
        parameters.add(new StringPair("password", "" + user.password));
        parameters.add(new StringPair("latitude", "" + GpsUtils.getLatitude(getActivity())));
        parameters.add(new StringPair("longitude", "" + GpsUtils.getLongitude(getActivity())));
        parameters.add(new StringPair("altitude", "" + GpsUtils.getAltitude(getActivity())));

        if (NetUtils.isInternetConnection(mActivity)) {
            (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeUser, new IPostExecuteListener() {
                @Override
                public void onPostExecute(JSONObject json, String body) {
                    try {
                        if (json != null) {
                            if (json.has("succeed") && json.getBoolean("succeed")) {
                                connectionSucceed();
                            }
                            if (json.has("user")) {
                                JSONObject user = json.getJSONObject("user");
                                if (user.has("id")) {
                                    mApplicationCallback.getConfig().setUserId(user.getInt("id"));
                                }
                            }
                        } else {
                            Toast.makeText(mActivity, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mRequestLaunched = false;
                }
            }, parameters)).execute();
        } else {
            mRequestLaunched = false;
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }
}
