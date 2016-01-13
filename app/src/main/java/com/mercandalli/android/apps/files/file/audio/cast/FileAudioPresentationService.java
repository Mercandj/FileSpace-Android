package com.mercandalli.android.apps.files.file.audio.cast;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.List;

/**
 * A simple {@link CastRemoteDisplayLocalService} to display
 * {@link com.mercandalli.android.apps.files.file.audio.FileAudioModel}.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class FileAudioPresentationService extends CastRemoteDisplayLocalService {

    private static final String TAG = "FileAudioPresentSer";

    /**
     * A {@link Presentation}
     */
    private FileAudioPresentation mPresentation;

    @Override
    public void onCreatePresentation(Display display) {
        createPresentation(display);
    }

    @Override
    public void onDismissPresentation() {
        dismissPresentation();
    }

    private void dismissPresentation() {
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    private void createPresentation(Display display) {
        dismissPresentation();
        mPresentation = new FileAudioPresentation(this, display);
        try {
            mPresentation.show();
        } catch (WindowManager.InvalidDisplayException ex) {
            Log.e(TAG, "Unable to show presentation, display was removed.", ex);
            dismissPresentation();
        }
    }

    public void startMusic(int currentMusicIndex, List<FileAudioModel> fileAudioModelList) {
        mPresentation.startMusic(currentMusicIndex, fileAudioModelList);
    }

    /**
     * A simple {@link CastPresentation} to display
     * {@link com.mercandalli.android.apps.files.file.audio.FileAudioModel}.
     */
    private final class FileAudioPresentation extends CastPresentation {

        private TextView mTitleTextView;
        private ProgressBar mProgressBar;

        public FileAudioPresentation(Context context, Display display) {
            super(context, display);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.view_cast_audio);
            mTitleTextView = (TextView) findViewById(R.id.view_cast_audio_title);
            mProgressBar = (ProgressBar) findViewById(R.id.view_cast_audio_progress_bar);
        }

        public void startMusic(int currentMusicIndex, List<FileAudioModel> fileAudioModelList) {
            Preconditions.checkNotNull(fileAudioModelList);
            if (currentMusicIndex < fileAudioModelList.size()) {
                mProgressBar.setVisibility(View.GONE);
                mTitleTextView.setText(fileAudioModelList.get(currentMusicIndex).getFullName());
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }
}
