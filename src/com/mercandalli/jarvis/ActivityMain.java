/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis;

import android.os.Bundle;

import com.mercandalli.jarvis.dialog.DialogRegisterLogin;
import com.mercandalli.jarvis.fragment.FileManagerFragment;
import com.mercandalli.jarvis.listener.IListener;

public class ActivityMain extends ApplicationDrawer {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);

		dialog = new DialogRegisterLogin(this, new IListener() {			
			@Override
			public void execute() {
				ActivityMain.this.config.isLoginSucceed = true;
				if(fragment instanceof FileManagerFragment) {
					FileManagerFragment fragmentFileManager = (FileManagerFragment) fragment;					
					fragmentFileManager.refreshListServer();					
				}
			}
		});
	}
}
