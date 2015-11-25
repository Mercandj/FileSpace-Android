/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.filespace.extras.physics.lib;

import java.text.DecimalFormat;

/**
 * Triplet with vector functions
 *
 * @author Jonathan
 */
public class myVector3D {

    public float dX;
    public float dY;
    public float dZ;

    public myVector3D() {
    }

    public myVector3D(final myVector3D v) {
        this.dX = v.dX;
        this.dY = v.dY;
        this.dZ = v.dZ;
    }

    public myVector3D(float dx, float dy, float dz) {
        this.dX = dx;
        this.dY = dy;
        this.dZ = dz;
    }

    float dot(myVector3D v1) {
        return (v1.dX * dX + v1.dY * dY + v1.dZ * dZ);
    }

    public myVector3D add(myVector3D v1) {
        return this.plus(v1);
    }

    public myVector3D plus(myVector3D v1) {
        return new myVector3D(dX + v1.dX, dY + v1.dY, dZ + v1.dZ);
    }

    public myVector3D sub(myVector3D v1) {
        return new myVector3D(dX - v1.dX, dY - v1.dY, dZ - v1.dZ);
    }

    public myVector3D minus() {
        return new myVector3D(-dX, -dY, -dZ);
    }

    public myVector3D div(float s) {
        return new myVector3D(dX / s, dY / s, dZ / s);
    }

    public myVector3D mult(float s) {
        return new myVector3D(dX * s, dY * s, dZ * s);
    }

    public myVector3D mult(myVector3D s) {
        return new myVector3D(dX * s.dX, dY * s.dY, dZ * s.dZ);
    }

    public myVector3D mult(float x, float y, float z) {
        return new myVector3D(dX * x, dY * y, dZ * z);
    }

    void cross(myVector3D v1, myVector3D v2) {
        dX = v1.dY * v2.dZ - v1.dZ * v2.dY;
        dY = v1.dZ * v2.dX - v1.dX * v2.dZ;
        dZ = v1.dX * v2.dY - v1.dY * v2.dX;
    }

    public myVector3D cross(myVector3D v1) {
        myVector3D result = new myVector3D();
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

    public myVector3D normalizeVector() {
        float l = length();
        return new myVector3D(dX / l, dY = dY / l, dZ = dZ / l);
    }

    public void rotate(myVector3D lp, float theta) {
        //rotate vector this around the line defined by lp through the origin by theta degrees.
        float cos_theta = (float) Math.cos(theta);
        float dot = this.dot(lp);
        myVector3D cross = this.cross(lp);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof myVector3D))
            return false;
        myVector3D obj = ((myVector3D) o);
        return obj.dX == this.dX && obj.dY == this.dY && obj.dZ == this.dZ;
    }

    public void resetAlmostNull(float bound) {
        if (-bound < this.dX || this.dX < bound)
            this.dX = 0;
        if (-bound < this.dY || this.dY < bound)
            this.dY = 0;
        if (-bound < this.dZ || this.dZ < bound)
            this.dZ = 0;
    }
}