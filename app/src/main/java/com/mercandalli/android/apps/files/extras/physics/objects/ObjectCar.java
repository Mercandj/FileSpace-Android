package com.mercandalli.android.apps.files.extras.physics.objects;

import android.content.Context;

import com.mercandalli.android.apps.files.extras.physics.lib.Vector3D;

public class ObjectCar extends Object3D {

    public Vector3D forward;
    public float angleY = 0;

    public ObjectCar(Context context, Vector3D forward) {
        super(context);
        this.forward = forward;
    }

    public void updateForward() {
        this.forward.dX = (float) Math.cos(this.angleY);
        this.forward.dY = 0;
        this.forward.dZ = (float) Math.sin(this.angleY);
        this.forward.normalize();
    }
}
