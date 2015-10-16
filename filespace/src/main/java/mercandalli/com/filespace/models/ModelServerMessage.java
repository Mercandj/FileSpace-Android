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
package mercandalli.com.filespace.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.filespace.ui.activities.ApplicationActivity;

public class ModelServerMessage extends Model {

	private String content, id_conversation;

	public ModelServerMessage() {
		super();
	}

    public ModelServerMessage(ApplicationActivity app, JSONObject json) {
        super(app);

        try {
            if(json.has("content"))
                this.content = json.getString("content");
            if(json.has("id_conversation"))
                this.id_conversation = json.getString("id_conversation");
        } catch (JSONException e) {
            Log.e("ModelServerMessage", "JSONException");
            e.printStackTrace();
        }
    }

    public ModelServerMessage(JSONObject json) {
        try {
            if(json.has("content"))
                this.content = json.getString("content");
            if(json.has("id_conversation"))
                this.id_conversation = json.getString("id_conversation");
        } catch (JSONException e) {
            Log.e("ModelServerMessage", "JSONException");
            e.printStackTrace();
        }
    }

    public ModelServerMessage(String content) {
        super();
        this.content = content;
    }

    public ModelServerMessage(String content, String id_conversation) {
        super();
        this.content = content;
        this.id_conversation = id_conversation;
    }

    public boolean isConversationMessage() {
        if(this.id_conversation==null)
            return false;
        return !this.id_conversation.equals("");
    }

    public String getContent() {
        return content;
    }

    public String getId_conversation() {
        return id_conversation;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        try {
            result.put("content", this.content);
            result.put("id_conversation", this.id_conversation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(o==null)
            return false;
        if(!(o instanceof ModelServerMessage))
            return false;
        ModelServerMessage obj = (ModelServerMessage)o;
        if((obj.content == null && this.content!=null) || (obj.content != null && this.content==null))
            return false;
        if((obj.id_conversation == null && this.id_conversation!=null) || (obj.id_conversation != null && this.id_conversation==null))
            return false;
        return (obj.content.equals(this.content)) && (obj.id_conversation.equals(this.id_conversation));
    }
}
