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
package mercandalli.com.filespace.ui.dialog;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.util.StringPair;

public class DialogCreateArticle extends Dialog {

	Application app;

    EditText article_title_1, article_content_1;

	public DialogCreateArticle(final Application app, final IPostExecuteListener listener) {
		super(app);
		this.app = app;
		
		this.setContentView(R.layout.dialog_create_article);
		this.setTitle("Create Article");
		this.setCancelable(true);

        this.article_title_1 = (EditText) this.findViewById(R.id.title);
        this.article_content_1 = (EditText) this.findViewById(R.id.content);

        ((Button) this.findViewById(R.id.request)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));

				String nowAsISO = dateFormatGmt.format(new Date());

				JSONObject json = new JSONObject();
				try {
					json.put("type", "article");
					json.put("date_creation", nowAsISO);
                    json.put("article_title_1", article_title_1.getText().toString());
                    json.put("article_content_1", article_content_1.getText().toString());

                    SimpleDateFormat dateFormatGmtTZ = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm'Z'");
                    dateFormatGmtTZ.setTimeZone(TimeZone.getTimeZone("UTC"));
                    nowAsISO = dateFormatGmtTZ.format(new Date());

					List<StringPair> parameters = new ArrayList<>();
					parameters.add(new StringPair("content",json.toString()));
					parameters.add(new StringPair("name","ARTICLE_"+nowAsISO));
					new TaskPost(DialogCreateArticle.this.app,
							app.getConfig().getUrlServer()+app.getConfig().routeFile,
							new IPostExecuteListener() {
								@Override
								public void execute(JSONObject json, String body) {
									if(listener!=null)
										listener.execute(json, body);
								}
							}
                            , parameters, "text/html; charset=utf-8").execute();
				} catch (JSONException e) {
					e.printStackTrace();
				}

				
				DialogCreateArticle.this.dismiss();
			}        	
        });
        
        DialogCreateArticle.this.show();
	}
}
