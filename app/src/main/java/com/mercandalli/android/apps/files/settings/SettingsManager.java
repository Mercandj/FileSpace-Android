package com.mercandalli.android.apps.files.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercandalli.android.library.base.su.SuperUserManager;

public final class SettingsManager {

    /**
     * The shared preference key.
     */
    @NonNull
    private static final String SHARED_PREFERENCES_KEY = "SettingsManager.SHARED_PREFERENCES_KEY";

    @NonNull
    private static final String SHARED_PREFERENCES_IS_SUPER_USER_ENABLE = "SettingsManager.SHARED_PREFERENCES_IS_SUPER_USER_ENABLE";

    @Nullable
    private static SettingsManager sInstance;

    @NonNull
    public static SettingsManager getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new SettingsManager(context);
        }
        return sInstance;
    }

    private boolean mIsSuperUser;

    @NonNull
    private final SharedPreferences mSharedPreferences;

    private SettingsManager(@NonNull final Context context) {
        mSharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        mIsSuperUser = mSharedPreferences.getBoolean(SHARED_PREFERENCES_IS_SUPER_USER_ENABLE, false);
    }

    public boolean isSuperUser() {
        return mIsSuperUser;
    }

    /**
     * @return True if the value change.
     */
    public boolean setIsSuperUser(final boolean isSuperUser) {
        if (!SuperUserManager.getInstance().isRooted()) {
            return false;
        }
        mIsSuperUser = isSuperUser;
        final SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(SHARED_PREFERENCES_IS_SUPER_USER_ENABLE, mIsSuperUser);
        edit.apply();
        return true;
    }
}
