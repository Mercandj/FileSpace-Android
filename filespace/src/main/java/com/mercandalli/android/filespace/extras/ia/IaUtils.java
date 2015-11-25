package com.mercandalli.android.filespace.extras.ia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.AlarmClock;

import com.mercandalli.android.filespace.common.util.StringUtils;

/**
 * Created by Jonathan on 25/11/2015.
 */
public class IaUtils {

    public static boolean launchPackage(Context context, String pckg) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        PackageManager manager = context.getPackageManager();
        i = manager.getLaunchIntentForPackage(pckg);
        if (i == null)
            return false;
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);

        return true;
    }

    public static boolean checkPackage(String pakage, Activity activity) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        PackageManager manager = activity.getPackageManager();
        i = manager.getLaunchIntentForPackage(pakage);
        return i != null;
    }

    public static void setAlarm(Context context, int hours, int minutes) {
        if (hours != -1 && minutes != -1) {
            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.putExtra(AlarmClock.EXTRA_HOUR, hours);
            i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
            context.startActivity(i);
        } else
            launchPackage(context, "com.android.deskclock");
    }

    public static void setAlarmFromString(Context context, String sentence) {
        String[] message = StringUtils.getWords(sentence);
        int hours = -1, minutes = 0;

        label1:
        for (String var : message) {
            int var_int = -1;
            try {
                var_int = Integer.parseInt(var);
            } catch (Exception e) {
                if (var != null) {
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
            if (var_int != -1) {
                if (hours == -1 && var_int <= 24)
                    hours = var_int;
                else
                    minutes = var_int;
            }
        }
        setAlarm(context, hours, minutes);
    }
}
