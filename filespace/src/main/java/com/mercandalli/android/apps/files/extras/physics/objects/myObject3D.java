/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.objects;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.extras.physics.lib.IFunctionEntity;
import com.mercandalli.android.apps.files.extras.physics.lib.IndicesVertices;
import com.mercandalli.android.apps.files.extras.physics.lib.myVector3D;
import com.mercandalli.android.apps.files.extras.physics.physics.Force;
import com.mercandalli.android.apps.files.extras.physics.physics.ForceToEntity;
import com.mercandalli.android.apps.files.extras.physics.physics.PhysicsConst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * "Real" object
 *
 * @author Jonathan
 */
public class myObject3D extends Entity {
    private final String TAG = "myObject3D";

    Context context;

    static String vertexShaderCode;
    static String fragmentShaderCode;

    private float[] vertices;
    private float[] texturecoords;
    private short[] indices;
    private float[] normals, tangents;
    private int[] buffers = new int[5];
    private float color[] = {0.8f, 0.409803922f, 0.498039216f, 1.0f};
    public float[] transformationMatrix = new float[16];

    public myTexture texture, texture_bump;

    private int mProgram;
    private int mPositionHandle;
    private int mTexturecoordsHandle;
    private int mColorHandle;
    private int mNormalHandle;
    private int mTangentHandle;
    private int mMVPMatrixHandle;
    private int mNMatrixHandle;
    private int mMVMatrixHandle;

    IFunctionEntity contactFloorListener;

    public myObject3D(Context context, IFunctionEntity contactFlourListener) {
        this.context = context;
        this.id = ++count;
        if (vertexShaderCode == null) {
            vertexShaderCode = readShaderFromRawResource(R.raw.shader_vertex);
        }
        if (fragmentShaderCode == null) {
            fragmentShaderCode = readShaderFromRawResource(R.raw.shader_fragment);
        }
        Matrix.setIdentityM(transformationMatrix, 0);
        this.contactFloorListener = contactFlourListener;
    }

    public myObject3D(Context context) {
        this.context = context;
        this.id = ++count;
        if (vertexShaderCode == null) {
            vertexShaderCode = readShaderFromRawResource(R.raw.shader_vertex);
        }
        if (fragmentShaderCode == null) {
            fragmentShaderCode = readShaderFromRawResource(R.raw.shader_fragment);
        }
        Matrix.setIdentityM(transformationMatrix, 0);
    }

    private myVector3D computeTangent(int v0, int v1, int v2) {
        float du1 = texturecoords[2 * v1] - texturecoords[2 * v0];
        float dv1 = texturecoords[2 * v1 + 1] - texturecoords[2 * v0 + 1];
        float du2 = texturecoords[2 * v2] - texturecoords[2 * v0];
        float dv2 = texturecoords[2 * v2 + 1] - texturecoords[2 * v0 + 1];

        float f = 1.0f / (du1 * dv2 - du2 * dv1);
        if ((du1 * dv2 - du2 * dv1) == 0) {
            return new myVector3D(0, 0, 0);
        }

        float e1x = vertices[3 * v1] - vertices[3 * v0];
        float e1y = vertices[3 * v1 + 1] - vertices[3 * v0 + 1];
        float e1z = vertices[3 * v1 + 2] - vertices[3 * v0 + 2];

        float e2x = vertices[3 * v2] - vertices[3 * v0];
        float e2y = vertices[3 * v2 + 1] - vertices[3 * v0 + 1];
        float e2z = vertices[3 * v2 + 2] - vertices[3 * v0 + 2];

        return new myVector3D(f * (dv2 * e1x - dv1 * e2x), f * (dv2 * e1y - dv1 * e2y), f * (dv2 * e1z - dv1 * e2z));
    }

