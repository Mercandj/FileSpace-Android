package com.mercandalli.android.apps.files;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.support.test.runner.AndroidJUnitRunner;

import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.POWER_SERVICE;

@SuppressWarnings({"deprecation", "unused"})
public class FileSpaceJUnitRunner extends AndroidJUnitRunner {

    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onStart() {
        final Context app = getTargetContext().getApplicationContext();

        final String name = FileSpaceJUnitRunner.class.getSimpleName();
        final KeyguardManager keyguard = (KeyguardManager) app.getSystemService(KEYGUARD_SERVICE);
        //noinspection MissingPermission
        keyguard.newKeyguardLock(name).disableKeyguard();
        mWakeLock = ((PowerManager) app.getSystemService(POWER_SERVICE))
                .newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, name);
        mWakeLock.acquire();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        mWakeLock.release();
        super.onDestroy();
    }
}
