/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.fragment.FileManagerFragment;
import mercandalli.com.jarvis.notification.Notif;

public class ActivityMain extends ApplicationDrawer {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		
		if(fragment instanceof FileManagerFragment) {
			FileManagerFragment fragmentFileManager = (FileManagerFragment) fragment;					
			fragmentFileManager.refreshListServer();					
		}

        // Notif
        if (TextUtils.isEmpty(Notif.regId)) {
            Notif.regId = Notif.registerGCM(this);
            Log.d("ActivityMain", "GCM RegId: " + Notif.regId);
        } else {
            Log.d("ActivityMain", "Already Registered with GCM Server!");
            Notif.mainActNotif(this);
        }
    }
	
	@Override
	public void updateAdapters() {
		if(fragment instanceof FileManagerFragment) {
			FileManagerFragment fragmentFileManager = (FileManagerFragment) fragment;					
			fragmentFileManager.updateAdapterListServer();					
		}
	}

	@Override
	public void refreshAdapters() {
		if(fragment instanceof FileManagerFragment) {
			FileManagerFragment fragmentFileManager = (FileManagerFragment) fragment;					
			fragmentFileManager.refreshAdapterListServer();					
		}
	}
}
