package mercandalli.com.jarvis.library;

import java.util.Date;
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

    public String printDifferenceFuture(Date endDate, Date startDate){
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        if(different<0)
            return "Finished";

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return
                (elapsedDays!=0 ? (elapsedDays + "d ") : "") +
                        (elapsedHours!=0 ? (elapsedHours + "h ") : "") +
                        (elapsedMinutes!=0 ? (elapsedMinutes + "m ") : "") +
                        (elapsedSeconds!=0 ? (elapsedSeconds + "s") : "") ;
    }

    public String printDifferencePast(Date endDate, Date startDate){
        //milliseconds
        long different = startDate.getTime() - endDate.getTime();

        if(different<0)
            return "Future";

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return
                (elapsedDays!=0 ? (elapsedDays + "d ") : "") +
                        (elapsedHours!=0 ? (elapsedHours + "h ") : "") +
                        (elapsedMinutes!=0 ? (elapsedMinutes + "m ") : "") +
                        (elapsedSeconds!=0 ? (elapsedSeconds + "s") : "") ;
    }
}
