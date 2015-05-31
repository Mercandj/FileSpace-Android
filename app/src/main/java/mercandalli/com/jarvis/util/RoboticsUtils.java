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
package mercandalli.com.jarvis.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static mercandalli.com.jarvis.util.TimeUtils.getCurrentDate;

/**
 * Created by Jonathan on 31/05/2015.
 */
public class RoboticsUtils {

    public static JSONObject createUser(int id, String username) {
        JSONObject result = new JSONObject();
        try {
            if(id > -1)
                result.put("id", id);
            if(username != null)
                result.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject createHardware(int id, boolean read, String value, String type, boolean succeed) {
        JSONObject result = new JSONObject();
        try {
            if(id > -1)
                result.put("id", id);
            result.put("read", read);
            if(value != null)
                result.put("value", value);
            if(type != null)
                result.put("type", type);
            result.put("succeed", succeed);
        } catch (JSONException e) {
            e.printStackTrace();
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
            if(user != null)
                result.put("user", user);
            if(date_request != null)
                result.put("date_request", date_request);
            if(hardware != null)
                result.put("hardware", hardware);
            result.put("init_hardware", init_hardware);
            if(state > -1)
                result.put("state", state);
            if(ai_mode > -1)
                result.put("ai_mode", ai_mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject createProtocol(boolean succeed, String toast, String debug, JSONObject content) {
        JSONObject result = new JSONObject();
        try {
            result.put("succeed", succeed);
            if(toast != null)
                result.put("toast", toast);
            if(debug != null)
                result.put("debug", debug);
            if(content != null)
                result.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Control LED, hardware id = 1 ???     *
     * value = "1" || "0"     *
     * @return JSONObject to raspberry
     */
    public static JSONObject createProtocolLed(String value) {
        JSONObject user = createUser(
                1, // id
                "Jonathan" // username
        );

        JSONArray hardware  = new JSONArray();
        hardware.put(
                createHardware(
                        1, // id
                        false, // read
                        value, // value,
                        "led", // type,
                        true // succeed
                )
        );

        JSONObject content = createContent(
                user, // user
                getCurrentDate(), // date_request
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

}
