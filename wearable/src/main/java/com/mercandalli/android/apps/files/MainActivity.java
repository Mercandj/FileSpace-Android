package com.mercandalli.android.apps.files;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * The wear main {@link WearableActivity}. Select the action.
 */
public class MainActivity extends WearableActivity implements View.OnClickListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    /**
     * The title.
     */
    private TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        findViews();
        initViews();
    }

    @Override
    public void onClick(final View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.activity_main_audios:
                onAudioClicked();
                break;
            default:
                Toast.makeText(this, "Upcoming feature", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Find the different {@link View}s.
     */
    private void findViews() {
        mTitleTextView = (TextView) findViewById(R.id.activity_main_title);
        (findViewById(R.id.activity_main_files)).setOnClickListener(this);
        (findViewById(R.id.activity_main_audios)).setOnClickListener(this);
        (findViewById(R.id.activity_main_photos)).setOnClickListener(this);
    }

    /**
     * Initialize the different {@link View}s.
     */
    private void initViews() {
        mTitleTextView.setText(String.format("%s - %s", getString(R.string.app_name), BuildConfig.VERSION_NAME));
    }

    /**
     * The audio button is clicked.
     */
    private void onAudioClicked() {
        final Intent startIntent = new Intent(this, AudioActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
    }
}
