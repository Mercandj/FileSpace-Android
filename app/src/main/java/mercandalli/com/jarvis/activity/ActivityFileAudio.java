package mercandalli.com.jarvis.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.net.Base64;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class ActivityFileAudio extends Application {

    private String login, password;
    private boolean online;

    private ModelFile file;
    private List<ModelFile> files;

    private SeekBar seekBar;
    private ImageButton play;
    private TextView title, size;
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

    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // Play the next song
            if(files!=null) {
                boolean idMark = false;
                for (ModelFile f:files) {
                    if(idMark && f.isAudio()) {
                        ActivityFileAudio.this.file = f;
                        start();
                        return;
                    }
                    if(f.equals(file))
                        idMark = true;
                }
                for (ModelFile f:files) {
                    if(f.isAudio()) {
                        ActivityFileAudio.this.file = f;
                        start();
                        return;
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file_audio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.actionbar_audio));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.notifications_bar_audio));

        this.title = (TextView) this.findViewById(R.id.title);
        this.size = (TextView) this.findViewById(R.id.size);
        this.seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        this.play = (ImageButton) this.findViewById(R.id.play);
        this.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityFileAudio.this.player!=null) {
                    if(ActivityFileAudio.this.player.isPlaying()) {
                        play.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                        ActivityFileAudio.this.player.pause();
                    }
                    else {
                        play.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                        ActivityFileAudio.this.player.start();
                    }
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e(""+getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }
        else {
            this.login = extras.getString("LOGIN");
            this.password = extras.getString("PASSWORD");
            this.online = extras.getBoolean("ONLINE");
            this.file = (ModelFile) extras.getParcelable("FILE");
            this.files = (ArrayList) extras.getParcelableArrayList("FILES");
            start();
        }
    }

    public void start() {
        try {
            Uri uri = Uri.parse((this.online) ? file.onlineUrl : file.url);

            this.player = new MediaPlayer();
            this.player.setOnCompletionListener(this.onCompletion);
            this.seekBar.setOnSeekBarChangeListener(this.seekBarChanged);

            if(this.online) {
                Map<String, String> headers = new HashMap<String, String>();
                StringBuilder authentication = new StringBuilder().append(this.login).append(":").append(this.password);
                String result = Base64.encodeBytes(authentication.toString().getBytes());
                headers.put("Authorization", "Basic " + result);
                this.player.setDataSource(this, uri, headers);
            }
            else
                this.player.setDataSource(this, uri);

            this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.player.prepare();
            this.player.start();

            this.seekBar.setProgress(0);
            this.seekBar.setMax(this.player.getDuration());
            this.title.setText(""+this.file.name);
            this.size.setText(""+(this.player.getDuration()/60000)+":"+((this.player.getDuration()-((this.player.getDuration()/60000)*60000))/1000));

            updatePosition();

        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
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
