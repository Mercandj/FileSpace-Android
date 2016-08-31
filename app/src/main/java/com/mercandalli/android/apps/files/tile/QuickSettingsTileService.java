package com.mercandalli.android.apps.files.tile;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.apps.files.file.audio.FileAudioPlayerManager;
import com.mercandalli.android.apps.files.shared.SharedAudioPlayerUtils;
import com.mercandalli.android.apps.files.splash.SplashActivity;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickSettingsTileService extends TileService implements
        FileAudioPlayerManager.OnPlayerStatusChangeListener {

    @Nullable
    private FileAudioPlayerManager mFileAudioPlayerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        syncTile(false);
        mFileAudioPlayerManager = FileAudioPlayerManager.getInstance(getApplicationContext());
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.d("vbarthel", "onClick");
        //getFileAudioPlayer().next();
        syncTile(true);
        SplashActivity.start(getApplicationContext());
        //syncTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.d("vbarthel", "onStartListening");
        getFileAudioPlayerManager().addOnPlayerStatusChangeListener(this);
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        getFileAudioPlayerManager().removeOnPreviewPlayerStatusChangeListener(this);
        Log.d("vbarthel", "onStopListening");
    }

    @Override
    public void onTileAdded() {
        Log.d("vbarthel", "onTileAdded");
        super.onTileAdded();
    }

    @Override
    public boolean onPlayerStatusChanged(@SharedAudioPlayerUtils.Status final int status) {
        return false;
    }

    @Override
    public boolean onPlayerProgressChanged(final int progress, final int duration) {
        return false;
    }

    @Override
    public boolean onAudioChanged(
            final int musicPosition,
            final List<FileAudioModel> musics,
            @SharedAudioPlayerUtils.Action final int action) {
        return false;
    }

    private void syncTile(final boolean active) {
        final Icon loopIcon = getTileIcon();
        final String loopTitle = getTileTitle();
        final int loopTileState = getTileState(active);

        final Tile qsTile = getQsTile();
        if (qsTile != null) {
            qsTile.setLabel(loopTitle);
            qsTile.setIcon(loopIcon);
            qsTile.setState(loopTileState);

            qsTile.updateTile();
        }
    }

    private int getTileState(final boolean active) {
        return active ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;
    }

    @NonNull
    private Icon getTileIcon() {
        return Icon.createWithResource(this, R.drawable.ic_folder_open_white_24dp);
    }

    @NonNull
    private String getTileTitle() {
        return "Open FileSpace";
    }

    @NonNull
    private FileAudioPlayerManager getFileAudioPlayerManager() {
        if (mFileAudioPlayerManager == null) {
            return mFileAudioPlayerManager = FileAudioPlayerManager.getInstance(getApplicationContext());
        }
        return mFileAudioPlayerManager;
    }
}
