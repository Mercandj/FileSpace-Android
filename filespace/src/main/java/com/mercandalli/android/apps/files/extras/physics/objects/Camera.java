/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.mercandalli.android.apps.files.extras.physics.lib.MyVector3D;

/**
 * "Main" Camera used as the principal view
 *
 * @author Jonathan
 */
public class Camera {

    Context context;

    public boolean forward = false;
    public boolean back = false;
    public boolean left = false;
    public boolean right = false;

    public MyVector3D mEye;
    public MyVector3D mForward;
    public MyVector3D mUp;
    public float fovy, zNear, zFar;

    public Camera(Context context) {
        this.context = context;
    }

    public void init() {
        mEye = new MyVector3D(0, 35, 35);
        mForward = new MyVector3D(0, 0, -1);
        mUp = new MyVector3D(0, 1, 0);
        fovy = 90;
        zNear = 0.1f;
        zFar = 150;
    }

    public void look(float[] mVMatrix) {
        Matrix.setLookAtM(mVMatrix, 0, mEye.dX, mEye.dY, mEye.dZ,
                mEye.dX + mForward.dX, mEye.dY + mForward.dY, mEye.dZ + mForward.dZ,
                mUp.dX, mUp.dY, mUp.dZ);
    }

    float vx;
    float vy;

    public void setView(float dx, float dy) {
        vx += dx;
        vy += dy;
        computeForward();
    }

    public void computeForward() {
        if (mForward == null) {
            return;
        }
        mForward.dX = (float) Math.sin(vx) * (float) Math.cos(vy);
        mForward.dY = (float) Math.sin(vy);
        mForward.dZ = -(float) Math.cos(vx) * (float) Math.cos(vy);
        mForward.normalize();
    }
}
