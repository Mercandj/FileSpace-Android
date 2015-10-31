/**
 * This mFileModel is part of FileSpace for Android, an app for managing your server (mFileModelList, talks...).
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
package mercandalli.com.filespace.ui.activitiy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
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
import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.ui.view.PlayPauseView;
import mercandalli.com.filespace.ui.view.slider.Slider;
import mercandalli.com.filespace.util.FileUtils;

public class FileAudioActivity extends ApplicationActivity {

    private boolean mIsOnline;

    private FileModel mFileModel;
    private List<FileModel> mFileModelList;

    private Slider mSliderNumber;
    private PlayPauseView mPlayPauseView;
    private TextView mTitleTextView;
    private TextView mSizeTextView;
    private MediaPlayer mMediaPlayer;
    private final Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_audio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.actionbar_audio));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("FileSpace - Audio");
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.notifications_bar_audio));
        }

        this.mTitleTextView = (TextView) this.findViewById(R.id.title);
        this.mSizeTextView = (TextView) this.findViewById(R.id.size);
        this.mSliderNumber = (Slider) this.findViewById(R.id.sliderNumber);
        this.mSliderNumber.setValueToDisplay(new Slider.ValueToDisplay() {
            @Override
            public String convert(int value) {
                long minutes = value / 60000;
                long seconds = (value - (minutes * 60000)) / 1000;
                return (minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
            }
        });

        this.mPlayPauseView = (PlayPauseView) this.findViewById(R.id.play);
        //this.mPlayPauseView.setImageResource(android.R.drawable.ic_media_pause);
        this.mPlayPauseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileAudioActivity.this.mMediaPlayer != null) {
                    if (FileAudioActivity.this.mMediaPlayer.isPlaying()) {
                        //mPlayPauseView.setImageResource(android.R.drawable.ic_media_play);
                        FileAudioActivity.this.mMediaPlayer.pause();
                        setNotification(false);
                    } else {
                        //mPlayPauseView.setImageResource(android.R.drawable.ic_media_pause);
                        FileAudioActivity.this.mMediaPlayer.start();
                        setNotification(true);
                    }
                    mPlayPauseView.toggle();
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
        if (extras == null) {
            Log.e("" + getClass().getName(), "extras == null");

            String action = intent.getAction();
            String type = intent.getType();

            if (type != null) {
                if (type.startsWith("audio/")) {
                    ArrayList<Uri> audioUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    Uri audioUri = (Uri) intent.getData();
                    if (audioUris != null) {
                        this.mFileModelList = new ArrayList<>();
                        this.mIsOnline = false;
                        for (Uri uri : audioUris) {
                            this.mFileModelList.add(new FileModel.FileModelBuilder().file(new File(uri.getPath())).build());
                        }
                        if (audioUris.size() != 0) {
                            this.mFileModel = new FileModel.FileModelBuilder().file(new File(audioUris.get(0).getPath())).build();
                            start();
                            return;
                        }
                    } else if (audioUri != null) {
                        this.mFileModelList = new ArrayList<>();
                        this.mIsOnline = false;
                        this.mFileModel = new FileModel.FileModelBuilder().file(new File("file".equals(audioUri.getScheme()) ? audioUri.getPath() : FileUtils.getRealPathFromURI(this, audioUri))).build();
                        this.mFileModelList.add(this.mFileModel);
                        start();
                        return;
                    }
                }
            }
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        } else {
            this.mIsOnline = extras.getBoolean("ONLINE");
            this.mFileModel = extras.getParcelable("FILE");
            this.mFileModelList = extras.getParcelableArrayList("FILES");
            start();
        }

        String action = (String) extras.get("do_action");
        if (action != null) {
            switch (action) {
                case "close":
                    if (mMediaPlayer != null) {
                        mMediaPlayer.pause();
                    }
                    break;
                case "next":
                    next();
                    break;
                case "prev":
                    previous();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String action = extras.getString("do_action");
        if (action == null)
            action = intent.getStringExtra("do_action");
        if (action != null) {
            switch (action) {
                case "close":
                    if (mMediaPlayer != null) {
                        mMediaPlayer.pause();
                    }
                    break;
                case "next":
                    next();
                    break;
                case "prev":
                    previous();
                    break;
            }
        }
    }

    private String getTimeStr(long milliseconds) {
        long minutes = milliseconds / 60000;
        long seconds = (milliseconds - (minutes * 60000)) / 1000;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
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
        mHandler.removeCallbacks(updatePositionRunnable);
        if (!this.mSliderNumber.isPress())
            this.mSliderNumber.setProgress(mMediaPlayer.getCurrentPosition());
        int updateFrequency = 1000;
        this.mHandler.postDelayed(updatePositionRunnable, updateFrequency);
        this.mSizeTextView.setText(getTimeStr(this.mMediaPlayer.getCurrentPosition()) + " / " + getTimeStr(this.mMediaPlayer.getDuration()));
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
        if (this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.stop();
        }
        if (mFileModelList != null) {
            boolean idMark = false;
            for (FileModel f : mFileModelList) {
                if (idMark && f.isAudio()) {
                    FileAudioActivity.this.mFileModel = f;
                    start();
                    return;
                }
                if (f.equals(mFileModel))
                    idMark = true;
            }
            for (FileModel f : mFileModelList) {
                if (f.isAudio()) {
                    FileAudioActivity.this.mFileModel = f;
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
        if (this.mMediaPlayer != null)
            if (this.mMediaPlayer.isPlaying())
                this.mMediaPlayer.stop();
        if (mFileModelList != null) {
            boolean idMark = false;
            for (int i = mFileModelList.size() - 1; i >= 0; i--) {
                if (idMark && mFileModelList.get(i).isAudio()) {
                    FileAudioActivity.this.mFileModel = mFileModelList.get(i);
                    start();
                    return;
                }
                if (mFileModelList.get(i).equals(mFileModel))
                    idMark = true;
            }
            for (int i = mFileModelList.size() - 1; i >= 0; i--) {
                if (mFileModelList.get(i).isAudio()) {
                    FileAudioActivity.this.mFileModel = mFileModelList.get(i);
                    start();
                    return;
                }
            }
        }
    }

    public void start() {
        if (mFileModel == null)
            return;
        try {
            Uri uri = Uri.parse((this.mIsOnline) ? mFileModel.getOnlineUrl() : mFileModel.getUrl());

            this.mMediaPlayer = new MediaPlayer();
            this.mMediaPlayer.setOnCompletionListener(this.onCompletion);
            this.mSliderNumber.setOnValueChangedListener(new Slider.OnValueChangedListener() {
                @Override
                public void onValueChanged(int value) {
                }

                @Override
                public void onValueChangedUp(int value) {
                    mMediaPlayer.seekTo(value);
                }
            });

            if (this.mIsOnline) {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Basic " + Config.getUserToken());
                this.mMediaPlayer.setDataSource(this, uri, headers);
            } else
                this.mMediaPlayer.setDataSource(this, uri);

            this.mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.mMediaPlayer.prepare();
            this.mMediaPlayer.start();

            this.mSliderNumber.setProgress(0);
            this.mSliderNumber.setMax(this.mMediaPlayer.getDuration());
            this.mTitleTextView.setText("" + this.mFileModel.getName());
            this.mSizeTextView.setText(getTimeStr(this.mMediaPlayer.getCurrentPosition()) + " / " + getTimeStr(this.mMediaPlayer.getDuration()));

            updatePosition();

            setNotification(true);

        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setNotification(boolean activated) {
        if (activated) {
            Intent intent = this.getIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Intent buttonsIntent_close = new Intent(this, FileAudioActivity.class);
            buttonsIntent_close.putExtra("do_action", "close");
            Intent buttonsIntent_next = new Intent(this, FileAudioActivity.class);
            buttonsIntent_close.putExtra("do_action", "next");
            Intent buttonsIntent_prev = new Intent(this, FileAudioActivity.class);
            buttonsIntent_close.putExtra("do_action", "prev");

            RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_musique);
            remoteViews.setTextViewText(R.id.titre_notif, mFileModel.getName());
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
            if (mMediaPlayer.isPlaying()) {
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, foregroundNote);
            }
        } else {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        }
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
        if (updatePositionRunnable != null)
            updatePositionRunnable.kill();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        supportFinishAfterTransition();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mSliderNumber.updateAfterRotation();

        ViewTreeObserver observer = mSliderNumber.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Log.v("ActivityFileAudio",
                        String.format("new width=%d; new height=%d", mSliderNumber.getWidth(),
                                mSliderNumber.getHeight()));

                mSliderNumber.updateAfterRotation();
                mSliderNumber.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
