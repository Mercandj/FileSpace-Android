package com.mercandalli.android.apps.files.file.audio;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mercandalli.android.apps.files.main.FileApp;

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

    /**
     * The action close.
     */
    private static final String ACTION_CLOSE = "NotificationAudioPlayerReceiver.Actions.ACTION_CLOSE";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final FileAudioPlayer fileAudioPlayer = FileApp.get(context).getFileAppComponent().provideFileAudioPlayer();
        if (fileAudioPlayer.isEmpty()) {
            fileAudioPlayer.setNotification(false);
            return;
        }
        switch (action) {
            case ACTION_PLAY_PAUSE:
                if (fileAudioPlayer.isPlaying()) {
                    fileAudioPlayer.pause();
                } else {
                    fileAudioPlayer.play();
                }
                break;
            case ACTION_NEXT:
                fileAudioPlayer.next();
                break;
            case ACTION_PREVIOUS:
                fileAudioPlayer.previous();
                break;
            case ACTION_CLOSE:
                if (fileAudioPlayer.isPlaying()) {
                    fileAudioPlayer.pause();
                }
                break;
        }
    }

    public static PendingIntent getNotificationIntentPlayPause(Context context) {
        return getPendingIntent(context, ACTION_PLAY_PAUSE);
    }

    public static PendingIntent getNotificationIntentPrevious(Context context) {
        return getPendingIntent(context, ACTION_PREVIOUS);
    }

    public static PendingIntent getNotificationIntentNext(Context context) {
        return getPendingIntent(context, ACTION_NEXT);
    }

    public static PendingIntent getNotificationIntentClose(Context context) {
        return getPendingIntent(context, ACTION_CLOSE);
    }

    private static PendingIntent getPendingIntent(Context context, String action) {
        final Intent intent = new Intent(context, NotificationAudioPlayerReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
