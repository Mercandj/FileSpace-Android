package com.mercandalli.android.apps.files.file.audio.cast;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

import com.google.android.gms.cast.CastPresentation;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.library.mainlibrary.precondition.Preconditions;

import java.util.List;

/**
 * A simple {@link CastPresentation} to display
 * {@link com.mercandalli.android.apps.files.file.audio.FileAudioModel}.
 */
/* package */ final class FileAudioPresentation extends CastPresentation {

    private TextView mTitleTextView;
    //private ProgressBar mProgressBar;

    public FileAudioPresentation(Context context, Display display) {
        super(context, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_cast_audio);
        mTitleTextView = (TextView) findViewById(R.id.view_cast_audio_title);
        //mProgressBar = (ProgressBar) findViewById(R.id.view_cast_audio_progress_bar);
    }

    public void startMusic(int currentMusicIndex, List<FileAudioModel> fileAudioModelList) {
        Preconditions.checkNotNull(fileAudioModelList);
        if (currentMusicIndex < fileAudioModelList.size()) {
            //mProgressBar.setVisibility(View.GONE);
            mTitleTextView.setText(fileAudioModelList.get(currentMusicIndex).getFullName());
        } else {
            //mProgressBar.setVisibility(View.VISIBLE);
        }
    }
}
