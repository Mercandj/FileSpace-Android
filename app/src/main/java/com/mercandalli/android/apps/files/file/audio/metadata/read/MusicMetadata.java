package com.mercandalli.android.apps.files.file.audio.metadata.read;

import java.util.Collections;
import java.util.Vector;

public class MusicMetadata extends SimpleMap {
    public final String name;

    public MusicMetadata(String name) {
        this.name = name;
    }

    public MusicMetadata(MusicMetadata other) {
        this.name = other.name;
        this.putAll(other);
    }

    public static MusicMetadata createEmptyMetadata() {
        return new MusicMetadata("New Metadata");
    }

    public boolean hasBasicInfo() {
        return this.getArtist() != null && (this.getSongTitle() != null &&
                (this.getAlbum() != null && this.getTrackNumber() != null));
    }

    private Number getNumber(Object key) {
        Object result = this.get(key);
        return result == null ? null : (Number) result;
    }

    private String getString(Object key) {
        Object result = this.get(key);
        return result == null ? null : (String) result;
    }

    private Vector getVector(Object key) {
        Object result = this.get(key);
        return result == null ? null : (Vector) result;
    }

    public String getSongTitle() {
        return this.getString("title");
    }

    public String getArtist() {
        return this.getString("artist");
    }

    public String getAlbum() {
        return this.getString("album");
    }

    public String getYear() {
        return this.getString("year");
    }

    public String getComment() {
        return this.getString("comment");
    }

    public Number getTrackNumber() {
        return this.getNumber("track_number");
    }

    public String getGenre() {
        return this.getString("genre");
    }

    public String getDurationSeconds() {
        return this.getString("duration_seconds");
    }

    public String getComposer() {
        return this.getString("composer");
    }

    public String getProducerArtist() {
        return this.getString("album_artist");
    }

    public String getComposer2() {
        return this.getString("composer_2");
    }

    public String getCompilation() {
        return this.getString("compilation");
    }

    public void clearSongTitle() {
        this.remove("title");
    }

    public void clearArtist() {
        this.remove("artist");
    }

    public void clearAlbum() {
        this.remove("album");
    }

    public void clearYear() {
        this.remove("year");
    }

    public void clearComment() {
        this.remove("comment");
    }

    public void clearTrackNumber() {
        this.remove("track_number");
    }

    public void clearGenre() {
        this.remove("genre");
    }

    public void clearDurationSeconds() {
        this.remove("duration_seconds");
    }

    public void clearComposer() {
        this.remove("composer");
    }

    public void clearProducerArtist() {
        this.remove("album_artist");
    }

    public void clearComposer2() {
        this.remove("composer_2");
    }

    public void clearCompilation() {
        this.remove("compilation");
    }

    public void clearFeaturingList() {
        this.remove("featuring_list");
    }

    public void setFeaturingList(Vector v) {
        this.put("featuring_list", v);
    }

    public Vector getFeaturingList() {
        return this.getVector("featuring_list");
    }

    public void clearPictureList() {
        this.remove("pictures");
    }

    public void setPictureList(Vector v) {
        this.put("pictures", v);
    }

    public Vector getPictureList() {
        Vector result = this.getVector("pictures");
        if (result == null) {
            result = new Vector();
        }

        return result;
    }

    public void addPicture(ImageData image) {
        Vector v = this.getVector("pictures");
        if (v == null) {
            v = new Vector();
        }

        v.add(image);
        this.put("pictures", v);
    }

    public void setSongTitle(String s) {
        this.put("title", s);
    }

    public void setArtist(String s) {
        this.put("artist", s);
    }

    public void setAlbum(String s) {
        this.put("album", s);
    }

    public void setYear(String s) {
        this.put("year", s);
    }

    public void setComment(String s) {
        this.put("comment", s);
    }

    public void setTrackNumber(Number s) {
        this.put("track_number", s);
    }

    public void setGenre(String s) {
        this.put("genre", s);
    }

    public void setDurationSeconds(String s) {
        this.put("duration_seconds", s);
    }

    public void setComposer(String s) {
        this.put("composer", s);
    }

    public void setProducerArtist(String s) {
        this.put("album_artist", s);
    }

    public void setComposer2(String s) {
        this.put("composer_2", s);
    }

    public void setCompilation(String s) {
        this.put("compilation", s);
    }

    public String getProducer() {
        return this.getString("producer");
    }

    public void setProducer(String s) {
        this.put("producer", s);
    }

    public void clearProducer() {
        this.remove("producer");
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("{ ");
        Vector keys = new Vector(this.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); ++i) {
            Object key = keys.get(i);
            Object value = this.get(key);
            if (i > 0) {
                result.append(", ");
            }

            result.append(key + ": " + value);
        }

        result.append(" }");
        return result.toString();
    }
}

