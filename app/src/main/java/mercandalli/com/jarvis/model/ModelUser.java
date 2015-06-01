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
package mercandalli.com.jarvis.model;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.listener.IBitmapListener;
import mercandalli.com.jarvis.net.TaskGetDownloadImage;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.util.FileUtils;
import mercandalli.com.jarvis.util.HashUtils;

public class ModelUser extends Model {

    public int id, id_file_profile_picture = -1;
	public String username;
	public String password;
	public String currentToken;
    public String regId, file_profile_picture_url;
    public Date date_creation, date_last_connection;
    public long size_files, file_profile_picture_size = -1;
    private boolean admin = false;
    public Bitmap bitmap;
	
	public ModelUser() {
		
	}

	public ModelUser(Application app, int id, String username, String password, String currentToken, String regId, boolean admin) {
		super();
        this.id = id;
		this.username = username;
		this.password = password;
		this.currentToken = currentToken;
        this.regId = regId;
        this.admin = admin;
	}

    public ModelUser(Application app, JSONObject json) {
        super();
        this.app = app;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if(json.has("id"))
                this.id = json.getInt("id");
            if(json.has("username"))
                this.username = json.getString("username");
            if(json.has("password"))
                this.password = json.getString("password");
            if(json.has("currentToken"))
                this.currentToken = json.getString("currentToken");
            if(json.has("regId"))
                this.regId = json.getString("regId");
            if(json.has("date_creation") && !json.isNull("date_creation"))
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
            if(json.has("date_last_connection") && !json.isNull("date_last_connection"))
                this.date_last_connection = dateFormat.parse(json.getString("date_last_connection"));
            if(json.has("size_files") && !json.isNull("size_files"))
                this.size_files = json.getLong("size_files");
            if(json.has("admin"))
                this.admin = json.getBoolean("admin");
            if(json.has("id_file_profile_picture"))
                this.id_file_profile_picture = json.getInt("id_file_profile_picture");
            if(json.has("file_profile_picture_size"))
                this.file_profile_picture_size = json.getLong("file_profile_picture_size");
            if(json.has("file_profile_picture_url"))
                this.file_profile_picture_url = json.getString("file_profile_picture_url");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(hasPicture()) {
            ModelFile picture = new ModelFile(app);
            picture.id = this.id_file_profile_picture;
            picture.size = this.file_profile_picture_size;
            picture.onlineUrl = this.file_profile_picture_url;

            new TaskGetDownloadImage(app, this.app.getConfig().getUser(), picture, Const.SIZE_MAX_ONLINE_PICTURE_ICON, new IBitmapListener() {
                @Override
                public void execute(Bitmap bitmap) {
                    if(bitmap != null) {
                        ModelUser.this.bitmap = bitmap;
                        ModelUser.this.app.updateAdapters();
                    }
                }
            }).execute();
        }
    }

    public boolean hasPicture() {
        return id_file_profile_picture != -1 && file_profile_picture_size != -1 && file_profile_picture_url != null;
    }

    public String getAdapterTitle() {
        return this.username;
    }

    public String getAdapterSubtitle() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String date = dateFormat.format(date_last_connection.getTime());
        return "#" + this.id + "   " + date + "   " + FileUtils.humanReadableByteCount(size_files);
    }
	
	public String getAccessLogin() {
		if(this.currentToken==null)
			return this.username;
		return this.currentToken;
	}
	
	public String getAccessPassword() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDate = dateFormatGmt.format(calendar.getTime());
        if(currentToken==null)
            return HashUtils.sha1(HashUtils.sha1(this.password) + currentDate);
		return "empty";
	}

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}
