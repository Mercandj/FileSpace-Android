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
package mercandalli.com.filespace.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.util.StringPair;

import static mercandalli.com.filespace.util.NetUtils.isInternetConnection;
import static mercandalli.com.filespace.util.RoboticsUtils.createProtocolLed;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class RoboticsFragment extends Fragment {

    private Application app;
    private View rootView;
    private ToggleButton toggleButton1;
    private EditText output, id, value;
    Switch order;

    public RoboticsFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_robotics, container, false);

        this.toggleButton1 = (ToggleButton) this.rootView.findViewById(R.id.toggleButton1);
        this.toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /*
                List<StringPair> parameters = new ArrayList<>();
                parameters.add(new StringPair("value", (isChecked) ? "1" : "0"));
                new TaskPost(
                        RoboticsFragment.this.app,
                        RoboticsFragment.this.app.getConfig().getUrlServer() + RoboticsFragment.this.app.getConfig().routeRobotics + "/18",
                        null,
                        parameters
                ).execute();
                */
            }
        });

        this.output = (EditText) this.rootView.findViewById(R.id.output);
        this.id = (EditText) this.rootView.findViewById(R.id.id);
        this.value = (EditText) this.rootView.findViewById(R.id.value);
        this.order = (Switch) this.rootView.findViewById(R.id.order);

        this.value.setVisibility(View.INVISIBLE);
        this.order.setText("Measure");
        this.order.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                value.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                order.setText(isChecked ? "Write" : "Read");
            }
        });

        ((Button) this.rootView.findViewById(R.id.launch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInternetConnection(app)) {
                    List<StringPair> parameters = new ArrayList<>();

                    JSONObject json = createProtocolLed(
                            Integer.parseInt(id.getText().toString()),
                            !order.isChecked(),
                            value.getText().toString());

                    parameters.add(new StringPair("json", ""+json.toString()));

                    new TaskPost(
                            app,
                            app.getConfig().getUrlServer() + RoboticsFragment.this.app.getConfig().routeRobotics,
                            new IPostExecuteListener() {
                                @Override
                                public void execute(JSONObject json, String body) {
                                    addLog(body);
                                }
                            },
                            parameters
                    ).execute();
                }
            }
        });

        return rootView;
    }

    private void addLog(String log) {
        output.setText("\n\n"+log);
    }

    @Override
    public boolean back() {
        return false;
    }
}
