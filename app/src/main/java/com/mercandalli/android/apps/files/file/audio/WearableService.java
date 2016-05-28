package com.mercandalli.android.apps.files.file.audio;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.mercandalli.android.library.base.java.StringUtils;

/**
 * The listener to communicate with the wearable.
 */
public class WearableService extends WearableListenerService {

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        showToast(
                messageEvent.getPath(),
                StringUtils.byteArrayToString(messageEvent.getData()));
    }

    private void showToast(
            final String path,
            final String message) {
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Broadcast message to wearable activity for display
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
    }
}