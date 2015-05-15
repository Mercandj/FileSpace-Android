package mercandalli.com.jarvis.util;

import java.util.Random;

/**
 * Created by Jonathan on 15/05/2015.
 */
public class MathUtils {

    public static int random(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

}
