package com.mercandalli.android.apps.files.file.audio.metadata.read;

/* package */ class MyID3v2FrameImage extends MyID3v2FrameData {
    public final byte[] imageData;
    public final String mimeType;
    public final String description;
    public final int pictureType;

    public MyID3v2FrameImage(
            final String frame_id,
            final byte[] data_bytes,
            final ID3v2FrameFlags flags,
            final byte[] imageData,
            final String mimeType,
            final String description,
            final int pictureType) {
        super(frame_id, data_bytes, flags);
        this.imageData = imageData;
        this.mimeType = mimeType;
        this.description = description;
        this.pictureType = pictureType;
    }

    public ImageData getImageData() {
        return new ImageData(this.imageData, this.mimeType, this.description, this.pictureType);
    }
}