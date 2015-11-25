/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.filespace.extras.physics.lib;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;

public class lib {

    /* OLD : New way to read from raw (not like RollingBall : files include on apk) */
    public static IndicesVertices readMeshLocal(Context ctxt, int id_raw) {
        float[] vertices = null;
        short[] indices = null;
        short i1, i2, i3;
        String t, u;
        Scanner lineTokenizer;
        InputStream inputStream = ctxt.getResources().openRawResource(id_raw);
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            Vector<Float> verts = new Vector<Float>();
            Vector<Short> inds = new Vector<Short>();
            while ((line = r.readLine()) != null) {

                lineTokenizer = new Scanner(line);
                t = lineTokenizer.next();
                if (t.equals("v")) {
                    u = lineTokenizer.next();
                    verts.addElement(Float.parseFloat(u));
                    u = lineTokenizer.next();
                    verts.addElement(Float.parseFloat(u));
                    u = lineTokenizer.next();
                    verts.addElement(Float.parseFloat(u));
                } else if (t.equals("f")) {
                    u = lineTokenizer.next();
                    i1 = (short) (Short.parseShort(u) - 1);
                    u = lineTokenizer.next();
                    i2 = (short) (Short.parseShort(u) - 1);

                    while (lineTokenizer.hasNext()) {
                        u = lineTokenizer.next();
                        i3 = (short) (Short.parseShort(u) - 1);

                        inds.addElement(i1);
                        inds.addElement(i2);
                        inds.addElement(i3);
                        i2 = i3;
                    }
                }
                lineTokenizer.close();
            }
            vertices = new float[verts.size()];
            for (int i = 0; i < vertices.length; i++) vertices[i] = verts.get(i);
            indices = new short[inds.size()];
            for (int i = 0; i < indices.length; i++) indices[i] = inds.get(i);

            vertices = normalize(vertices);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new IndicesVertices(vertices, indices);
    }

    public static float[] normalize(float[] vertices) {
        int i;
        int tmpxmin = 0, tmpymin = 0, tmpzmin = 0, tmpxmax = 0, tmpymax = 0, tmpzmax = 0;

        int n = vertices.length / 3;

        for (i = 0; i < n; i++) {
            if (vertices[3 * i] < vertices[3 * tmpxmin]) tmpxmin = i;
            if (vertices[3 * i] > vertices[3 * tmpxmax]) tmpxmax = i;

            if (vertices[3 * i + 1] < vertices[3 * tmpymin + 1]) tmpymin = i;
            if (vertices[3 * i + 1] > vertices[3 * tmpymax + 1]) tmpymax = i;

            if (vertices[3 * i + 2] < vertices[3 * tmpzmin + 2]) tmpzmin = i;
            if (vertices[3 * i + 2] > vertices[3 * tmpzmax + 2]) tmpzmax = i;
        }

        double xmin = vertices[3 * tmpxmin], xmax = vertices[3 * tmpxmax],
                ymin = vertices[3 * tmpymin + 1], ymax = vertices[3 * tmpymax + 1],
                zmin = vertices[3 * tmpzmin + 2], zmax = vertices[3 * tmpzmax + 2];

        double scale = (xmax - xmin) <= (ymax - ymin) ? (xmax - xmin) : (ymax - ymin);
        scale = scale >= (zmax - zmin) ? scale : (zmax - zmin);

        for (i = 0; i < n; i++) {
            vertices[3 * i] -= (xmax + xmin) / 2;
            vertices[3 * i + 1] -= (ymax + ymin) / 2;
            vertices[3 * i + 2] -= (zmax + zmin) / 2;

            vertices[3 * i] /= scale;
            vertices[3 * i + 1] /= scale;
            vertices[3 * i + 2] /= scale;
        }
        return vertices;
    }

    /* New way to read from raw (not like RollingBall : files include on apk) */
    public static IndicesVertices readMeshLocalNomalizedOpti(Context ctxt, int id_raw) {
        float[] vertices = null;
        short[] indices = null;
        short i1, i2, i3;
        String t, u;
        Scanner lineTokenizer;
        InputStream inputStream = ctxt.getResources().openRawResource(id_raw);
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        int i = 0;
        float x, y, z;
        float xmin = Float.MAX_VALUE, xmax = Float.MIN_VALUE,
                ymin = Float.MAX_VALUE, ymax = Float.MIN_VALUE,
                zmin = Float.MAX_VALUE, zmax = Float.MIN_VALUE;

        float scale = 1.0f;

        try {
            Vector<Float> verts = new Vector<Float>();
            Vector<Short> inds = new Vector<Short>();
            while ((line = r.readLine()) != null) {

                lineTokenizer = new Scanner(line);
                t = lineTokenizer.next();
                if (t.equals("v")) {
                    u = lineTokenizer.next();
                    x = Float.parseFloat(u);
                    verts.addElement(x);

                    u = lineTokenizer.next();
                    y = Float.parseFloat(u);
                    verts.addElement(y);

                    u = lineTokenizer.next();
                    z = Float.parseFloat(u);
                    verts.addElement(z);

                    if (x < xmin) xmin = verts.get(3 * i);
                    else if (x > xmax) xmax = verts.get(3 * i);

                    if (y < ymin) ymin = verts.get(3 * i + 1);
                    else if (y > ymax) ymax = verts.get(3 * i + 1);

                    if (z < zmin) zmin = verts.get(3 * i + 2);
                    else if (z > zmax) zmax = verts.get(3 * i + 2);

                    i++;
                } else if (t.equals("f")) {
                    u = lineTokenizer.next();
                    i1 = (short) (Short.parseShort(u) - 1);

                    u = lineTokenizer.next();
                    i2 = (short) (Short.parseShort(u) - 1);

                    while (lineTokenizer.hasNext()) {
                        u = lineTokenizer.next();
                        i3 = (short) (Short.parseShort(u) - 1);

                        inds.addElement(i1);
                        inds.addElement(i2);
                        inds.addElement(i3);
                        i2 = i3;
                    }
                }
                lineTokenizer.close();
            }

            indices = new short[inds.size()];
            for (i = 0; i < indices.length; i++) indices[i] = inds.get(i);

            vertices = new float[verts.size()];
            int n = vertices.length / 3;

            scale = (xmax - xmin) <= (ymax - ymin) ? (xmax - xmin) : (ymax - ymin);
            scale = scale >= (zmax - zmin) ? scale : (zmax - zmin);

            for (i = 0; i < n; i++) {
                vertices[3 * i] = (verts.get(3 * i) - (xmax + xmin) / 2) / scale;
                vertices[3 * i + 1] = (verts.get(3 * i + 1) - (ymax + ymin) / 2) / scale;
                vertices[3 * i + 2] = (verts.get(3 * i + 2) - (zmax + zmin) / 2) / scale;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new IndicesVertices(vertices, indices, new myVector3D((xmin - (xmax + xmin) / 2) / scale, (ymin - (ymax + ymin) / 2) / scale, (zmin - (zmax + zmin) / 2) / scale), new myVector3D((xmax - (xmax + xmin) / 2) / scale, (ymax - (ymax + ymin) / 2) / scale, (zmax - (zmax + zmin) / 2) / scale));
    }
}
