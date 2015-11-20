package mercandalli.com.filespace.main;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

public class Permission {

    public static final int REQUEST_CODE = 26;

    final Activity mActivity;
    final String[] mPermissions;

    public Permission(Activity activity, String[] permissions) {
        mActivity = activity;
        mPermissions = permissions;
    }

    public void askPermissions() {
        ActivityCompat.requestPermissions(mActivity, mPermissions, REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {

        } else {

        }
    }

}
