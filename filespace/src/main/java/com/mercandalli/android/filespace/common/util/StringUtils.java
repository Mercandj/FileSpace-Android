/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.filespace.common.util;

import java.text.Normalizer;
import java.util.StringTokenizer;

/**
 * Created by Jonathan on 17/05/2015.
 */
public class StringUtils {

    public static String[] getWords(String sentence) {
        if (sentence == null)
            return null;
        StringTokenizer stToken = new StringTokenizer(sentence, " ");
        int nbToken = stToken.countTokens();
        String[] messageTab = new String[nbToken];
        int token = 0;
        while (stToken.hasMoreTokens()) {
            messageTab[token] = stToken.nextToken();
            token++;
        }
        return messageTab;
    }

    public static String nomalizeString(String message) {
        if (message == null)
            return null;
        return replaceAccents(message.toLowerCase());
    }

    public static String replaceAccents(String message) {
        if (message == null)
            return null;
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = message.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return message;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.replaceAll(" ", "").equals("");
    }

    public static String toEmptyIfNull(String str) {
        if (str == null)
            return "";
        return str;
    }

    public static String intToShortString(int nb) {
        if (nb < 1000) return nb + "";
        int exp = (int) (Math.log(nb) / Math.log(1000));
        String pre = "" + ("KMGTPE").charAt(exp - 1);
        return String.format("%.1f %s", nb / Math.pow(1000, exp), pre);
    }

    public static String longToShortString(long nb) {
        if (nb < 1000) return nb + "";
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
        if (str == null)
            return "";
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
}
