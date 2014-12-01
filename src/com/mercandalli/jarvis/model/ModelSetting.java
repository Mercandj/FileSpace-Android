/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.model;

import com.mercandalli.jarvis.Application;

public class ModelSetting {
	
	@SuppressWarnings("unused")
	private Application app;
	
	public String name;
	
	public ModelSetting(Application app) {
		this.app = app;
	}
	
	public ModelSetting(Application app, String name) {
		this.app = app;
		this.name = name;
	}
	
}
