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
package mercandalli.com.filespace.file.image;

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

import java.io.File;
import java.util.Date;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.common.listener.IBitmapListener;
import mercandalli.com.filespace.common.listener.ILongListener;
import mercandalli.com.filespace.common.net.TaskGetDownloadImage;
import mercandalli.com.filespace.main.ApplicationActivity;
import mercandalli.com.filespace.common.util.ColorUtils;
import mercandalli.com.filespace.common.util.FontUtils;
import mercandalli.com.filespace.common.util.ImageUtils;

public class FileImageActivity extends ApplicationActivity {
    private String url, title;
    private int id;
    private boolean online;
    private long sizeFile;
    private Date date_creation;
    private ImageButton circle;
    private TextView title_tv, progress_tv;

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
        this.title_tv = (TextView) this.findViewById(R.id.title);
        this.progress_tv = (TextView) this.findViewById(R.id.progress_tv);

        this.progressBar.setProgress(0);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("" + getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        } else {
            this.id = extras.getInt("ID");
            this.title = extras.getString("TITLE");
            this.url = extras.getString("URL_FILE");
            this.online = extras.getBoolean("ONLINE");
            this.sizeFile = extras.getLong("SIZE_FILE");
            this.date_creation = (Date) extras.getSerializable("DATE_FILE");

            if (this.title != null) {
                title_tv.setText(this.title);
                FontUtils.applyFont(this, title_tv, "fonts/Roboto-Regular.ttf");
            }

            if (ImageUtils.is_image(this, this.id)) {
                bitmap = ImageUtils.load_image(this, this.id);
                ((ImageView) this.findViewById(R.id.icon)).setImageBitmap(bitmap);
                int bgColor = ColorUtils.getMutedColor(bitmap);
                if (bgColor != 0) {
                    title_tv.setBackgroundColor(bgColor);
                    title_tv.setTextColor(ColorUtils.colorText(bgColor));
                    RippleDrawable cir = ImageUtils.getPressedColorRippleDrawable(bgColor, ColorUtils.getDarkMutedColor(bitmap));
                    this.circle.setBackground(cir);
                }
                this.progressBar.setVisibility(View.GONE);
                this.progress_tv.setVisibility(View.GONE);
            } else if (this.id != 0) {
                this.progressBar.setVisibility(View.VISIBLE);
                this.progress_tv.setVisibility(View.VISIBLE);
                (new TaskGetDownloadImage(this, this, url, id, sizeFile, -1, new IBitmapListener() {
                    @Override
                    public void execute(Bitmap bitmap) {
                        ((ImageView) findViewById(R.id.icon)).setImageBitmap(bitmap);
                        int bgColor = ColorUtils.getMutedColor(bitmap);
                        if (bgColor != 0) {
                            title_tv.setBackgroundColor(bgColor);
                            title_tv.setTextColor(ColorUtils.colorText(bgColor));
                            RippleDrawable cir = ImageUtils.getPressedColorRippleDrawable(bgColor, ColorUtils.getDarkMutedColor(bitmap));
                            circle.setBackground(cir);
                        }
                        progressBar.setVisibility(View.GONE);
                        progress_tv.setVisibility(View.GONE);
                    }
                }, new ILongListener() {
                    @Override
                    public void execute(long text) {
                        progressBar.setProgress((int) text);
                        progress_tv.setText(text + "%");
                    }
                })).execute();
            }
        }

        this.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent();
                picIntent.setAction(Intent.ACTION_VIEW);
                picIntent.setDataAndType(Uri.parse("file://" + (new File(FileImageActivity.this.getFilesDir() + "/file_" + id)).getAbsolutePath()), "image/*");
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
