package com.mercandalli.android.apps.files.file.audio.metadata.read;

/* package */ class ParsedFilename {
    public final String raw;
    private String artist = null;
    private String title = null;
    private String album = null;
    private String trackNumber = null;

    public ParsedFilename(String raw) {
        this.raw = raw;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrackNumber() {
        return this.trackNumber;
    }
}