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

import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.dialog.DialogAddGenealogyUser;
import mercandalli.com.filespace.util.HtmlUtils;
import mercandalli.com.filespace.util.StringPair;
import mercandalli.com.filespace.util.StringUtils;

public class ModelGenealogyUser extends Model {

	public String first_name_1, first_name_2, first_name_3, last_name, date_birth, date_death, description;
	public int id, id_father, id_mother;
    public Date date_creation;
    public boolean is_man = false;

    public boolean selected = false;

	public ModelGenealogyUser() {
		super();
	}

    public ModelGenealogyUser(Application app, JSONObject json) {
        super(app);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if(json.has("id"))
                this.id = json.getInt("id");
            if(json.has("first_name_1"))
                this.first_name_1 = json.getString("first_name_1");
            if(json.has("first_name_2"))
                this.first_name_2 = json.getString("first_name_2");
            if(json.has("first_name_3"))
                this.first_name_3 = json.getString("first_name_3");
            if(json.has("last_name"))
                this.last_name = json.getString("last_name");
            if(json.has("date_birth"))
                this.date_birth = json.getString("date_birth");
            if(json.has("date_death"))
                this.date_death = json.getString("date_death");
            if(json.has("description"))
                this.description = json.getString("description");
            if(json.has("date_creation") && !json.isNull("date_creation"))
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
            if(json.has("id_father"))
                this.id_father = json.getInt("id_father");
            if(json.has("id_mother"))
                this.id_mother = json.getInt("id_mother");
            if(json.has("is_man"))
                this.is_man = json.getInt("is_man") != 0;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void delete(IPostExecuteListener listener) {
        if(this.app != null) {
            if(this.app.getConfig().isUserAdmin() && this.id != this.app.getConfig().getUserId()) {
                String url = this.app.getConfig().getUrlServer() + this.app.getConfig().routeGenealogyDelete + "/" + this.id;
                new TaskPost(this.app, url, listener).execute();
                return;
            }
        }
        if(listener!=null)
            listener.execute(null, null);
    }

    public void modify(IPostExecuteListener listener) {
        if(this.app != null) {
            if(this.app.getConfig().isUserAdmin() && this.id != this.app.getConfig().getUserId()) {
                app.dialog = new DialogAddGenealogyUser(app, listener, app.getString(R.string.modify), this);
                return;
            }
        }
        if(listener!=null)
            listener.execute(null, null);
    }

    public String getAdapterTitle() {
        if(!StringUtils.isNullOrEmpty(first_name_1) && !StringUtils.isNullOrEmpty(last_name))
            return StringUtils.uppercase(last_name) + " " + StringUtils.capitalize(first_name_1);
        if(!StringUtils.isNullOrEmpty(last_name))
            return StringUtils.uppercase(last_name);
        return StringUtils.capitalize(first_name_1);
    }

    public String getAdapterSubtitle() {
        if(!StringUtils.isNullOrEmpty(this.date_birth) && !StringUtils.isNullOrEmpty(this.date_death))
            return "(" + StringUtils.substring(this.date_birth, 10) + "  -  " + StringUtils.substring(this.date_death, 10) + ")";
        if(!StringUtils.isNullOrEmpty(this.date_birth))
            return "Born: " + StringUtils.substring(this.date_birth, 10);
        if(!StringUtils.isNullOrEmpty(this.date_death))
            return "Death: " + StringUtils.substring(this.date_death, 10);
        return "";
    }

    public Spanned toSpanned() {
        List<StringPair> spl = new ArrayList<>();
        if(!StringUtils.isNullOrEmpty(this.first_name_1))
            spl.add(new StringPair("First name 1", StringUtils.capitalize(this.first_name_1)));
        if(!StringUtils.isNullOrEmpty(this.first_name_2))
            spl.add(new StringPair("First name 2", StringUtils.capitalize(this.first_name_2)));
        if(!StringUtils.isNullOrEmpty(this.first_name_3))
            spl.add(new StringPair("First name 3", StringUtils.capitalize(this.first_name_3)));
        if(!StringUtils.isNullOrEmpty(this.last_name))
            spl.add(new StringPair("Last name", StringUtils.uppercase(this.last_name)));
        if(!StringUtils.isNullOrEmpty(this.date_birth))
            spl.add(new StringPair("Born", StringUtils.substring(this.date_birth, 10)));
        if(!StringUtils.isNullOrEmpty(this.date_death))
            spl.add(new StringPair("Death", StringUtils.substring(this.date_death, 10)));
        spl.add(new StringPair("Sexe", this.is_man ? "Man" : "Woman"));
        if(!StringUtils.isNullOrEmpty(this.description))
            spl.add(new StringPair("Notes",this.description));
        return HtmlUtils.createListItem(spl);
    }

	@Override
	public JSONObject toJSONObject() {
		return null;
	}
}
