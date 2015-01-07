package mercandalli.com.jarvis.activity;

import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.net.Base64;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class ActivityFileAudio extends Application {

    private String url, login, password, name;
    private boolean online;


    private SeekBar seekBar;
    private MediaPlayer player;
    private final Handler handler = new Handler();
    private final int UPDATE_FREQUENCY = 1000;
    private boolean isMovingSeekBar = false;

    class UpdaterPosition implements Runnable {
        private boolean kill = false;
        @Override
        public void run() {
            if(!kill)
                updatePosition();
        }
        public void kill() {
            this.kill = true;
        }
    }

    public final UpdaterPosition updatePositionRunnable = new UpdaterPosition();

    private void updatePosition() {
        this.handler.removeCallbacks(updatePositionRunnable);
        this.seekBar.setProgress(player.getCurrentPosition());
        this.handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isMovingSeekBar)
                player.seekTo(progress);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.view_file_audio);
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.drawable.transparent);

        getActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.actionbar_audio)));
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.notifications_bar_audio));

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e(""+getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }
        else {
            this.url = extras.getString("URL_FILE");
            this.login = extras.getString("LOGIN");
            this.password = extras.getString("PASSWORD");
            this.online = extras.getBoolean("ONLINE");
            this.name = extras.getString("NAME");

            ((TextView) this.findViewById(R.id.title)).setText(this.name);
            this.seekBar = (SeekBar) this.findViewById(R.id.seekBar);

            try {
                Uri uri = Uri.parse(this.url);

                player = new MediaPlayer();
                seekBar.setOnSeekBarChangeListener(seekBarChanged);

                if(online) {
                    Map<String, String> headers = new HashMap<String, String>();
                    StringBuilder authentication = new StringBuilder().append(this.login).append(":").append(this.password);
                    String result = Base64.encodeBytes(authentication.toString().getBytes());
                    headers.put("Authorization", "Basic " + result);
                    player.setDataSource(this, uri, headers);
                }
                else
                    player.setDataSource(this, uri);

                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepare();
                player.start();

                seekBar.setProgress(0);
                seekBar.setMax(player.getDuration());

                updatePosition();

            } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void refreshAdapters() {

    }

    @Override
    public void updateAdapters() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            finishActivity();
        return super.onKeyDown(keyCode, event);
    }

    public void finishActivity() {
        if(updatePositionRunnable!=null)
            updatePositionRunnable.kill();
        if(player!=null) {
            player.stop();
            player.reset();
        }
        this.finish();
        this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
