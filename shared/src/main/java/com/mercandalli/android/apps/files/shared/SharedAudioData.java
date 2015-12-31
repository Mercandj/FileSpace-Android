package com.mercandalli.android.apps.files.shared;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedAudioData {

    private static final String TAG = "SharedAudioData";
    public static final String WEAR_COMMUNICATION_KEY_STATUS = "audio_status";

    private int mId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;

    @SharedAudioPlayerUtils.Status
    private int mStatus;

    @SharedAudioPlayerUtils.Order
    private int mOrder = SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_UNKNOWN;

    public SharedAudioData(final String json) {
        try {
            final JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(WEAR_COMMUNICATION_KEY_STATUS)) {
                updateStatus(jsonObject.getInt(WEAR_COMMUNICATION_KEY_STATUS));
            }
            if (jsonObject.has("order")) {
                updateOrder(jsonObject.getInt("order"));
            }
            if (jsonObject.has("file")) {
                JSONObject file = jsonObject.getJSONObject("file");
                if (file.has("id")) {
                    mId = file.getInt("id");
                }
                if (file.has("title")) {
                    mTitle = file.getString("title");
                }
                if (file.has("artist")) {
                    mArtist = file.getString("artist");
                }
                if (file.has("album")) {
                    mAlbum = file.getString("album");
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

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

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

    @SharedAudioPlayerUtils.Order
    public int getOrder() {
        return mOrder;
    }

    public void setOrder(@SharedAudioPlayerUtils.Order int order) {
        mOrder = order;
    }

    @SharedAudioPlayerUtils.Order
    public int getTogglePlayPauseOrder() {
        if (mStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED) {
            return SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PLAY;
        } else {
            return SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PAUSE;
        }
    }

    public JSONObject toJson() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject file = new JSONObject();
            file.put("id", mId);
            file.put("title", mTitle);
            file.put("album", mAlbum);
            file.put("artist", mArtist);
            jsonObject.put("file", file);
            jsonObject.put(WEAR_COMMUNICATION_KEY_STATUS, mStatus);
            jsonObject.put("order", mOrder);
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
            case SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_NEXT:
                mOrder = SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_NEXT;
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PAUSE:
                mOrder = SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PAUSE;
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PLAY:
                mOrder = SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PLAY;
                break;
            case SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PREVIOUS:
                mOrder = SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PREVIOUS;
                break;
            default:
                mOrder = SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_UNKNOWN;
        }
    }
}
