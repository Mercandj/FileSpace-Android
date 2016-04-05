/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.mercandalli.android.apps.files.extras.physics.lib.Vector3D;

/**
 * "Main" Camera used as the principal view
 *
 * @author Jonathan
 */
public class Camera {

    Context context;

    public boolean isForward = false;
    public boolean isBack = false;
    public boolean isLeft = false;
    public boolean isRight = false;

    public Vector3D eyeVector3D;
    public Vector3D forwardVector3D;
    public Vector3D upVector3D;

    public float fovy, zNear, zFar;

    public Camera(Context context) {
        this.context = context;
    }

    public void init() {
        eyeVector3D = new Vector3D(0, 35, 35);
        forwardVector3D = new Vector3D(0, 0, -1);
        upVector3D = new Vector3D(0, 1, 0);
        fovy = 90;
        zNear = 0.1f;
        zFar = 150;
    }

    public void look(float[] mVMatrix) {
        Matrix.setLookAtM(mVMatrix, 0, eyeVector3D.dX, eyeVector3D.dY, eyeVector3D.dZ,
                eyeVector3D.dX + forwardVector3D.dX, eyeVector3D.dY + forwardVector3D.dY, eyeVector3D.dZ + forwardVector3D.dZ,
                upVector3D.dX, upVector3D.dY, upVector3D.dZ);
    }

    float vx;
    float vy;

    public void setView(float dx, float dy) {
        vx += dx;
        vy += dy;
        computeForward();
    }

    public void computeForward() {
        if (forwardVector3D == null) {
            return;
        }
        forwardVector3D.dX = (float) Math.sin(vx) * (float) Math.cos(vy);
        forwardVector3D.dY = (float) Math.sin(vy);
        forwardVector3D.dZ = -(float) Math.cos(vx) * (float) Math.cos(vy);
        forwardVector3D.normalize();
    }
}
