package com.mercandalli.jarvis.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.listener.IModelFileListener;
import com.mercandalli.jarvis.model.ModelSetting;

public class AdapterModelSetting extends ArrayAdapter<ModelSetting> {

	Application app;
	List<ModelSetting> settings;
	IModelFileListener clickListener, moreListener;
	
	public AdapterModelSetting(Application app, int resource, List<ModelSetting> settings, IModelFileListener clickListener, IModelFileListener moreListener) {
		super(app, resource, settings);
		this.app = app;
		this.settings = settings;
		this.clickListener = clickListener;
		this.moreListener = moreListener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		LayoutInflater inflater = app.getLayoutInflater();
		
		if(position<settings.size()) {		
			final ModelSetting setting = settings.get(position);			
			convertView = inflater.inflate(R.layout.tab_setting, parent, false);
			
			if(setting.name!=null)
				((TextView) convertView.findViewById(R.id.title)).setText(setting.name);			
		}
		return convertView;
	}
	
	@Override
	public int getCount() {
		return settings.size();
	}
}
