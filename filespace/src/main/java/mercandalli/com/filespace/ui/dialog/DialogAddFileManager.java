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
package mercandalli.com.filespace.ui.dialog;

import android.app.Activity;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.listener.IFileModelListener;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.listener.IStringListener;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activitiy.ApplicationActivity;
import mercandalli.com.filespace.ui.activitiy.ApplicationCallback;
import mercandalli.com.filespace.util.DialogUtils;
import mercandalli.com.filespace.util.StringPair;

public class DialogAddFileManager extends Dialog {

    private final Activity mActivity;
    private final ApplicationCallback mApplicationCallback;
    private IListener dismissListener;

    public DialogAddFileManager(final Activity activity, final ApplicationCallback applicationCallback, final int id_file_parent, final IPostExecuteListener listener, final IListener dismissListener) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        this.mActivity = activity;
        this.mApplicationCallback = applicationCallback;
        this.dismissListener = dismissListener;

        this.setContentView(R.layout.dialog_add_file);
        this.setCancelable(true);

        Animation animOpen = AnimationUtils.loadAnimation(mActivity, R.anim.dialog_add_file_open);
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
                new DialogFileChooser(mActivity, new IFileModelListener() {
                    @Override
                    public void executeFileModel(final FileModel fileModel) {
                        new DialogUpload(mActivity, mApplicationCallback, id_file_parent, fileModel, listener);
                    }
                });
                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.addDirectory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.prompt(mActivity, mActivity.getString(R.string.dialog_file_create_folder), mActivity.getString(R.string.dialog_file_name_interrogation), mActivity.getString(R.string.dialog_file_create), new IStringListener() {
                    @Override
                    public void execute(String text) {
                        FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder();
                        fileModelBuilder.name(text);
                        fileModelBuilder.isDirectory(true);
                        fileModelBuilder.idFileParent(id_file_parent);
                        List<StringPair> parameters = FileManager.getForUpload(fileModelBuilder.build());
                        (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeFile, new IPostExecuteListener() {
                            @Override
                            public void onPostExecute(JSONObject json, String body) {
                                if (listener != null)
                                    listener.onPostExecute(json, body);
                            }
                        }, parameters)).execute();
                    }
                }, mActivity.getString(R.string.cancel), null);
                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.txtFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.prompt(mActivity, mActivity.getString(R.string.dialog_file_create_txt), mActivity.getString(R.string.dialog_file_name_interrogation), mActivity.getString(R.string.dialog_file_create), new IStringListener() {
                    @Override
                    public void execute(String text) {
                        //TODO create a online txt with content
                        Toast.makeText(getContext(), getContext().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                    }
                }, mActivity.getString(R.string.cancel), null);
                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.scan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    ApplicationActivity.mPhotoFile = mApplicationCallback.createImageFile();
                    // Continue only if the File was successfully created
                    if (ApplicationActivity.mPhotoFile != null) {
                        if (listener != null)
                            ApplicationActivity.mPhotoFileListener = listener;
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ApplicationActivity.mPhotoFile.getFile()));
                        mActivity.startActivityForResult(takePictureIntent, ApplicationActivity.REQUEST_TAKE_PHOTO);
                    }
                }
                DialogAddFileManager.this.dismiss();
            }
        });

        (this.findViewById(R.id.addTimer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mCurrentTime = Calendar.getInstance();

                DialogDatePicker dialogDate = new DialogDatePicker(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {

                        Calendar currentTime = Calendar.getInstance();

                        DialogTimePicker dialogTime = new DialogTimePicker(mActivity, new TimePickerDialog.OnTimeSetListener() {
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
                                    new TaskPost(mActivity, mApplicationCallback,
                                            mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeFile,
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
                DialogCreateArticle dialogCreateArticle = new DialogCreateArticle(mActivity, mApplicationCallback, listener);
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