    public void computeTangents() {
        int i, j;
        float x1, y1, z1;

        int n = vertices.length / 3;
        int m = indices.length / 3;

        tangents = new float[3 * n];
        int[] incidences = new int[n];
        for (i = 0; i < 3 * n; i++) {
            tangents[i] = 0.0f;
        }
        for (i = 0; i < n; i++) {
            incidences[i] = 0;
        }

        for (j = 0; j < m; j++) {
            myVector3D v = computeTangent(indices[3 * j], indices[3 * j + 1], indices[3 * j + 2]);
            x1 = v.dX;
            y1 = v.dY;
            z1 = v.dZ;
            tangents[3 * indices[3 * j]] += x1;
            tangents[3 * indices[3 * j] + 1] += y1;
            tangents[3 * indices[3 * j] + 2] += z1;
            tangents[3 * indices[3 * j + 1]] += x1;
            tangents[3 * indices[3 * j + 1] + 1] += y1;
            tangents[3 * indices[3 * j + 1] + 2] += z1;
            tangents[3 * indices[3 * j + 2]] += x1;
            tangents[3 * indices[3 * j + 2] + 1] += y1;
            tangents[3 * indices[3 * j + 2] + 2] += z1;
            incidences[indices[3 * j]]++;
            incidences[indices[3 * j + 1]]++;
            incidences[indices[3 * j + 2]]++;
        }
        for (i = 0; i < n; i++) {
            float l = (float) Math.sqrt(tangents[3 * i] * tangents[3 * i] + tangents[3 * i + 1] * tangents[3 * i + 1] + tangents[3 * i + 2] * tangents[3 * i + 2]);
            tangents[3 * i] /= l;
            tangents[3 * i + 1] /= l;
            tangents[3 * i + 2] /= l;
        }
    }

    public void createBuffers() {
        GLES30.glGenBuffers(5, buffers, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[0]);
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer indexBuffer = dlb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 2, indexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[2]);
        bb = ByteBuffer.allocateDirect(normals.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer normalBuffer = bb.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, normalBuffer.capacity() * 4, normalBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[3]);
        bb = ByteBuffer.allocateDirect(texturecoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer texturecoordsBuffer = bb.asFloatBuffer();
        texturecoordsBuffer.put(texturecoords);
        texturecoordsBuffer.position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, texturecoordsBuffer.capacity() * 4, texturecoordsBuffer, GLES30.GL_STATIC_DRAW);

