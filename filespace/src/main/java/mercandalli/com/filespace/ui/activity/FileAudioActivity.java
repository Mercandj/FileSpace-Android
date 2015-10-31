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
package mercandalli.com.filespace.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
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

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.model.file.FileMusicModel;
import mercandalli.com.filespace.service.AudioService;
import mercandalli.com.filespace.ui.view.PlayPauseView;
import mercandalli.com.filespace.ui.view.slider.Slider;

public class FileAudioActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection, AudioService.ViewUpdater {

    private static final String EXTRA_IS_ONLINE = "FileAudioActivity.Extra.EXTRA_IS_ONLINE";
    private static final String EXTRA_FILE_CURRENT_POSITION = "FileAudioActivity.Extra.EXTRA_FILE_CURRENT_POSITION";
    private static final String EXTRA_FILES_PATH = "FileAudioActivity.Extra.EXTRA_FILES_PATH";

    private boolean mIsOnline;

    private int mCurrentPosition;
    private final List<FileMusicModel> mFileMusicModelList = new ArrayList<>();

    private Slider mSliderNumber;
    private TextView mTitleTextView;
    private TextView mSizeTextView;
    private PlayPauseView mPlayPauseView;

    private AudioService mAudioService;
    private Intent mPlayIntent;
    private boolean mMusicBound;

    public static void startLocal(Activity activity, final int currentPosition, final List<String> fileMusicPath, final View animationView) {
        Bundle args = new Bundle();
        final Intent intent = new Intent(activity, FileAudioActivity.class);

        intent.putExtra(EXTRA_IS_ONLINE, true);
        intent.putExtra(EXTRA_FILE_CURRENT_POSITION, currentPosition);
        intent.putExtra(EXTRA_FILES_PATH, new ArrayList<>(fileMusicPath));//fileMusicPath.toArray());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && animationView != null) {
            Pair<View, String> sharedPictureElt = Pair.create(animationView, "transitionIcon");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedPictureElt);
            args = options.toBundle();
        }

        activity.startActivity(intent, args);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, AudioService.class);
            bindService(mPlayIntent, this, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_audio);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.actionbar_audio));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("FileSpace - Audio");
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.notifications_bar_audio));
        }

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
                if (mAudioService != null) {
                    mAudioService.seekTo(value);
                }
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
                    mFileMusicModelList.add(new FileMusicModel.FileMusicModelBuilder().file(new File(absolutePath)).build());
                }
            }
        } else {
            throw new IllegalArgumentException("Use static start() method");
        }

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        AudioService.MusicBinder binder = (AudioService.MusicBinder) service;
        mAudioService = binder.getService();
        mAudioService.setList(mFileMusicModelList);
        mAudioService.setViewUpdater(this);
        mMusicBound = true;

        if (!mAudioService.isPlaying()) {
            play();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mMusicBound = false;
    }

    @Override
    protected void onDestroy() {
        stopService(mPlayIntent);
        mAudioService = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        final int idView = v.getId();
        switch (idView) {
            case R.id.play:
                if (isPlaying()) {
                    pause();
                } else {
                    start();
                }
                mPlayPauseView.toggle();
                break;
            case R.id.next:
                next();
                break;
            case R.id.previous:
                previous();
                break;
        }
    }

    private boolean isPlaying() {
        return mAudioService.isPlaying();
    }

    private void start() {
        mAudioService.start();
    }

    private void play() {
        mAudioService.setPlayingIndex(mCurrentPosition);
        mAudioService.play();
    }

    /**
     * Pauses playback. Call start() to resume.
     */
    private void pause() {
        mAudioService.pause();
    }

    private void next() {
        mAudioService.next();
    }

    private void previous() {
        mAudioService.previous();
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
    public void updateViewAudioService(int playingIndex) {
        mCurrentPosition = playingIndex;
        final FileMusicModel currentMusicModel = mFileMusicModelList.get(mCurrentPosition);
        mSliderNumber.setProgress(mAudioService.getCurrentPosition());
        mSliderNumber.setMax(mAudioService.getDuration());
        mTitleTextView.setText(currentMusicModel.getName());
        mSizeTextView.setText(getTimeStr(mAudioService.getCurrentPosition()) + " / " + getTimeStr(mAudioService.getDuration()));
    }
}
