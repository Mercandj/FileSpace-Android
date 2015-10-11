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
package mercandalli.com.filespace.ia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.ui.activities.Application;
import mercandalli.com.filespace.ui.activities.ApplicationDrawer;
import mercandalli.com.filespace.ui.fragments.HomeFragment;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.utils.StringPair;

import static mercandalli.com.filespace.utils.NetUtils.isInternetConnection;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterRoboticsEquals extends Interpreter {

    public InterpreterRoboticsEquals(Application app, Resource res) {
        super(app, res);
    }

    @Override
    public InterpreterResult interpret(String input) {
        String output = null;

        if(this.res.equalsSentenece("raspberry êtat", input))
            if(isInternetConnection(app)) {
                new TaskGet(
                        this.app,
                        this.app.getConfig().getUser(),
                        this.app.getConfig().getUrlServer() + app.getConfig().routeRobotics + "/18",
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                try {
                                    if (json.has("result")) {
                                        JSONArray result = json.getJSONArray("result");
                                        if (result != null)
                                            if (result.getJSONObject(0).has("value")) {
                                                JSONObject value = new JSONObject(result.getJSONObject(0).getString("value"));
                                                if (value.has("value"))
                                                    speak((value.getInt("value") == 1)?"La Pin 18 du raspberry est activée.":"La Pin 18 du raspberry est éteinte.");
                                            }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        null
                ).execute();
                return new InterpreterResult("");
            }

        if(this.res.equalsSentenece("raspberry led on", input))
            if(isInternetConnection(app)) {
                List<StringPair> parameters = new ArrayList<>();
                parameters.add(new StringPair("value", "1"));
                new TaskPost(
                        this.app,
                        this.app.getConfig().getUrlServer() + this.app.getConfig().routeRobotics + "/18",
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                speak("Je viens d'allumer la LED. Je reste à votre disposition.");
                            }
                        },
                        parameters
                ).execute();
                return new InterpreterResult("");
            }

        if(this.res.equalsSentenece("raspberry led off", input))
            if(isInternetConnection(app)) {
                List<StringPair> parameters = new ArrayList<>();
                parameters.add(new StringPair("value", "0"));
                new TaskPost(
                        this.app,
                        this.app.getConfig().getUrlServer() + this.app.getConfig().routeRobotics + "/18",
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                speak("Je viens d'éteindre la LED. Je reste à votre disposition.");
                            }
                        },
                        parameters
                ).execute();
                return new InterpreterResult("");
            }

        return new InterpreterResult(output);
    }

    /**
     * Use the HomeFragment to speak
     * @param input
     */
    public void speak(String input) {
        if(app instanceof ApplicationDrawer) {
            ApplicationDrawer tmpApp = (ApplicationDrawer) app;
            if(tmpApp.backFragment != null)
                if(tmpApp.backFragment instanceof HomeFragment)
                {
                    ((HomeFragment)tmpApp.backFragment).addItemList("Jarvis", new InterpreterResult(input));
                }
        }
    }
}
