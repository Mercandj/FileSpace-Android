package mercandalli.com.filespace.ui.view.game;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Jonathan on 02/09/2015.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public GameThread thread;
    private int x_down, y_down, x_move, y_move, x_up, y_up;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // ecoute les changement de surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // creer le thread : lance surfaceCreated()
        this.thread = new GameThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });

        setKeepScreenOn(true);
        setFocusable(true); // être sur d'avoir les events
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x_move = (int)event.getX(0);
        y_move = (int)event.getY(0);


        // Remet les variables du tactile à zero
        x_down=0; y_down=0; x_move=0; y_move=0; x_up=0; y_up=0;

        // Appel ondraw
        thread.repaint();
        return true;
    } // Fin onTouch

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (thread.getState() == Thread.State.TERMINATED) thread = new GameThread(getHolder(), getContext(), getHandler());
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}