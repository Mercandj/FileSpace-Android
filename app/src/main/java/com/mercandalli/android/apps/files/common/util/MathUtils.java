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
package com.mercandalli.android.apps.files.common.util;

import java.util.Random;

public class MathUtils {

    public static int random(final int min, final int max) {
        return random(new Random(), min, max);
    }

    public static int random(final Random r, final int min, final int max) {
        return r.nextInt(max - min + 1) + min;
    }

    public static int min(final int i, final int j) {
        if (i > j) {
            return j;
        }
        return i;
    }

    public static int max(final int i, final int j) {
        if (i < j) {
            return j;
        }
        return i;
    }

    public static int abs(final int i) {
        if (i < 0) {
            return -i;
        }
        return i;
    }

}
