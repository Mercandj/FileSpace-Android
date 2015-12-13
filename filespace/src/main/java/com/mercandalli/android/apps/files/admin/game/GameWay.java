package com.mercandalli.android.apps.files.admin.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jonathan on 02/09/2015.
 */
public class GameWay implements Iterable<GameCase> {

    public List<GameCase> array;
    private boolean mRevert = false;

    public GameWay() {
        this.array = new ArrayList<>();
    }

    public GameWay(List<GameCase> array) {
        this.array = array;
    }

    public void revert() {
        this.mRevert = !this.mRevert;
    }

    public void add(GameCase parm) {
        this.array.add(parm);
    }

    @Override
    public Iterator<GameCase> iterator() {
        return new MyIterator();
    }

    public class MyIterator implements Iterator<GameCase> {

        private int pointer = 0;

        @Override
        public boolean hasNext() {
            return mRevert ? pointer > 0 : pointer < array.size();
        }

        @Override
        public GameCase next() {
            if (mRevert) {
                pointer--;
                return array.get(pointer + 1);
            }
            pointer++;
            return array.get(pointer - 1);
        }

        @Override
        public void remove() {

        }
    }
}


