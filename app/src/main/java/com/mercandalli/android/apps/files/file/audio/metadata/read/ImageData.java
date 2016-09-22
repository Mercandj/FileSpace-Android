package com.mercandalli.android.apps.files.file.audio.metadata.read;

/* package */ class ImageData {
    /* package */ final byte[] imageData;
    /* package */ final String mimeType;
    /* package */ final String description;
    /* package */ final int pictureType;

    /* package */ ImageData(byte[] imageData, String mimeType, String description, int pictureType) {
        this.imageData = imageData;
        this.mimeType = mimeType;
        this.description = description;
        this.pictureType = pictureType;
    }
}
