package com.mercandalli.android.apps.files.file.audio.metadata.read;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * // Try to remove 'org.jaudiotagger:jaudiotagger:2.0.1'
 */
@SuppressWarnings("unused")
public final class AudioMetaDataRead {

    private AudioMetaDataRead() {
    }

    @Nullable
    public static MusicMetadataSet extract(@Nullable final File file) {
        if (file == null) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        if (!file.getName().toLowerCase().endsWith(".mp3")) {
            return null;
        }
        final ID3Tag tagV1 = readID3v1(file);
        final ID3Tag.ID3TagV2 id3v2 = readID3v2(file, tagV1 != null);
        return MusicMetadataSet.factoryMethod(tagV1, id3v2, file.getName(), file.getParentFile().getName());
    }

    /**
     * @param file Check if this file exist before call.
     * @return The {@link ID3Tag}.
     */
    @Nullable
    private static ID3Tag readID3v1(@NonNull final File file) {
        final long length = file.length();
        if (length < 128L) {
            return null;
        } else {
            BufferedInputStream is = null;
            byte[] bytes = null;
            try {
                is = new BufferedInputStream(new FileInputStream(file), 8192);
                is.skip(length - 128L);
                bytes = readArray(is, 128);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ignored) {
                }
            }
            if (bytes == null) {
                return null;
            }

            if (bytes[0] != 84) {
                return null;
            } else if (bytes[1] != 65) {
                return null;
            } else if (bytes[2] != 71) {
                return null;
            } else {
                MyID3v1 id3v1 = new MyID3v1();
                MusicMetadata tags = id3v1.parseTags(bytes);
                return new ID3Tag.ID3TagV1(bytes, tags);
            }
        }
    }

    @Nullable
    private static ID3Tag.ID3TagV2 readID3v2(@NonNull final File file, final boolean hasId3v1) {
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

        final MyID3v2Read parser = new MyID3v2Read(new ByteArrayInputStream(bytes), false);
        while (!parser.isComplete()) {
            parser.iteration();
        }

        if (parser.isError()) {
            parser.dump();
            return null;
        }
        if (!parser.hasTags()) {
            return null;
        }
        final Vector tags = parser.getTags();
        final MusicMetadata values = (new ID3v2DataMapping()).process(tags);
        return new ID3Tag.ID3TagV2(parser.getVersionMajor(), parser.getVersionMinor(), bytes, values, tags);
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

    @NonNull
    private static byte[] readArray(InputStream is, int length) throws IOException {
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

    /**
     * @param file     Check if file exists before call.
     * @param hasId3v1
     * @return
     */
    @Nullable
    private static byte[] readID3v2Tail(@NonNull File file, final boolean hasId3v1) {
        long length = file.length();
        int index = hasId3v1 ? 128 : 0;
        index += 10;
        if ((long) index > length) {
            return null;
        } else {
            BufferedInputStream is = null;

            byte[] headerAndBody = null;
            try {
                FileInputStream is1 = new FileInputStream(file);
                is = new BufferedInputStream(is1, 8192);
                is.skip(length - (long) index);
                byte[] footer = readArray(is, 10);
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
                headerAndBody = readArray(is, 10 + bodyLength + 10);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ignored) {
                }
            }
            return headerAndBody;
        }
    }

    /**
     * @param file Check if file exists before call.
     * @return
     * @throws IOException
     */
    @Nullable
    private static byte[] readID3v2Head(@NonNull final File file) throws IOException {
        long length = file.length();
        if (length < 10L) {
            return null;
        } else {
            BufferedInputStream is = null;

            try {
                FileInputStream is1 = new FileInputStream(file);
                is = new BufferedInputStream(is1, 8192);
                byte[] header = readArray(is, 10);
                if (header[0] == 73 && header[1] == 68 && header[2] == 51) {
                    byte flags = header[5];
                    boolean hasFooter = (flags & 16) > 0;
                    Number tagLength = MyID3v2Read.readSynchsafeInt(header, 6);
                    if (tagLength != null) {
                        int bodyLength = tagLength.intValue();
                        if (hasFooter) {
                            bodyLength += 10;
                        }

                        if ((long) (10 + bodyLength) <= length) {
                            byte[] body = readArray(is, bodyLength);
                            byte[] result = new byte[header.length + body.length];
                            System.arraycopy(header, 0, result, 0, header.length);
                            System.arraycopy(body, 0, result, header.length, body.length);
                            return result;
                        }
                    }
                }
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ignored) {
                }
            }
            return null;
        }
    }
}
