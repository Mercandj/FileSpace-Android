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
}
