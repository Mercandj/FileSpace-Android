package com.mercandalli.android.apps.files.file.audio.cast;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;

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
}
