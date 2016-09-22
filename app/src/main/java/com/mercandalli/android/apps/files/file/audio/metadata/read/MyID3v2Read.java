package com.mercandalli.android.apps.files.file.audio.metadata.read;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/* package */ class MyID3v2Read {

    private static final String LEGAL_FRAME_ID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int kHIGH_BIT = 128;
    private final InputStream mInputStream;
    private final boolean async;
    private boolean complete = false;
    private boolean error = false;
    private boolean no_tag = false;
    private boolean stream_complete = false;
    private boolean header_read = false;
    private boolean tagRead = false;
    private int index = 0;
    private int last = -1;
    private byte versionMajor;
    private byte versionMinor;
    private boolean tagUnsynchronization = false;
    private boolean tagCompression = false;
    private boolean tagExtendedHeader = false;
    private boolean tagExperimentalIndicator = false;
    private boolean tagFooterPresent = false;
    @NonNull
    private final Vector mTags = new Vector();
    @NonNull
    private final ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();
    private long mBytesRead = 0L;
    private int tagLength = 0;
    @NonNull
    private final byte[] mBuffer = new byte[1024];
    private String errorMessage = null;

    public MyID3v2Read(final InputStream inputStream, final boolean async) {
        this.mInputStream = inputStream;
        this.async = async;
    }

    public void dump() {
    }

    public boolean isComplete() {
        return this.complete || this.error || this.no_tag;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean hasTags() {
        return !this.error && this.complete && !this.no_tag;
    }

    public boolean iteration() {
        if (this.isComplete()) {
            return true;
        }
        if (!this.read()) {
            return false;
        }
        if (this.isComplete()) {
            return true;
        }
        if (!this.header_read) {
            if (this.mBytesRead < 10L) {
                if (this.stream_complete) {
                    this.error = true;
                }
                return true;
            }
            this.readHeader();
        }
        if (!this.tagRead) {
            if (this.mBytesRead < (long) this.tagLength) {
                if (this.stream_complete) {
                    this.error = true;
                }
                return true;
            }
            this.readTag();
            this.complete = true;
        }
        return true;
    }

    private int readInt3(byte[] bytes, boolean check_tagLength) {
        if (this.index + 2 >= this.tagLength && check_tagLength) {
            this.setError("readInt3(index: " + this.index + ", tagLength: " + this.tagLength);
            return -1;
        } else if (this.index + 3 >= bytes.length) {
            this.setError("readInt3(index: " + this.index + ", bytes.length: " + bytes.length);
            return -1;
        } else {
            int[] array = new int[]{255 & bytes[this.index++], 255 & bytes[this.index++], 255 & bytes[this.index++]};
            return array[0] << 16 | array[1] << 8 | array[2] << 0;
        }
    }

    public static Number readSynchsafeInt(byte[] bytes, int start) {
        if (start + 3 >= bytes.length) {
            return null;
        } else {
            int[] var10000 = new int[4];
            int index = start + 1;
            var10000[0] = 255 & bytes[start];
            var10000[1] = 255 & bytes[index++];
            var10000[2] = 255 & bytes[index++];
            var10000[3] = 255 & bytes[index++];
            int[] array = var10000;

            int result;
            for (result = 0; result < array.length; ++result) {
                if ((array[result] & 128) > 0) {
                    array[result] &= 128;
                }
            }
            result = array[0] << 21 | array[1] << 14 | array[2] << 7 | array[3] << 0;
            return new Integer(result);
        }
    }

    private int readSynchsafeInt(byte[] bytes, boolean check_tagLength) {
        if (this.index + 3 >= this.tagLength && check_tagLength) {
            this.setError("readSynchsafeInt(index: " + this.index + ", tagLength: " + this.tagLength);
            return -1;
        } else if (this.index + 3 >= bytes.length) {
            this.setError("readSynchsafeInt(index: " + this.index + ", bytes.length: " + bytes.length);
            return -1;
        } else {
            int[] array = new int[]{255 & bytes[this.index++], 255 & bytes[this.index++], 255 & bytes[this.index++], 255 & bytes[this.index++]};

            int result;
            for (result = 0; result < array.length; ++result) {
                if ((array[result] & 128) > 0) {
                    array[result] &= 128;
                }
            }

            result = array[0] << 21 | array[1] << 14 | array[2] << 7 | array[3] << 0;
            return result;
        }
    }

    private int readInt(byte[] bytes, boolean check_tagLength) {
        if (this.index + 3 >= this.tagLength && check_tagLength) {
            this.setError("readInt(index: " + this.index + ", tagLength: " + this.tagLength);
            return -1;
        } else if (this.index + 3 >= bytes.length) {
            this.setError("readInt(index: " + this.index + ", bytes.length: " + bytes.length);
            return -1;
        } else {
            int[] array = new int[]{255 & bytes[this.index++], 255 & bytes[this.index++], 255 & bytes[this.index++], 255 & bytes[this.index++]};
            return array[0] << 24 | array[1] << 16 | array[2] << 8 | array[3] << 0;
        }
    }

    private int readShort(byte[] bytes) {
        if (this.index + 1 < this.tagLength && this.index + 1 < bytes.length) {
            byte[] array = new byte[]{bytes[this.index++], bytes[this.index++]};
            return array[0] << 8 | array[1] << 0;
        } else {
            this.setError("readShort(index: " + this.index + ", tagLength: " + this.tagLength + ", bytes.length: " + bytes.length);
            return -1;
        }
    }

    private void readHeader() {
        byte[] bytes = this.mByteArrayOutputStream.toByteArray();
        if (bytes.length < 10) {
            this.setError("missing header");
        } else {
            if (bytes[this.index++] != 73) {
                this.no_tag = true;
            } else if (bytes[this.index++] != 68) {
                this.no_tag = true;
            } else if (bytes[this.index++] != 51) {
                this.no_tag = true;
            }

            if (!this.error && !this.no_tag) {
                this.versionMajor = bytes[this.index++];
                this.versionMinor = bytes[this.index++];

                if (this.versionMajor >= 2 && this.versionMajor <= 4) {
                    long flags = (long) bytes[this.index++];
                    long workingFlags = flags;
                    if (this.versionMajor == 2) {
                        if ((flags & 128L) > 0L) {
                            this.tagUnsynchronization = true;
                            workingFlags = flags ^ 128L;
                        }

                        if ((workingFlags & 64L) > 0L) {
                            this.tagCompression = true;
                            workingFlags ^= 64L;
                        }
                    } else if (this.versionMajor == 3) {
                        if ((flags & 128L) > 0L) {
                            this.tagUnsynchronization = true;
                            workingFlags = flags ^ 128L;
                        }

                        if ((workingFlags & 64L) > 0L) {
                            this.tagExtendedHeader = true;
                            workingFlags ^= 64L;
                        }

                        if ((workingFlags & 32L) > 0L) {
                            this.tagExperimentalIndicator = true;
                            workingFlags ^= 32L;
                        }

                        if ((workingFlags & 16L) > 0L) {
                            workingFlags ^= 16L;
                        }
                    } else {
                        if (this.versionMajor != 4) {
                            this.setError("Unknown id3v2 Major Version: " + this.versionMajor);
                            return;
                        }

                        if ((flags & 128L) > 0L) {
                            this.tagUnsynchronization = true;
                            workingFlags = flags ^ 128L;
                        }

                        if ((workingFlags & 64L) > 0L) {
                            this.tagExtendedHeader = true;
                            workingFlags ^= 64L;
                        }

                        if ((workingFlags & 32L) > 0L) {
                            this.tagExperimentalIndicator = true;
                            workingFlags ^= 32L;
                        }

                        if ((workingFlags & 16L) > 0L) {
                            this.tagFooterPresent = true;
                            workingFlags ^= 16L;
                        }
                    }

                    if (workingFlags > 0L) {
                        this.setError("Unknown id3v2 tag flags(id3v2 version: " + this.versionMajor + "): " + Long.toHexString(flags));
                    } else {

                        this.tagLength = this.readSynchsafeInt(bytes, false);
                        this.tagLength += 10;
                        this.last = this.tagLength;
                        if (this.tagFooterPresent) {
                            this.tagLength += 10;
                        }

                        this.header_read = true;
                        if (this.index != 10) {
                            this.setError("index!=kHEADER_SIZE");
                        }
                    }
                } else {
                    this.setError("Unknown id3v2 Major Version: " + this.versionMajor);
                }
            }
        }
    }

    private byte[] ununsynchronize(byte[] bytes) {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        int i = 0;

        while (i < bytes.length) {
            byte b = bytes[i++];
            result.write(b);
            if ((255 & b) == 255) {
                if (i >= bytes.length) {
                    break;
                }

                byte b1 = bytes[i];
                if ((255 & b1) == 0) {
                    ++i;
                }
            }
        }

        bytes = result.toByteArray();
        return bytes;
    }

    private String parseFrameID(byte[] bytes) {
        for (final byte aByte : bytes) {
            int b = 255 & aByte;
            char c = (char) b;
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".indexOf(c) < 0) {
                this.setError("invalid id3v2 frame id byte: " + Integer.toHexString(b));
                return null;
            }
        }
        return new String(bytes);
    }

    private boolean isZeroFrameId(byte[] bytes) {
        for (final byte aByte : bytes) {
            if ((255 & aByte) > 0) {
                return false;
            }
        }
        return true;
    }

    private void readTag() {
        byte[] bytes = this.mByteArrayOutputStream.toByteArray();
        if (bytes.length < this.tagLength) {
            this.setError("missing tag");
        } else {
            if (this.tagUnsynchronization) {
                bytes = this.ununsynchronize(bytes);
            }

            if (this.tagExtendedHeader) {
                this.index += 4;
            }

            for (int tag_num = 0; this.index + 7 < this.last && !this.error && this.index + 7 < this.last; ++tag_num) {
                byte[] frameID;
                if (this.versionMajor >= 3) {
                    frameID = new byte[]{bytes[this.index++], bytes[this.index++], bytes[this.index++], bytes[this.index++]};
                } else {
                    frameID = new byte[]{bytes[this.index++], bytes[this.index++], bytes[this.index++]};
                }

                if (this.isZeroFrameId(frameID)) {
                    break;
                }

                String frameIDString = this.parseFrameID(frameID);
                if (frameIDString == null) {
                    break;
                }

                int frameLength;
                if (this.versionMajor >= 4) {
                    frameLength = this.readSynchsafeInt(bytes, true);
                } else if (this.versionMajor >= 3) {
                    frameLength = this.readInt(bytes, true);
                } else {
                    frameLength = this.readInt3(bytes, true);
                }

                int maxTagLength = this.tagLength - this.index;
                if (this.versionMajor >= 3) {
                    maxTagLength += 2;
                }

                if (frameLength == 0) {
                    break;
                }

                if (frameLength > maxTagLength || frameLength < 0) {
                    this.setError("bad frame length(" + tag_num + ": " + frameIDString + "): " + frameLength + " (" + new String(frameID));
                    break;
                }

                final ID3v2FrameFlags flags = new ID3v2FrameFlags();
                int dataLengthIndicator;
                if (this.versionMajor != 3 && this.versionMajor != 4) {
                    if (this.versionMajor != 2) {
                        this.setError("Unknown ID3v2 version: " + this.versionMajor);
                        return;
                    }
                } else {
                    dataLengthIndicator = this.readShort(bytes);
                    int frameBytes = dataLengthIndicator;
                    if (this.versionMajor == 3) {
                        if ((dataLengthIndicator & '耀') > 0) {
                            flags.setTagAlterPreservation(true);
                            frameBytes = dataLengthIndicator ^ '耀';
                        }

                        if ((frameBytes & 16384) > 0) {
                            flags.setFileAlterPreservation(true);
                            frameBytes ^= 16384;
                        }

                        if ((frameBytes & 8192) > 0) {
                            flags.setReadOnly(true);
                            frameBytes ^= 8192;
                        }

                        if ((frameBytes & 32) > 0) {
                            flags.setGroupingIdentity(true);
                            frameBytes ^= 32;
                        }

                        if ((frameBytes & 128) > 0) {
                            flags.setCompression(true);
                            frameBytes ^= 128;
                        }

                        if ((frameBytes & 64) > 0) {
                            flags.setEncryption(true);
                            frameBytes ^= 64;
                        }
                    } else if (this.versionMajor == 4) {
                        if ((dataLengthIndicator & 16384) > 0) {
                            flags.setTagAlterPreservation(true);
                            frameBytes = dataLengthIndicator ^ 16384;
                        }

                        if ((frameBytes & 8192) > 0) {
                            flags.setFileAlterPreservation(true);
                            frameBytes ^= 8192;
                        }

                        if ((frameBytes & 4096) > 0) {
                            flags.setReadOnly(true);
                            frameBytes ^= 4096;
                        }

                        if ((frameBytes & 64) > 0) {
                            flags.setGroupingIdentity(true);
                            frameBytes ^= 64;
                        }

                        if ((frameBytes & 8) > 0) {
                            flags.setCompression(true);
                            frameBytes ^= 8;
                        }

                        if ((frameBytes & 4) > 0) {
                            flags.setEncryption(true);
                            frameBytes ^= 4;
                        }

                        if ((frameBytes & 2) > 0) {
                            flags.setUnsynchronisation(true);
                            frameBytes ^= 2;
                        }

                        if ((frameBytes & 1) > 0) {
                            flags.setDataLengthIndicator(true);
                            frameBytes ^= 1;
                        }
                    }

                    if (frameBytes > 0) {
                        this.setError("Unknown id3v2 frame flags(id3v2 version: " + this.versionMajor + "): " + Long.toHexString((long) dataLengthIndicator));
                        return;
                    }
                }

                if (frameLength > 0) {
                    if (flags.isDataLengthIndicator()) {
                        dataLengthIndicator = this.readSynchsafeInt(bytes, true);
                        frameLength -= 4;
                    }

                    byte[] var14 = new byte[frameLength];
                    System.arraycopy(bytes, this.index, var14, 0, frameLength);
                    this.index += frameLength;
                    if (flags.isUnsynchronisation()) {
                        var14 = this.ununsynchronize(var14);
                    }

                    try {
                        if (frameID[0] == 84) {
                            this.readTextTag(frameLength, frameID, var14, frameIDString);
                        } else {
                            this.readDataTag(frameLength, frameID, var14, frameIDString, flags);
                        }
                    } catch (IOException var12) {
                        this.setError(var12.getMessage());
                        return;
                    }
                }
            }
            this.tagRead = true;
        }
    }

    private void readDataTag(
            final int frameLength,
            final byte[] frameID,
            final byte[] frameBytes,
            final String frameIDString,
            final ID3v2FrameFlags flags) throws IOException {
        byte frameIndex;
        int ownerIdentifier;
        int var15;
        if (!frameIDString.equals("COMM") && !frameIDString.equals("COM")) {
            int var18;
            if (!frameIDString.equals("PIC") && !frameIDString.equals("APIC")) {
                if (frameIDString.equals("PRIV")) {
                    frameIndex = 0;
                    byte var19 = 0;
                    String var16 = this.readString(frameBytes, frameIndex, var19);
                    var18 = this.findStringDataLength(frameBytes, frameIndex, var19);
                    int var10000 = frameIndex + var18;
                    if (var16.startsWith("WM/")) {
                        return;
                    }
                } else {
                    this.mTags.add(new MyID3v2FrameData(frameIDString, frameBytes, flags));
                }
            } else {
                frameIndex = 0;
                var15 = frameIndex + 1;
                ownerIdentifier = 255 & frameBytes[frameIndex];
                int imageData;
                String var17;
                if (frameIDString.equals("PIC")) {
                    var18 = 255 & frameBytes[var15++];
                    int var20 = 255 & frameBytes[var15++];
                    imageData = 255 & frameBytes[var15++];
                    String var23 = "" + (char) var18 + (char) var20 + (char) imageData;
                    var17 = var23.toLowerCase();
                    if (!var17.startsWith("image/")) {
                        var17 = "image/" + var17;
                    }
                } else {
                    var17 = this.readString(frameBytes, var15, ownerIdentifier);
                    var18 = this.findStringDataLength(frameBytes, var15, ownerIdentifier);
                    var15 += var18;
                }

                var18 = 255 & frameBytes[var15++];
                String var21 = this.readString(frameBytes, var15, ownerIdentifier);
                imageData = this.findStringDataLength(frameBytes, var15, ownerIdentifier);
                var15 += imageData;
                byte[] var22 = new byte[frameBytes.length - var15];
                System.arraycopy(frameBytes, var15, var22, 0, var22.length);
                this.mTags.add(new MyID3v2FrameImage(frameIDString, frameBytes, flags, var22, var17, var21, var18));
            }
        } else {
            if (frameBytes.length < 5) {
                this.setError("Unexpected COMM frame length(1): " + frameLength + " (" + new String(frameID));
                return;
            }

            frameIndex = 0;
            var15 = frameIndex + 1;
            ownerIdentifier = 255 & frameBytes[frameIndex];
            this.readString(frameBytes, var15, ownerIdentifier);
            int extension = this.findStringDataLength(frameBytes, var15, ownerIdentifier);
            var15 += extension;
            final String comment = this.readString(frameBytes, var15, ownerIdentifier);
            this.mTags.add(new MyID3v2FrameText(frameIDString, frameBytes, comment));
        }
    }

    private void readTextTag(int frameLength, byte[] frameID, byte[] frameBytes, String frameIDString) throws IOException {
        if (frameLength != 1) {
            if (frameLength < 2) {
                this.setError("Unexpected frame length(1): " + frameLength + " (" + new String(frameID));
            } else {
                int charEncodingCode = 255 & frameBytes[0];
                byte frameIndex = 1;
                String value = this.readString(frameBytes, frameIndex, charEncodingCode);
                String value2 = null;
                MyID3v2FrameText tag;
                if (frameIDString.equals("TXXX")) {
                    int stringDataLength = this.findStringDataLength(frameBytes, frameIndex, charEncodingCode);
                    int frameIndex1 = frameIndex + stringDataLength;
                    value2 = this.readString(frameBytes, frameIndex1, charEncodingCode);
                    tag = new MyID3v2FrameText(frameIDString, frameBytes, value, value2);
                } else {
                    tag = new MyID3v2FrameText(frameIDString, frameBytes, value);
                }
                this.mTags.add(tag);
            }
        }
    }

    private String getCharacterEncodingName(final int charEncodingCode) throws IOException {
        switch (charEncodingCode) {
            case 0:
                return "ISO-8859-1";
            case 1:
                return "UTF-16";
            case 2:
                return "UTF-16";
            case 3:
                return "UTF-8";
            default:
                throw new IOException("Unknown charEncodingCode: " + charEncodingCode);
        }
    }

    private String readString(byte[] bytes, int start, int charEncodingCode) throws IOException {
        UnicodeMetrics unicodeMetrics = UnicodeMetrics.getInstance(charEncodingCode);
        int unicodeMetricsEnd = unicodeMetrics.findEndWithoutTerminator(bytes, start);
        int unicodeMetricsLength = unicodeMetricsEnd - start;
        String charsetName = this.getCharacterEncodingName(charEncodingCode);
        return new String(bytes, start, unicodeMetricsLength, charsetName);
    }

    private int findStringDataLength(byte[] bytes, int start, int charEncodingCode) throws IOException {
        UnicodeMetrics unicodeMetrics = UnicodeMetrics.getInstance(charEncodingCode);
        int unicodeMetricsEnd = unicodeMetrics.findEndWithTerminator(bytes, start);
        return unicodeMetricsEnd - start;
    }

    private boolean read() {
        try {
            if (mInputStream.available() < 0) {
                this.stream_complete = true;
                return true;
            } else if (!this.async && mInputStream.available() < 1) {
                this.stream_complete = true;
                return true;
            } else if (mInputStream.available() < 1) {
                return false;
            } else {
                final int e = mInputStream.read(this.mBuffer);
                if (e < 1) {
                    this.setError("unexpected stream closed");
                    return true;
                } else {
                    this.mByteArrayOutputStream.write(this.mBuffer, 0, e);
                    this.mBytesRead += (long) e;
                    return true;
                }
            }
        } catch (IOException var2) {
            this.setError(var2.getMessage());
            return true;
        }
    }

    private void setError(String s) {
        this.error = true;
        this.errorMessage = s;
    }

    public Vector getTags() {
        return this.mTags;
    }

    public byte getVersionMajor() {
        return this.versionMajor;
    }

    public byte getVersionMinor() {
        return this.versionMinor;
    }
}
