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
package com.mercandalli.android.apps.files.file;

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

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.dialog.DialogCreateArticle;
import com.mercandalli.android.apps.files.common.dialog.DialogDatePicker;
import com.mercandalli.android.apps.files.common.dialog.DialogTimePicker;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.IStringListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.main.ApplicationActivity;
import com.mercandalli.android.apps.files.main.ApplicationCallback;
import com.mercandalli.android.apps.files.main.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FileAddDialog extends Dialog implements View.OnClickListener {

    private final Activity mActivity;
    private final ApplicationCallback mApplicationCallback;
    private IListener mDismissListener;
    private final int mFileParentId;
    private IListener mListener;

    public FileAddDialog(final Activity activity, final ApplicationCallback applicationCallback, final int id_file_parent, final IListener listener, final IListener dismissListener) {
        super(activity, R.style.DialogFullscreen);
        mActivity = activity;
        mApplicationCallback = applicationCallback;
        mDismissListener = dismissListener;
        mFileParentId = id_file_parent;
        mListener = listener;

        setContentView(R.layout.dialog_add_file);
        setCancelable(true);

        Animation animOpen = AnimationUtils.loadAnimation(mActivity, R.anim.dialog_add_file_open);

        final View rootView = findViewById(R.id.dialog_add_file_root);
        rootView.startAnimation(animOpen);
        rootView.setOnClickListener(this);

        findViewById(R.id.uploadFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FileChooserDialog(mActivity, new FileModelListener() {
                    @Override
                    public void executeFileModel(final FileModel fileModel) {
                        new FileUploadDialog(mActivity, mApplicationCallback, id_file_parent, fileModel, listener);
                    }
                });
                FileAddDialog.this.dismiss();
            }
        });

        findViewById(R.id.dialog_add_file_add_directory).setOnClickListener(this);

        findViewById(R.id.txtFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.prompt(mActivity, mActivity.getString(R.string.dialog_file_create_txt), mActivity.getString(R.string.dialog_file_name_interrogation), mActivity.getString(R.string.dialog_file_create), new IStringListener() {
                    @Override
                    public void execute(String text) {
                        //TODO create a online txt with content
                        Toast.makeText(getContext(), getContext().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                    }
                }, mActivity.getString(R.string.cancel), null);
                FileAddDialog.this.dismiss();
            }
        });

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
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
                FileAddDialog.this.dismiss();
            }
        });

        findViewById(R.id.addTimer).setOnClickListener(new View.OnClickListener() {
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
                                                        listener.execute();
                                                }
                                            }
                                            , parameters).execute();
                                } catch (JSONException | ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
                        dialogTime.show();

                    }
                }, mCurrentTime.get(Calendar.YEAR), mCurrentTime.get(Calendar.MONTH), mCurrentTime.get(Calendar.DAY_OF_MONTH));
                dialogDate.show();

                FileAddDialog.this.dismiss();
            }
        });

        findViewById(R.id.addArticle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCreateArticle dialogCreateArticle = new DialogCreateArticle(mActivity, mApplicationCallback, listener);
                dialogCreateArticle.show();
                FileAddDialog.this.dismiss();
            }
        });

        FileAddDialog.this.show();
    }

    @Override
    public void dismiss() {
        if (mDismissListener != null)
            mDismissListener.execute();
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.dialog_add_file_root:
                FileAddDialog.this.dismiss();
                break;
            case R.id.dialog_add_file_add_directory:
                DialogUtils.prompt(mActivity, mActivity.getString(R.string.dialog_file_create_folder), mActivity.getString(R.string.dialog_file_name_interrogation), mActivity.getString(R.string.dialog_file_create), new IStringListener() {
                    @Override
                    public void execute(String text) {
                        FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder();
                        fileModelBuilder.name(text);
                        fileModelBuilder.isDirectory(true);
                        fileModelBuilder.idFileParent(mFileParentId);
                        List<StringPair> parameters = FileManager.getForUpload(fileModelBuilder.build());
                        (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeFile, new IPostExecuteListener() {
                            @Override
                            public void onPostExecute(JSONObject json, String body) {
                                if (mListener != null) {
                                    mListener.execute();
                                }
                            }
                        }, parameters)).execute();
                    }
                }, mActivity.getString(R.string.cancel), null);
                FileAddDialog.this.dismiss();
                break;
        }
    }
}
