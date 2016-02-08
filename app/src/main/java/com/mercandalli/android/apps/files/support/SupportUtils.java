package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Secure;

import com.mercandalli.android.apps.files.precondition.Preconditions;

/**
 * Support utils.
 */
public class SupportUtils {

    private static String getIdentifier(final Context context) {
        Preconditions.checkNotNull(context);
        return Settings.Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

}
