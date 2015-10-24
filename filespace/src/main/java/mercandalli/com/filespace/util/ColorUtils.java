package mercandalli.com.filespace.util;
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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.util.Log;

/**
 * Created by Jonathan on 6/19/15.
 */
public class ColorUtils {

    public static int getMutedColor(Bitmap bitmap) {
        return Palette.from(bitmap).generate().getMutedColor(0x000000);
    }

    public static int getDarkMutedColor(Bitmap bitmap) {
        return Palette.from(bitmap).generate().getDarkMutedColor(0x000000);
    }

    public static int colorText(int backgroundColor) {
        return isBrightColor(backgroundColor) ? Color.BLACK : Color.WHITE;
    }

    public static boolean isBrightColor(int color) {
        if (android.R.color.transparent == color)
            return true;

        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};

        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * 0.299 + rgb[1]
                * rgb[1] * 0.587 + rgb[2] * rgb[2] * 0.114);

        Log.d("ColorUtils", "brightness = " + brightness);
        return brightness == 0 || brightness >= 200;
    }
}
