/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.lib;

import java.text.DecimalFormat;

/**
 * Triplet with vector functions
 *
 * @author Jonathan
 */
public class Vector3D {

    public float dX;
    public float dY;
    public float dZ;

    public Vector3D() {
    }

    public Vector3D(final Vector3D v) {
        this.dX = v.dX;
        this.dY = v.dY;
        this.dZ = v.dZ;
    }

    public Vector3D(float dx, float dy, float dz) {
        this.dX = dx;
        this.dY = dy;
        this.dZ = dz;
    }

    float dot(Vector3D v1) {
        return (v1.dX * dX + v1.dY * dY + v1.dZ * dZ);
    }

    public Vector3D add(Vector3D v1) {
        return this.plus(v1);
    }

    public Vector3D plus(Vector3D v1) {
        return new Vector3D(dX + v1.dX, dY + v1.dY, dZ + v1.dZ);
    }

    public Vector3D sub(Vector3D v1) {
        return new Vector3D(dX - v1.dX, dY - v1.dY, dZ - v1.dZ);
    }

    public Vector3D minus() {
        return new Vector3D(-dX, -dY, -dZ);
    }

    public Vector3D div(float s) {
        return new Vector3D(dX / s, dY / s, dZ / s);
    }

    public Vector3D mult(float s) {
        return new Vector3D(dX * s, dY * s, dZ * s);
    }

    public Vector3D mult(Vector3D s) {
        return new Vector3D(dX * s.dX, dY * s.dY, dZ * s.dZ);
    }

    public Vector3D mult(float x, float y, float z) {
        return new Vector3D(dX * x, dY * y, dZ * z);
    }

    void cross(Vector3D v1, Vector3D v2) {
        dX = v1.dY * v2.dZ - v1.dZ * v2.dY;
        dY = v1.dZ * v2.dX - v1.dX * v2.dZ;
        dZ = v1.dX * v2.dY - v1.dY * v2.dX;
    }

    public Vector3D cross(Vector3D v1) {
        Vector3D result = new Vector3D();
        result.cross(this, v1);
        return result;
    }

    public float length() {
        return (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }

    public void normalize() {
        float l = length();
        dX = dX / l;
        dY = dY / l;
        dZ = dZ / l;
    }

    public Vector3D normalizeVector() {
        float l = length();
        return new Vector3D(dX / l, dY = dY / l, dZ = dZ / l);
    }

    public void rotate(Vector3D lp, float theta) {
        //rotate vector this around the line defined by lp through the origin by theta degrees.
        float cos_theta = (float) Math.cos(theta);
        float dot = this.dot(lp);
        Vector3D cross = this.cross(lp);
        dX *= cos_theta;
        dY *= cos_theta;
        dZ *= cos_theta;
        dX += lp.dX * dot * (1.0 - cos_theta);
        dY += lp.dY * dot * (1.0 - cos_theta);
        dZ += lp.dZ * dot * (1.0 - cos_theta);
        dX -= cross.dX * Math.sin(theta);
        dY -= cross.dY * Math.sin(theta);
        dZ -= cross.dZ * Math.sin(theta);
    }

    @Override
    public String toString() {
        DecimalFormat myFormat = new DecimalFormat("0.0");
        return "(" + myFormat.format(dX) + " " + myFormat.format(dY) + " " + myFormat.format(dZ) + ")";
    }

    public void reset() {
        this.dX = 0;
        this.dY = 0;
        this.dZ = 0;
    }

    @Override
    public int hashCode() {
        return (dX + " " + dY + " " + dZ).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vector3D)) {
            return false;
        }
        Vector3D obj = ((Vector3D) o);
        return obj.dX == this.dX && obj.dY == this.dY && obj.dZ == this.dZ;
    }

    public void resetAlmostNull(float bound) {
        if (-bound < this.dX || this.dX < bound) {
            this.dX = 0;
        }
        if (-bound < this.dY || this.dY < bound) {
            this.dY = 0;
        }
        if (-bound < this.dZ || this.dZ < bound) {
            this.dZ = 0;
        }
    }
}