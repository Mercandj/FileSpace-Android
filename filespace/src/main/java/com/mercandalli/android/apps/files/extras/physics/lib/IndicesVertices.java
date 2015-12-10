/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.lib;

public class IndicesVertices {
    public float[] vertices;
    public short[] indices;

    public MyVector3D edgeVerticeMin = null;
    public MyVector3D edgeVerticeMax = null;

    public IndicesVertices(float[] vertices, short[] indices) {
        super();
        this.vertices = vertices;
        this.indices = indices;
    }

    public IndicesVertices(float[] vertices, short[] indices, MyVector3D edgeVerticeMin, MyVector3D edgeVerticeMax) {
        super();
        this.vertices = vertices;
        this.indices = indices;
        this.edgeVerticeMin = new MyVector3D(edgeVerticeMin);
        this.edgeVerticeMax = new MyVector3D(edgeVerticeMax);
    }

    public IndicesVertices(IndicesVertices indicesVertices) {
        super();
        vertices = new float[indicesVertices.vertices.length];
        indices = new short[indicesVertices.indices.length];
        int max = Math.max(indicesVertices.vertices.length, indicesVertices.indices.length);
        for (int i = 0; i < max; i++) {
            if (i < indicesVertices.vertices.length) {
                vertices[i] = indicesVertices.vertices[i];
            }
            if (i < indicesVertices.indices.length) {
                indices[i] = indicesVertices.indices[i];
            }
        }
        this.edgeVerticeMin = new MyVector3D(indicesVertices.edgeVerticeMin);
        this.edgeVerticeMax = new MyVector3D(indicesVertices.edgeVerticeMax);
    }
}
