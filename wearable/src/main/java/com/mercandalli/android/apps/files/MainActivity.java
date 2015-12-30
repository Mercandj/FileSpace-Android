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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercandalli.android.apps.files.shared.AudioPlayerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private ProgressBar mProgressBar;
    private ImageView mPlayPauseImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.activity_main_container);
        mTextView = (TextView) findViewById(R.id.activity_main_title);
        mClockView = (TextView) findViewById(R.id.activity_main_clock);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_main_progress_bar);
        mPlayPauseImageView = (ImageView) findViewById(R.id.activity_main_play_pause);

        // Register the local broadcast receiver, defined in step 3.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
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

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            // Display message in UI
            mTextView.setText(message);

            int audioStatus = -1;
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.has(AudioPlayerUtils.WEAR_COMMUNICATION_KEY_STATUS)) {
                    audioStatus = jsonObject.getInt(AudioPlayerUtils.WEAR_COMMUNICATION_KEY_STATUS);
                }
            } catch (JSONException e) {
                Log.e(MainActivity.class.getName(), "Failed to convert Json", e);
            }

            switch (audioStatus) {
                case AudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED:
                    mProgressBar.setVisibility(View.GONE);
                    mPlayPauseImageView.setVisibility(View.VISIBLE);
                    mPlayPauseImageView.setImageResource(R.drawable.ic_play_arrow_white_18dp);
                    break;
                case AudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING:
                    mProgressBar.setVisibility(View.GONE);
                    mPlayPauseImageView.setVisibility(View.VISIBLE);
                    mPlayPauseImageView.setImageResource(R.drawable.ic_pause_white_18dp);
                    break;
                default:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPlayPauseImageView.setVisibility(View.GONE);
            }
        }
    }
}
