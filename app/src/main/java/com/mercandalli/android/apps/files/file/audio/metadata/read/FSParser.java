package com.mercandalli.android.apps.files.file.audio.metadata.read;

import android.support.annotation.Nullable;

import java.io.File;

/* package */ abstract class FSParser {
    private static final TagFormat UTILS = new TagFormat();
    //private static final NameRectifier nameRectifier = new NameRectifier();

    public FSParser() {
    }

    public static ParsedFilename parseFolder(File file) {
        return parseFolder(file.getName());
    }

    public static ParsedFilename parseFolder(String s) {
        ParsedFilename result = new ParsedFilename(s);
        int hyphenCount = TextUtils.split(s, "-").length - 1;
        if (hyphenCount != 1) {
            return result;
        } else {
            String artist = s.substring(0, s.indexOf(45));
            String album = s.substring(s.indexOf(45) + 1);
            //artist = nameRectifier.rectifyArtist(artist);
            //album = nameRectifier.rectifyAlbum(album);
            if (artist == null && album == null) {
                return result;
            } else {
                result.setArtist(artist);
                result.setAlbum(album);
                return result;
            }
        }
    }

    public static boolean isTrackNumber(String s) {
        if (s == null) {
            return false;
        } else {
            s = s.trim();
            if (s.length() >= 1 && s.length() <= 3) {
                if (TextUtils.kALPHABET.indexOf(s.charAt(0)) >= 0) {
                    s = s.substring(1);
                    if (s.length() < 1) {
                        return false;
                    }
                }
                return TextUtils.filter(s, "0123456789").equals(s);
            } else {
                return false;
            }
        }
    }

    @Nullable
    private static Number getTrackNumber(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Throwable var2) {
            return null;
        }
    }

    @Nullable
    public static MusicMetadata parseFilename(String fileName, String folderName) {
        if (fileName == null) {
            return null;
        } else if (!fileName.toLowerCase().endsWith(".mp3")) {
            return null;
        } else {
            fileName = fileName.substring(0, fileName.length() - 4);
            String[] splits = fileName.split("-");
            String artist;
            String song_title;
            Number track_number;
            if (splits.length == 2) {
                if (isTrackNumber(splits[0])) {
                    artist = null;
                    track_number = getTrackNumber(splits[0].trim());
                    song_title = UTILS.processSongTitle(splits[1]);
                } else if (isTrackNumber(splits[1])) {
                    artist = null;
                    song_title = UTILS.processSongTitle(splits[0]);
                    track_number = getTrackNumber(splits[1].trim());
                } else {
                    artist = UTILS.processArtist(splits[0]);
                    song_title = UTILS.processSongTitle(splits[1]);
                    track_number = null;
                }
            } else {
                if (splits.length != 3) {
                    return null;
                }

                if (isTrackNumber(splits[0])) {
                    track_number = getTrackNumber(splits[0].trim());
                    artist = UTILS.processArtist(splits[1]);
                    song_title = UTILS.processSongTitle(splits[2]);
                } else {
                    if (!isTrackNumber(splits[1])) {
                        return null;
                    }

                    artist = UTILS.processArtist(splits[0]);
                    track_number = getTrackNumber(splits[1].trim());
                    song_title = UTILS.processSongTitle(splits[2]);
                }
            }

            if (isTrackNumber(artist)) {
                return null;
            } else if (isTrackNumber(song_title)) {
                return null;
            } else {
                if (folderName != null && folderName.endsWith("(!)")) {
                    folderName = folderName.substring(0, folderName.length() - 3);
                }

                String kVariousArtists = "Various Artists";
                String album = null;
                if (folderName != null && !folderName.startsWith("@")) {
                    if (artist != null) {
                        if (folderName.toLowerCase().startsWith(kVariousArtists.toLowerCase())) {
                            folderName = folderName.substring(kVariousArtists.length());
                        } else if (folderName.toLowerCase().startsWith(artist.toLowerCase())) {
                            folderName = folderName.substring(artist.length());
                        } else {
                            if (!folderName.toLowerCase().endsWith(artist.toLowerCase())) {
                                return null;
                            }

                            folderName = folderName.substring(0, folderName.length() - artist.length());
                        }

                        album = UTILS.processAlbum(folderName);
                    } else {
                        int result = folderName.indexOf(45);
                        int lastHyphen = folderName.lastIndexOf(45);
                        if (result < 0 || result != lastHyphen) {
                            return null;
                        }

                        artist = UTILS.processArtist(folderName.substring(0, result));
                        album = UTILS.processAlbum(folderName.substring(result + 1));
                    }
                }

                if (artist == null) {
                    return null;
                } else {
                    if (artist.equalsIgnoreCase(kVariousArtists)) {
                        artist = null;
                    }
                    MusicMetadata result1 = new MusicMetadata("filename");
                    result1.setAlbum(album);
                    result1.setArtist(artist);
                    result1.setSongTitle(song_title);
                    if (track_number != null) {
                        result1.setTrackNumber(track_number);
                    }
                    return result1;
                }
            }
        }
    }
}

