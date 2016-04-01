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
package com.mercandalli.android.apps.files.extras.robotics;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.view.slider.Slider;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RoboticsFragment extends BackFragment implements SensorEventListener {

    private static final String BUNDLE_ARG_TITLE = "RoboticsFragment.Args.BUNDLE_ARG_TITLE";

    static final int ID_LED_1 = 1;
    static final int ID_DISTANCE_1 = 2;
    static final int ID_DISTANCE_2 = 3;
    static final int ID_SERVO_1 = 4;
    static final int ID_SERVO_2 = 5;

    ModelHardware mLED1;
    ModelHardware mServo1;
    ModelHardware mServo2;

    private View rootView;
    private ToggleButton toggleButton1, toggleButton2, toggleButton3;
    private EditText output, distance_right, distance_left, times;
    private Button button4;
    private TextView tv_seekBar_dir, tv_seekBar_speed;

    private Slider seekBar_dir, seekBar_speed;

    private boolean request_ready = true;
    private double car_direction = 0.5;
    private double car_speed = 0.5;

    private boolean mModeConnection = false;
    private boolean mModeAccelero = false;
    private boolean mModeLED1 = false;

    private DecimalFormat df;

    private String mTitle;
    private SetToolbarCallback mSetToolbarCallback;
    private Toolbar mToolbar;

    public static RoboticsFragment newInstance(String title) {
        final RoboticsFragment fragment = new RoboticsFragment();
        final Bundle args = new Bundle();
        args.putString(BUNDLE_ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SetToolbarCallback) {
            mSetToolbarCallback = (SetToolbarCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a HomeActivity. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSetToolbarCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (!args.containsKey(BUNDLE_ARG_TITLE)) {
            throw new IllegalStateException("Missing args. Please use newInstance()");
        }
        mTitle = args.getString(BUNDLE_ARG_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_robotics, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.fragment_robotics_toolbar);
        mToolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(mToolbar);
        setStatusBarColor(getActivity(), R.color.notifications_bar_robotics);

        // Create hardware
        this.mServo1 = new ModelHardware();
        this.mServo1.id = ID_SERVO_1;
        this.mServo1.value = "" + this.car_direction;

        this.mServo2 = new ModelHardware();
        this.mServo2.id = ID_SERVO_2;
        this.mServo2.value = "0.5";

        this.mLED1 = new ModelHardware();
        this.mLED1.id = ID_LED_1;
        this.mLED1.value = "0";

        this.toggleButton1 = (ToggleButton) this.rootView.findViewById(R.id.toggleButton1);
        this.toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mModeConnection = isChecked;
            }
        });

        this.toggleButton2 = (ToggleButton) this.rootView.findViewById(R.id.toggleButton2);
        this.toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mModeAccelero = isChecked;
            }
        });

        this.toggleButton3 = (ToggleButton) this.rootView.findViewById(R.id.toggleButton3);
        this.toggleButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mModeLED1 = isChecked;
            }
        });

        this.tv_seekBar_dir = (TextView) this.rootView.findViewById(R.id.tv_seekBar_dir);
        this.tv_seekBar_speed = (TextView) this.rootView.findViewById(R.id.tv_seekBar_speed);
        this.seekBar_dir = (Slider) this.rootView.findViewById(R.id.seekBar_dir);
        this.seekBar_speed = (Slider) this.rootView.findViewById(R.id.seekBar_speed);
        this.output = (EditText) this.rootView.findViewById(R.id.output);
        this.distance_left = (EditText) this.rootView.findViewById(R.id.distance_left);
        this.distance_right = (EditText) this.rootView.findViewById(R.id.distance_right);
        this.times = (EditText) this.rootView.findViewById(R.id.times);
        this.button4 = (Button) this.rootView.findViewById(R.id.button4);

        this.output.setMovementMethod(null);

        this.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar_dir.setProgress(50);
                seekBar_speed.setProgress(50);
                tv_seekBar_dir.setText("Direction : " + valueToStr(50));
                tv_seekBar_speed.setText("Speed : " + valueToStr(50));
                RoboticsFragment.this.car_direction = 0.5f;
                RoboticsFragment.this.car_speed = 0.5f;
            }
        });

        this.df = new DecimalFormat("###.##");
        this.seekBar_dir.isNumberIndicator = false;
        this.seekBar_dir.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                RoboticsFragment.this.car_direction = Math.round(value) / 100.0;
                tv_seekBar_dir.setText("Direction : " + valueToStr(value));
            }

            @Override
            public void onValueChangedUp(int value) {
            }
        });

        this.seekBar_speed.isNumberIndicator = false;
        this.seekBar_speed.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                RoboticsFragment.this.car_speed = Math.round(value) / 100.0;
                tv_seekBar_speed.setText("Speed : " + valueToStr(value));
            }

            @Override
            public void onValueChangedUp(int value) {
            }
        });

        senSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        return rootView;
    }

    private String valueToStr(int value) {
        if (df == null || value - 50.0 == 0) {
            return "0.00";
        }
        double res = Math.round(value - 50.0) / 100.0;
        return "" + df.format(res) + (res % 0.1 == 0 || Math.abs(res) == 0.3 || Math.abs(res) == 0.5 ? "0" : "");
    }

    private long id_log = 0;

    private void log(String log) {
        times.setText("#" + id_log);
        output.setText("#" + id_log + " : " + log + "\n" + output.getText().toString());
        id_log++;
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }


    /********
     * SENSOR
     **********/

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate = 0;
    private long lastUpdate2 = 0;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            final Context context = getContext();
            if(context == null) {
                return;
            }

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate2) > 50) {
                lastUpdate2 = curTime;

                if (mModeAccelero) {
                    double tmp_y = y + 5;
                    if (tmp_y < 0) {
                        tmp_y = 0;
                    }
                    if (tmp_y > 10) {
                        tmp_y = 10;
                    }
                    int value = (int) (tmp_y * 10);
                    RoboticsFragment.this.car_direction = value;
                    this.seekBar_dir.setProgress(value);
                    this.tv_seekBar_dir.setText("Direction : " + valueToStr(value));
                }
            }

            if ((curTime - lastUpdate) > 10) {
                lastUpdate = curTime;

                //log("x = " + x + "    y = " + y + "    z = " + z);

                if (NetUtils.isInternetConnection(context) && request_ready && mModeConnection) {
                    List<StringPair> parameters = new ArrayList<>();

                    mServo1.read = false; // write
                    mServo1.value = "" + this.car_direction;

                    mServo2.read = false; // write
                    mServo2.value = "" + this.car_speed;

                    mLED1.read = false; // write
                    mLED1.value = "" + (mModeLED1 ? 1 : 0);

                    parameters.add(new StringPair("json", "" + RoboticsUtils.createProtocolHardware(mServo1, mServo2, mLED1).toString()));

                    request_ready = false;

                    new TaskPost(
                            getActivity(),
                            mApplicationCallback,
                            Constants.URL_DOMAIN + Config.ROUTE_ROBOTICS,
                            new IPostExecuteListener() {
                                @Override
                                public void onPostExecute(JSONObject json, String body) {
                                    log(body);
                                    handleResponse(RoboticsUtils.parseRaspberry(json));
                                    request_ready = true;
                                }
                            },
                            parameters
                    ).execute();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void handleResponse(List<ModelHardware> list) {
        for (ModelHardware hardware : list) {
            switch (hardware.id) {
                case ID_DISTANCE_1:
                    this.distance_left.setText("" + hardware.value);
                    break;
                case ID_DISTANCE_2:
                    this.distance_right.setText("" + hardware.value);
                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        seekBar_dir.updateAfterRotation();
        ViewTreeObserver observer = seekBar_dir.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                seekBar_dir.updateAfterRotation();
                seekBar_dir.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        seekBar_speed.updateAfterRotation();
        observer = seekBar_speed.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                seekBar_speed.updateAfterRotation();
                seekBar_speed.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
