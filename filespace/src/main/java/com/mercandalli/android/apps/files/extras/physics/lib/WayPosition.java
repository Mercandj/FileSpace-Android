package com.mercandalli.android.apps.files.extras.physics.lib;

import java.util.ArrayList;
import java.util.List;

public class WayPosition {

    public List<MyVector3D> way = new ArrayList<MyVector3D>();
    private int currentID = 0;
    public boolean reverse = false;

    public WayPosition() {

    }

    public WayPosition(List<MyVector3D> way) {
        this.way = way;
    }

    public void add(MyVector3D v) {
        way.add(v);
    }

    public int size() {
        return way.size();
    }

    public MyVector3D get(int i) {
        return way.get(i);
    }

    public MyVector3D getCurrentPosition() {
        int res = currentID;
        if (!reverse) {
            if (currentID + 1 >= size()) {
                currentID = 0;
            } else {
                currentID++;
            }
        } else {
            if (currentID - 1 < 0) {
                currentID = size() - 1;
            } else {
                currentID--;
            }
        }
        return get(res);
    }

    public void initCubeWabHorizontal(float centerX, float centerY, float centerZ, float size, float foot, boolean right) {
        way = new ArrayList<MyVector3D>();
        float divcote = size / 2;

        for (float i = centerX - divcote; i <= centerX + divcote; i += foot) {
            add(new MyVector3D(i, centerY, centerZ - divcote));
        }

        for (float i = centerZ - divcote; i <= centerZ + divcote; i += foot) {
            add(new MyVector3D(centerX + divcote, centerY, i));
        }

        for (float i = centerX + divcote; i >= centerX - divcote; i -= foot) {
            add(new MyVector3D(i, centerY, centerZ + divcote));
        }

        for (float i = centerZ + divcote; i >= centerZ - divcote; i -= foot) {
            add(new MyVector3D(centerX - divcote, centerY, i));
        }

        reverse = !right;
    }
}
