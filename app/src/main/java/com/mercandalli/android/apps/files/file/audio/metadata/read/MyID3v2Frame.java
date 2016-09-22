package com.mercandalli.android.apps.files.file.audio.metadata.read;

import java.util.Comparator;

/* package */ class MyID3v2Frame {
    public final String frameId;
    public final byte[] dataBytes;
    public static final Comparator COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            MyID3v2Frame ph1 = (MyID3v2Frame) o1;
            MyID3v2Frame ph2 = (MyID3v2Frame) o2;
            return ph1.frameId.compareTo(ph2.frameId);
        }
    };

    public MyID3v2Frame(String frameId, byte[] dataBytes) {
        this.frameId = frameId;
        this.dataBytes = dataBytes;
    }

    public String toString() {
        return "{" + this.frameId + "}";
    }
}

