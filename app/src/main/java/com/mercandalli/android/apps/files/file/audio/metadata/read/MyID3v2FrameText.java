package com.mercandalli.android.apps.files.file.audio.metadata.read;

/* package */ class MyID3v2FrameText extends MyID3v2Frame {
    public final String value;
    public final String value2;

    public MyID3v2FrameText(String frame_id, byte[] data_bytes, String value) {
        this(frame_id, data_bytes, value, null);
    }

    public MyID3v2FrameText(String frame_id, byte[] data_bytes, String value, String value2) {
        super(frame_id, data_bytes);
        this.value = value;
        this.value2 = value2;
    }

    public String toString() {
        return "{" + this.frameId + ": " + this.value +
                (this.value2 == null ? "" : " (" + this.value2 + ")") + "}";
    }
}
