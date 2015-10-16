/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package mercandalli.com.filespace.extras.physics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Android View use with GLFragment
 * Instantiate renderer and catch touch gesture
 * @author Jonathan
 *
 */
public class MyGLSurfaceView extends GLSurfaceView implements SensorEventListener {

    public final myRenderer mRenderer;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new myRenderer(context, this);        
        
        setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        setRenderer(mRenderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                      
        // super(context, attrs, defStyle);
        // mIcon = context.getResources().getDrawable(R.drawable.icon);
        // mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight());
        
        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        
        
        senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	    senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
	}
    
    private float density;
    public void setDensity(float density) {
    	this.density = density;
    }

    private float mPreviousX;
    private float mPreviousY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private int mActivePointerId = -1;
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	mScaleDetector.onTouchEvent(e);
    	float x, y;
    	final int action = e.getAction(); 
        switch (action & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN: {
	            mPreviousX = e.getX();
	            mPreviousY = e.getY();
	            mActivePointerId = e.getPointerId(0);
	            break;	
	        }        
            case MotionEvent.ACTION_MOVE: {
	                final int pointerIndex = e.findPointerIndex(mActivePointerId);
	                x = e.getX(pointerIndex);
	                y = e.getY(pointerIndex);
	                float dx = x - mPreviousX;
	                float dy = y - mPreviousY;
	                
	                if (!mScaleDetector.isInProgress()) {   
	                	mRenderer.camera.setView(dx/(density*100.0f), -dy/(density*100.0f));
	                	requestRender();
	                }
                mPreviousX = x;
                mPreviousY = y;
                break;
            }
            case MotionEvent.ACTION_UP: {
                mActivePointerId = -1;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = -1;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = e.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    //mPreviousX = e.getX(newPointerIndex);
                    //mPreviousY = e.getY(newPointerIndex);
                    mActivePointerId = e.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor = detector.getScaleFactor();
  
            Log.v("mydebugger", Float.toString(mScaleFactor));
            if (mScaleFactor > 1) mScaleFactor = -1/mScaleFactor;
            mScaleFactor /= -4;
            mScaleFactor *= Math.sqrt(mRenderer.camera.mEye.length())/10; 
            
            //mRenderer.mEye[2] *= (1 + mScaleFactor);
            mRenderer.camera.mEye = mRenderer.camera.mEye.plus(mRenderer.camera.mForward.mult(mScaleFactor));
            
            //mRenderer.mEye[0] += mRenderer.mForwarddirection[0]*mScaleFactor;
            //mRenderer.mEye[1] += mRenderer.mForwarddirection[1]*mScaleFactor;
            //mRenderer.mEye[2] += mRenderer.mForwarddirection[2]*mScaleFactor;

            requestRender();
            return true;
        }
    }
    
    
    
    
    /******** SHAKE **********/
	
	private SensorManager senSensorManager;
	private Sensor senAccelerometer;
	
	private long lastUpdate = 0;
	//private float last_x, last_y, last_z;
	public static float rotationCar;
	
	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;
		
	    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	        float y = sensorEvent.values[1];
	        long curTime = System.currentTimeMillis();
	        
	        if ((curTime - lastUpdate) > 100) {
	            lastUpdate = curTime;	            
	            if(mRenderer!=null)
	            	if(mRenderer.world!=null) {

                    }
	        }
	    }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
}
