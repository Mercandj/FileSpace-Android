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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.model.ModelHardware;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.view.slider.Slider;
import mercandalli.com.filespace.util.StringPair;

import static mercandalli.com.filespace.util.NetUtils.isInternetConnection;
import static mercandalli.com.filespace.util.RoboticsUtils.createProtocolHardware;
import static mercandalli.com.filespace.util.RoboticsUtils.parseRaspberry;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class RoboticsFragment extends Fragment implements SensorEventListener {

    static final int ID_LED_1		= 1;
    static final int ID_DISTANCE_1	= 2;
    static final int ID_DISTANCE_2	= 3;
    static final int ID_SERVO_1		= 4;
    static final int ID_SERVO_2		= 5;

    ModelHardware LED_1;
    ModelHardware SERVO_1;
    ModelHardware SERVO_2;


    private Application app;
    private View rootView;
    private ToggleButton toggleButton1, toggleButton2, toggleButton3;
    private EditText output, distance_right, distance_left, times;
    private Button button4;

    private Slider seekBar_dir, seekBar_speed;

    private boolean request_ready = true;
    private double car_direction = 0.5;
    private double car_speed = 0.5;

    private boolean MODE_CONNECTION = false;
    private boolean MODE_ACCELERO = false;
    private boolean MODE_LED_1 = false;

    public RoboticsFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_robotics, container, false);

        // Create hardware
        this.SERVO_1 = new ModelHardware();
        this.SERVO_1.id = ID_SERVO_1;
        this.SERVO_1.value = ""+this.car_direction;

        this.SERVO_2 = new ModelHardware();
        this.SERVO_2.id = ID_SERVO_2;
        this.SERVO_2.value = "0.5";

        this.LED_1 = new ModelHardware();
        this.LED_1.id = ID_LED_1;
        this.LED_1.value = "0";


        this.toggleButton1 = (ToggleButton) this.rootView.findViewById(R.id.toggleButton1);
        this.toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MODE_CONNECTION = isChecked;
            }
        });

        this.toggleButton2 = (ToggleButton) this.rootView.findViewById(R.id.toggleButton2);
        this.toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MODE_ACCELERO = isChecked;
            }
        });

        this.toggleButton3 = (ToggleButton) this.rootView.findViewById(R.id.toggleButton3);
        this.toggleButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MODE_LED_1 = isChecked;
            }
        });

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
            }
        });

        this.seekBar_dir.setValueToDisplay(new Slider.ValueToDisplay() {
            @Override
            public String convert(int value) {
                return "" + ((value - 50) * 0.01);
            }
        });
        this.seekBar_dir.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                RoboticsFragment.this.car_direction = value * 0.01;
            }

            @Override
            public void onValueChangedUp(int value) {

            }
        });

        this.seekBar_speed.setValueToDisplay(new Slider.ValueToDisplay() {
            @Override
            public String convert(int value) {
                return "" + ((value - 50) * 0.01);
            }
        });
        this.seekBar_speed.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                RoboticsFragment.this.car_speed = value * 0.01;
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

    private long id_log = 0;
    private void log(String log) {
        times.setText("#"+id_log);
        output.setText( "#" + id_log + " : " + log + "\n" + output.getText().toString() );
        id_log++;
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }


    /******** SENSOR **********/

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate = 0;
    private long lastUpdate2 = 0;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate2) > 50) {
                lastUpdate2 = curTime;

                if(MODE_ACCELERO) {
                    double tmp_y = y + 5;
                    if(tmp_y < 0)
                        tmp_y = 0;
                    if(tmp_y > 10)
                        tmp_y = 10;
                    this.seekBar_dir.setProgress((int)(tmp_y*10));
                }
            }

            if ((curTime - lastUpdate) > 10) {
                lastUpdate = curTime;

                //log("x = " + x + "    y = " + y + "    z = " + z);

                if (isInternetConnection(app) && request_ready && MODE_CONNECTION) {
                    List<StringPair> parameters = new ArrayList<>();

                    SERVO_1.read = false; // write
                    SERVO_1.value = ""+this.car_direction;

                    SERVO_2.read = false; // write
                    SERVO_2.value = ""+this.car_speed;

                    LED_1.read = false; // write
                    LED_1.value = ""+(MODE_LED_1?1:0);

                    parameters.add(new StringPair("json", "" + createProtocolHardware(SERVO_1, SERVO_2, LED_1).toString()));

                    request_ready = false;

                    new TaskPost(
                            app,
                            app.getConfig().getUrlServer() + RoboticsFragment.this.app.getConfig().routeRobotics,
                            new IPostExecuteListener() {
                                @Override
                                public void execute(JSONObject json, String body) {
                                    log(body);
                                    handleResponse(parseRaspberry(json));
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
            switch(hardware.id) {
                case ID_DISTANCE_1:
                    this.distance_left.setText(""+hardware.value);
                    break;
                case ID_DISTANCE_2:
                    this.distance_right.setText(""+hardware.value);
                    break;
            }
        }
    }
}
