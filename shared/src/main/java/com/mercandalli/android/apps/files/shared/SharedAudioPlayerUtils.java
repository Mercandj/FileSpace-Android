package com.mercandalli.android.apps.files.shared;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SharedAudioPlayerUtils {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AUDIO_PLAYER_STATUS_UNKNOWN, AUDIO_PLAYER_STATUS_PAUSED, AUDIO_PLAYER_STATUS_PLAYING, AUDIO_PLAYER_STATUS_PREPARING})
    public @interface Status {
    }

    public static final int AUDIO_PLAYER_STATUS_UNKNOWN = -1;
    public static final int AUDIO_PLAYER_STATUS_PAUSED = 0;
    public static final int AUDIO_PLAYER_STATUS_PLAYING = 1;
    public static final int AUDIO_PLAYER_STATUS_PREPARING = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AUDIO_PLAYER_ORDER_UNKNOWN, AUDIO_PLAYER_ORDER_PAUSE, AUDIO_PLAYER_ORDER_PLAY, AUDIO_PLAYER_ORDER_NEXT, AUDIO_PLAYER_ORDER_PREVIOUS})
    public @interface Order {
    }

    public static final int AUDIO_PLAYER_ORDER_UNKNOWN = -1;
    public static final int AUDIO_PLAYER_ORDER_PAUSE = 0;
    public static final int AUDIO_PLAYER_ORDER_PLAY = 1;
    public static final int AUDIO_PLAYER_ORDER_NEXT = 2;
    public static final int AUDIO_PLAYER_ORDER_PREVIOUS = 3;

    public static String sendTrackData(int audioId, String title, String album, String artist, @Status int status) {
        SharedAudioData sharedAudioData = new SharedAudioData(audioId, title, album, artist);
        sharedAudioData.setStatus(status);
        return sharedAudioData.toJson().toString();
    }

    public static String sendTrackData(final SharedAudioData sharedAudioData) {
        return sharedAudioData.toJson().toString();
    }
}
