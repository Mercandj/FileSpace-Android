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

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;

import static mercandalli.com.jarvis.util.AppUtils.launchPackage;
import static mercandalli.com.jarvis.util.StringUtils.getWords;

/**
 * Created by Jonathan on 17/05/2015.
 */
public class AlarmUtils {

    public static void setAlarm(Context context, int hours, int minutes) {
        if(hours!=-1 && minutes!=-1) {
            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.putExtra(AlarmClock.EXTRA_HOUR, hours);
            i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
            context.startActivity(i);
        }
        else
            launchPackage(context, "com.android.deskclock");
    }

    public static void setAlarmFromString(Context context, String sentence) {
        String[] message = getWords(sentence);
        int hours=-1, minutes=0;

        label1:for(String var : message ) {
            int var_int = -1;
            try{
                var_int=Integer.parseInt(var);
            } catch(Exception e) {
                if(var!=null) {
                    if (var.length() >= 2) {
                        for (int i = 1; i < var.length(); i++) {
                            if (var.charAt(i) == 'h' || var.charAt(i) == 'H') {

                                String heure = var.substring(0, i);
                                String minute = var.substring(i + 1, var.length());
                                //Toast.makeText(MainActivity.activity, heure+" "+minute,Toast.LENGTH_SHORT).show();
                                int h = -1;
                                int m = -1;
                                try {
                                    h = Integer.parseInt(heure);

                                } catch (Exception e2) {
                                    break label1;
                                }
                                try {
                                    m = Integer.parseInt(minute);
                                } catch (Exception e2) {
                                }
                                if (h != -1 && m != -1) {
                                    hours = h;
                                    minutes = m;
                                    setAlarm(context, hours, minutes);
                                    return;
                                }
                                if (h != -1) hours = h;
                            }
                        }
                    }
                    if (var.equals("minuit")) {
                        hours = 0;
                        minutes = 0;
                    } else if (var.equals("midi")) {
                        hours = 12;
                        minutes = 0;
                    } else
                        var_int = -1;
                }
            }
            if(var_int!=-1) {
                if(hours==-1 && var_int<=24)
                    hours=var_int;
                else
                    minutes=var_int;
            }
        }
        setAlarm(context, hours, minutes);
    }
}
