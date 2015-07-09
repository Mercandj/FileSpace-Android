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
package mercandalli.com.filespace.model;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.listener.IBitmapListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.net.TaskDelete;
import mercandalli.com.filespace.net.TaskGetDownloadImage;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.HashUtils;

import static mercandalli.com.filespace.util.ImageUtils.is_image;
import static mercandalli.com.filespace.util.ImageUtils.load_image;

public class ModelUser extends Model {

    public int id, id_file_profile_picture = -1;
	public String username;
	public String password;
    public String regId;
    public Date date_creation, date_last_connection;
    public long size_files, file_profile_picture_size = -1, num_files, server_max_size_end_user;
    private boolean admin = false;
    public Bitmap bitmap;
    public ModelUserLocation userLocation;
	
	public ModelUser() {
		
	}

	public ModelUser(Application app, int id, String username, String password, String regId, boolean admin) {
		super(app);
        this.id = id;
		this.username = username;
		this.password = password;
        this.regId = regId;
        this.admin = admin;
	}

    public ModelUser(Application app, JSONObject json) {
        super(app);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if(json.has("id"))
                this.id = json.getInt("id");
            if(json.has("username"))
                this.username = json.getString("username");
            if(json.has("password"))
                this.password = json.getString("password");
            if(json.has("regId"))
                this.regId = json.getString("regId");
            if(json.has("date_creation") && !json.isNull("date_creation"))
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
            if(json.has("date_last_connection") && !json.isNull("date_last_connection"))
                this.date_last_connection = dateFormat.parse(json.getString("date_last_connection"));
            if(json.has("size_files") && !json.isNull("size_files"))
                this.size_files = json.getLong("size_files");
            if(json.has("server_max_size_end_user") && !json.isNull("server_max_size_end_user"))
                this.server_max_size_end_user = json.getLong("server_max_size_end_user");
            if(json.has("admin")) {
                Object admin_obj = json.get("admin");
                if(admin_obj instanceof Integer)
                    this.admin = json.getInt("admin") == 1;
                else if(admin_obj instanceof Boolean)
                    this.admin = json.getBoolean("admin");
            }

            if(json.has("id_file_profile_picture"))
                this.id_file_profile_picture = json.getInt("id_file_profile_picture");
            if(json.has("file_profile_picture_size"))
                this.file_profile_picture_size = json.getLong("file_profile_picture_size");
            if(json.has("num_files") && !json.isNull("num_files"))
                this.num_files = json.getLong("num_files");

            userLocation = new ModelUserLocation(app, json);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(hasPicture()) {
            if(is_image(this.app, this.id_file_profile_picture)) {
                ModelUser.this.bitmap = load_image(this.app, this.id_file_profile_picture);
                ModelUser.this.app.updateAdapters();
            }
            else {
                ModelFile picture = new ModelFile(app);
                picture.id = this.id_file_profile_picture;
                picture.size = this.file_profile_picture_size;
                picture.onlineUrl = this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+id_file_profile_picture;

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
    }

    public boolean hasPicture() {
        return id_file_profile_picture != -1 && file_profile_picture_size != -1;
    }

    public String getAdapterTitle() {
        return this.username;
    }

    public String getAdapterSubtitle() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String date = dateFormat.format(date_last_connection.getTime());
        return date + "   " + FileUtils.humanReadableByteCount(size_files) + "   " + this.num_files + " file" + (this.num_files>1?"s":"");
    }
	
	public String getAccessLogin() {
        return this.username;
	}
	
	public String getAccessPassword() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDate = dateFormatGmt.format(calendar.getTime());
        return HashUtils.sha1(HashUtils.sha1(this.password) + currentDate);
	}

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }

    public void delete(IPostExecuteListener listener) {
        if(this.app != null) {
            if(this.app.getConfig().isUserAdmin() && this.id != this.app.getConfig().getUserId()) {
                String url = this.app.getConfig().getUrlServer() + this.app.getConfig().routeUser + "/" + this.id;
                new TaskDelete(this.app, url, listener).execute();
                return;
            }
        }
        if(listener!=null)
            listener.execute(null, null);
    }
}
