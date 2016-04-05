/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.physics;

import android.content.Context;

import com.mercandalli.android.apps.files.extras.physics.Const;
import com.mercandalli.android.apps.files.extras.physics.MyGLSurfaceView;
import com.mercandalli.android.apps.files.extras.physics.lib.Vector3D;

/**
 * Define the physic's thread behavior
 *
 * @author Jonathan
 */
public class PhysicsEngine {

    final Context context;
    MyGLSurfaceView mGLView;
    public PhysicsThread thread;
    IPhysicsThreadContent threadContent;
    public static boolean actionButton = false;

    public PhysicsEngine(final Context context, final MyGLSurfaceView mGLView) {
        this.context = context;
        this.mGLView = mGLView;

        threadContent = new IPhysicsThreadContent() {

            @Override
            public void execute() {

                // Compute Forces to entities and myObject3D
                mGLView.mRenderer.world.computeForces(mGLView.mRenderer.world);
                // Apply Forces to entities and myObject3D
                mGLView.mRenderer.world.applySumForces(mGLView.mRenderer.world);

                // Bot or repeated moves
                mGLView.mRenderer.world.translateRepetedWayPosition();

                // Camera Controls
                if (mGLView.mRenderer.camera.isForward) {

                    float moveX = mGLView.mRenderer.camera.forwardVector3D.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.6f : 6.0f);
                    if ((moveX < 0 && -Const.LIMIT < mGLView.mRenderer.camera.eyeVector3D.dX) || (moveX > 0 && mGLView.mRenderer.camera.eyeVector3D.dX < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dX += moveX;
                    }

                    float moveY = mGLView.mRenderer.camera.forwardVector3D.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.6f : 6.0f);
                    if ((moveY < 0 && 0 < mGLView.mRenderer.camera.eyeVector3D.dY + moveY) || (moveY > 0 && mGLView.mRenderer.camera.eyeVector3D.dY < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dY += moveY;
                    }

                    float moveZ = mGLView.mRenderer.camera.forwardVector3D.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.6f : 6.0f);
                    if ((moveZ < 0 && -Const.LIMIT < mGLView.mRenderer.camera.eyeVector3D.dZ) || (moveZ > 0 && mGLView.mRenderer.camera.eyeVector3D.dZ < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dZ += moveZ;
                    }

                    mGLView.mRenderer.camera.computeForward();
                } else if (mGLView.mRenderer.camera.isBack) {

                    float moveX = -mGLView.mRenderer.camera.forwardVector3D.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.6f : 6.0f);
                    if ((moveX < 0 && -Const.LIMIT < mGLView.mRenderer.camera.eyeVector3D.dX) || (moveX > 0 && mGLView.mRenderer.camera.eyeVector3D.dX < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dX += moveX;
                    }

                    float moveY = -mGLView.mRenderer.camera.forwardVector3D.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.6f : 6.0f);
                    if ((moveY < 0 && 0 < mGLView.mRenderer.camera.eyeVector3D.dY + moveY) || (moveY > 0 && mGLView.mRenderer.camera.eyeVector3D.dY < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dY += moveY;
                    }

                    float moveZ = -mGLView.mRenderer.camera.forwardVector3D.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.6f : 6.0f);
                    if ((moveZ < 0 && -Const.LIMIT < mGLView.mRenderer.camera.eyeVector3D.dZ) || (moveZ > 0 && mGLView.mRenderer.camera.eyeVector3D.dZ < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dZ += moveZ;
                    }

                    mGLView.mRenderer.camera.computeForward();
                } else if (mGLView.mRenderer.camera.isRight) {

                    Vector3D tmp = mGLView.mRenderer.camera.forwardVector3D.cross(mGLView.mRenderer.camera.upVector3D);

                    float moveX = tmp.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.5f : 6.5f);
                    if ((moveX < 0 && -Const.LIMIT < mGLView.mRenderer.camera.eyeVector3D.dX) || (moveX > 0 && mGLView.mRenderer.camera.eyeVector3D.dX < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dX += moveX;
                    }

                    float moveY = tmp.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.5f : 6.5f);
                    if ((moveY < 0 && 0 < mGLView.mRenderer.camera.eyeVector3D.dY + moveY) || (moveY > 0 && mGLView.mRenderer.camera.eyeVector3D.dY < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dY += moveY;
                    }

                    float moveZ = tmp.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.5f : 6.5f);
                    if ((moveZ < 0 && -Const.LIMIT < mGLView.mRenderer.camera.eyeVector3D.dZ) || (moveZ > 0 && mGLView.mRenderer.camera.eyeVector3D.dZ < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dZ += moveZ;
                    }
                } else if (mGLView.mRenderer.camera.isLeft) {

                    Vector3D tmp = mGLView.mRenderer.camera.upVector3D.cross(mGLView.mRenderer.camera.forwardVector3D);

                    float moveX = tmp.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.5f : 6.5f);
                    if ((moveX < 0 && -Const.LIMIT < mGLView.mRenderer.camera.eyeVector3D.dX) || (moveX > 0 && mGLView.mRenderer.camera.eyeVector3D.dX < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dX += moveX;
                    }

                    float moveY = tmp.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.5f : 6.5f);
                    if ((moveY < 0 && 0 < mGLView.mRenderer.camera.eyeVector3D.dY + moveY) || (moveY > 0 && mGLView.mRenderer.camera.eyeVector3D.dY < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dY += moveY;
                    }

                    float moveZ = tmp.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.5f : 6.5f);
                    if ((moveZ < 0 && -Const.LIMIT < mGLView.mRenderer.camera.eyeVector3D.dZ) || (moveZ > 0 && mGLView.mRenderer.camera.eyeVector3D.dZ < Const.LIMIT)) {
                        mGLView.mRenderer.camera.eyeVector3D.dZ += moveZ;
                    }
                }

                mGLView.requestRender();
            }
        };

        thread = new PhysicsThread(threadContent, PhysicsConst.REAL_LOOP_TIME);
        thread.start();
    }
}
