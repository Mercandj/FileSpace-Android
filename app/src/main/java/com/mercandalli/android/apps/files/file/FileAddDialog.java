/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p>
 * LICENSE:
 * <p>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p>
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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
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
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.FileApp;

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

public class FileAddDialog extends Dialog implements
        View.OnClickListener,
        FileChooserDialog.FileChooserDialogSelection {

    private final Activity mActivity;
    private IListener mDismissListener;
    private final int mFileParentId;
    private IListener mListener;

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public FileAddDialog(
            final Activity activity,
            final int id_file_parent,
            final @Nullable IListener listener,
            final @Nullable IListener dismissListener) {
        super(activity, R.style.DialogFullscreen);
        mActivity = activity;
        mDismissListener = dismissListener;
        mFileParentId = id_file_parent;
        mListener = listener;

        setContentView(R.layout.dialog_add_file);
        setCancelable(true);

        final View rootView = findViewById(R.id.dialog_add_file_root);
        rootView.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.dialog_add_file_open));
        rootView.setOnClickListener(this);

        findViewById(R.id.dialog_add_file_upload_file).setOnClickListener(this);

        findViewById(R.id.dialog_add_file_add_directory).setOnClickListener(this);

        findViewById(R.id.dialog_add_file_text_doc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.prompt(mActivity, mActivity.getString(R.string.dialog_file_create_txt), mActivity.getString(R.string.dialog_file_name_interrogation), mActivity.getString(R.string.dialog_file_create), new IStringListener() {
                    @Override
                    public void execute(String text) {
                        //TODO create a online txt with content
                        Toast.makeText(getContext(), getContext().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                    }
                }, mActivity.getString(android.R.string.cancel), null);
                FileAddDialog.this.dismiss();
            }
        });

        findViewById(R.id.dialog_add_file_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    ApplicationActivity.sPhotoFile = createImageFile();
                    // Continue only if the File was successfully created
                    if (ApplicationActivity.sPhotoFile != null) {
                        if (listener != null) {
                            ApplicationActivity.sPhotoFileListener = listener;
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ApplicationActivity.sPhotoFile.getFile()));
                        mActivity.startActivityForResult(takePictureIntent, ApplicationActivity.REQUEST_TAKE_PHOTO);
                    }
                }
                FileAddDialog.this.dismiss();
            }
        });

        findViewById(R.id.dialog_add_file_add_timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar currentTime = Calendar.getInstance();

                DialogDatePicker dialogDate = new DialogDatePicker(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {

                        Calendar currentTime = Calendar.getInstance();

                        DialogTimePicker dialogTime = new DialogTimePicker(mActivity, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Log.d("TIme Picker", hourOfDay + ":" + minute);

                                final SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                                final SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                String nowAsISO = dateFormatGmt.format(new Date());

                                final JSONObject json = new JSONObject();
                                try {
                                    json.put("type", "timer");
                                    json.put("date_creation", nowAsISO);
                                    json.put("timer_date", "" + dateFormatGmt.format(dateFormatLocal.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute + ":00")));

                                    final SimpleDateFormat dateFormatGmtTZ = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm'Z'");
                                    dateFormatGmtTZ.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    nowAsISO = dateFormatGmtTZ.format(new Date());

                                    final List<StringPair> parameters = new ArrayList<>();
                                    parameters.add(new StringPair("content", json.toString()));
                                    parameters.add(new StringPair("name", "TIMER_" + nowAsISO));
                                    parameters.add(new StringPair("id_file_parent", "" + id_file_parent));
                                    new TaskPost(mActivity, Constants.URL_DOMAIN + Config.ROUTE_FILE,
                                            new IPostExecuteListener() {
                                                @Override
                                                public void onPostExecute(JSONObject json, String body) {
                                                    if (listener != null) {
                                                        listener.execute();
                                                    }
                                                }
                                            }
                                            , parameters).execute();
                                } catch (JSONException | ParseException e) {
                                    Log.e(getClass().getName(), "Failed to convert Json", e);
                                }

                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
                        dialogTime.show();

                    }
                }, currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH));
                dialogDate.show();

                FileAddDialog.this.dismiss();
            }
        });

        findViewById(R.id.dialog_add_file_article).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCreateArticle dialogCreateArticle = new DialogCreateArticle(mActivity, listener);
                dialogCreateArticle.show();
                FileAddDialog.this.dismiss();
            }
        });

        FileAddDialog.this.show();
    }

    public FileModel createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_FileSpace_";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Config.getLocalFolderName());
        FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder();
        fileModelBuilder.name(imageFileName + ".jpg");
        try {
            fileModelBuilder.file(File.createTempFile(imageFileName, ".jpg", storageDir));
        } catch (IOException e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return fileModelBuilder.build();
    }

    @Override
    public void dismiss() {
        if (mDismissListener != null) {
            mDismissListener.execute();
        }
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
                        List<StringPair> parameters = FileApp.get().getFileAppComponent()
                                .provideFileManager().getForUpload(fileModelBuilder.build());
                        (new TaskPost(mActivity, Constants.URL_DOMAIN + Config.ROUTE_FILE, new IPostExecuteListener() {
                            @Override
                            public void onPostExecute(JSONObject json, String body) {
                                if (mListener != null) {
                                    mListener.execute();
                                }
                            }
                        }, parameters)).execute();
                    }
                }, mActivity.getString(android.R.string.cancel), null);
                FileAddDialog.this.dismiss();
                break;
            case R.id.dialog_add_file_upload_file:
                new FileChooserDialog(mActivity, this);
                FileAddDialog.this.dismiss();
                break;
        }
    }

    @Override
    public void onFileChooserDialogSelected(final FileModel fileModel, final View view) {
        new FileUploadDialog(mActivity, mFileParentId, fileModel, mListener);
    }
}
