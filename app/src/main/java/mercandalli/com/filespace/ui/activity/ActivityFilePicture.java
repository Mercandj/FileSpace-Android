/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IBitmapListener;
import mercandalli.com.filespace.net.TaskGetDownloadImage;
import mercandalli.com.filespace.util.ColorUtils;
import mercandalli.com.filespace.util.FontUtils;

import static mercandalli.com.filespace.util.ImageUtils.is_image;
import static mercandalli.com.filespace.util.ImageUtils.load_image;

/**
 * Created by Jonathan on 29/05/2015.
 */
public class ActivityFilePicture extends Application {
    private String initate, url, login, password, title;
    private int id;
    private boolean online;
    private long sizeFile;

    Bitmap bitmap;
    Palette palette;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picture);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Translucent notification bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Visibility
        //((ImageView) this.findViewById(R.id.icon)).setVisibility(View.GONE);
        ((ProgressBar) this.findViewById(R.id.circulerProgressBar)).setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e(""+getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }
        else {
            this.id = extras.getInt("ID");
            this.title = extras.getString("TITLE");
            this.url = extras.getString("URL_FILE");
            this.login = extras.getString("LOGIN");
            this.password = extras.getString("PASSWORD");
            this.online = extras.getBoolean("ONLINE");
            this.sizeFile = extras.getLong("SIZE_FILE");

            TextView title = (TextView) this.findViewById(R.id.title);

            if(this.title != null) {
                title.setText(this.title);
                FontUtils.applyFont(this, title, "fonts/Roboto-Regular.ttf");
            }

            if(is_image(this, this.id)) {
                bitmap = load_image(this, this.id);
                ((ImageView) this.findViewById(R.id.icon)).setImageBitmap(bitmap);
                int bgColor = ColorUtils.getColor(bitmap);
                title.setBackgroundColor(bgColor);
                title.setTextColor(ColorUtils.colorText(bgColor));
            }
            else if(this.id != 0)
                (new TaskGetDownloadImage(this, login, password, url, id, sizeFile, -1,  new IBitmapListener() {
                    @Override
                    public void execute(Bitmap bitmap) {
                        ((ImageView) findViewById(R.id.icon)).setImageBitmap(bitmap);
                        palette  = Palette.from(bitmap).generate();
                    }
                })).execute();
        }
    }

    @Override
    public void refreshAdapters() {

    }

    @Override
    public void updateAdapters() {

    }

    @Override
    public View getFab() {
        return null;
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
