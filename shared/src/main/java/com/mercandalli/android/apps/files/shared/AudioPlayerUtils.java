package com.mercandalli.android.apps.files.shared;

import android.support.annotation.IntDef;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AudioPlayerUtils {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AUDIO_PLAYER_STATUS_PAUSED, AUDIO_PLAYER_STATUS_PLAYING, AUDIO_PLAYER_STATUS_PREPARING})
    public @interface Status {
    }

    public static final int AUDIO_PLAYER_STATUS_PAUSED = 0;
    public static final int AUDIO_PLAYER_STATUS_PLAYING = 1;
    public static final int AUDIO_PLAYER_STATUS_PREPARING = 2;

    public static final String WEAR_COMMUNICATION_KEY_STATUS = "audio_status";

    public static String sendToWear(int audioId, String album, String artist, @Status int status) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject file = new JSONObject();
            file.put("id", audioId);
            file.put("album", album);
            file.put("artist", artist);
            jsonObject.put("file", file);
            jsonObject.put(WEAR_COMMUNICATION_KEY_STATUS, status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
