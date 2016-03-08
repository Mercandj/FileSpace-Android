package com.mercandalli.android.apps.files;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.mercandalli.android.apps.files.shared.SharedAudioData;
import com.mercandalli.android.apps.files.shared.SharedAudioPlayerUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A {@link WearableActivity} used to control the phone or tablet audio player.
 */
public class AudioActivity extends WearableActivity implements View.OnClickListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private ProgressBar mProgressBar;
    private ImageView mPlayPauseImageView;
    private ImageView mNextImageView;
    private ImageView mPreviousImageView;

    private GoogleApiClient mGoogleApiClient;
    private String mTelNodeId;

    private SharedAudioData mSharedAudioData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        setAmbientEnabled();
        findViews();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        retrieveDeviceNode();

        // Register the local broadcast receiver, defined in step 3.
        final IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        final MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    @Override
    public void onClick(View v) {
        if (mSharedAudioData == null) {
            return;
        }
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.activity_audio_play_pause:
                sendToPhone(mSharedAudioData.getTogglePlayPauseOrder());
                break;
            case R.id.activity_audio_previous:
                sendToPhone(SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PREVIOUS);
                break;
            case R.id.activity_audio_next:
                sendToPhone(SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_NEXT);
                break;
        }
    }

    private void sendToPhone(@SharedAudioPlayerUtils.Action int order) {
        if (mSharedAudioData != null) {
            mSharedAudioData.setAction(order);
            WearableService.sendPhoneAudioData(mGoogleApiClient, mTelNodeId, mSharedAudioData);
        }
    }

    private void findViews() {
        mContainerView = (BoxInsetLayout) findViewById(R.id.activity_audio_container);
        mTextView = (TextView) findViewById(R.id.activity_audio_title);
        mClockView = (TextView) findViewById(R.id.activity_audio_clock);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_main_progress_bar);
        mPlayPauseImageView = (ImageView) findViewById(R.id.activity_audio_play_pause);
        mPreviousImageView = (ImageView) findViewById(R.id.activity_audio_previous);
        mNextImageView = (ImageView) findViewById(R.id.activity_audio_next);

        mPlayPauseImageView.setOnClickListener(this);
        mPreviousImageView.setOnClickListener(this);
        mNextImageView.setOnClickListener(this);
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(Color.WHITE);
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(Color.BLACK);
            mClockView.setVisibility(View.GONE);
        }
    }

    private void syncControlVisibility(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(View.GONE);
            mPlayPauseImageView.setVisibility(View.VISIBLE);
            mPreviousImageView.setVisibility(View.VISIBLE);
            mNextImageView.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mPlayPauseImageView.setVisibility(View.GONE);
            mPreviousImageView.setVisibility(View.GONE);
            mNextImageView.setVisibility(View.GONE);
        }
    }

    private void retrieveDeviceNode() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mGoogleApiClient.blockingConnect(WearableService.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                final NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                final List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    mTelNodeId = nodes.get(0).getId();
                }
                mGoogleApiClient.disconnect();

                WearableService.askPhoneToBeNotified(mGoogleApiClient, mTelNodeId);
            }
        }).start();
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSharedAudioData = new SharedAudioData(intent.getStringExtra("message"));
            switch (mSharedAudioData.getStatus()) {
                case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED:
                    mPlayPauseImageView.setImageResource(R.drawable.ic_play_arrow_white_18dp);
                    syncControlVisibility(true);
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING:
                    mPlayPauseImageView.setImageResource(R.drawable.ic_pause_white_18dp);
                    syncControlVisibility(true);
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PREPARING:
                    syncControlVisibility(false);
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_UNKNOWN:
                    syncControlVisibility(false);
                    break;
            }
            final StringBuilder text = new StringBuilder();

            final String title = mSharedAudioData.getTitle();
            if (title != null) {
                text.append(title).append("\n");
            }
            final String artist = mSharedAudioData.getArtist();
            if (artist != null) {
                text.append(artist).append("\n");
            }
            final String album = mSharedAudioData.getAlbum();
            if (album != null) {
                text.append(album).append("\n");
            }
            mTextView.setText(text.toString());
        }
    }
}
