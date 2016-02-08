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
package com.mercandalli.android.apps.files.file.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.view.PlayPauseView;
import com.mercandalli.android.apps.files.common.view.slider.Slider;
import com.mercandalli.android.apps.files.file.audio.cast.FileAudioCast;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.precondition.Preconditions;
import com.mercandalli.android.apps.files.shared.SharedAudioPlayerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link AppCompatActivity} to play/pause an audio file.
 */
public class FileAudioActivity extends AppCompatActivity implements
        View.OnClickListener,
        FileAudioPlayer.OnPlayerStatusChangeListener {

    /* package */ static final String EXTRA_IS_ONLINE = "FileAudioActivity.Extra.EXTRA_IS_ONLINE";
    /* package */ static final String EXTRA_FILE_CURRENT_POSITION = "FileAudioActivity.Extra.EXTRA_FILE_CURRENT_POSITION";
    /* package */ static final String EXTRA_FILES_PATH = "FileAudioActivity.Extra.EXTRA_FILES_PATH";

    private boolean mIsOnline;

    private int mCurrentPosition;
    private final List<FileAudioModel> mFileAudioModelList = new ArrayList<>();

    private Slider mSliderNumber;
    private TextView mTitleTextView;
    private TextView mSizeTextView;
    private PlayPauseView mPlayPauseView;

    private FileAudioPlayer mFileAudioPlayer;

    private boolean mFirstStart;
    private final FileAudioCast mFileAudioCast = new FileAudioCast();

    /**
     * Start this {@link AppCompatActivity}.
     */
    public static void start(
            final Activity activity,
            final int currentPosition,
            final List<String> fileMusicPath,
            final View animationView,
            final boolean isOnline) {

        Bundle args = new Bundle();
        final Intent intent = new Intent(activity, FileAudioActivity.class);

        intent.putExtra(EXTRA_IS_ONLINE, isOnline);
        intent.putExtra(EXTRA_FILE_CURRENT_POSITION, currentPosition);
        intent.putExtra(EXTRA_FILES_PATH, new ArrayList<>(fileMusicPath));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && animationView != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, animationView, "transitionIcon");
            args = options.toBundle();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.startActivity(intent, args);
        } else {
            activity.startActivity(intent);
        }
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void resume(final Context context) {
        final Intent intent = new Intent(context, FileAudioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View, player and ChromeCast.
        setContentView(R.layout.activity_file_audio);
        mFileAudioPlayer = FileApp.get().getFileAppComponent().provideFileAudioPlayer();
        mFileAudioPlayer.registerOnPlayerStatusChangeListener(this);

        mFileAudioCast.onCreate(this);

        if (savedInstanceState == null) {
            mFirstStart = true;
        }

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

        final Window window = getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.notifications_bar_audio));
        }

        mTitleTextView = (TextView) findViewById(R.id.title);
        mSizeTextView = (TextView) findViewById(R.id.size);
        mSliderNumber = (Slider) findViewById(R.id.sliderNumber);
        mSliderNumber.setValueToDisplay(new Slider.ValueToDisplay() {
            @Override
            public String convert(int value) {
                return getTimeStr(value);
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

        mPlayPauseView = (PlayPauseView) findViewById(R.id.activity_file_audio_play);
        mPlayPauseView.setOnClickListener(this);
        findViewById(R.id.activity_file_audio_next).setOnClickListener(this);
        findViewById(R.id.activity_file_audio_previous).setOnClickListener(this);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null &&
                bundle.containsKey(EXTRA_IS_ONLINE) &&
                bundle.containsKey(EXTRA_FILE_CURRENT_POSITION) &&
                bundle.containsKey(EXTRA_FILES_PATH)) {

            // Get data
            mIsOnline = bundle.getBoolean(EXTRA_IS_ONLINE);
            mCurrentPosition = bundle.getInt(EXTRA_FILE_CURRENT_POSITION);
            final List<String> absolutePathArray = bundle.getStringArrayList(EXTRA_FILES_PATH);
            if (absolutePathArray != null) {
                for (String absolutePath : absolutePathArray) {
                    if (mIsOnline) {
                        mFileAudioModelList.add(
                                ((FileAudioModel.FileMusicModelBuilder) (
                                        new FileAudioModel.FileMusicModelBuilder()
                                                .isOnline(true)
                                                .url(absolutePath)))
                                        .build());
                    } else {
                        mFileAudioModelList.add(
                                new FileAudioModel.FileMusicModelBuilder()
                                        .file(new File(absolutePath)).build());
                    }
                }
            }

            if (mFileAudioModelList.isEmpty()) {
                Toast.makeText(this, "Oops, I have an empty list :(", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (mFirstStart) {
                mFileAudioPlayer.startMusic(mCurrentPosition, mFileAudioModelList);
                mFileAudioCast.startMusic(mCurrentPosition, mFileAudioModelList);
            } else {
                syncSongs(mFileAudioPlayer.getCurrentMusicIndex(), mFileAudioPlayer.getFileAudioModelList());
            }
        }

        mFirstStart = false;
    }

    @Override
    protected void onDestroy() {
        mFileAudioPlayer.unregisterOnPreviewPlayerStatusChangeListener(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFileAudioCast.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            mFileAudioCast.onPause();
        }
    }

    @Override
    public void onClick(final View v) {
        final int idView = v.getId();
        switch (idView) {
            case R.id.activity_file_audio_play:
                if (mFileAudioPlayer.isPlaying()) {
                    mFileAudioPlayer.pause();
                } else {
                    mFileAudioPlayer.play();
                }
                break;
            case R.id.activity_file_audio_next:
                mFileAudioPlayer.next();
                break;
            case R.id.activity_file_audio_previous:
                mFileAudioPlayer.previous();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_activity, menu);
        mFileAudioCast.onCreateOptionsMenu(menu.findItem(R.id.action_cast));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivity();
                return true;
            case R.id.action_cast:
                mFileAudioCast.onCreateOptionsMenu(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPlayerStatusChanged(@SharedAudioPlayerUtils.Status final int status) {
        switch (status) {
            case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED:
                if (!mPlayPauseView.isPlay() && !mPlayPauseView.isAnimationPlaying()) {
                    mPlayPauseView.toggle();
                }
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING:
                if (mPlayPauseView.isPlay() && !mPlayPauseView.isAnimationPlaying()) {
                    mPlayPauseView.toggle();
                }
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PREPARING:
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_UNKNOWN:
                break;
        }
    }

    @Override
    public void onPlayerProgressChanged(final int progress, final int duration) {
        mSliderNumber.setProgress(progress);
        mSliderNumber.setMax(duration);
        mSizeTextView.setText(String.format("%s / %s", getTimeStr(progress), getTimeStr(duration)));
    }

    @Override
    public void onAudioChanged(final int musicPosition, final List<FileAudioModel> musics) {
        Preconditions.checkNotNull(musics);
        syncSongs(musicPosition, musics);
    }

    public void finishActivity() {
        supportFinishAfterTransition();
    }

    private String getTimeStr(final long milliseconds) {
        final long minutes = milliseconds / 60_000;
        final long seconds = (milliseconds / 1_000) % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    private void syncSongs(final int musicPosition, final List<FileAudioModel> musics) {
        Preconditions.checkNotNull(musics);
        if (musics.isEmpty()) {
            finish();
            return;
        }
        mCurrentPosition = musicPosition;
        mFileAudioModelList.clear();
        mFileAudioModelList.addAll(musics);
        mTitleTextView.setText(musics.get(musicPosition).getName());
    }
}
