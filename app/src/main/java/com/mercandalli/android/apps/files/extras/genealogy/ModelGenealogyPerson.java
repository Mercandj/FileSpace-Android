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
package com.mercandalli.android.apps.files.extras.genealogy;

import android.app.Activity;
import android.text.Spanned;
import android.util.Log;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.HtmlUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.main.ApplicationCallback;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.library.mainlibrary.java.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModelGenealogyPerson {

    protected Activity mActivity;
    protected ApplicationCallback mApp;

    public String first_name_1, first_name_2, first_name_3, last_name, date_birth, date_death, description;
    public int mId, id_father, id_mother;
    public Date date_creation;
    public boolean is_man = false;

    public boolean selected = false;
    private boolean valid = true;

    public ModelGenealogyPerson mother, father;

    public List<ModelGenealogyPerson> brothers_sisters_from_mother, brothers_sisters_from_father, partners;

    public ModelGenealogyPerson() {
        super();
    }

    public ModelGenealogyPerson(boolean valid) {
        super();
        this.valid = valid;
    }

    public ModelGenealogyPerson(Activity activity, ApplicationCallback app, JSONObject json) {
        mActivity = activity;
        mApp = app;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if (json.has("id")) {
                this.mId = json.getInt("id");
            }
            if (json.has("first_name_1")) {
                this.first_name_1 = json.getString("first_name_1");
            }
            if (json.has("first_name_2")) {
                this.first_name_2 = json.getString("first_name_2");
            }
            if (json.has("first_name_3")) {
                this.first_name_3 = json.getString("first_name_3");
            }
            if (json.has("last_name")) {
                this.last_name = json.getString("last_name");
            }
            if (json.has("date_birth")) {
                this.date_birth = json.getString("date_birth");
            }
            if (json.has("date_death")) {
                this.date_death = json.getString("date_death");
            }
            if (json.has("description")) {
                this.description = json.getString("description");
            }
            if (json.has("date_creation") && !json.isNull("date_creation")) {
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
            }
            if (json.has("id_father")) {
                this.id_father = json.getInt("id_father");
            }
            if (json.has("id_mother")) {
                this.id_mother = json.getInt("id_mother");
            }
            if (json.has("is_man")) {
                this.is_man = json.getInt("is_man") != 0;
            }
            if (json.has("father")) {
                this.father = new ModelGenealogyPerson(activity, app, json.getJSONObject("father"));
            }
            if (json.has("mother")) {
                this.mother = new ModelGenealogyPerson(activity, app, json.getJSONObject("mother"));
            }
            if (json.has("brothers_sisters_from_mother")) {
                this.brothers_sisters_from_mother = new ArrayList<>();
                JSONArray json_b_s = json.getJSONArray("brothers_sisters_from_mother");
                for (int i = 0; i < json_b_s.length(); i++) {
                    this.brothers_sisters_from_mother.add(new ModelGenealogyPerson(activity, app, json_b_s.getJSONObject(i)));
                }
            }
            if (json.has("brothers_sisters_from_father")) {
                this.brothers_sisters_from_father = new ArrayList<>();
                JSONArray json_b_s = json.getJSONArray("brothers_sisters_from_father");
                for (int i = 0; i < json_b_s.length(); i++) {
                    this.brothers_sisters_from_father.add(new ModelGenealogyPerson(activity, app, json_b_s.getJSONObject(i)));
                }
            }
        } catch (JSONException | ParseException e) {
            Log.e(getClass().getName(), "Failed to convert Json or ParseException", e);
        }
    }

    public void delete(IPostExecuteListener listener) {
        if (this.mApp != null && Config.isUserAdmin() && this.mId != Config.getUserId()) {
            String url = Constants.URL_DOMAIN + Config.ROUTE_GENEALOGY_DELETE + "/" + this.mId;
            new TaskPost(mActivity, this.mApp, url, listener).execute();
            return;
        }
        if (listener != null) {
            listener.onPostExecute(null, null);
        }
    }

    public void modify(IPostExecuteListener listener) {
        if (this.mApp != null && Config.isUserAdmin() && this.mId != Config.getUserId()) {
            new DialogAddGenealogyPerson(mActivity, mApp, listener, mActivity.getString(R.string.modify), this);
            return;
        }
        if (listener != null) {
            listener.onPostExecute(null, null);
        }
    }

    public String getAllFirstName() {
        return "" + (first_name_1 != null ? StringUtils.capitalize(first_name_1) : "") + (first_name_2 != null ? ", " + StringUtils.capitalize(first_name_2) : "") + (first_name_3 != null ? ", " + StringUtils.capitalize(first_name_3) : "");
    }

    public String getAdapterTitle() {
        if (!valid) {
            return "xxx xxx";
        }
        if (!StringUtils.isNullOrEmpty(getAllFirstName()) && !StringUtils.isNullOrEmpty(last_name)) {
            return StringUtils.uppercase(last_name) + " " + getAllFirstName();
        }
        if (!StringUtils.isNullOrEmpty(last_name)) {
            return StringUtils.uppercase(last_name);
        }
        return getAllFirstName();
    }

    public String getAdapterSubtitle() {
        if (!StringUtils.isNullOrEmpty(this.date_birth) && !StringUtils.isNullOrEmpty(this.date_death)) {
            return "(" + StringUtils.substring(this.date_birth, 10) + "  -  " + StringUtils.substring(this.date_death, 10) + ")";
        }
        if (!StringUtils.isNullOrEmpty(this.date_birth)) {
            return "Born: " + StringUtils.substring(this.date_birth, 10);
        }
        if (!StringUtils.isNullOrEmpty(this.date_death)) {
            return "Death: " + StringUtils.substring(this.date_death, 10);
        }
        return "";
    }

    public Spanned toSpanned() {
        List<StringPair> spl = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(this.getAllFirstName())) {
            spl.add(new StringPair("First names", this.getAllFirstName()));
        }
        if (!StringUtils.isNullOrEmpty(this.last_name)) {
            spl.add(new StringPair("Last name", StringUtils.uppercase(this.last_name)));
        }
        if (!StringUtils.isNullOrEmpty(this.date_birth)) {
            spl.add(new StringPair("Born", StringUtils.substring(this.date_birth, 10)));
        }
        if (!StringUtils.isNullOrEmpty(this.date_death)) {
            spl.add(new StringPair("Death", StringUtils.substring(this.date_death, 10)));
        }
        spl.add(new StringPair("Sexe", this.is_man ? "Man" : "Woman"));
        if (this.father != null) {
            spl.add(new StringPair("Father", father.getAdapterTitle()));
        }
        if (this.mother != null) {
            spl.add(new StringPair("Mother", mother.getAdapterTitle()));
        }
        if (!StringUtils.isNullOrEmpty(this.description)) {
            spl.add(new StringPair("Notes", this.description));
        }
        return HtmlUtils.createListItem(spl);
    }

    @Override
    public int hashCode() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ModelGenealogyPerson)) {
            return false;
        }
        ModelGenealogyPerson obj = (ModelGenealogyPerson) o;
        return obj.mId == this.mId;
    }

    public List<ModelGenealogyPerson> getBrothersSisters() {
        List<ModelGenealogyPerson> result = new ArrayList<>();
        if (this.brothers_sisters_from_mother != null) {
            result = this.brothers_sisters_from_mother;
        }
        if (this.brothers_sisters_from_father != null) {
            for (ModelGenealogyPerson tmp_father : this.brothers_sisters_from_father) {
                boolean bool = true;
                for (ModelGenealogyPerson tmp_mother : brothers_sisters_from_mother) {
                    if (tmp_mother.mId == tmp_father.mId) {
                        bool = false;
                    }
                }
                if (bool) {
                    result.add(tmp_father);
                }
            }
        }
        return result;
    }

    public List<ModelGenealogyPerson> getPartners() {
        if (this.partners != null) {
            return this.partners;
        }
        return new ArrayList<>();
    }

    public boolean isValid() {
        return valid;
    }
}
