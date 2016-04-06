package com.mercandalli.android.apps.files.common.util;

import android.support.annotation.Nullable;

import java.text.Normalizer;
import java.util.StringTokenizer;

public class StringUtils {
    public static String[] getWords(String sentence) {
        if (sentence == null) {
            return null;
        }
        final StringTokenizer stToken = new StringTokenizer(sentence, " ");
        final int nbToken = stToken.countTokens();
        final String[] messageTab = new String[nbToken];
        int token = 0;
        while (stToken.hasMoreTokens()) {
            messageTab[token] = stToken.nextToken();
            token++;
        }
        return messageTab;
    }

    public static String normalizeString(final String message) {
        if (message == null) {
            return null;
        }
        return replaceAccents(message.toLowerCase());
    }

    public static String replaceAccents(String message) {
        if (message == null) {
            return null;
        }
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = message.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return message;
    }

    public static boolean isNullOrEmpty(final String str) {
        return str == null || str.replaceAll(" ", "").equals("");
    }

    public static String toEmptyIfNull(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    public static String intToShortString(final int nb) {
        if (nb < 1000) {
            return nb + "";
        }
        int exp = (int) (Math.log(nb) / Math.log(1000));
        String pre = "" + ("KMGTPE").charAt(exp - 1);
        return String.format("%.1f %s", nb / Math.pow(1000, exp), pre);
    }

    public static String longToShortString(long nb) {
        if (nb < 1000) {
            return nb + "";
        }
        int exp = (int) (Math.log(nb) / Math.log(1000));
        String pre = "" + ("KMGTPE").charAt(exp - 1);
        return String.format("%.1f %s", nb / Math.pow(1000, exp), pre);
    }

    public static String capitalize(final String str) {
        return capitalize(str, null);
    }

    public static String capitalize(final String str, final char... delimiters) {
        final int delimLen = delimiters == null ? -1 : delimiters.length;
        if (isNullOrEmpty(str) || delimLen == 0) {
            return str;
        }
        final char[] buffer = str.toCharArray();
        boolean capitalizeNext = true;
        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (isDelimiter(ch, delimiters)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer[i] = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
        }
        return new String(buffer);
    }

    public static String uppercase(final String str) {
        return str.toUpperCase();
    }

    public static String substring(final String str, final int nb_first) {
        if (str == null) {
            return "";
        }
        return str.substring(0, nb_first);
    }

    private static boolean isDelimiter(final char ch, final char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (final char delimiter : delimiters) {
            if (ch == delimiter) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEquals(@Nullable final String s1, @Nullable final String s2) {
        return s1 == null && s2 == null || !(s1 == null || s2 == null) && s1.equals(s2);
    }
}
