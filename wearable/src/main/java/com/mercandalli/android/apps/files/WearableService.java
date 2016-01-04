package com.mercandalli.android.apps.files;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.mercandalli.android.apps.files.shared.SharedAudioData;
import com.mercandalli.android.apps.files.shared.SharedAudioPlayerUtils;

import java.util.concurrent.TimeUnit;

/**
 * The listener to communicate with the phone or tablet.
 */
public class WearableService extends WearableListenerService {

    public static final long CONNECTION_TIME_OUT_MS = 5000;

    public static void sendPhoneAudioData(final GoogleApiClient client, final String telNodeId, final SharedAudioData sharedAudioData) {
        if (telNodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, telNodeId,
                            SharedAudioPlayerUtils.sendTrackData(sharedAudioData),
                            null);
                    client.disconnect();
                }
            }).start();
        }
    }

    /**
     * Ask to Phone/Tablet to be notified. Then will call {@link #onMessageReceived(MessageEvent)} if
     * the Phone/Tablet is connected.
     *
     * @param client    The {@link GoogleApiClient}.
     * @param telNodeId The Phone/Tablet id.
     */
    public static void askPhoneToBeNotified(final GoogleApiClient client, final String telNodeId) {
        if (telNodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, telNodeId,
                            " ",
                            null);
                    client.disconnect();
                }
            }).start();
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        forwardMessageToActivity(messageEvent.getPath());
    }

    /**
     * Broadcast message to wearable activity.
     *
     * @param message The message.
     */
    private void forwardMessageToActivity(String message) {
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
    }
}