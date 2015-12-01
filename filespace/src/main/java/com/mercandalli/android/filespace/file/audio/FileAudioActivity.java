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
package com.mercandalli.android.filespace.file.audio;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.view.PlayPauseView;
import com.mercandalli.android.filespace.common.view.slider.Slider;
import com.mercandalli.android.filespace.main.App;

public class FileAudioActivity extends AppCompatActivity implements View.OnClickListener, FileAudioPlayer.OnPlayerStatusChangeListener {

    public static final String EXTRA_IS_ONLINE = "FileAudioActivity.Extra.EXTRA_IS_ONLINE";
    public static final String EXTRA_FILE_CURRENT_POSITION = "FileAudioActivity.Extra.EXTRA_FILE_CURRENT_POSITION";
    public static final String EXTRA_FILES_PATH = "FileAudioActivity.Extra.EXTRA_FILES_PATH";

    private boolean mIsOnline;

    private int mCurrentPosition;
    private final List<FileAudioModel> mFileAudioModelList = new ArrayList<>();

    private Slider mSliderNumber;
    private TextView mTitleTextView;
    private TextView mSizeTextView;
    private PlayPauseView mPlayPauseView;

    FileAudioPlayer mFileAudioPlayer;

    static boolean firstStart;

    public static void startLocal(Activity activity, final int currentPosition, final List<String> fileMusicPath, final View animationView) {
        Bundle args = new Bundle();
        final Intent intent = new Intent(activity, FileAudioActivity.class);

        intent.putExtra(EXTRA_IS_ONLINE, false);
        intent.putExtra(EXTRA_FILE_CURRENT_POSITION, currentPosition);
        intent.putExtra(EXTRA_FILES_PATH, new ArrayList<>(fileMusicPath));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && animationView != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, animationView, "transitionIcon");
            args = options.toBundle();
        }

        firstStart = true;

        activity.startActivity(intent, args);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_audio);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.actionbar_audio));

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("FileSpace - Audio");
            }
        }

        Window window = this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.notifications_bar_audio));
        }

        mFileAudioPlayer = App.get(this).getAppComponent().provideMusicPlayer();

        mTitleTextView = (TextView) this.findViewById(R.id.title);
        mSizeTextView = (TextView) this.findViewById(R.id.size);
        mSliderNumber = (Slider) this.findViewById(R.id.sliderNumber);
        mSliderNumber.setValueToDisplay(new Slider.ValueToDisplay() {
            @Override
            public String convert(int value) {
                long minutes = value / 60000;
                long seconds = (value - (minutes * 60000)) / 1000;
                return (minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
            }
        });
        mSliderNumber.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {

            }

            @Override
            public void onValueChangedUp(int value) {
                mFileAudioPlayer.seekTo(value);
            }
        });

        mPlayPauseView = (PlayPauseView) findViewById(R.id.play);
        mPlayPauseView.setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        findViewById(R.id.previous).setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null &&
                bundle.containsKey(EXTRA_IS_ONLINE) &&
                bundle.containsKey(EXTRA_FILE_CURRENT_POSITION) &&
                bundle.containsKey(EXTRA_FILES_PATH)) {

            // Get data
            mIsOnline = bundle.getBoolean(EXTRA_IS_ONLINE);
            mCurrentPosition = bundle.getInt(EXTRA_FILE_CURRENT_POSITION);
            List<String> absolutePathArray = bundle.getStringArrayList(EXTRA_FILES_PATH);
            if (absolutePathArray != null) {
                for (String absolutePath : absolutePathArray) {
                    mFileAudioModelList.add(new FileAudioModel.FileMusicModelBuilder().file(new File(absolutePath)).build());
                }
            }

            if (firstStart) {
                mFileAudioPlayer.startMusic(mCurrentPosition, mFileAudioModelList);
            }

        } else {
            throw new IllegalArgumentException("Use static start() method");
        }

        firstStart = false;
    }

    @Override
    public void onClick(View v) {
        final int idView = v.getId();
        switch (idView) {
            case R.id.play:
                if (mFileAudioPlayer.isPlaying()) {
                    mFileAudioPlayer.pause();
                } else {
                    mFileAudioPlayer.play();
                }
                mPlayPauseView.toggle();
                break;
            case R.id.next:
                mFileAudioPlayer.next();
                break;
            case R.id.previous:
                mFileAudioPlayer.previous();
                break;
        }
    }

    private String getTimeStr(long milliseconds) {
        long minutes = milliseconds / 60000;
        long seconds = (milliseconds - (minutes * 60000)) / 1000;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
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
        supportFinishAfterTransition();
    }


    @Override
    public void onPlayerStatusChanged(int status) {

    }

    @Override
    public void onPlayerProgressChanged(int progress, int duration, int musicPosition, FileAudioModel music) {
        //mCurrentPosition = mAudioService.getPlayingIndex();
        mCurrentPosition = musicPosition;
        mSliderNumber.setProgress(progress);
        mSliderNumber.setMax(duration);
        mTitleTextView.setText(music.getName());
        mSizeTextView.setText(String.format("%s / %s", getTimeStr(progress), getTimeStr(duration)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFileAudioPlayer.registerOnPlayerStatusChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFileAudioPlayer.unregisterOnPreviewPlayerStatusChangeListener(this);
    }
}
