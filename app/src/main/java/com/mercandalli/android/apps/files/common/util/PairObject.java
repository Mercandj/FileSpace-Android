package com.mercandalli.android.apps.files.common.util;

/**
 * Created by Jonathan on 21/09/2015.
 */
public class PairObject<F, S> {
    private F first; //first member of pair
    private S second; //second member of pair

    public PairObject(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
