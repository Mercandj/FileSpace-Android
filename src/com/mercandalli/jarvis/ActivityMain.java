/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis;

import android.os.Bundle;
import android.view.KeyEvent;

import com.mercandalli.jarvis.fragment.FileManagerFragment;

public class ActivityMain extends ApplicationDrawer {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		
		if(fragment instanceof FileManagerFragment) {
			FileManagerFragment fragmentFileManager = (FileManagerFragment) fragment;					
			fragmentFileManager.refreshListServer();					
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		this.finish();
		return super.onKeyDown(keyCode, event);
	}
}
