package com.mercandalli.android.apps.files.file.audio.metadata.read;

import android.support.annotation.NonNull;

import java.util.Map;
import java.util.Vector;

@SuppressWarnings("unused")
public class MusicMetadataSet {
    public final ID3Tag id3v1Raw;
    public final ID3Tag.ID3TagV2 id3v2Raw;
    public final MusicMetadata id3v1Clean;
    public final MusicMetadata id3v2Clean;
    public final MusicMetadata filename;
    public final MusicMetadata merged;
    public static final String NEW_LINE = System.getProperty("line.separator");
    private static final TagFormat UTILS = new TagFormat();

    private MusicMetadataSet(
            ID3Tag id3_v1_raw,
            ID3Tag.ID3TagV2 id3_v2_raw,
            MusicMetadata id3_v1_clean,
            MusicMetadata id3_v2_clean,
            String file_name,
            String folder_name) {
        this.id3v1Raw = id3_v1_raw;
        this.id3v2Raw = id3_v2_raw;
        this.id3v1Clean = id3_v1_clean;
        this.id3v2Clean = id3_v2_clean;
        this.filename = FSParser.parseFilename(file_name, folder_name);
        this.merged = new MusicMetadata("merged");
        this.merge();
    }

    @NonNull
    public MusicMetadata getSimplified() {
        return new MusicMetadata(this.merged);
    }

    public String toString() {
        return "{ID3TagSet. " +
                NEW_LINE +
                "v1_raw: " + this.id3v1Raw +
                NEW_LINE +
                "v2_raw: " + this.id3v2Raw +
                NEW_LINE +
                "v1: " + this.id3v1Clean +
                NEW_LINE +
                "v2: " + this.id3v2Clean +
                NEW_LINE +
                "filename: " + this.filename +
                NEW_LINE +
                "merged: " + this.merged +
                NEW_LINE +
                " }";
    }

    private void merge(Map src) {
        if (src != null) {
            Vector keys = new Vector(src.keySet());

            for (int i = 0; i < keys.size(); ++i) {
                Object key = keys.get(i);
                if (this.merged.get(key) == null) {
                    Object value = src.get(key);
                    this.merged.put(key, value);
                }
            }
        }
    }

    private void merge() {
        if (this.id3v2Clean != null) {
            this.merged.putAll(this.id3v2Clean);
        }
        this.merge(this.id3v1Clean);
        this.merge(this.filename);
    }

    public static MusicMetadataSet factoryMethod(
            ID3Tag id3_v1_raw, ID3Tag.ID3TagV2 id3_v2_raw, String filename, String folder_name) {
        final MusicMetadata id3V1Clean = id3_v1_raw == null ? null : UTILS.process(id3_v1_raw.values);
        final MusicMetadata id3V2Clean = id3_v2_raw == null ? null : UTILS.process(id3_v2_raw.values);
        return new MusicMetadataSet(id3_v1_raw, id3_v2_raw, id3V1Clean, id3V2Clean, filename, folder_name);
    }
}

