package com.mercandalli.android.apps.files.file.audio.metadata;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cmc.music.myid3.MyID3v2Read;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileAudioMetaDataExtractor {

    private static FileAudioMetaDataExtractor sFileAudioMetaDataExtractor;

    public static FileAudioMetaDataExtractor getInstance() {
        if (sFileAudioMetaDataExtractor != null) {
            return sFileAudioMetaDataExtractor;
        }
        return sFileAudioMetaDataExtractor = new FileAudioMetaDataExtractor();
    }

    @Nullable
    public MetaData extract(@Nullable final File file) {
        if (file == null) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        if (!file.getName().toLowerCase().endsWith(".mp3")) {
            return null;
        }
        final MetaData readID3v1 = readID3v1(file);
        readID3v2(file, readID3v1 != null);
        return readID3v1;
    }

    @Nullable
    private MetaData readID3v1(final @NonNull File file) {
        final long length = file.length();
        if (length < 128L) {
            return null;
        }
        BufferedInputStream is = null;

        byte[] bytes;
        try {
            FileInputStream is1;
            try {
                is1 = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                return null;
            }
            is = new BufferedInputStream(is1, 8192);
            try {
                is.skip(length - 128L);
            } catch (IOException e) {
                return null;
            }
            try {
                bytes = readArray(is, 128);
            } catch (IOException e) {
                return null;
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignored) {
            }
        }

        if (bytes[0] != 84) {
            return null;
        } else if (bytes[1] != 65) {
            return null;
        } else if (bytes[2] != 71) {
            return null;
        } else {
            return parseTags(bytes);
        }
    }

    private MetaDataV2 readID3v2(File file, boolean hasId3v1) {
        byte[] bytes = readID3v2Tail(file, hasId3v1);
        if (bytes == null) {
            try {
                bytes = readID3v2Head(file);
            } catch (IOException ignored) {
            }
        }
        if (bytes == null) {
            return null;
        }
        //TODO

        return null;
    }

    public MetaData parseTags(byte[] bytes) {
        MetaData tags = new MetaData();

        byte counter = 3;
        tags.title = this.getField(bytes, counter, 30);

        int counter1 = counter + 30;
        tags.artist = this.getField(bytes, counter1, 30);

        counter1 += 30;
        tags.album = this.getField(bytes, counter1, 30);

        counter1 += 30;
        tags.year = this.getField(bytes, counter1, 4);

        counter1 += 4;
        tags.comment = this.getField(bytes, counter1, 30);

        counter1 += 30;
        int genre;
        if (bytes[counter1 - 2] == 0 && bytes[counter1 - 1] != 0) {
            genre = 255 & bytes[counter1 - 1];
            tags.trackNumber = genre;
        }

        genre = 255 & bytes[counter1];
        if (genre < 80 && genre > 0) {
            tags.genreId = genre;
        }
        return tags;
    }

    @Nullable
    private String getField(byte[] bytes, int start, int length) {
        for (int result = start; result < start + length; ++result) {
            if (bytes[result] == 0) {
                length = result - start;
                break;
            }
        }
        if (length <= 0) {
            return null;
        }
        try {
            String var7 = new String(bytes, start, length, "UTF-8").trim();
            if (var7.length() < 1) {
                return null;
            }
            return var7;
        } catch (Throwable ignored) {
        }
        return null;
    }

    private byte[] readArray(InputStream is, int length) throws IOException {
        final byte[] result = new byte[length];
        int read;
        for (int total = 0; total < length; total += read) {
            read = is.read(result, total, length - total);
            if (read < 0) {
                throw new IOException("bad read");
            }
        }
        return result;
    }

    @Nullable
    private byte[] readID3v2Tail(File file, boolean hasId3v1) {
        if (file != null && file.exists()) {
            long length = file.length();
            int index = hasId3v1 ? 128 : 0;
            index += 10;
            if ((long) index > length) {
                return null;
            } else {
                BufferedInputStream is = null;

                byte[] var15 = null;
                try {
                    FileInputStream is1 = new FileInputStream(file);
                    is = new BufferedInputStream(is1, 8192);
                    is.skip(length - (long) index);
                    byte[] footer = this.readArray(is, 10);
                    if (footer[2] != 51 || footer[1] != 68 || footer[0] != 73) {
                        return null;
                    }

                    Number tagLength = MyID3v2Read.readSynchsafeInt(footer, 6);
                    if (tagLength == null) {
                        return null;
                    }

                    int bodyLength = tagLength.intValue();
                    if ((long) (index + bodyLength) > length) {
                        return null;
                    }

                    is.close();
                    is = null;
                    is1 = new FileInputStream(file);
                    is = new BufferedInputStream(is1, 8192);
                    long skip = length - 10L;
                    skip -= (long) bodyLength;
                    skip -= 10L;
                    if (hasId3v1) {
                        skip -= 128L;
                    }

                    is.skip(skip);
                    byte[] header_and_body = this.readArray(is, 10 + bodyLength + 10);
                    var15 = header_and_body;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException var21) {
                        ;
                    }

                }

                return var15;
            }
        } else {
            return null;
        }
    }

    private byte[] readID3v2Head(File file) throws IOException {
        if (file != null && file.exists()) {
            long length = file.length();
            if (length < 10L) {
                return null;
            } else {
                BufferedInputStream is = null;

                try {
                    FileInputStream is1 = new FileInputStream(file);
                    is = new BufferedInputStream(is1, 8192);
                    byte[] header = this.readArray(is, 10);
                    if (header[0] == 73 && header[1] == 68 && header[2] == 51) {
                        byte flags = header[5];
                        boolean has_footer = (flags & 16) > 0;
                        Number tagLength = MyID3v2Read.readSynchsafeInt(header, 6);
                        if (tagLength != null) {
                            int bodyLength = tagLength.intValue();
                            if (has_footer) {
                                bodyLength += 10;
                            }

                            if ((long) (10 + bodyLength) <= length) {
                                byte[] body = this.readArray(is, bodyLength);
                                byte[] result = new byte[header.length + body.length];
                                System.arraycopy(header, 0, result, 0, header.length);
                                System.arraycopy(body, 0, result, header.length, body.length);
                                byte[] var13 = result;
                                return var13;
                            }
                        }
                    }
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException var19) {
                        ;
                    }

                }

                return null;
            }
        } else {
            return null;
        }
    }

    public static final class MetaData {
        public String title;
        public String artist;
        public String album;
        public String year;
        public String comment;
        public int trackNumber;
        public int genreId;
    }

    public static final class MetaDataV2 {
        public String title;
        public String artist;
        public String album;
        public String year;
        public String comment;
        public int trackNumber;
        public int genreId;
    }
}
