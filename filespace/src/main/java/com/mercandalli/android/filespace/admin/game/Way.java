package com.mercandalli.android.filespace.admin.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jonathan on 02/09/2015.
 */
public class Way implements Iterable<Case> {

    public List<Case> array;
    private boolean revert = false;

    public Way() {
        this.array = new ArrayList<>();
    }

    public Way(List<Case> array) {
        this.array = array;
    }

    public void revert() {
        this.revert = !this.revert;
    }

    public void add(Case parm) {
        this.array.add(parm);
    }

    @Override
    public Iterator<Case> iterator() {
        return new MyIterator();
    }

    public class MyIterator implements Iterator<Case> {

        private int pointer = 0;

        @Override
        public boolean hasNext() {
            return revert ? pointer > 0 : pointer < array.size();
        }

        @Override
        public Case next() {
            if (revert) {
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


