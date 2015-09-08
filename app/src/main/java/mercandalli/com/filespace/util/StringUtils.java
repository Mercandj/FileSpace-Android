/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.util;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Jonathan on 17/05/2015.
 */
public class StringUtils {

    public static String[] getWords(String sentence) {
        if(sentence == null)
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
        if(message == null)
            return null;
        return remplaceAccents(message.toLowerCase());
    }

    public static String remplaceAccents(String message) {
        if(message == null)
            return null;
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = message.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return message;
    }

    public static boolean isNullOrEmpty(String str) {
        if(str == null)
            return true;
        return str.replaceAll(" ","").equals("");
    }

    public static String intToShortString(int nb) {
        if (nb < 1000) return nb + "";
        int exp = (int) (Math.log(nb) / Math.log(1000));
        String pre = "" + ("KMGTPE").charAt(exp-1);
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
        if(str == null)
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

    public static class Utf8Converter {
        private static final Map<String, String> CP1252_CONVERSION = new HashMap<String, String>() {
            {
                put("\u00C3\u20AC", "�");
                put("\u00C3\uFFFD", "�");
                put("\u00C3\u201A", "�");
                put("\u00C3\u0192", "�");
                put("\u00C3\u201E", "�");
                put("\u00C3\u2026", "�");
                put("\u00C3\u2020", "�");
                put("\u00C3\u2021", "�");
                put("\u00C3\u02C6", "�");
                put("\u00C3\u2030", "�");
                put("\u00C3\u0160", "�");
                put("\u00C3\u2039", "�");
                put("\u00C3\u0152", "�");
                put("\u00C3\uFFFD", "�");
                put("\u00C3\u017D", "�");
                put("\u00C3\uFFFD", "�");
                put("\u00C3\uFFFD", "�");
                put("\u00C3\u2018", "�");
                put("\u00C3\u2019", "�");
                put("\u00C3\u201C", "�");
                put("\u00C3\u201D", "�");
                put("\u00C3\u2022", "�");
                put("\u00C3\u2013", "�");
                put("\u00C3\u02DC", "�");
                put("\u00C3\u2122", "�");
                put("\u00C3\u0161", "�");
                put("\u00C3\u203A", "�");
                put("\u00C3\u0153", "�");
                put("\u00C3\uFFFD", "�");
                put("\u00C3\u017E", "�");
                put("\u00C3\u0178", "�");
                put("\u00C3\u00A0", "�");
                put("\u00C3\u00A1", "�");
                put("\u00C3\u00A2", "�");
                put("\u00C3\u00A3", "�");
                put("\u00C3\u00A4", "�");
                put("\u00C3\u00A5", "�");
                put("\u00C3\u00A6", "�");
                put("\u00C3\u00A7", "�");
                put("\u00C3\u00A8", "�");
                put("\u00C3\u00A9", "�");
                put("\u00C3\u00AA", "�");
                put("\u00C3\u00AB", "�");
                put("\u00C3\u00AC", "�");
                put("\u00C3\u00AD", "�");
                put("\u00C3\u00AE", "�");
                put("\u00C3\u00AF", "�");
                put("\u00C3\u00B0", "�");
                put("\u00C3\u00B1", "�");
                put("\u00C3\u00B2", "�");
                put("\u00C3\u00B3", "�");
                put("\u00C3\u00B4", "�");
                put("\u00C3\u00B5", "�");
                put("\u00C3\u00B6", "�");
                put("\u00C3\u00B8", "�");
                put("\u00C3\u00B9", "�");
                put("\u00C3\u00BA", "�");
                put("\u00C3\u00BB", "�");
                put("\u00C3\u00BC", "�");
                put("\u00C3\u00BD", "�");
                put("\u00C3\u00BE", "�");
                put("\u00C3\u00BF", "�");
            }
        };

        public static String convertToUTF8(String value) {
            for (String source : CP1252_CONVERSION.keySet()) {
                value = value.replace(source, CP1252_CONVERSION.get(source));
            }
            return value;
        }

    }
}
