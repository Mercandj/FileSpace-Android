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

import android.util.Log;

import com.mercandalli.android.apps.files.common.util.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 31/05/2015.
 */
public class RoboticsUtils {

    private static final String TAG = "RoboticsUtils";
    private static final String SUCCEED = "succeed";
    private static final String FAILED_JSON = "Failed to convert Json";

    public static JSONObject createUser(int id, String username) {
        JSONObject result = new JSONObject();
        try {
            if (id > -1) {
                result.put("id", id);
            }
            if (username != null) {
                result.put("username", username);
            }
        } catch (JSONException e) {
            Log.e(TAG, FAILED_JSON, e);
        }
        return result;
    }

    public static JSONObject createHardware(int id, boolean read, String value, String type, boolean succeed) {
        JSONObject result = new JSONObject();
        try {
            if (id > -1) {
                result.put("id", id);
            }
            result.put("read", read);
            if (value != null) {
                result.put("value", value);
            }
            if (type != null) {
                result.put("type", type);
            }
            result.put(SUCCEED, succeed);
        } catch (JSONException e) {
            Log.e(TAG, FAILED_JSON, e);
        }
        return result;
    }

    public static JSONObject createContent(
            JSONObject user,
            String date_request,
            JSONArray hardware,
            boolean init_hardware,
            int state,
            int ai_mode) {

        JSONObject result = new JSONObject();
        try {
            if (user != null) {
                result.put("user", user);
            }
            if (date_request != null) {
                result.put("date_request", date_request);
            }
            if (hardware != null) {
                result.put("hardware", hardware);
            }
            result.put("init_hardware", init_hardware);
            if (state > -1) {
                result.put("state", state);
            }
            if (ai_mode > -1) {
                result.put("ai_mode", ai_mode);
            }
        } catch (JSONException e) {
            Log.e(TAG, FAILED_JSON, e);
        }
        return result;
    }

    public static JSONObject createProtocol(boolean succeed, String toast, String debug, JSONObject content) {
        JSONObject result = new JSONObject();
        try {
            result.put(SUCCEED, succeed);
            if (toast != null) {
                result.put("toast", toast);
            }
            if (debug != null) {
                result.put("debug", debug);
            }
            if (content != null) {
                result.put("content", content);
            }
        } catch (JSONException e) {
            Log.e(TAG, FAILED_JSON, e);
        }
        return result;
    }

    /**
     * Control LED, hardware id = 1 ???     *
     * value = "1" || "0"     *
     *
     * @return JSONObject to raspberry
     */
    public static JSONObject createProtocolHardware(ModelHardware hard1) {
        JSONObject user = createUser(
                1, // id
                "Jonathan" // username
        );

        JSONArray hardware = new JSONArray();
        hardware.put(
                createHardware(
                        hard1.id, // id
                        hard1.read, // read
                        hard1.value, // value,
                        hard1.type, // type,
                        true // succeed
                )
        );

        JSONObject content = createContent(
                user, // user
                TimeUtils.getCurrentDate(), // date_request
                hardware, // hardware
                false, // init_hardware
                -1, // state
                -1 // ai_mode
        );

        return createProtocol(
                true, // succeed
                "Test toast", // toast
                "on/off led", // debug
                content // content
        );
    }

    /**
     * Control LED, hardware id = 1 ???     *
     * value = "1" || "0"     *
     *
     * @return JSONObject to raspberry
     */
    public static JSONObject createProtocolHardware(ModelHardware hard1, ModelHardware hard2, ModelHardware hard3) {
        JSONObject user = createUser(
                1, // id
                "Jonathan" // username
        );

        JSONArray hardware = new JSONArray();
        if (hard1 != null) {
            hardware.put(
                    createHardware(
                            hard1.id, // id
                            hard1.read, // read
                            hard1.value, // value,
                            hard1.type, // type,
                            true // succeed
                    )
            );
        }
        if (hard2 != null) {
            hardware.put(
                    createHardware(
                            hard2.id, // id
                            hard2.read, // read
                            hard2.value, // value,
                            hard2.type, // type,
                            true // succeed
                    )
            );
        }
        if (hard3 != null) {
            hardware.put(
                    createHardware(
                            hard3.id, // id
                            hard3.read, // read
                            hard3.value, // value,
                            hard3.type, // type,
                            true // succeed
                    )
            );
        }

        JSONObject content = createContent(
                user, // user
                TimeUtils.getCurrentDate(), // date_request
                hardware, // hardware
                false, // init_hardware
                -1, // state
                -1 // ai_mode
        );

        return createProtocol(
                true, // succeed
                "Test toast", // toast
                "on/off led", // debug
                content // content
        );
    }


    public static List<ModelHardware> parseRaspberry(JSONObject json) {
        List<ModelHardware> result = new ArrayList<>();
        if (json == null) {
            return result;
        }
        try {
            if (!json.has("raspberry-content")) {
                return result;
            }

            json = new JSONObject(json.getString("raspberry-content"));

            if (json.has("content")) {
                JSONObject content = json.getJSONObject("content");
                if (content.has("hardware")) {
                    JSONArray hardware = content.getJSONArray("hardware");
                    for (int i = 0; i < hardware.length(); i++) {
                        JSONObject hard_json = hardware.getJSONObject(i);
                        ModelHardware hard = new ModelHardware();
                        if (hard_json.has(SUCCEED)) {
                            hard.succeed = hard_json.getBoolean(SUCCEED);
                        }
                        if (hard_json.has("id")) {
                            hard.id = hard_json.getInt("id");
                        }
                        if (hard_json.has("value")) {
                            hard.value = hard_json.getString("value");
                        }
                        if (hard_json.has("type")) {
                            hard.type = hard_json.getString("type");
                        }
                        result.add(hard);
                    }
                }

            }
        } catch (JSONException e) {
            Log.e(TAG, FAILED_JSON, e);
        }

        return result;
    }
}
