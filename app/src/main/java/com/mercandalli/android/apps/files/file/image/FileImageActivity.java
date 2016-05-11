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
package com.mercandalli.android.apps.files.file.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IBitmapListener;
import com.mercandalli.android.apps.files.common.listener.ILongListener;
import com.mercandalli.android.apps.files.common.net.TaskGetDownloadImage;
import com.mercandalli.android.apps.files.common.util.ColorUtils;
import com.mercandalli.android.apps.files.common.util.ImageUtils;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.main.ApplicationActivity;
import com.mercandalli.android.library.base.precondition.Preconditions;

import java.io.File;
import java.util.Date;

/**
 * An {@link Activity} for displaying the image
 * {@link com.mercandalli.android.apps.files.file.FileModel}: online or offline.
 */
public class FileImageActivity extends ApplicationActivity {

    private String mUrl, mTitle;
    private int mId;
    private boolean online;
    private long sizeFile;
    private Date date_creation;
    private ImageButton mCircle;
    private TextView mTitleTextView, mProgressTextView;

    private Bitmap mBitmap;
    private ProgressBar mProgressBar;

    public static void startOnlineImage(
            final Activity activity,
            final FileModel fileModel) {
        startOnlineImage(activity, fileModel, null, null);
    }

    public static void startOnlineImage(
            final @NonNull Activity activity,
            final @NonNull FileModel fileModel,
            final @Nullable View iconAnimationView,
            final @Nullable View titleAnimationView) {
        Preconditions.checkNotNull(activity);
        Preconditions.checkNotNull(fileModel);

        final Intent intent = new Intent(activity, FileImageActivity.class);
        intent.putExtra("ID", fileModel.getId());
        intent.putExtra("TITLE", "" + fileModel.getFullName());
        intent.putExtra("URL_FILE", "" + fileModel.getOnlineUrl());
        intent.putExtra("CLOUD", true);
        intent.putExtra("SIZE_FILE", fileModel.getSize());
        intent.putExtra("DATE_FILE", fileModel.getDateCreation());
        if (iconAnimationView == null || titleAnimationView == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.startActivity(intent, ActivityOptionsCompat.
                    makeSceneTransitionAnimation(
                            activity,
                            Pair.create(iconAnimationView, "transitionIcon"),
                            Pair.create(titleAnimationView, "transitionTitle"))
                    .toBundle());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picture);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        // Translucent notification bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Get views
        mProgressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        mCircle = (ImageButton) this.findViewById(R.id.circle);
        mTitleTextView = (TextView) this.findViewById(R.id.title);
        mProgressTextView = (TextView) this.findViewById(R.id.progress_tv);

        mProgressBar.setProgress(0);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("" + getClass().getName(), "extras == null");
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        } else {
            mId = extras.getInt("ID");
            mTitle = extras.getString("TITLE");
            mUrl = extras.getString("URL_FILE");
            online = extras.getBoolean("CLOUD");
            sizeFile = extras.getLong("SIZE_FILE");
            date_creation = (Date) extras.getSerializable("DATE_FILE");

            if (mTitle != null) {
                mTitleTextView.setText(mTitle);
            }

            if (ImageUtils.isImage(this, this.mId)) {
                mBitmap = ImageUtils.loadImage(this, this.mId);
                ((ImageView) this.findViewById(R.id.tab_icon)).setImageBitmap(mBitmap);
                int bgColor = ColorUtils.getMutedColor(mBitmap);
                if (bgColor != 0) {
                    mTitleTextView.setBackgroundColor(bgColor);
                    mTitleTextView.setTextColor(ColorUtils.colorText(bgColor));
                    RippleDrawable cir = ImageUtils.getPressedColorRippleDrawable(bgColor, ColorUtils.getDarkMutedColor(mBitmap));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mCircle.setBackground(cir);
                    }
                }
                mProgressBar.setVisibility(View.GONE);
                mProgressTextView.setVisibility(View.GONE);
            } else if (this.mId != 0) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressTextView.setVisibility(View.VISIBLE);
                (new TaskGetDownloadImage(this, mUrl, mId, sizeFile, -1, new IBitmapListener() {
                    @Override
                    public void execute(Bitmap bitmap) {
                        ((ImageView) findViewById(R.id.tab_icon)).setImageBitmap(bitmap);
                        int bgColor = ColorUtils.getMutedColor(bitmap);
                        if (bgColor != 0) {
                            mTitleTextView.setBackgroundColor(bgColor);
                            mTitleTextView.setTextColor(ColorUtils.colorText(bgColor));
                            RippleDrawable cir = ImageUtils.getPressedColorRippleDrawable(bgColor, ColorUtils.getDarkMutedColor(bitmap));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                mCircle.setBackground(cir);
                            }
                        }
                        mProgressBar.setVisibility(View.GONE);
                        mProgressTextView.setVisibility(View.GONE);
                    }
                }, new ILongListener() {
                    @Override
                    public void execute(long text) {
                        mProgressBar.setProgress((int) text);
                        mProgressTextView.setText(text + "%");
                    }
                })).execute();
            }
        }

        mCircle.setOnClickListener(new View.OnClickListener() {
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
