package com.mercandalli.android.apps.files.file.audio.metadata.read;

import java.util.Vector;

/* package */ class ID3Tag {
    public static final int TAG_TYPE_ID3_V1 = 1;
    public static final int TAG_TYPE_ID3_V2 = 2;
    public final int tagType;
    public final byte[] bytes;
    public final MusicMetadata values;

    public ID3Tag(int tagType, byte[] bytes, MusicMetadata values) {
        this.tagType = tagType;
        this.bytes = bytes;
        this.values = values;
    }

    public String toString() {
        return "{ID3Tag. " +
                "values: " + this.values +
                " }";
    }

    public static class ID3TagV1 extends ID3Tag {
        public ID3TagV1(byte[] bytes, MusicMetadata values) {
            super(1, bytes, values);
        }
    }

    public static class ID3TagV2 extends ID3Tag {
        public final Vector frames;
        public final byte versionMajor;
        public final byte versionMinor;

        public ID3TagV2(byte versionMajor, byte versionMinor, byte[] bytes, MusicMetadata values, Vector frames) {
            super(2, bytes, values);
            this.versionMajor = versionMajor;
            this.versionMinor = versionMinor;
            this.frames = frames;
        }
    }
}

