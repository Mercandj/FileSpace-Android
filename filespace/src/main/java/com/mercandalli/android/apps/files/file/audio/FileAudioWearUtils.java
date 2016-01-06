package com.mercandalli.android.apps.files.file.audio;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.mercandalli.android.apps.files.precondition.Preconditions;
import com.mercandalli.android.apps.files.shared.SharedAudioPlayerUtils;

import java.util.concurrent.TimeUnit;

public class FileAudioWearUtils {

    public static final long CONNECTION_TIME_OUT_MS = 5000;

    public static void sendWearMessage(final GoogleApiClient client, final String watchNodeId, final @SharedAudioPlayerUtils.Status int currentStatus, final FileAudioModel fileAudioModel) {
        Preconditions.checkNotNull(client);
        Preconditions.checkNotNull(fileAudioModel);
        if (watchNodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, watchNodeId,
                            SharedAudioPlayerUtils.sendTrackData(
                                    fileAudioModel.getId(),
                                    fileAudioModel.getName(),
                                    fileAudioModel.getAlbum(),
                                    fileAudioModel.getArtist(),
                                    currentStatus
                            ),
                            null);
                    client.disconnect();
                }
            }).start();
        }
    }
}
