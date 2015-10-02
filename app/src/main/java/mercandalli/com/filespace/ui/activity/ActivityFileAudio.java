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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.model.ModelFile;
import mercandalli.com.filespace.net.Base64;
import mercandalli.com.filespace.ui.view.PlayPauseView;
import mercandalli.com.filespace.ui.view.slider.Slider;

import static mercandalli.com.filespace.util.FileUtils.getRealPathFromURI;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class ActivityFileAudio extends Application {

    private String login, password;
    private boolean online;

    private ModelFile file;
    private List<ModelFile> files;

    private Slider sliderNumber;
    private PlayPauseView play;
    private TextView title, size;
    private MediaPlayer player;
    private final Handler handler = new Handler();
    private final int UPDATE_FREQUENCY = 1000;
    private boolean isMovingSeekBar = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_audio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.actionbar_audio));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("FileSpace - Audio");
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.notifications_bar_audio));

        this.title = (TextView) this.findViewById(R.id.title);
        this.size = (TextView) this.findViewById(R.id.size);
        this.sliderNumber = (Slider) this.findViewById(R.id.sliderNumber);
        this.sliderNumber.setValueToDisplay(new Slider.ValueToDisplay() {
            @Override
            public String convert(int value) {
                long minutes = value/60000;
                long seconds = (value-(minutes*60000))/1000;
                return (minutes+":"+(seconds<10?"0":"")+seconds);
            }
        });

        this.play = (PlayPauseView) this.findViewById(R.id.play);
        //this.play.setImageResource(android.R.drawable.ic_media_pause);
        this.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityFileAudio.this.player!=null) {
                    if(ActivityFileAudio.this.player.isPlaying()) {
                        //play.setImageResource(android.R.drawable.ic_media_play);
                        ActivityFileAudio.this.player.pause();
                        setNotification(false);
                    }
                    else {
                        //play.setImageResource(android.R.drawable.ic_media_pause);
                        ActivityFileAudio.this.player.start();
                        setNotification(true);
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

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras == null) {
            Log.e(""+getClass().getName(), "extras == null");

            String action = intent.getAction();
            String type = intent.getType();

            if(type != null) {
                if(type.startsWith("audio/")) {
                    ArrayList<Uri> audioUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    Uri audioUri = (Uri) intent.getData();
                    if (audioUris != null) {
                        this.files = new ArrayList<>();
                        this.online = false;
                        for(Uri uri: audioUris) {
                            this.files.add(new ModelFile(this, new File(uri.getPath())));
                        }
                        if(audioUris.size()!=0) {
                            this.file = new ModelFile(this, new File(audioUris.get(0).getPath()));
                            start();
                            return;
                        }
                    }
                    else if (audioUri != null) {
                        this.files = new ArrayList<>();
                        this.online = false;
                        this.file = new ModelFile(this, new File( "file".equals(audioUri.getScheme()) ? audioUri.getPath() : getRealPathFromURI(this, audioUri) ));
                        this.files.add(this.file);
                        start();
                        return;
                    }
                }
            }
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }
        else {
            this.login = extras.getString("LOGIN");
            this.password = extras.getString("PASSWORD");
            this.online = extras.getBoolean("ONLINE");
            this.file = (ModelFile) extras.getParcelable("FILE");
            this.files = (ArrayList) extras.getParcelableArrayList("FILES");
            start();
        }

        String action = (String) extras.get("do_action");
        if (action != null) {
            if (action.equals("close")) {
                if(player!=null) {
                    player.pause();
                }
            }
            else if (action.equals("next")) {
                next();
            }
            else if (action.equals("prev")) {
                previous();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String action = extras.getString("do_action");
        if(action == null)
            action = intent.getStringExtra("do_action");
        if (action != null) {
            if (action.equals("close")) {
                if(player!=null) {
                    player.pause();
                }
            }
            else if (action.equals("next")) {
                next();
            }
            else if (action.equals("prev")) {
                previous();
            }
        }
    }

    private String getTimeStr(long milliseconds) {
        long minutes = milliseconds/60000;
        long seconds = (milliseconds-(minutes*60000))/1000;
        return minutes+":"+(seconds<10?"0":"")+seconds;
    }

    class UpdaterPosition implements Runnable {
        private boolean kill = false;
        @Override
        public void run() {
            if(!kill)
                updatePosition();
        }
        public void kill() {
            this.kill = true;
        }
    }

    public final UpdaterPosition updatePositionRunnable = new UpdaterPosition();

    private void updatePosition() {
        this.handler.removeCallbacks(updatePositionRunnable);
        if(!this.sliderNumber.isPress())
            this.sliderNumber.setProgress(player.getCurrentPosition());
        this.handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
        this.size.setText(getTimeStr(this.player.getCurrentPosition())+" / "+getTimeStr(this.player.getDuration()));
    }

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
        if(this.player.isPlaying()) {
            this.player.stop();
        }
        if(files!=null) {
            boolean idMark = false;
            for (ModelFile f:files) {
                if(idMark && f.isAudio()) {
                    ActivityFileAudio.this.file = f;
                    start();
                    return;
                }
                if(f.equals(file))
                    idMark = true;
            }
            for (ModelFile f:files) {
                if(f.isAudio()) {
                    ActivityFileAudio.this.file = f;
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
        if(this.player != null)
            if(this.player.isPlaying())
                this.player.stop();
        if(files!=null) {
            boolean idMark = false;
            for (int i = files.size() - 1; i>=0; i--) {
                if(idMark && files.get(i).isAudio()) {
                    ActivityFileAudio.this.file = files.get(i);
                    start();
                    return;
                }
                if(files.get(i).equals(file))
                    idMark = true;
            }
            for (int i = files.size() - 1; i>=0; i--) {
                if(files.get(i).isAudio()) {
                    ActivityFileAudio.this.file = files.get(i);
                    start();
                    return;
                }
            }
        }
    }

    public void start() {
        if(file == null)
            return;
        try {
            Uri uri = Uri.parse((this.online) ? file.onlineUrl : file.url);

            this.player = new MediaPlayer();
            this.player.setOnCompletionListener(this.onCompletion);
            this.sliderNumber.setOnValueChangedListener(new Slider.OnValueChangedListener() {
                @Override
                public void onValueChanged(int value) {
                }

                @Override
                public void onValueChangedUp(int value) {
                    player.seekTo(value);
                }
            });

            if(this.online) {
                Map<String, String> headers = new HashMap<String, String>();
                StringBuilder authentication = new StringBuilder().append(this.login).append(":").append(this.password);
                String result = Base64.encodeBytes(authentication.toString().getBytes());
                headers.put("Authorization", "Basic " + result);
                this.player.setDataSource(this, uri, headers);
            }
            else
                this.player.setDataSource(this, uri);

            this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.player.prepare();
            this.player.start();

            this.sliderNumber.setProgress(0);
            this.sliderNumber.setMax(this.player.getDuration());
            this.title.setText("" + this.file.name);
            this.size.setText(getTimeStr(this.player.getCurrentPosition()) + " / " + getTimeStr(this.player.getDuration()));

            updatePosition();

            setNotification(true);

        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setNotification(boolean activated) {
        if(activated) {
            Intent intent = this.getIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Intent buttonsIntent_close = new Intent(this, ActivityFileAudio.class);
            buttonsIntent_close.putExtra("do_action", "close");
            Intent buttonsIntent_next = new Intent(this, ActivityFileAudio.class);
            buttonsIntent_close.putExtra("do_action", "next");
            Intent buttonsIntent_prev = new Intent(this, ActivityFileAudio.class);
            buttonsIntent_close.putExtra("do_action", "prev");

            RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_musique);
            remoteViews.setTextViewText(R.id.titre_notif, file.name);
            remoteViews.setOnClickPendingIntent(R.id.close, PendingIntent.getActivity(this, 0, buttonsIntent_close, 0));
            remoteViews.setOnClickPendingIntent(R.id.play, PendingIntent.getActivity(this, 0, buttonsIntent_close, 0));
            remoteViews.setOnClickPendingIntent(R.id.next, PendingIntent.getActivity(this, 0, buttonsIntent_next, 0));
            remoteViews.setOnClickPendingIntent(R.id.prev, PendingIntent.getActivity(this, 0, buttonsIntent_prev, 0));
            remoteViews.setOnClickPendingIntent(R.id.titre_notif, PendingIntent.getActivity(this, 0, intent, 0));

            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Notification.Builder mNotifyBuilder = new Notification.Builder(this);
            Notification foregroundNote = mNotifyBuilder.setSmallIcon(R.drawable.audio)
                    /*
                    .setContentTitle("Music")
                    .setContentText( "Text" )*/
                    //.setContentIntent(pIntent)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setContent(remoteViews)
                    .build();
            foregroundNote.bigContentView = remoteViews;
            if (player.isPlaying()) {
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, foregroundNote);
            }
        }
        else {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
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
                finishActivity();
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
        setNotification(false);
        if(updatePositionRunnable!=null)
            updatePositionRunnable.kill();
        if(player!=null) {
            player.stop();
            player.reset();
        }
        supportFinishAfterTransition();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        sliderNumber.updateAfterRotation();

        ViewTreeObserver observer = sliderNumber.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Log.v("ActivityFileAudio",
                        String.format("new width=%d; new height=%d", sliderNumber.getWidth(),
                                sliderNumber.getHeight()));

                sliderNumber.updateAfterRotation();
                sliderNumber.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
