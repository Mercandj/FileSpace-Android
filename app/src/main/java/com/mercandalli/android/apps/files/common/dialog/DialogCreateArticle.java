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
package com.mercandalli.android.apps.files.common.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DialogCreateArticle extends Dialog {

    private static final String TAG = "DialogCreateArticle";
    private Activity mActivity;

    EditText article_title_1, article_content_1;

    public DialogCreateArticle(
            final Activity activity,
            final IListener listener) {
        super(activity);
        this.mActivity = activity;

        this.setContentView(R.layout.dialog_create_article);
        this.setTitle("Create Article");
        this.setCancelable(true);

        this.article_title_1 = (EditText) this.findViewById(R.id.title);
        this.article_content_1 = (EditText) this.findViewById(R.id.content);

        this.findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));

                String nowAsISO = dateFormatGmt.format(new Date());

                JSONObject json = new JSONObject();
                try {
                    json.put("type", "article");
                    json.put("date_creation", nowAsISO);
                    json.put("article_title_1", article_title_1.getText().toString());
                    json.put("article_content_1", article_content_1.getText().toString());

                    SimpleDateFormat dateFormatGmtTZ = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm'Z'", Locale.US);
                    dateFormatGmtTZ.setTimeZone(TimeZone.getTimeZone("UTC"));
                    nowAsISO = dateFormatGmtTZ.format(new Date());

                    List<StringPair> parameters = new ArrayList<>();
                    parameters.add(new StringPair("content", json.toString()));
                    parameters.add(new StringPair("name", "ARTICLE_" + nowAsISO));
                    new TaskPost(mActivity,
                            Constants.URL_DOMAIN + Config.ROUTE_FILE,
                            new IPostExecuteListener() {
                                @Override
                                public void onPostExecute(JSONObject json, String body) {
                                    if (listener != null) {
                                        listener.execute();
                                    }
                                }
                            }
                            , parameters, "text/html; charset=utf-8").execute();
                } catch (JSONException e) {
                    Log.e(TAG, "DialogCreateArticle: failed to convert Json", e);
                }


                DialogCreateArticle.this.dismiss();
            }
        });

        DialogCreateArticle.this.show();
    }
}
