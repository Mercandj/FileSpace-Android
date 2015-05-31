/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.jarvis.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Jonathan on 15/05/2015.
 */
public class TimeUtils {
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String printDifferenceFuture(Date endDate, Date startDate){
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        if(different<0)
            return "Finished";

        long elapsedDays = different / DAY;
        different = different % DAY;

        long elapsedHours = different / HOUR;
        different = different % HOUR;

        long elapsedMinutes = different / MINUTE;
        different = different % MINUTE;

        long elapsedSeconds = different / SECOND;

        return
                (elapsedDays!=0 ? (elapsedDays + "d ") : "") +
                        (elapsedHours!=0   ? ((elapsedHours<10 && elapsedDays!=0)?"0"+elapsedHours+"h ":elapsedHours+"h ") : "") +
                        (elapsedMinutes!=0 ? ((elapsedMinutes<10 && (elapsedDays!=0 || elapsedHours!=0))?"0"+elapsedMinutes+"m ":elapsedMinutes+"m ") : "") +
                        (elapsedSeconds!=0 ? ((elapsedSeconds<10 && (elapsedDays!=0 || elapsedHours!=0 || elapsedMinutes!=0))?"0"+elapsedSeconds+"s ":elapsedSeconds+"s ") : "") ;
    }

    public static String printDifferencePast(Date endDate, Date startDate){
        //milliseconds
        long different = startDate.getTime() - endDate.getTime();

        if(different<0)
            return "Future";

        long elapsedDays = different / DAY;
        different = different % DAY;

        long elapsedHours = different / HOUR;
        different = different % HOUR;

        long elapsedMinutes = different / MINUTE;
        different = different % MINUTE;

        long elapsedSeconds = different / SECOND;

        return
                (elapsedDays!=0 ? (elapsedDays + "d ") : "") +
                        (elapsedHours!=0   ? ((elapsedHours<10 && elapsedDays!=0)?"0"+elapsedHours+"h ":elapsedHours+"h ") : "") +
                        (elapsedMinutes!=0 ? ((elapsedMinutes<10 && (elapsedDays!=0 || elapsedHours!=0))?"0"+elapsedMinutes+"m ":elapsedMinutes+"m ") : "") +
                        (elapsedSeconds!=0 ? ((elapsedSeconds<10 && (elapsedDays!=0 || elapsedHours!=0 || elapsedMinutes!=0))?"0"+elapsedSeconds+"s ":elapsedSeconds+"s ") : "") ;
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return  dateFormatGmt.format(new Date()).toString();
    }

}
