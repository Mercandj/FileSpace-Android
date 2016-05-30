package com.mercandalli.android.apps.files.file.video;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.VideoView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.ApplicationActivity;

import java.io.File;

public class FileVideoActivity extends ApplicationActivity {

    @NonNull
    private static final String EXTRA_FILE_VIDEO_PATH = "FileVideoActivity.extra.EXTRA_FILE_VIDEO_PATH";

    public static void startVideo(
            final Activity activity,
            final File file) {
        final Intent intent = new Intent(activity, FileVideoActivity.class);
        intent.putExtra(EXTRA_FILE_VIDEO_PATH, file.getAbsolutePath());
        activity.startActivity(intent);
    }

    @Nullable
    private String mPath;

    @Nullable
    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_video);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPath = extras.getString(EXTRA_FILE_VIDEO_PATH);
        }

        findViews();
        initViews();
    }

    private void findViews() {
        mVideoView = (VideoView) findViewById(R.id.activity_file_video_video_view);
    }

    private void initViews() {
        mVideoView.setVideoURI(Uri.fromFile(new File(mPath)));
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // video play complete
            }
        });
        mVideoView.start();
    }
}
