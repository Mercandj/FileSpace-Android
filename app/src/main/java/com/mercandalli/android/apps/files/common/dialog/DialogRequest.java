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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskGet;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.file.FileChooserDialog;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.main.Constants;

import org.json.JSONObject;

import java.io.File;

public class DialogRequest extends Dialog {

    FileChooserDialog mFileChooserDialog;
    private final Activity mActivity;
    private File file;
    FileModel mFileModel;

    private final int GET = 0;
    private final int POST = 1;
    private final int PUT = 2;
    private final int DELETE = 3;
    private final int nbMethod = 4;
    private int currentMethod = GET;

    public DialogRequest(final Activity activity, final IPostExecuteListener listener) {
        super(activity);
        this.mActivity = activity;

        this.setContentView(R.layout.dialog_request);
        this.setTitle(R.string.app_name);
        this.setCancelable(true);

        this.findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (currentMethod) {

                    case POST:
                        if (!((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString().equals("")) {
                            (new TaskPost(mActivity, Constants.URL_DOMAIN + "FileSpace-API/" + ((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString(), new IPostExecuteListener() {
                                @Override
                                public void onPostExecute(JSONObject json, String body) {
                                    if (listener != null) {
                                        listener.onPostExecute(json, body);
                                    }
                                }
                            })).execute();
                        }
                        break;

                    case PUT:
                        //TODO Dev: request PUT
                        Toast.makeText(getContext(), getContext().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                        break;

                    case DELETE:
                        //TODO Dev: request DELETE
                        Toast.makeText(getContext(), getContext().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                        break;

                    default: //GET
                        if (!((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString().equals("")) {
                            (new TaskGet(mActivity,
                                    Constants.URL_DOMAIN + "FileSpace-API/" + ((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString(),
                                    new IPostExecuteListener() {
                                        @Override
                                        public void onPostExecute(JSONObject json, String body) {
                                            if (listener != null) {
                                                listener.onPostExecute(json, body);
                                            }
                                        }
                                    }, null)).execute();
                        }
                }
                DialogRequest.this.dismiss();
            }
        });

        findViewById(R.id.fileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileChooserDialog = new FileChooserDialog(mActivity, new FileChooserDialog.FileChooserDialogSelection() {
                    @Override
                    public void onFileChooserDialogSelected(FileModel fileModel, View view) {
                        ((TextView) DialogRequest.this.findViewById(R.id.label)).setText(fileModel.getUrl());
                        DialogRequest.this.file = new File(fileModel.getUrl());
                        DialogRequest.this.mFileModel = fileModel;
                    }
                });
            }
        });

        findViewById(R.id.method).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMethod++;
                if (currentMethod >= nbMethod) {
                    currentMethod = 0;
                }
                refreshButtonMethod();
            }
        });
        refreshButtonMethod();

        show();
    }

    public void refreshButtonMethod() {
        switch (currentMethod) {
            case 1:
                ((TextView) this.findViewById(R.id.method)).setText("POST");
                break;
            case 2:
                ((TextView) this.findViewById(R.id.method)).setText("PUT");
                break;
            case 3:
                ((TextView) this.findViewById(R.id.method)).setText("DELETE");
                break;
            default:
                ((TextView) this.findViewById(R.id.method)).setText("GET");
        }
    }
}
