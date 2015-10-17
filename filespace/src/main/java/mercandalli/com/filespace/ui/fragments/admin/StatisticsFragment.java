package mercandalli.com.filespace.ui.fragments.admin;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mercandalli.com.filespace.extras.physics.MyGLSurfaceView;
import mercandalli.com.filespace.ui.activities.ApplicationDrawerActivity;
import mercandalli.com.filespace.ui.fragments.BackFragment;

import mercandalli.com.filespace.R;

/**
 * Created by Jonathan on 02/09/2015.
 */
public class StatisticsFragment extends BackFragment {

    private View rootView;
    public MyGLSurfaceView mGLView;
    public SensorManager mSensorManager;
    public Sensor mRotation;

    public static String info = "";
    public static int progress = 0;
    public static int progress_length = 100;

    public static Button forward, back, left, right;

    public static StatisticsFragment newInstance() {
        Bundle args = new Bundle();
        StatisticsFragment fragment = new StatisticsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (ApplicationDrawerActivity) activity;
    }

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
        forward.setOnTouchListener(new View.OnTouchListener() {
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
        back.setOnTouchListener(new View.OnTouchListener() {
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
        left.setOnTouchListener(new View.OnTouchListener() {
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
        right.setOnTouchListener(new View.OnTouchListener() {
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
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }
}
