package mercandalli.com.jarvis.library;

import java.util.Random;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 26/03/2015.
 */
public class Library {

    private Application app;

    public Library(Application app) {
        this.app = app;
    }

    public String humanReadableByteCount(long bytes) {
        return humanReadableByteCount(bytes, true);
    }

    public String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public int random(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }
}