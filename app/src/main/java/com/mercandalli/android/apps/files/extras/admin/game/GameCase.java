package com.mercandalli.android.apps.files.extras.admin.game;

public class GameCase {

    public int value;
    public final int x, y;

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