package com.mercandalli.android.apps.files.file.audio.metadata.read;

/* package */ class MyID3v1 {

    public MyID3v1() {
    }

    private String getField(byte[] bytes, int start, int length) {
        for (int result = start; result < start + length; ++result) {
            if (bytes[result] == 0) {
                length = result - start;
                break;
            }
        }

        if (length > 0) {
            try {
                String var7 = new String(bytes, start, length, "UTF-8");
                var7 = var7.trim();
                if (var7.length() < 1) {
                    return null;
                }
                return var7;
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    public MusicMetadata parseTags(byte[] bytes) {
        MusicMetadata tags = new MusicMetadata("id3v1");
        byte counter = 3;
        tags.put("title", this.getField(bytes, counter, 30));

        int counter1 = counter + 30;
        tags.put("artist", this.getField(bytes, counter1, 30));

        counter1 += 30;
        tags.put("album", this.getField(bytes, counter1, 30));

        counter1 += 30;
        tags.put("year", this.getField(bytes, counter1, 4));

        counter1 += 4;
        tags.put("comment", this.getField(bytes, counter1, 30));

        int genre;
        counter1 += 30;
        if (bytes[counter1 - 2] == 0 && bytes[counter1 - 1] != 0) {
            genre = 255 & bytes[counter1 - 1];
            tags.put("track_number", genre);
        }

        genre = 255 & bytes[counter1];
        if (genre < 80 && genre > 0) {
            tags.put("genre_id", genre);
        }
        return tags;
    }
}

