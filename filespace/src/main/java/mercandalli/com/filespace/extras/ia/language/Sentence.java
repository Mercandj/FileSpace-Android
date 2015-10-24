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
package mercandalli.com.filespace.extras.ia.language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.util.LogUtils;

/**
 * Created by Jonathan on 24/04/2015.
 */
public class Sentence {
    private static final String TAG = LogUtils.makeLogTag(Sentence.class);

    private List<String> sentence;
    private int id;
    private String title;

    public Sentence(JSONObject json) {
        this.sentence = new ArrayList<>();
        this.id = -1;

        try {
            if (json.has("id")) {
                this.id = json.getInt("id");
            }
            if (json.has("title")) {
                this.title = json.getString("title");
            }
            if (json.has("sentence")) {
                JSONArray sentence_array = json.getJSONArray("sentence");
                for (int j = sentence_array.length() - 1; j >= 0; j--)
                    sentence.add(sentence_array.getString(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.LOGE(TAG, "Constructor: Parsing JSON");
        }
    }

    public boolean isValid() {
        return this.id != -1;
    }

    public List<String> getSentence() {
        return sentence;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
