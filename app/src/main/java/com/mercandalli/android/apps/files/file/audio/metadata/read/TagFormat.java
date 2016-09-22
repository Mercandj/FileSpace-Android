package com.mercandalli.android.apps.files.file.audio.metadata.read;

/* package */ class TagFormat {

    public TagFormat() {
    }

    public String processArtist(String s) {
        return s;
    }

    public String processAlbum(String s) {
        return s;
    }

    public String processSongTitle(String s) {
        return s;
    }

    public MusicMetadata process(MusicMetadata src) {
        MusicMetadata result = new MusicMetadata(src.name + " clean");
        result.putAll(src);
        String s = src.getArtist();
        s = this.processArtist(s);
        result.setArtist(s);
        s = src.getAlbum();
        s = this.processAlbum(s);
        result.setAlbum(s);
        s = src.getSongTitle();
        s = this.processSongTitle(s);
        result.setSongTitle(s);
        return result;
    }
}
