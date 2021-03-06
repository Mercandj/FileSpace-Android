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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.dialog.DialogRequest;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;

import org.json.JSONObject;

public class RequestFragment extends BackFragment {

    private View rootView;

    public static RequestFragment newInstance() {
        return new RequestFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_request, container, false);

        Animation animOpen = AnimationUtils.loadAnimation(getContext(), R.anim.circle_button_bottom_open);
        rootView.findViewById(R.id.circle).startAnimation(animOpen);

        rootView.findViewById(R.id.circle).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogRequest(getActivity(), new IPostExecuteListener() {
                    @Override
                    public void onPostExecute(JSONObject json, String body) {
                        if (json != null) {
                            ((EditText) rootView.findViewById(R.id.console)).setText(((EditText) rootView.findViewById(R.id.console)).getText().toString() + "JSON : " + json + "\n\n");
                        } else {
                            ((EditText) rootView.findViewById(R.id.console)).setText(((EditText) rootView.findViewById(R.id.console)).getText().toString() + "BODY : " + body + "\n\n");
                        }
                    }
                });
            }
        });

        return rootView;
    }

    public void delete() {
        ((EditText) rootView.findViewById(R.id.console)).setText("");
    }

    @Override
    public boolean back() {
        return false;
    }
}
