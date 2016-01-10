package com.mercandalli.android.apps.files.file.audio;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.MenuItem;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mercandalli.android.apps.files.R;

import java.io.IOException;

/**
 * Created by Jonathan on 10/01/2016.
 */
public class FileAudioChromeCast {

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private RemoteMediaPlayer mRemoteMediaPlayer;
    private Cast.Listener mCastClientListener;
    private boolean mWaitingForReconnect = false;
    private boolean mApplicationStarted = false;
    private boolean mVideoIsLoaded;
    private boolean mIsPlaying;

    private Activity mActivity;

    /* package */ void onCreate(final Activity activity) {
        mActivity = activity;
        mMediaRouter = MediaRouter.getInstance(activity.getApplicationContext());
        mMediaRouterCallback = new MediaRouterCallback();
        mCastClientListener = new Cast.Listener() {
            @Override
            public void onApplicationStatusChanged() {
            }

            @Override
            public void onVolumeChanged() {
            }

            @Override
            public void onApplicationDisconnected(int statusCode) {
                teardown();
            }
        };
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(mActivity.getString(R.string.cast_id)))
                .build();
    }

    /* package */ void onCreateOptionsMenu(MenuItem mediaRouteMenuItem) {
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
    }

    /* package */ void onResume() {
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    /* package */ void onPause() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        mActivity = null;
    }

    /* package */ void launchReceiver() {
        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                .builder(mSelectedDevice, mCastClientListener);

        ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks();
        mApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(mConnectionCallbacks)
                .build();

        mApiClient.connect();
    }

    private void initCastClientListener() {
        mCastClientListener = new Cast.Listener() {
            @Override
            public void onApplicationStatusChanged() {
            }

            @Override
            public void onVolumeChanged() {
            }

            @Override
            public void onApplicationDisconnected(int statusCode) {
                teardown();
            }
        };
    }

    private void reconnectChannels(Bundle hint) {
        if ((hint != null) && hint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
            //Log.e( TAG, "App is no longer running" );
            teardown();
        } else {
            try {
                Cast.CastApi.setMessageReceivedCallbacks(mApiClient, mRemoteMediaPlayer.getNamespace(), mRemoteMediaPlayer);
            } catch (IOException e) {
                //Log.e( TAG, "Exception while creating media channel ", e );
            } catch (NullPointerException e) {
                //Log.e( TAG, "Something wasn't reinitialized for reconnectChannels" );
            }
        }
    }

    private void initRemoteMediaPlayer() {
        mRemoteMediaPlayer = new RemoteMediaPlayer();
        mRemoteMediaPlayer.setOnStatusUpdatedListener(new RemoteMediaPlayer.OnStatusUpdatedListener() {
            @Override
            public void onStatusUpdated() {
                MediaStatus mediaStatus = mRemoteMediaPlayer.getMediaStatus();
                mIsPlaying = mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING;
            }
        });

        mRemoteMediaPlayer.setOnMetadataUpdatedListener(new RemoteMediaPlayer.OnMetadataUpdatedListener() {
            @Override
            public void onMetadataUpdated() {
            }
        });
    }

    private void teardown() {
        if (mApiClient != null) {
            if (mApplicationStarted) {
                try {
                    Cast.CastApi.stopApplication(mApiClient);
                    if (mRemoteMediaPlayer != null) {
                        Cast.CastApi.removeMessageReceivedCallbacks(mApiClient, mRemoteMediaPlayer.getNamespace());
                        mRemoteMediaPlayer = null;
                    }
                } catch (IOException e) {
                    //Log.e( TAG, "Exception while removing application " + e );
                }
                mApplicationStarted = false;
            }
            if (mApiClient.isConnected()) {
                mApiClient.disconnect();
            }
            mApiClient = null;
        }
        mSelectedDevice = null;
        mVideoIsLoaded = false;
    }

    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle hint) {
            if (mWaitingForReconnect) {
                mWaitingForReconnect = false;
                reconnectChannels(hint);
            } else {
                try {
                    Cast.CastApi.launchApplication(mApiClient, mActivity.getString(R.string.cast_id), false)
                            .setResultCallback(
                                    new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(
                                                Cast.ApplicationConnectionResult applicationConnectionResult) {
                                            Status status = applicationConnectionResult.getStatus();
                                            if (status.isSuccess()) {
                                                //Values that can be useful for storing/logic
                                                ApplicationMetadata applicationMetadata =
                                                        applicationConnectionResult.getApplicationMetadata();
                                                String sessionId =
                                                        applicationConnectionResult.getSessionId();
                                                String applicationStatus =
                                                        applicationConnectionResult.getApplicationStatus();
                                                boolean wasLaunched =
                                                        applicationConnectionResult.getWasLaunched();

                                                mApplicationStarted = true;
                                                reconnectChannels(null);
                                            }
                                        }
                                    }
                            );
                } catch (Exception ignored) {
                }
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            mWaitingForReconnect = true;
        }
    }

    private class MediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            super.onRouteSelected(router, info);
            initCastClientListener();
            initRemoteMediaPlayer();

            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());

            launchReceiver();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            super.onRouteUnselected(router, info);
            teardown();
            mSelectedDevice = null;
            //mButton.setText(getString(R.string.play_video));
            mVideoIsLoaded = false;
        }
    }

    public void startFileAudio(FileAudioModel fileAudioModel) {
        if (mApiClient != null && mApiClient.isConnected()) {
            MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            mediaMetadata.putString(MediaMetadata.KEY_TITLE, fileAudioModel.getName());

            MediaInfo mediaInfo = new MediaInfo.Builder("http://static1.grsites.com/archive/sounds/birds/birds013.mp3")
                    .setContentType("audio/mpeg")
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(mediaMetadata)
                    .build();
            try {
                mRemoteMediaPlayer.load(mApiClient, mediaInfo, true)
                        .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                            @Override
                            public void onResult(RemoteMediaPlayer.MediaChannelResult mediaChannelResult) {
                                if (mediaChannelResult.getStatus().isSuccess()) {
                                    mVideoIsLoaded = true;
                                }
                            }
                        });
            } catch (Exception e) {
            }
        }
    }
}
