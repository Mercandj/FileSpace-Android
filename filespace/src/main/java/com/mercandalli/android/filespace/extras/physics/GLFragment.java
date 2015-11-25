/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.filespace.extras.physics;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.mercandalli.android.filespace.R;

/**
 * OpenGL Fragment : display GLSurfaceView
 *
 * @author Jonathan
 */
public class GLFragment extends Fragment implements SensorEventListener {

    public MyGLSurfaceView mGLView;
    public SensorManager mSensorManager;
    public Sensor mRotation;

    public static String info = "";
    public static int progress = 0;
    public static int progress_length = 100;

    public View rootView;

    public static Button forward, back, left, right;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.glview, container, false);
        mGLView = (MyGLSurfaceView) rootView.findViewById(R.id.GLview);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mGLView.setDensity(displayMetrics.density);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


        forward = (Button) rootView.findViewById(R.id.forward);
        forward.setVisibility(View.GONE);
        forward.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    mGLView.mRenderer.camera.forward = false;
                else if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mGLView.mRenderer.camera.forward = true;
                return false;
            }
        });

        back = (Button) rootView.findViewById(R.id.back);
        back.setVisibility(View.GONE);
        back.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    mGLView.mRenderer.camera.back = false;
                else if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mGLView.mRenderer.camera.back = true;
                return false;
            }
        });

        left = (Button) rootView.findViewById(R.id.left);
        left.setVisibility(View.GONE);
        left.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    mGLView.mRenderer.camera.left = false;
                else if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mGLView.mRenderer.camera.left = true;
                return false;
            }
        });

        right = (Button) rootView.findViewById(R.id.right);
        right.setVisibility(View.GONE);
        right.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    mGLView.mRenderer.camera.right = false;
                else if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mGLView.mRenderer.camera.right = true;
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.v("mysensors: ", Float.toString(event.values[0]) +","+ Float.toString(event.values[1]) +","+ Float.toString(event.values[2]));
        //((MyGLSurfaceView)mGLView).mRenderer.myobj.color[0]= lux;
        if ((((MyGLSurfaceView) mGLView).mRenderer).camera.mEye != null) {
            /*(((MyGLSurfaceView)mGLView).mRenderer.mEye.dX) += (event.values[0]/1000.0);
            (((MyGLSurfaceView)mGLView).mRenderer.mEye.dY) += (event.values[1]/1000.0);
			(((MyGLSurfaceView)mGLView).mRenderer.mEye.dZ) += (event.values[2]/1000.0);*/
            //(((MyGLSurfaceView)mGLView).mRenderer.myobj).translateM((float)(event.values[0]/10.0), 0, (float)(event.values[2]/10.0));

            //SensorManager.getRotationMatrixFromVector(((MyGLSurfaceView)mGLView).mRenderer.myobj.transformationMatrix, event.values);
            //((WindowManager) getSystemService(WINDOW_SERVICE).mWindowManager.getDefaultDisplay()).;
            ((MyGLSurfaceView) mGLView).requestRender();
        }
    }
}
