/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.model;

import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mercandalli.jarvis.Application;

public class ModelSetting {
	
	@SuppressWarnings("unused")
	private Application app;

	public OnCheckedChangeListener toggleButtonListener = null;
	public boolean toggleButtonInitValue = false;
	
	public String name;
	
	public ModelSetting(Application app) {
		this.app = app;
	}
	
	public ModelSetting(Application app, String name) {
		this.app = app;
		this.name = name;
	}
	
	public ModelSetting(Application app, String name, OnCheckedChangeListener toggleButtonListener, boolean toggleButtonInitValue) {
		this.app = app;
		this.name = name;
		this.toggleButtonListener = toggleButtonListener;
		this.toggleButtonInitValue = toggleButtonInitValue;
	}
	
}
