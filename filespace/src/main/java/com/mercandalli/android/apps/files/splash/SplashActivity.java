package com.mercandalli.android.apps.files.splash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.MainActivity;
import com.mercandalli.android.apps.files.main.Permission;

/**
 * The first {@link Activity} launched.
 */
public class SplashActivity extends AppCompatActivity implements Permission.OnPermissionResult {

    Permission mPermission;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final String EXTRA_START_BY_INTENT = "SplashActivity.Extra.EXTRA_START_BY_INTENT";

    private static final String SHARED_PREFERENCES_INIT = "SplashActivity.Permission";
    private static final String KEY_IS_FIRST_LOGIN = "LoginPermission.Key.KEY_IS_FIRST_LOGIN";

    public static void start(Activity activity) {
        final Intent intent = new Intent(activity, SplashActivity.class);
        intent.putExtra(EXTRA_START_BY_INTENT, true);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean start = false;
        Bundle extras = getIntent().getExtras();
        if (isFirstLogin()) {
            start = true;
        } else if (extras != null &&
                extras.containsKey(EXTRA_START_BY_INTENT) &&
                extras.getBoolean(EXTRA_START_BY_INTENT)) {
            start = true;
        }

        if (start) {
            mPermission = new Permission(this, PERMISSIONS);
            mPermission.askPermissions(this);
        } else {
            MainActivity.start(this);
            finish();
        }
    }

    private boolean isFirstLogin() {
        final SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.SHARED_PREFERENCES_INIT, MODE_PRIVATE);
        return sharedPreferences.getBoolean(SplashActivity.KEY_IS_FIRST_LOGIN, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(boolean succeed) {
        end();
    }

    private void end() {
        final SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES_INIT, MODE_PRIVATE)
                .edit();
        editor.putBoolean(KEY_IS_FIRST_LOGIN, false);
        editor.apply();

        MainActivity.start(this);
        finish();
    }
}