package mercandalli.com.filespace.ui.view.game;

/**
 * Created by Jonathan on 02/09/2015.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;

import java.util.Timer;
import java.util.TimerTask;

public class GameThread extends Thread {

    public Grille grille = new Grille(30);
    public int getValeurCase(int i, int j) {return this.grille.getValeurCase(i, j);}

    private int couleur_non_brouillon_ = Color.rgb(10, 83, 180);
    private int couleur_brouillon_ = Color.rgb(10, 180, 70);

    public static int hauteurPixel, largeurPixel;

    private Paint paint = new Paint();


    private int mCanvasHeight = 1;
    private int mCanvasWidth = 1;
    private boolean mRun = false;
    private SurfaceHolder mSurfaceHolder;
    private int tick;

    public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
        mSurfaceHolder = surfaceHolder;

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                tick++;
                GameThread.this.repaint();
            }
        };
        t.scheduleAtFixedRate(task, 0, 150);
    }

    public void doStart() {
        synchronized (mSurfaceHolder) {
        }
    }

    public void pause() {
        synchronized (mSurfaceHolder) {
        }
    }

    public synchronized void restoreState(Bundle savedState) {
        synchronized (mSurfaceHolder) {
        }
    }

    public void repaint() {
        Canvas c = null;
        try {
            c = mSurfaceHolder.lockCanvas();
            if (c!=null){
                synchronized (mSurfaceHolder) {
                    doDraw(c);				// dessine le canvas
                }
            }
        } finally {
            if (c != null) {
                mSurfaceHolder.unlockCanvasAndPost(c);
            }
        }
    }

    @Override
    public void run() {
        repaint();
    }

    public void setRunning(boolean b) {
        mRun = b;
    }

    public void setSurfaceSize(int width, int height) {
        synchronized (mSurfaceHolder) {
            mCanvasWidth = width;
            mCanvasHeight = height;
        }
    }

    private void doDraw(Canvas canvas) {
        hauteurPixel = mCanvasHeight;
        largeurPixel = mCanvasWidth;

        dessineBackground(canvas);
        dessineMatrices(canvas);
        dessineTableau(canvas);
    }

    private void dessineBackground(Canvas canvas) {
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, largeurPixel, hauteurPixel, paint);
    }

    private void dessineMatrices(Canvas canvas) {
        if (largeurPixel < hauteurPixel) { // Portrait
            int decalageY = (hauteurPixel-largeurPixel)/2;
            dessineMatrice(canvas, largeurPixel / grille.size, 0, decalageY);
        }
        else { // Landscape
            int decalageX = (largeurPixel-hauteurPixel)/2;
            dessineMatrice(canvas, hauteurPixel / grille.size, decalageX, 0);
        }
    }

    private void dessineMatrice(Canvas canvas, int coteCarreau, int decalageX, int decalageY) {
        int hauteur_txt = coteCarreau/2;
        paint.setColor(couleur_non_brouillon_);
        paint.setTextSize(hauteur_txt);
        paint.setFakeBoldText(true);
        for(int i=0;i<grille.size;i++)
            for(int j=0;j<grille.size;j++)
                if(-10<getValeurCase(i,j) && getValeurCase(i,j)<10 && getValeurCase(i,j) != 0) {
                    paint.setColor(getValeurCase(i,j) < 0 ? couleur_brouillon_ : couleur_non_brouillon_);
                    canvas.drawText(""+getValeurCase(i,j),i*coteCarreau+coteCarreau/2-(int)paint.measureText(""+getValeurCase(i,j))/2+decalageX,j*coteCarreau+coteCarreau/2+hauteur_txt/2+decalageY,paint);
                }
    }

    private void dessineTableau(Canvas canvas) {
        if (largeurPixel < hauteurPixel) { // Portrait
            int decalageY = (hauteurPixel-largeurPixel)/2;
            dessineTableau(canvas, largeurPixel/grille.size, 0, decalageY);
        }
        else { // Landscape
            int decalageX = (largeurPixel-hauteurPixel)/2;
            dessineTableau(canvas, hauteurPixel/grille.size, decalageX, 0);
        }
    }

    private void dessineTableau(Canvas canvas, int coteCarreau, int decalageX, int decalageY) {
        paint.setColor(Color.BLACK);
        for (int i = 0; i < grille.size + 1; i++) {
            if ((i) % 3 == 0) paint.setStrokeWidth(4);
            else paint.setStrokeWidth(2);
            canvas.drawLine(decalageX, i*coteCarreau+decalageY, largeurPixel-decalageX, i*coteCarreau+decalageY, paint);
            canvas.drawLine(i*coteCarreau+decalageX, decalageY, i*coteCarreau+decalageX, hauteurPixel-decalageY, paint);
        }
    }

}
