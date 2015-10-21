/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.extras.ia;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.utils.MathUtils;

/**
 * Created by Jonathan on 21/04/2015.
 */
public class QA {

    private JSONObject qa_json;
    private List<String> q, a;

    public QA(JSONObject qa_json) {
        this.qa_json = qa_json;
        this.q = new ArrayList<>();
        this.a = new ArrayList<>();
        try {
            if (this.qa_json.has("q")) {
                JSONArray q_array = this.qa_json.getJSONArray("q");
                for (int j = q_array.length() - 1; j >= 0; j--)
                    q.add(q_array.getString(j));
            }
            if (this.qa_json.has("a")) {
                JSONArray a_array = this.qa_json.getJSONArray("a");
                for (int j = a_array.length() - 1; j >= 0; j--)
                    a.add(a_array.getString(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public QA(JSONObject qa_json, List<String> q, List<String> a) {
        this.qa_json = qa_json;
        this.q = q;
        this.a = a;
    }

    public List<String> getQ() {
        return q;
    }

    public List<String> getA() {
        return a;
    }

    public String getAnswer(String input) {
        for (String q_str : q)
            if (q_str.equals(input))
                return a.get(MathUtils.random(0, a.size() - 1));
        return null;
    }

    public boolean isValid() {
        boolean result = true;
        if (this.q == null || this.a == null)
            result = false;
        if (this.q.isEmpty() || this.a.isEmpty())
            result = false;
        if (!result)
            Log.e("action.QA", "qa object is not valid.");
        return result;
    }
}
