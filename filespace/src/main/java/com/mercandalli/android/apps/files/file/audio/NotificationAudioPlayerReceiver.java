package com.mercandalli.android.apps.files.file.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * A {@link BroadcastReceiver} to receive the {@link FileAudioModel} notification actions.
 * TODO
 */
public class NotificationAudioPlayerReceiver extends BroadcastReceiver {

    /**
     * The action play or pause.
     */
    private static final String ACTION_PLAY_PAUSE = "NotificationAudioPlayerReceiver.Actions.ACTION_PLAY_PAUSE";

    /**
     * The action previous.
     */
    private static final String ACTION_PREVIOUS = "NotificationAudioPlayerReceiver.Actions.ACTION_PREVIOUS";

    /**
     * The action next.
     */
    private static final String ACTION_NEXT = "NotificationAudioPlayerReceiver.Actions.ACTION_NEXT";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
