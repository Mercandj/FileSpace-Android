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

import android.app.Dialog;
import android.widget.EditText;

import java.io.File;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.FileChooserDialog;
import com.mercandalli.android.apps.files.main.ApplicationActivity;

public class DialogShowTxt extends Dialog {

    FileChooserDialog mFileChooserDialog;
    ApplicationActivity app;
    File file;

    public DialogShowTxt(final ApplicationActivity app, String txt) {
        super(app);
        this.app = app;

        this.setContentView(R.layout.activity_file_text);
        this.setTitle(R.string.app_name);
        this.setCancelable(true);

        ((EditText) this.findViewById(R.id.txt)).setText("" + txt);

        DialogShowTxt.this.show();
    }
}
