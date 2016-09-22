package com.mercandalli.android.apps.files.file.audio.metadata.read;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

/* package */ class ID3v2DataMapping {

    private static final ID3v2DataMapping.ID3v2TagHandler[] HANDLERS
            = new ID3v2DataMapping.ID3v2TagHandler[]{new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.COMMENT;
        }

        protected Object getKey() {
            return "comment";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.ALBUM;
        }

        protected Object getKey() {
            return "album";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.ARTIST;
        }

        protected Object getKey() {
            return "artist";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.TITLE;
        }

        protected Object getKey() {
            return "title";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.CONTENTTYPE;
        }

        protected Object getKey() {
            return null;
        }

        public void process(MusicMetadata values, MyID3v2FrameText tag) {
            String value = tag.value;

            try {
                if (value == null || value.trim().length() < 1) {
                    return;
                }

                boolean idOnly = Pattern.compile("^\\(\\d+\\)").matcher(value).matches();
                if (idOnly) {
                    int numeric_only = value.indexOf(41);
                    String id = value.substring(1, numeric_only);
                    id = id.trim();
                    if (ID3v2DataMapping.isNumber(id)) {
                        Integer genre = new Integer(id);
                        if (genre.intValue() != 0) {
                            values.put("genre_id", genre);
                            String genre1 = ID3v1Genre.get(genre);
                            if (genre1 != null) {
                                values.put("genre", genre1);
                            }
                        }

                        value = value.substring(numeric_only + 1);
                    }
                } else {
                    boolean numeric_only1 = Pattern.compile("^\\d+$").matcher(value).matches();
                    if (numeric_only1) {
                        Integer id1 = new Integer(value);
                        if (id1.intValue() != 0) {
                            values.put("genre_id", id1);
                            String genre2 = ID3v1Genre.get(id1);
                            if (genre2 != null) {
                                values.put("genre", genre2);
                            }
                        }
                        value = "";
                    }
                }

                if (value.length() > 0) {
                    values.put("genre", value);
                }
            } catch (Throwable ignored) {
            }
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.PUBLISHER;
        }

        protected Object getKey() {
            return "publisher";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.YEAR;
        }

        protected Object getKey() {
            return "year";
        }

        public void process(MusicMetadata values, MyID3v2FrameText tag) {
            try {
                String value = tag.value;
                if (value == null || value.trim().length() < 1) {
                    return;
                }

                value = value.trim();
                if (!ID3v2DataMapping.isNumber(value)) {
                    return;
                }

                Integer number = Integer.valueOf(value);
                values.put("year", number);
            } catch (Throwable ignored) {
            }
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.TRACKNUM;
        }

        protected Object getKey() {
            return "track_number";
        }

        public void process(MusicMetadata values, MyID3v2FrameText tag) {
            try {
                String value = tag.value;
                if (value == null || value.trim().length() < 1) {
                    return;
                }

                if (value.indexOf(47) >= 0) {
                    try {
                        String number = value.substring(value.indexOf(47) + 1);
                        number = number.trim();
                        if (ID3v2DataMapping.isNumber(number)) {
                            values.put("track_count", new Integer(number));
                        }
                    } catch (Throwable ignored) {
                    }
                    value = value.substring(0, value.indexOf(47));
                }

                value = value.trim();
                if (ID3v2DataMapping.isNumber(value)) {
                    values.put("track_number", new Integer(value));
                }
            } catch (Throwable ignored) {
            }
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.SONGLEN;
        }

        protected Object getKey() {
            return "duration_seconds";
        }

        public void process(MusicMetadata values, MyID3v2FrameText tag) {
            try {
                final String value = tag.value;
                if (value == null || value.trim().length() < 1) {
                    return;
                }

                final Long number = Long.valueOf(value) / 1000L;
                if (number.intValue() == 0) {
                    return;
                }
                values.put("duration_seconds", number);
            } catch (Throwable ignored) {
            }
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.COMPOSER;
        }

        protected Object getKey() {
            return "composer";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.CONDUCTOR;
        }

        protected Object getKey() {
            return "conductor";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.BAND;
        }

        protected Object getKey() {
            return "band";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.MIXARTIST;
        }

        protected Object getKey() {
            return "mix_artist";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.LYRICIST;
        }

        protected Object getKey() {
            return "lyricist";
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.USERTEXT;
        }

        protected Object getKey() {
            return null;
        }

        public void process(MusicMetadata values, MyID3v2FrameText tag) {
            if (tag.value != null && tag.value2 != null) {
                String key = tag.value;
                String value = tag.value2;
                if (key.equalsIgnoreCase("engineer")) {
                    values.put("engineer", value);
                }
            }
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.ENCODEDBY;
        }

        protected Object getKey() {
            return null;
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.ENCODERSETTINGS;
        }

        protected Object getKey() {
            return null;
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.MEDIATYPE;
        }

        protected Object getKey() {
            return null;
        }
    }, new ID3v2DataMapping.ID3v2TagHandler() {
        protected ID3FrameType getFrameType() {
            return ID3FrameType.FILETYPE;
        }

        protected Object getKey() {
            return null;
        }
    }};
    private static final Map keyToFrameTypeMap = new HashMap();
    private static final Vector ignoredFrameTypes = new Vector();

    static {
        for (ID3v2TagHandler handler : HANDLERS) {
            Object key = handler.getKey();
            if (key != null) {
                keyToFrameTypeMap.put(key, handler.getFrameType());
            } else {
                ignoredFrameTypes.add(handler.getFrameType());
            }
        }
    }

    public ID3v2DataMapping() {
    }

    public ID3FrameType getID3FrameType(Object key) {
        return key.equals("pictures") ? ID3FrameType.PICTURE : (ID3FrameType) keyToFrameTypeMap.get(key);
    }

    public boolean isIgnoredID3FrameType(ID3FrameType frame_type) {
        return ignoredFrameTypes.contains(frame_type);
    }

    public MusicMetadata process(Vector tags) {
        if (tags == null) {
            return null;
        } else {
            try {
                MusicMetadata e = new MusicMetadata("id3v2");
                for (int i = 0; i < tags.size(); ++i) {
                    Object o = tags.get(i);
                    if (o instanceof MyID3v2FrameImage) {
                        MyID3v2FrameImage tag = (MyID3v2FrameImage) o;
                        ImageData imageData = tag.getImageData();
                        e.addPicture(imageData);
                    } else if (o instanceof MyID3v2FrameText) {
                        MyID3v2FrameText var8 = (MyID3v2FrameText) tags.get(i);
                        this.process(e, var8);
                    }
                }
                return e;
            } catch (Throwable var7) {
                return null;
            }
        }
    }

    private void process(MusicMetadata values, MyID3v2FrameText tag) {
        for (ID3v2TagHandler handler : HANDLERS) {
            if (handler.matches(tag.frameId)) {
                handler.process(values, tag);
                return;
            }
        }
    }

    private static boolean isNumber(String s) {
        Pattern p = Pattern.compile("^-?[0-9]+$");
        return p.matcher(s).matches();
    }

    private abstract static class ID3v2TagHandler {
        private ID3v2TagHandler() {
        }

        protected abstract ID3FrameType getFrameType();

        protected abstract Object getKey();

        public boolean matches(String s) {
            return this.getFrameType().matches(s);
        }

        public void process(MusicMetadata values, MyID3v2FrameText tag) {
            Object key = this.getKey();
            if (key != null) {
                values.put(key, tag.value);
            }
        }
    }
}

