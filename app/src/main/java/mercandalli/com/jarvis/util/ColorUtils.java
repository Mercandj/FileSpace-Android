package mercandalli.com.jarvis.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;

/**
 * Created by Jonathan on 6/19/15.
 */
public class ColorUtils {

    public static int getColor(Bitmap bitmap) {
        return Palette.from(bitmap).generate().getMutedColor(0x000000);
    }

    public static int colorText(int backgroundColor) {

        return isBrightColor(backgroundColor) ? Color.BLACK : Color.WHITE;
    }

    public static boolean isBrightColor(int color) {
        if (android.R.color.transparent == color)
            return true;

        if (Color.alpha(color)>100)
            return true;

        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };

        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * 0.299 + rgb[1]
                * rgb[1] * 0.587 + rgb[2] * rgb[2] * 0.114);

        return (brightness >= 240);
    }
}
