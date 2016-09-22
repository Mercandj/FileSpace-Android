package com.mercandalli.android.apps.files.file.audio.metadata.read;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.IOException;

/* package */ abstract class UnicodeMetrics {
    public UnicodeMetrics() {
    }

    public final int findEndWithTerminator(byte[] bytes, int index) throws IOException {
        return this.findEnd(bytes, index, true);
    }

    public final int findEndWithoutTerminator(byte[] bytes, int index) throws IOException {
        return this.findEnd(bytes, index, false);
    }

    protected abstract int findEnd(byte[] var1, int var2, boolean var3) throws IOException;

    public static UnicodeMetrics getInstance(int charEncodingCode) throws IOException {
        switch (charEncodingCode) {
            case 0:
                return new UnicodeMetrics.UnicodeMetricsASCII();
            case 1:
                return new UnicodeMetrics.UnicodeMetricsUTF16WithBOM();
            case 2:
                return new UnicodeMetrics.UnicodeMetricsUTF16NoBOM();
            case 3:
                return new UnicodeMetrics.UnicodeMetricsUTF8();
            default:
                throw new IOException("Unknown char encoding code: " + charEncodingCode);
        }
    }

    private static class UnicodeMetricsASCII extends UnicodeMetrics {
        private UnicodeMetricsASCII() {
        }

        public int findEnd(byte[] bytes, int index, boolean includeTerminator) throws IOException {
            for (int i = index; i < bytes.length; ++i) {
                if (bytes[i] == 0) {
                    return includeTerminator ? i + 1 : i;
                }
            }

            return bytes.length;
        }
    }

    private abstract static class UnicodeMetricsUTF16 extends UnicodeMetrics {
        protected static final int BYTE_ORDER_BIG_ENDIAN = 0;
        protected static final int BYTE_ORDER_LITTLE_ENDIAN = 1;
        protected int byteOrder = 0;

        public UnicodeMetricsUTF16(int byteOrder) {
            this.byteOrder = byteOrder;
        }

        public int findEnd(byte[] bytes, int index, boolean includeTerminator) throws IOException {
            while (index != bytes.length) {
                if (index > bytes.length - 1) {
                    throw new IOException("Terminator not found.");
                }

                int c1 = 255 & bytes[index++];
                int c2 = 255 & bytes[index++];
                int msb1 = this.byteOrder == 0 ? c1 : c2;
                if (c1 == 0 && c2 == 0) {
                    return includeTerminator ? index : index - 2;
                }

                if (msb1 >= 216) {
                    if (index > bytes.length - 1) {
                        throw new IOException("Terminator not found.");
                    }

                    int c3 = 255 & bytes[index++];
                    int c4 = 255 & bytes[index++];
                    int msb2 = this.byteOrder == 0 ? c3 : c4;
                    if (msb2 < 220) {
                        throw new IOException("Invalid code point.");
                    }
                }
            }

            return bytes.length;
        }
    }

    private static class UnicodeMetricsUTF16NoBOM extends UnicodeMetrics.UnicodeMetricsUTF16 {
        public UnicodeMetricsUTF16NoBOM() {
            super(0);
        }
    }

    private static class UnicodeMetricsUTF16WithBOM extends UnicodeMetrics.UnicodeMetricsUTF16 {
        public UnicodeMetricsUTF16WithBOM() {
            super(0);
        }

        public int findEnd(byte[] bytes, int index, boolean includeTerminator) throws IOException {
            if (index >= bytes.length - 1) {
                throw new IOException("Missing BOM.");
            } else {
                int c1 = 255 & bytes[index++];
                int c2 = 255 & bytes[index++];
                if (c1 == 255 && c2 == 254) {
                    this.byteOrder = 1;
                } else {
                    if (c1 != 254 || c2 != 255) {
                        throw new IOException("Invalid byte order mark.");
                    }

                    this.byteOrder = 0;
                }

                return super.findEnd(bytes, index, includeTerminator);
            }
        }
    }

    private static class UnicodeMetricsUTF8 extends UnicodeMetrics {
        private UnicodeMetricsUTF8() {
        }

        public int findEnd(byte[] bytes, int index, boolean includeTerminator) throws IOException {
            while (index != bytes.length) {
                if (index > bytes.length) {
                    throw new IOException("Terminator not found.");
                }

                int c1 = 255 & bytes[index++];
                if (c1 == 0) {
                    return includeTerminator ? index : index - 1;
                }

                if (c1 > 127) {
                    int c2;
                    if (c1 <= 223) {
                        if (index >= bytes.length) {
                            throw new IOException("Invalid unicode.");
                        }

                        c2 = 255 & bytes[index++];
                        if (c2 < 128 || c2 > 191) {
                            throw new IOException("Invalid code point.");
                        }
                    } else {
                        int c3;
                        if (c1 <= 239) {
                            if (index >= bytes.length - 1) {
                                throw new IOException("Invalid unicode.");
                            }

                            c2 = 255 & bytes[index++];
                            if (c2 >= 128 && c2 <= 191) {
                                c3 = 255 & bytes[index++];
                                if (c3 >= 128 && c3 <= 191) {
                                    continue;
                                }

                                throw new IOException("Invalid code point.");
                            }

                            throw new IOException("Invalid code point.");
                        } else {
                            if (c1 <= 244) {
                                if (index >= bytes.length - 2) {
                                    throw new IOException("Invalid unicode.");
                                }

                                c2 = 255 & bytes[index++];
                                if (c2 >= 128 && c2 <= 191) {
                                    c3 = 255 & bytes[index++];
                                    if (c3 >= 128 && c3 <= 191) {
                                        int c4 = 255 & bytes[index++];
                                        if (c4 >= 128 && c4 <= 191) {
                                            continue;
                                        }

                                        throw new IOException("Invalid code point.");
                                    }

                                    throw new IOException("Invalid code point.");
                                }

                                throw new IOException("Invalid code point.");
                            }

                            throw new IOException("Invalid code point.");
                        }
                    }
                }
            }

            return bytes.length;
        }
    }
}
