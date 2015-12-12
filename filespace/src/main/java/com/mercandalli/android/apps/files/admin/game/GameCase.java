package com.mercandalli.android.apps.files.admin.game;

/**
 * Created by Jonathan on 02/09/2015.
 */
public class GameCase {

    public int value;
    public final int x, y;

    public GameCase() {
        x = -1;
        y = -1;
    }

    public GameCase(int x, int y) {
        this.x = x;
        this.y = y;
        this.value = 0;
    }

    public GameCase(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return (value + " " + x + " " + y).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof GameCase)) {
            return false;
        }
        GameCase obj = (GameCase) o;
        return obj.x == this.x && obj.y == this.y && obj.value == this.value;
    }
}