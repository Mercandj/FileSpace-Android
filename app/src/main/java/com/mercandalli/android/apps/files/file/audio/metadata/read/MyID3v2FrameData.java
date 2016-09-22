package com.mercandalli.android.apps.files.file.audio.metadata.read;

/* package */ class MyID3v2FrameData extends MyID3v2Frame {
    public final ID3v2FrameFlags flags;

    public MyID3v2FrameData(String frame_id, byte[] data_bytes, ID3v2FrameFlags flags) {
        super(frame_id, data_bytes);
        this.flags = flags;
    }

    public String toString() {
        return "{" + this.frameId + ": " + this.dataBytes.length + "}";
    }
}