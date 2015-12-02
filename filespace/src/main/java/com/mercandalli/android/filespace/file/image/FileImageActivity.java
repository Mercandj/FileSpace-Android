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
package com.mercandalli.android.filespace.file.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.listener.IBitmapListener;
import com.mercandalli.android.filespace.common.listener.ILongListener;
import com.mercandalli.android.filespace.common.net.TaskGetDownloadImage;
import com.mercandalli.android.filespace.common.util.ColorUtils;
import com.mercandalli.android.filespace.common.util.FontUtils;
import com.mercandalli.android.filespace.common.util.ImageUtils;
import com.mercandalli.android.filespace.main.ApplicationActivity;

import java.io.File;
import java.util.Date;

/**
 * An {@link Activity} for displaying the image
 * {@link com.mercandalli.android.filespace.file.FileModel}: online or offline.
 */
public class FileImageActivity extends ApplicationActivity {

    private String mUrl, mTitle;
    private int mId;
    private boolean online;
    private long sizeFile;
    private Date date_creation;
    private ImageButton circle;
    private TextView mTitleTextView, mProgressTextView;

    Bitmap bitmap;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picture);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Translucent notification bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Get views
        this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        this.circle = (ImageButton) this.findViewById(R.id.circle);
        this.mTitleTextView = (TextView) this.findViewById(R.id.title);
        this.mProgressTextView = (TextView) this.findViewById(R.id.progress_tv);

        this.progressBar.setProgress(0);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("" + getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        } else {
            this.mId = extras.getInt("ID");
            this.mTitle = extras.getString("TITLE");
            this.mUrl = extras.getString("URL_FILE");
            this.online = extras.getBoolean("ONLINE");
            this.sizeFile = extras.getLong("SIZE_FILE");
            this.date_creation = (Date) extras.getSerializable("DATE_FILE");

            if (this.mTitle != null) {
                mTitleTextView.setText(this.mTitle);
                FontUtils.applyFont(this, mTitleTextView, "fonts/Roboto-Regular.ttf");
            }

            if (ImageUtils.is_image(this, this.mId)) {
                bitmap = ImageUtils.load_image(this, this.mId);
                ((ImageView) this.findViewById(R.id.tab_icon)).setImageBitmap(bitmap);
                int bgColor = ColorUtils.getMutedColor(bitmap);
                if (bgColor != 0) {
                    mTitleTextView.setBackgroundColor(bgColor);
                    mTitleTextView.setTextColor(ColorUtils.colorText(bgColor));
                    RippleDrawable cir = ImageUtils.getPressedColorRippleDrawable(bgColor, ColorUtils.getDarkMutedColor(bitmap));
                    this.circle.setBackground(cir);
                }
                this.progressBar.setVisibility(View.GONE);
                this.mProgressTextView.setVisibility(View.GONE);
            } else if (this.mId != 0) {
                this.progressBar.setVisibility(View.VISIBLE);
                this.mProgressTextView.setVisibility(View.VISIBLE);
                (new TaskGetDownloadImage(this, this, mUrl, mId, sizeFile, -1, new IBitmapListener() {
                    @Override
                    public void execute(Bitmap bitmap) {
                        ((ImageView) findViewById(R.id.tab_icon)).setImageBitmap(bitmap);
                        int bgColor = ColorUtils.getMutedColor(bitmap);
                        if (bgColor != 0) {
                            mTitleTextView.setBackgroundColor(bgColor);
                            mTitleTextView.setTextColor(ColorUtils.colorText(bgColor));
                            RippleDrawable cir = ImageUtils.getPressedColorRippleDrawable(bgColor, ColorUtils.getDarkMutedColor(bitmap));
                            circle.setBackground(cir);
                        }
                        progressBar.setVisibility(View.GONE);
                        mProgressTextView.setVisibility(View.GONE);
                    }
                }, new ILongListener() {
                    @Override
                    public void execute(long text) {
                        progressBar.setProgress((int) text);
                        mProgressTextView.setText(text + "%");
                    }
                })).execute();
            }
        }

        this.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent();
                picIntent.setAction(Intent.ACTION_VIEW);
                picIntent.setDataAndType(Uri.parse("file://" + (new File(FileImageActivity.this.getFilesDir() + "/file_" + mId)).getAbsolutePath()), "image/*");
                FileImageActivity.this.startActivity(picIntent);
            }
        });
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void updateAdapters() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            supportFinishAfterTransition();
        }
        return super.onKeyDown(keyCode, event);
    }
}
