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
package mercandalli.com.filespace.ui.activities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.net.Base64;
import mercandalli.com.filespace.ui.views.PlayPauseView;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class FileVideoActivity extends ApplicationActivity {

    private String login, password;
    private boolean online;

    private ModelFile file;
    private List<ModelFile> files;

    private SeekBar seekBar;
    private PlayPauseView play;
    private TextView title, size;
    private MediaPlayer player;
    private final Handler handler = new Handler();
    private final int UPDATE_FREQUENCY = 1000;
    private boolean isMovingSeekBar = false;

    private SurfaceView videoSurface;
    private SurfaceHolder videoHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_video);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.actionbar_audio));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.notifications_bar_audio));

        this.videoSurface = (SurfaceView) this.findViewById(R.id.videoView);
        this.videoHolder = this.videoSurface.getHolder();
        this.title = (TextView) this.findViewById(R.id.title);
        this.size = (TextView) this.findViewById(R.id.size);
        this.seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        this.play = (PlayPauseView) this.findViewById(R.id.play);
        //this.play.setImageResource(android.R.drawable.ic_media_pause);
        this.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileVideoActivity.this.player != null) {
                    if (FileVideoActivity.this.player.isPlaying()) {
                        //play.setImageResource(android.R.drawable.ic_media_play);
                        FileVideoActivity.this.player.pause();
                    } else {
                        //play.setImageResource(android.R.drawable.ic_media_pause);
                        FileVideoActivity.this.player.start();
                    }
                    play.toggle();
                }
            }
        });

        this.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        this.findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("" + getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        } else {
            this.login = extras.getString("LOGIN");
            this.password = extras.getString("PASSWORD");
            this.online = extras.getBoolean("ONLINE");
            this.file = (ModelFile) extras.getParcelable("FILE");
            this.files = (ArrayList) extras.getParcelableArrayList("FILES");
            start();
        }
    }

    class UpdaterPosition implements Runnable {
        private boolean kill = false;

        @Override
        public void run() {
            if (!kill)
                updatePosition();
        }

        public void kill() {
            this.kill = true;
        }
    }

    public final UpdaterPosition updatePositionRunnable = new UpdaterPosition();

    private void updatePosition() {
        this.handler.removeCallbacks(updatePositionRunnable);
        this.seekBar.setProgress(player.getCurrentPosition());
        this.handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isMovingSeekBar)
                player.seekTo(progress);
        }
    };

    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    };

    /**
     * Play the next song
     */
    private void next() {
        if (this.player.isPlaying()) {
            this.player.stop();
        }
        if (files != null) {
            boolean idMark = false;
            for (ModelFile f : files) {
                if (idMark && f.isAudio()) {
                    FileVideoActivity.this.file = f;
                    start();
                    return;
                }
                if (f.equals(file))
                    idMark = true;
            }
            for (ModelFile f : files) {
                if (f.isAudio()) {
                    FileVideoActivity.this.file = f;
                    start();
                    return;
                }
            }
        }
    }

    /**
     * Play the previous song
     */
    private void previous() {
        if (this.player.isPlaying()) {
            this.player.stop();
        }
        if (files != null) {
            boolean idMark = false;
            for (int i = files.size() - 1; i >= 0; i--) {
                if (idMark && files.get(i).isAudio()) {
                    FileVideoActivity.this.file = files.get(i);
                    start();
                    return;
                }
                if (files.get(i).equals(file))
                    idMark = true;
            }
            for (int i = files.size() - 1; i >= 0; i--) {
                if (files.get(i).isAudio()) {
                    FileVideoActivity.this.file = files.get(i);
                    start();
                    return;
                }
            }
        }
    }

    public void start() {
        try {
            Uri uri = Uri.parse((this.online) ? file.onlineUrl : file.url);

            this.player = new MediaPlayer();
            this.player.setDisplay(videoHolder);
            this.player.setOnCompletionListener(this.onCompletion);
            this.seekBar.setOnSeekBarChangeListener(this.seekBarChanged);

            if (this.online) {
                Map<String, String> headers = new HashMap<String, String>();
                StringBuilder authentication = new StringBuilder().append(this.login).append(":").append(this.password);
                String result = Base64.encodeBytes(authentication.toString().getBytes());
                headers.put("Authorization", "Basic " + result);
                this.player.setDataSource(this, uri, headers);
            } else
                this.player.setDataSource(this, uri);

            this.player.prepare();
            this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            this.player.start();

            this.seekBar.setProgress(0);
            this.seekBar.setMax(this.player.getDuration());
            this.title.setText("" + this.file.name);
            long minutes = this.player.getDuration() / 60000;
            long seconds = (this.player.getDuration() - (minutes * 60000)) / 1000;
            this.size.setText("" + (this.player.getDuration() / 60000) + ":" + (seconds < 10 ? "0" : "") + seconds);

            updatePosition();

        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshAdapters() {

    }

    @Override
    public void updateAdapters() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (updatePositionRunnable != null)
                    updatePositionRunnable.kill();
                if (player != null) {
                    player.stop();
                    player.reset();
                }
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            finishActivity();
        return super.onKeyDown(keyCode, event);
    }

    public void finishActivity() {
        if (updatePositionRunnable != null)
            updatePositionRunnable.kill();
        if (player != null) {
            player.stop();
            player.reset();
        }
        supportFinishAfterTransition();
    }
}
