package com.mercandalli.android.apps.files.splash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;
import com.mercandalli.android.apps.files.main.MainActivity;
import com.mercandalli.android.library.base.permission.Permission;

/**
 * The first {@link Activity} launched.
 */
public class SplashActivity extends AppCompatActivity implements Permission.OnPermissionResult {

    @NonNull
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE//,
            //Manifest.permission.ACCESS_COARSE_LOCATION,
            //Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final String EXTRA_START_BY_INTENT = "SplashActivity.Extra.EXTRA_START_BY_INTENT";
    private static final String SHARED_PREFERENCES_INIT = "SplashActivity.Permission";
    private static final String KEY_IS_FIRST_LAUNCH = "SplashActivity.Key.KEY_IS_FIRST_LAUNCH";

    public static void start(@NonNull final Activity activity) {
        final Intent intent = new Intent(activity, SplashActivity.class);
        intent.putExtra(EXTRA_START_BY_INTENT, true);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Nullable
    private Permission mPermission;
    private boolean mIsFirstLaunch;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean start = false;
        final Bundle extras = getIntent().getExtras();
        if (mIsFirstLaunch = isFirstLaunch()) {
            start = true;
        } else if (extras != null &&
                extras.containsKey(EXTRA_START_BY_INTENT) &&
                extras.getBoolean(EXTRA_START_BY_INTENT)) {
            start = true;
        }

        mPermission = new Permission(PERMISSIONS);
        if (start) {
            mPermission.askPermissions(this, this);
            return;
        }
        if (!mPermission.checkPermission(this)) {
            mPermission.askPermissions(this, this);
            return;
        }
        launchMainActivity();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermission != null) {
            mPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            Toast.makeText(this, R.string.activity_splash_no_permission, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onPermissionResult(boolean allSucceed) {
        if (allSucceed) {
            end();
        } else {
            Toast.makeText(this, R.string.activity_splash_no_permission, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean isFirstLaunch() {
        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_INIT, MODE_PRIVATE);
        return sharedPreferences.getBoolean(SplashActivity.KEY_IS_FIRST_LAUNCH, true);
    }

    private void end() {
        launchMainActivity();
        if (mIsFirstLaunch) {
            final SharedPreferences.Editor editor =
                    getSharedPreferences(SHARED_PREFERENCES_INIT, MODE_PRIVATE).edit();
            editor.putBoolean(KEY_IS_FIRST_LAUNCH, false);
            editor.apply();
        }
    }

    private void launchMainActivity() {
        FileLocalProviderManager.getInstance(this).load();
        MainActivity.start(this);
        finish();
    }
}