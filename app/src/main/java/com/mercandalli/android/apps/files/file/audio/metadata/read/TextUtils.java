package com.mercandalli.android.apps.files.file.audio.metadata.read;

import java.util.Vector;

/* package */ class TextUtils {
    static String newline = System.getProperty("line.separator");

    public static final String kALPHABET_NUMERALS = "0123456789";
    public static final String kALPHABET_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String kALPHABET_UPPERCASE = "abcdefghijklmnopqrstuvwxyz".toUpperCase();
    public static final String kALPHABET;
    public static final String kFILENAME_SAFE;

    static {
        kALPHABET = "abcdefghijklmnopqrstuvwxyz" + kALPHABET_UPPERCASE;
        kFILENAME_SAFE = kALPHABET + "0123456789" + " ._-()&,[]";
    }

    private TextUtils() {
    }

    public static String filter(String s, String filter) {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (filter.indexOf(c) >= 0) {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static String head(String s, int count) {
        if (s != null && s.length() >= 1) {
            String[] lines = split(s, newline);
            if (lines.length < count) {
                count = lines.length;
            }

            String[] lines2 = new String[count];
            System.arraycopy(lines, 0, lines2, 0, count);
            return join(lines2, newline);
        } else {
            return s;
        }
    }

    public static String[] split(String s, String token) {
        Vector result;
        int index;
        for (result = new Vector(); (index = s.indexOf(token)) >= 0; s = s.substring(index + token.length())) {
            result.add(s.substring(0, index));
        }

        result.add(s);
        String[] splits = new String[result.size()];

        for (int i = 0; i < result.size(); ++i) {
            splits[i] = (String) result.get(i);
        }

        return splits;
    }

    public static String join(String[] splits, String token) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < splits.length; ++i) {
            if (i > 0) {
                result.append(token);
            }
            result.append(splits[i]);
        }
        return result.toString();
    }
}

