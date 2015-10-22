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
package mercandalli.com.filespace.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.listeners.IStringListener;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.utils.StringPair;

public class DialogAddFileManager extends Dialog {

    private ApplicationActivity app;
    private File file;
    private IListener dismissListener;

    public DialogAddFileManager(final ApplicationActivity app, final int id_file_parent, final IPostExecuteListener listener, final IListener dismissListener) {
        super(app, android.R.style.Theme_Translucent_NoTitleBar);
        this.app = app;
        this.dismissListener = dismissListener;

        this.setContentView(R.layout.dialog_add_file);
        this.setCancelable(true);

        Animation animOpen = AnimationUtils.loadAnimation(this.app, R.anim.dialog_add_file_open);
        (this.findViewById(R.id.root)).startAnimation(animOpen);

        (this.findViewById(R.id.root)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.uploadFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mDialog = new DialogUpload(app, id_file_parent, listener);
                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.addDirectory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.prompt(app.getString(R.string.dialog_file_create_folder), app.getString(R.string.dialog_file_name_interrogation), app.getString(R.string.dialog_file_create), new IStringListener() {
                    @Override
                    public void execute(String text) {
                        ModelFile folder = new ModelFile(DialogAddFileManager.this.app, DialogAddFileManager.this.app);
                        folder.name = text;
                        folder.directory = true;
                        folder.id_file_parent = id_file_parent;
                        List<StringPair> parameters = folder.getForUpload();
                        (new TaskPost(app, app, app.getConfig().getUrlServer() + app.getConfig().routeFile, new IPostExecuteListener() {
                            @Override
                            public void onPostExecute(JSONObject json, String body) {
                                if (listener != null)
                                    listener.onPostExecute(json, body);
                            }
                        }, parameters, file)).execute();
                    }
                }, app.getString(R.string.cancel), null);
                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.txtFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.prompt(app.getString(R.string.dialog_file_create_txt), app.getString(R.string.dialog_file_name_interrogation), app.getString(R.string.dialog_file_create), new IStringListener() {
                    @Override
                    public void execute(String text) {
                        //TODO create a online txt with content
                        Toast.makeText(getContext(), getContext().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                    }
                }, app.getString(R.string.cancel), null);
                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.scan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(app.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    app.mPhotoFile = new ModelFile(app, app);
                    try {
                        app.mPhotoFile = app.createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (app.mPhotoFile != null) {
                        if (listener != null)
                            app.mPhotoFileListener = listener;
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(app.mPhotoFile.getFile()));
                        app.startActivityForResult(takePictureIntent, app.REQUEST_TAKE_PHOTO);
                    }
                }
                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.addTimer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mCurrentTime = Calendar.getInstance();

                DialogDatePicker dialogDate = new DialogDatePicker(app, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {

                        Calendar currentTime = Calendar.getInstance();

                        DialogTimePicker dialogTime = new DialogTimePicker(app, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Log.d("TIme Picker", hourOfDay + ":" + minute);

                                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                                SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                String nowAsISO = dateFormatGmt.format(new Date());

                                JSONObject json = new JSONObject();
                                try {
                                    json.put("type", "timer");
                                    json.put("date_creation", nowAsISO);
                                    json.put("timer_date", "" + dateFormatGmt.format(dateFormatLocal.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute + ":00")));

                                    SimpleDateFormat dateFormatGmtTZ = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm'Z'");
                                    dateFormatGmtTZ.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    nowAsISO = dateFormatGmtTZ.format(new Date());

                                    List<StringPair> parameters = new ArrayList<>();
                                    parameters.add(new StringPair("content", json.toString()));
                                    parameters.add(new StringPair("name", "TIMER_" + nowAsISO));
                                    parameters.add(new StringPair("id_file_parent", "" + id_file_parent));
                                    new TaskPost(DialogAddFileManager.this.app, DialogAddFileManager.this.app,
                                            app.getConfig().getUrlServer() + app.getConfig().routeFile,
                                            new IPostExecuteListener() {
                                                @Override
                                                public void onPostExecute(JSONObject json, String body) {
                                                    if (listener != null)
                                                        listener.onPostExecute(json, body);
                                                }
                                            }
                                            , parameters).execute();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
                        dialogTime.show();

                    }
                }, mCurrentTime.get(Calendar.YEAR), mCurrentTime.get(Calendar.MONTH), mCurrentTime.get(Calendar.DAY_OF_MONTH));
                dialogDate.show();

                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.addArticle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCreateArticle dialogCreateArticle = new DialogCreateArticle(app, listener);
                dialogCreateArticle.show();
                DialogAddFileManager.this.dismiss();
            }
        });

        DialogAddFileManager.this.show();
    }

    @Override
    public void dismiss() {
        if (dismissListener != null)
            dismissListener.execute();
        super.dismiss();
    }
}