        if (tangents != null) { //TODO

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[4]);
            bb = ByteBuffer.allocateDirect(tangents.length * 4);
            bb.order(ByteOrder.nativeOrder());
            FloatBuffer tagentBuffer = bb.asFloatBuffer();
            tagentBuffer.put(tangents);
            tagentBuffer.position(0);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, tagentBuffer.capacity() * 4, tagentBuffer, GLES30.GL_STATIC_DRAW);
        }

        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES30.glCreateProgram();             // create empty OpenGL Program
        GLES30.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES30.glLinkProgram(mProgram);                  // create OpenGL program executables   	

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void draw(final float[] _mpMatrix, final float[] _mvMatrix) {

        float[] mvpMatrix = new float[16];
        float[] mvMatrix = new float[16];
        Matrix.multiplyMM(mvMatrix, 0, _mvMatrix, 0, transformationMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, _mpMatrix, 0, mvMatrix, 0);

        GLES30.glUseProgram(mProgram);

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[0]);
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 12, 0);

        mNormalHandle = GLES30.glGetAttribLocation(mProgram, "vNormal");
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[2]);
        GLES30.glEnableVertexAttribArray(mNormalHandle);
        GLES30.glVertexAttribPointer(mNormalHandle, 3, GLES30.GL_FLOAT, false, 12, 0);

        int mtexMapHandle = GLES30.glGetUniformLocation(mProgram, "texMap");
        GLES30.glUniform1i(mtexMapHandle, 6);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE6);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture.texName[0]);

        mTexturecoordsHandle = GLES30.glGetAttribLocation(mProgram, "vTexturecoords");
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[3]);
        GLES30.glEnableVertexAttribArray(mTexturecoordsHandle);
        GLES30.glVertexAttribPointer(mTexturecoordsHandle, 2, GLES30.GL_FLOAT, false, 8, 0);

        if (tangents != null) { //TODO
            int isBumpMapping = GLES30.glGetUniformLocation(mProgram, "isBumpMapping");
            GLES30.glUniform1i(isBumpMapping, 1);

            mTangentHandle = GLES30.glGetAttribLocation(mProgram, "vTangent");
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[4]);
            GLES30.glEnableVertexAttribArray(mTangentHandle);
            GLES30.glVertexAttribPointer(mTangentHandle, 3, GLES30.GL_FLOAT, false, 12, 0);

            int mtexMapHandle_bump = GLES30.glGetUniformLocation(mProgram, "texMap_bump");
            GLES30.glUniform1i(mtexMapHandle_bump, 7);
            GLES30.glActiveTexture(GLES30.GL_TEXTURE7);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture_bump.texName[0]);
        }

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        GLES30.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        mMVMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVMatrix");
        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

        mNMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uNMatrix");
        float[] mNMatrix = new float[16];
        Matrix.invertM(mNMatrix, 0, mvMatrix, 0);
        Matrix.transposeM(mNMatrix, 0, Arrays.copyOf(mNMatrix, 16), 0);
        GLES30.glUniformMatrix4fv(mNMatrixHandle, 1, false, mNMatrix, 0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.length, GLES30.GL_UNSIGNED_SHORT, 0);
    }

    void computeNormal(int v1, int v2, int v3, float[] output) {
        double dx1 = vertices[v2 * 3] - vertices[v1 * 3];
        double dx2 = vertices[v3 * 3] - vertices[v2 * 3];
        double dy1 = vertices[v2 * 3 + 1] - vertices[v1 * 3 + 1];
        double dy2 = vertices[v3 * 3 + 1] - vertices[v2 * 3 + 1];
        double dz1 = vertices[v2 * 3 + 2] - vertices[v1 * 3 + 2];
        double dz2 = vertices[v3 * 3 + 2] - vertices[v2 * 3 + 2];

        output[0] = (float) (dy1 * dz2 - dz1 * dy2);
        output[1] = (float) (dz1 * dx2 - dx1 * dz2);
        output[2] = (float) (dx1 * dy2 - dy1 * dx2);

        float length = (float) Math.sqrt(output[0] * output[0] + output[1] * output[1] + output[2] * output[2]);
        if (length <= 0) {
            Log.v(TAG, "normal length zero error!");
            output[0] = output[1] = output[2] = 1.0f;
            return;
        }

        output[0] = output[0] / length;
        output[1] = output[1] / length;
        output[2] = output[2] / length;
    }

    public void computeNormals() {
        int i, j;
        float[] tmp = new float[3];
        float length;

        int n = vertices.length / 3;
        int m = indices.length / 3;

        normals = new float[3 * n];
        int[] incidences = new int[n];
        for (i = 0; i < 3 * n; i++) normals[i] = 0.0f;
        for (i = 0; i < n; i++) incidences[i] = 0;

        for (j = 0; j < m; j++) {
            computeNormal(indices[3 * j], indices[3 * j + 1], indices[3 * j + 2], tmp);
            normals[3 * indices[3 * j]] += tmp[0];
            normals[3 * indices[3 * j] + 1] += tmp[1];
            normals[3 * indices[3 * j] + 2] += tmp[2];
            normals[3 * indices[3 * j + 1]] += tmp[0];
            normals[3 * indices[3 * j + 1] + 1] += tmp[1];
            normals[3 * indices[3 * j + 1] + 2] += tmp[2];
            normals[3 * indices[3 * j + 2]] += tmp[0];
            normals[3 * indices[3 * j + 2] + 1] += tmp[1];
            normals[3 * indices[3 * j + 2] + 2] += tmp[2];
            incidences[indices[3 * j]]++;
            incidences[indices[3 * j + 1]]++;
            incidences[indices[3 * j + 2]]++;
        }
        for (i = 0; i < n; i++) {
            if (incidences[i] != 0) {
                normals[3 * i] /= incidences[i];
            }
            normals[3 * i + 1] /= incidences[i];
            normals[3 * i + 2] /= incidences[i];

            length = (float) Math.sqrt(normals[3 * i] * normals[3 * i] + normals[3 * i + 1] * normals[3 * i + 1] + normals[3 * i + 2] * normals[3 * i + 2]);
            normals[3 * i] /= length;
            normals[3 * i + 1] /= length;
            normals[3 * i + 2] /= length;
        }
    }

    public void readMeshLocal(final IndicesVertices indicesVertices) {
        this.vertices = indicesVertices.vertices;
        this.indices = indicesVertices.indices;
        this.edgeVerticeMin = indicesVertices.edgeVerticeMin;
        this.edgeVerticeMax = indicesVertices.edgeVerticeMax;
    }

    public void computeSphereTexture() {
        int n = vertices.length / 3;
        texturecoords = new float[3 * n];
        double x, y, z;
        for (int i = 0; i < n; i++) {
            x = vertices[3 * i];
            y = vertices[3 * i + 1];
            z = vertices[3 * i + 2];
            if (x == 0 && y == 0 && z == 0) {
                continue;
            }
            double l = Math.sqrt(x * x + y * y + z * z);
            x = x / l;
            y = y / l;
            z = z / l;

            if (-z >= 0.0) {
                texturecoords[2 * i] = (float) (Math.atan2(-z, x) / (2 * Math.PI));
            } else {
                texturecoords[2 * i] = (float) ((2 * Math.PI + Math.atan2(-z, x)) / (2 * Math.PI));
            }

            if (y >= 0.0) {
                texturecoords[2 * i + 1] = (float) (Math.acos(y) / Math.PI);
            } else {
                texturecoords[2 * i + 1] = (float) ((Math.PI - Math.acos(-y)) / Math.PI);
            }
        }
    }

    //sxs grid centered at the origin in the xy plane.
    public void generateGrid(int num, float s) {
        int i, j, k;

        int n = (num + 1) * (num + 1);
        int m = num * num * 2;
        vertices = new float[3 * n];
        indices = new short[3 * m];
        texturecoords = new float[2 * n];

        this.edgeVerticeMin = new myVector3D(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        this.edgeVerticeMax = new myVector3D(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

        k = 0;
        for (i = 0; i <= num; i++) {
            for (j = 0; j <= num; j++) {
                vertices[3 * ((num + 1) * i + j)] = (float) s * i / (float) num - s / 2;
                vertices[3 * ((num + 1) * i + j) + 1] = (float) s * j / (float) num - s / 2;
                vertices[3 * ((num + 1) * i + j) + 2] = (float) 0.0;

                if (vertices[3 * ((num + 1) * i + j)] < edgeVerticeMin.dX) {
                    edgeVerticeMin.dX = vertices[3 * ((num + 1) * i + j)];
                } else if (vertices[3 * ((num + 1) * i + j)] > edgeVerticeMin.dX) {
                    edgeVerticeMax.dX = vertices[3 * ((num + 1) * i + j)];
                }

                if (vertices[3 * ((num + 1) * i + j) + 1] < edgeVerticeMin.dY) {
                    edgeVerticeMin.dY = vertices[3 * ((num + 1) * i + j) + 1];
                } else if (vertices[3 * ((num + 1) * i + j) + 1] > edgeVerticeMin.dY) {
                    edgeVerticeMax.dY = vertices[3 * ((num + 1) * i + j) + 1];
                }

                if (vertices[3 * ((num + 1) * i + j) + 2] < edgeVerticeMin.dZ) {
                    edgeVerticeMin.dZ = vertices[3 * ((num + 1) * i + j) + 2];
                } else if (vertices[3 * ((num + 1) * i + j) + 2] > edgeVerticeMin.dZ) {
                    edgeVerticeMax.dZ = vertices[3 * ((num + 1) * i + j) + 2];
                }

                texturecoords[2 * ((num + 1) * i + j)] = (float) i / (float) (num + 1);
                texturecoords[2 * ((num + 1) * i + j) + 1] = (float) j / (float) (num + 1);
            }
        }

        k = 0;
        for (i = 0; i < num; i++) {
            for (j = 0; j < num; j++) {
                indices[k++] = (short) ((num + 1) * i + j);
                indices[k++] = (short) ((num + 1) * (i + 1) + j);
                indices[k++] = (short) ((num + 1) * (i) + j + 1);

                indices[k++] = (short) ((num + 1) * (i + 1) + j);
                indices[k++] = (short) ((num + 1) * (i + 1) + j + 1);
                indices[k++] = (short) ((num + 1) * (i) + j + 1);
            }
        }
    }

    public void clear() {
        Matrix.setIdentityM(transformationMatrix, 0);
    }

    public void computePlaneTexture() {
        int n = vertices.length / 3;
        texturecoords = new float[2 * n];
        int num = (int) Math.sqrt((float) n);

        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                texturecoords[2 * (i * num + j)] = (float) i / num;
                texturecoords[2 * (i * num + j) + 1] = (float) (1.0 - (float) j / num);
            }
        }
    }

    public int loadShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        if (shader != 0) {
            GLES30.glShaderSource(shader, shaderCode);
            GLES30.glCompileShader(shader);
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0) {
                Log.e(TAG, "Error compiling shader: " + GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }
        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }
        return shader;
    }

    public String readShaderFromRawResource(final int resourceId) {
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }

    @Override
    public void translate(final float x, final float y, final float z) {
        this.position.dX += x;
        this.position.dY += y;
        this.position.dZ += z;

        float[] tmp = new float[16];
        Matrix.setIdentityM(tmp, 0);
        Matrix.translateM(tmp, 0, x, y, z);
        Matrix.multiplyMM(transformationMatrix, 0, tmp, 0, Arrays.copyOf(transformationMatrix, 16), 0);
        //Matrix.translateM(transformationMatrix, 0, x, y, z);
    }

    public void translate(final myVector3D pos) {
        translate(pos.dX, pos.dY, pos.dZ);
    }

    @Override
    public void teleport(float x, float y, float z) {
        // TODO Auto-generated method stub
    }

    @Override
    public void rotate(float a, float x, float y, float z) {
        float[] tmp = new float[16];
        Matrix.setIdentityM(tmp, 0);
        Matrix.rotateM(tmp, 0, a, x, y, z);
        Matrix.multiplyMM(transformationMatrix, 0, tmp, 0, Arrays.copyOf(transformationMatrix, 16), 0);
        //Matrix.rotateM(transformationMatrix, 0, a, x, y, z);

        if (a == -90 && x == 1 && y == 0 && z == 0) {
            float tmpMin = -this.edgeVerticeMin.dY;
            float tmpMax = -this.edgeVerticeMax.dY;
            this.edgeVerticeMin.dY = this.edgeVerticeMin.dZ;
            this.edgeVerticeMax.dY = this.edgeVerticeMax.dZ;
            this.edgeVerticeMin.dZ = tmpMin;
            this.edgeVerticeMax.dZ = tmpMax;
        }
    }

    @Override
    public void scale(float rate) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] *= rate;
        }

        myVector3D tmp_dim_div_2 = (this.edgeVerticeMax.add(this.edgeVerticeMin)).div(2.0f);

        this.edgeVerticeMin.dX = (this.edgeVerticeMin.dX - tmp_dim_div_2.dX) * rate;
        this.edgeVerticeMin.dY = (this.edgeVerticeMin.dY - tmp_dim_div_2.dY) * rate;
        this.edgeVerticeMin.dZ = (this.edgeVerticeMin.dZ - tmp_dim_div_2.dZ) * rate;

        this.edgeVerticeMax.dX = (this.edgeVerticeMax.dX - tmp_dim_div_2.dX) * rate;
        this.edgeVerticeMax.dY = (this.edgeVerticeMax.dY - tmp_dim_div_2.dY) * rate;
        this.edgeVerticeMax.dZ = (this.edgeVerticeMax.dZ - tmp_dim_div_2.dZ) * rate;
    }

    @Override
    public Entity isInside(final Entity object) {
        // TODO Use the cube made by the extreme point : easier

        if (object.physic.noContact || this.physic.noContact) {
            return null;
        }

        if (object.edgeVerticeMin.dX + object.position.dX > this.edgeVerticeMax.dX + this.position.dX) {
            return null;
        }
        if (this.edgeVerticeMin.dX + this.position.dX > object.edgeVerticeMax.dX + object.position.dX) {
            return null;
        }

        if (object.edgeVerticeMin.dY + object.position.dY > this.edgeVerticeMax.dY + this.position.dY) {
            return null;
        }
        if (this.edgeVerticeMin.dY + this.position.dY > object.edgeVerticeMax.dY + object.position.dY) {
            return null;
        }

        if (object.edgeVerticeMin.dZ + object.position.dZ > this.edgeVerticeMax.dZ + this.position.dZ) {
            return null;
        }
        if (this.edgeVerticeMin.dZ + this.position.dZ > object.edgeVerticeMax.dZ + object.position.dZ) {
            return null;
        }

        return object;
    }

    public boolean isInside(final EntityGroup contacts) {
        entitiesContact = new ArrayList<>();
        for (Entity entity : contacts.entities) {// Check contact
            if (entity.id != this.id && this.isInside(entity) != null) {
                entitiesContact.add(entity);
            }
        }

        if (entitiesContact.size() != 0 && id == 4444) {
            int i = 0;
            i++;
        }


        return (entitiesContact.size() != 0);
    }

    @Override
    public void translateRepetedWayPosition() {
        if (repetedWayPosition != null) {
            myVector3D tmp;
            if ((tmp = repetedWayPosition.getCurrentPosition()) != null) {
                translate(tmp.dX - this.position.dX, tmp.dY - this.position.dY, tmp.dZ - this.position.dZ);
            }
        }
    }

    /**
     * Execute by the physics thread
     */
    @Override
    public void computeForces(final EntityGroup contacts) {

        // TODO Newton 2 law

        if (this.physic.mass != 0) {

            this.sum_force.reset();

            for (Force force : this.forces) {
                if (force.isApplied(this.id)) {

                    if (force instanceof ForceToEntity) {
                        ForceToEntity forceToEntity = (ForceToEntity) force;
                        Entity dirEntity = contacts.getEntityById(forceToEntity.directionEntityId);
                        if (dirEntity != null) {
                            myVector3D forceV = forceToEntity.getForceV(this);
                            forceV = ((dirEntity.position.sub(this.position)).normalizeVector()).mult(forceV.length());
                            sum_force.dX += forceV.dX * ((force.dotMass) ? physic.mass : 1);
                            sum_force.dY += forceV.dY * ((force.dotMass) ? physic.mass : 1);
                            sum_force.dZ += forceV.dZ * ((force.dotMass) ? physic.mass : 1);
                        }
                    } else {
                        myVector3D forceV = force.getForceV(this);
                        sum_force.dX += forceV.dX * ((force.dotMass) ? physic.mass : 1);
                        sum_force.dY += forceV.dY * ((force.dotMass) ? physic.mass : 1);
                        sum_force.dZ += forceV.dZ * ((force.dotMass) ? physic.mass : 1);
                    }
                }
            }

            if (contactEnable) {
                boolean isInside = this.isInside(contacts);

                if ((this.position.dY <= 0.0f + Math.abs(this.edgeVerticeMin.dY)) && this.velocity.dY < 0) { // Cheat Contact with floor : contact force
                    if (contactFloorListener != null && contactFloorListener.condition(this)) {
                        contactFloorListener.execute(this);
                    }
                    this.velocity.dY = -this.velocity.dY * 0.65f;
                } else if (insideLastLoop && isInside) {
                    // Spring force : thanks teacher idea
                    if (entitiesContact != null) {
                        for (Entity entityContact : entitiesContact) {
                            if (entityContact.edgeVerticeMin.dY + entityContact.position.dY < this.edgeVerticeMin.dY + this.position.dY && this.edgeVerticeMin.dY + this.position.dY < entityContact.edgeVerticeMax.dY + entityContact.position.dY) {
                                if ((entityContact.edgeVerticeMax.dY + entityContact.position.dY) - (this.edgeVerticeMin.dY + this.position.dY) + this.position.dY > 0) {
                                    sum_force.dY += 0.000003f * ((entityContact.edgeVerticeMax.dY + entityContact.position.dY) - (this.edgeVerticeMin.dY + this.position.dY)) / Math.abs(entityContact.edgeVerticeMax.dY - entityContact.edgeVerticeMin.dY);
                                }
                            } else {
                                if ((entityContact.edgeVerticeMin.dY + entityContact.position.dY) - (this.edgeVerticeMax.dY + this.position.dY) + this.position.dY > 0) {
                                    sum_force.dY += 0.000003f * ((entityContact.edgeVerticeMin.dY + entityContact.position.dY) - (this.edgeVerticeMax.dY + this.position.dY)) / Math.abs(entityContact.edgeVerticeMax.dY - entityContact.edgeVerticeMin.dY);
                                }
                            }
                        }
                    }
                } else if ((isInside) && this.velocity.dY < 0) { // Contact with object
                    insideLastLoop = true;
                    this.velocity.dY = -this.velocity.dY * 0.65f; // Cheat

                    //this.velocity.dY = entityContact.velocity.dY * 1.0f; // Cheat
                    /*
                    this.velocity.dY = 0;
					for(Entity ent : entitiesContact)
						this.velocity.dY += ent.velocity.dY*ent.velocity.dY ;
					*/
                    //this.velocity.dY = (float) Math.sqrt(this.velocity.dY);

                    //this.velocity.dY = (entityContact.physic.mass/this.physic.mass) * entityContact.velocity.dY * 1.0f; // Best equation
                } else if (isInside) {
                    insideLastLoop = true;
                    this.velocity.dX = -this.velocity.dX * 0.65f;
                    this.velocity.dZ = -this.velocity.dZ * 0.65f;
                } else if (!isInside) {
                    insideLastLoop = false;
                }
            }
        }
    }

    boolean insideLastLoop = false;

    @Override
    public void applySumForces(final EntityGroup contacts) {
        if (physic.mass != 0) {

            this.acceleration.dX = (this.sum_force.dX) / this.physic.mass;
            this.acceleration.dY = (this.sum_force.dY) / this.physic.mass;
            this.acceleration.dZ = (this.sum_force.dZ) / this.physic.mass;

            if ((PhysicsConst.REAL_LOOP_TIME * (this.velocity.dY + PhysicsConst.REAL_LOOP_TIME * this.acceleration.dY / 2) < 0 && this.position.dY - Math.abs(this.edgeVerticeMin.dY) < 0)) {

                this.velocity.dX *= 0.97;
                this.velocity.dZ *= 0.97;

                translate(PhysicsConst.REAL_LOOP_TIME * (this.velocity.dX + PhysicsConst.REAL_LOOP_TIME * this.acceleration.dX / 2),
                        0,
                        PhysicsConst.REAL_LOOP_TIME * (this.velocity.dZ + PhysicsConst.REAL_LOOP_TIME * this.acceleration.dZ / 2));
            } else {
                translate(PhysicsConst.REAL_LOOP_TIME * (this.velocity.dX + PhysicsConst.REAL_LOOP_TIME * this.acceleration.dX / 2),
                        PhysicsConst.REAL_LOOP_TIME * (this.velocity.dY + PhysicsConst.REAL_LOOP_TIME * this.acceleration.dY / 2),
                        PhysicsConst.REAL_LOOP_TIME * (this.velocity.dZ + PhysicsConst.REAL_LOOP_TIME * this.acceleration.dZ / 2));
            }


            this.velocity.dX += PhysicsConst.REAL_LOOP_TIME * this.acceleration.dX;
            this.velocity.dY += PhysicsConst.REAL_LOOP_TIME * this.acceleration.dY;
            this.velocity.dZ += PhysicsConst.REAL_LOOP_TIME * this.acceleration.dZ;
        }
    }

    @Override
    public void addForce(Force force) {
        this.forces.add(force);
    }
}
