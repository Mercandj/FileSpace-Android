/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.filespace.extras.physics;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;

import com.mercandalli.android.filespace.extras.physics.implementation.World;
import com.mercandalli.android.filespace.extras.physics.objects.Camera;
import com.mercandalli.android.filespace.extras.physics.physics.PhysicsEngine;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * OpenGL Renderer : instantiate the camera and the world
 * Define what is draw
 *
 * @author Jonathan
 */
public class myRenderer implements GLSurfaceView.Renderer {

    Context context;
    MyGLSurfaceView mGLView;
    PhysicsEngine physicEngine;
    public World world;

    public float mWidth, mHeight;
    private float[] mMVPMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    public Camera camera;
    public long time, fps, tmp_time, tmp_fps; // fps measure

    public myRenderer(Context context, MyGLSurfaceView mGLView) {
        this.context = context;
        this.mGLView = mGLView;
        this.camera = new Camera(context);
        this.world = new World(context, camera);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES30.glClearColor(Const.BACKGROUND_COLOR.x, Const.BACKGROUND_COLOR.y, Const.BACKGROUND_COLOR.z, Const.BACKGROUND_COLOR.w);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_LESS);

        this.world.init();
        this.camera.init();

        physicEngine = new PhysicsEngine(context, mGLView);

        handler.post(new Runnable() { // Access UIThred
            public void run() {
                // TODO
            }
        });
    }

    Handler handler = new Handler();

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);

        camera.look(mVMatrix);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        world.draw(mProjMatrix, mVMatrix);

        GLES30.glFlush();

        // fps measure and buildDisplay
        tmp_fps++;
        if ((tmp_time = (System.currentTimeMillis() - time)) > 1000) {
            handler.post(new Runnable() { // Access UIThred
                public void run() {
                    String txt_display = "";
                    txt_display += "pos" + camera.mEye + " \t";
                    txt_display += "time = " + tmp_time + "\t  fps = " + fps + " fps";
                    GLFragment.info = txt_display;
                }
            });
            fps = tmp_fps;
            tmp_fps = 0;
            time = System.currentTimeMillis();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        Matrix.perspectiveM(mProjMatrix, 0, camera.fovy, ratio, camera.zNear, camera.zFar);
        mWidth = width;
        mHeight = height;
    }
}