package com.mercandalli.android.apps.files;

import android.app.Activity;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.test.runner.AndroidJUnitRunner;

import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.POWER_SERVICE;

/**
 * Tests can fail for other reasons than code, it´ because of the animations and espresso sync and
 * emulator state (screen off or locked).
 * <p/>
 * Before all the tests prepare the device to run tests and avoid these problems.
 * <p/>
 * - Disable animations
 * - Disable keyguard lock
 * - Set it to be awake all the time (don't let the processor sleep)
 *
 * @see <a href="u2020 open source app by Jake Wharton">https://github.com/JakeWharton/u2020</a>
 * @see <a href="Daj gist">https://gist.github.com/daj/7b48f1b8a92abf960e7b</a>
 * @see <a href="Android-test-kit Disabling Animations">https://code.google.com/p/android-test-kit/wiki/DisablingAnimations</a>
 * @see <a href="GitHub link 1">https://gist.github.com/xrigau/11284124#file-systemanimations-java-L37</a>
 * @see <a href="GitHub link 2">https://gist.github.com/danielgomezrico/9371a79a7222a156ddad</a>
 */
@SuppressWarnings({"deprecation", "unused"})
public class FileSpaceJUnitRunner extends AndroidJUnitRunner {

    private PowerManager.WakeLock mWakeLock;

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws
            InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, com.mercandalli.android.apps.files.TestApp.class.getName(), context);
    }

    @Override
    public void onStart() {
        runOnMainSync(new Runnable() {
            @Override
            public void run() {
                final Context app = getTargetContext().getApplicationContext();
                final String name = FileSpaceJUnitRunner.class.getSimpleName();
                final KeyguardManager keyguard = (KeyguardManager) app.getSystemService(KEYGUARD_SERVICE);
                //noinspection MissingPermission
                keyguard.newKeyguardLock(name).disableKeyguard();
                mWakeLock = ((PowerManager) app.getSystemService(POWER_SERVICE))
                        .newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP |
                                PowerManager.ON_AFTER_RELEASE, name);
                mWakeLock.acquire();
            }
        });
        super.onStart();
    }

    @Override
    public void onDestroy() {
        runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mWakeLock.release();
            }
        });
        super.onDestroy();
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            LayoutInflaterWrapper.wrap(activity);
        }
        super.callActivityOnCreate(activity, bundle);
    }
}
