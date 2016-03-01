package com.mercandalli.android.apps.files.shared;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedAudioData {

    private static final String TAG = "SharedAudioData";

    private static final String WEAR_KEY_STATUS = "s";
    private static final String WEAR_KEY_ACTION = "a";
    private static final String WEAR_KEY_FILE = "f";
    private static final String WEAR_KEY_FILE_ID = "id";
    private static final String WEAR_KEY_FILE_TITLE = "ti";
    private static final String WEAR_KEY_FILE_ALBUM = "al";
    private static final String WEAR_KEY_FILE_ARTIST = "at";

    private int mId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;

    @SharedAudioPlayerUtils.Status
    private int mStatus;

    @SharedAudioPlayerUtils.Action
    private int mAction = SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_UNKNOWN;

    public SharedAudioData(final String json) {
        try {
            final JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(WEAR_KEY_STATUS)) {
                updateStatus(jsonObject.getInt(WEAR_KEY_STATUS));
            }
            if (jsonObject.has(WEAR_KEY_ACTION)) {
                updateOrder(jsonObject.getInt(WEAR_KEY_ACTION));
            }
            if (jsonObject.has(WEAR_KEY_FILE)) {
                JSONObject file = jsonObject.getJSONObject(WEAR_KEY_FILE);
                if (file.has(WEAR_KEY_FILE_ID)) {
                    mId = file.getInt(WEAR_KEY_FILE_ID);
                }
                if (file.has(WEAR_KEY_FILE_TITLE)) {
                    mTitle = file.getString(WEAR_KEY_FILE_TITLE);
                }
                if (file.has(WEAR_KEY_FILE_ARTIST)) {
                    mArtist = file.getString(WEAR_KEY_FILE_ARTIST);
                }
                if (file.has(WEAR_KEY_FILE_ALBUM)) {
                    mAlbum = file.getString(WEAR_KEY_FILE_ALBUM);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to convert Json", e);
        }
    }

    public SharedAudioData(int id, String title, String artist, String album) {
        mId = id;
        mTitle = title;
        mArtist = artist;
        mAlbum = album;
    }

    @Nullable
    public int getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getArtist() {
        return mArtist;
    }

    @Nullable
    public String getAlbum() {
        return mAlbum;
    }

    @SharedAudioPlayerUtils.Status
    public int getStatus() {
        return mStatus;
    }

    public void setStatus(@SharedAudioPlayerUtils.Status int status) {
        this.mStatus = status;
    }

    @SharedAudioPlayerUtils.Action
    public int getAction() {
        return mAction;
    }

    public void setAction(@SharedAudioPlayerUtils.Action int action) {
        mAction = action;
    }

    @SharedAudioPlayerUtils.Action
    public int getTogglePlayPauseOrder() {
        if (mStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED) {
            return SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PLAY;
        } else {
            return SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PAUSE;
        }
    }

    @NonNull
    public JSONObject toJson() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject file = new JSONObject();
            file.put(WEAR_KEY_FILE_ID, mId);
            file.put(WEAR_KEY_FILE_TITLE, mTitle);
            file.put(WEAR_KEY_FILE_ALBUM, mAlbum);
            file.put(WEAR_KEY_FILE_ARTIST, mArtist);
            jsonObject.put(WEAR_KEY_FILE, file);
            jsonObject.put(WEAR_KEY_STATUS, mStatus);
            jsonObject.put(WEAR_KEY_ACTION, mAction);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void updateStatus(int status) {
        switch (status) {
            case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED:
                mStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED;
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING:
                mStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING;
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PREPARING:
                mStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PREPARING;
                break;
            default:
                mStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_UNKNOWN;
        }
    }

    private void updateOrder(int order) {
        switch (order) {
            case SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_NEXT:
                mAction = SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_NEXT;
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PAUSE:
                mAction = SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PAUSE;
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PLAY:
                mAction = SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PLAY;
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PREVIOUS:
                mAction = SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PREVIOUS;
                break;
            default:
                mAction = SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_UNKNOWN;
        }
    }
}
