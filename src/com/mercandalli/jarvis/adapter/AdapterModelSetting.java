package com.mercandalli.jarvis.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.listener.IModelSettingListener;
import com.mercandalli.jarvis.model.ModelSetting;

public class AdapterModelSetting extends ArrayAdapter<ModelSetting> {

	Application app;
	List<ModelSetting> settings;
	IModelSettingListener modelSettingListener;
	
	public AdapterModelSetting(Application app, int resource, List<ModelSetting> settings, IModelSettingListener modelSettingListener) {
		super(app, resource, settings);
		this.app = app;
		this.settings = settings;
		this.modelSettingListener = modelSettingListener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		LayoutInflater inflater = app.getLayoutInflater();
		
		if(position<settings.size()) {		
			final ModelSetting setting = settings.get(position);			
			convertView = inflater.inflate(R.layout.tab_setting, parent, false);
			
			if(setting.name!=null)
				((TextView) convertView.findViewById(R.id.title)).setText(setting.name);
			
			if(setting.toggleButtonListener==null)
				((ToggleButton) convertView.findViewById(R.id.toggleButton)).setVisibility(View.GONE);
			else {
				((ToggleButton) convertView.findViewById(R.id.toggleButton)).setVisibility(View.VISIBLE);
				((ToggleButton) convertView.findViewById(R.id.toggleButton)).setChecked(setting.toggleButtonInitValue);
				((ToggleButton) convertView.findViewById(R.id.toggleButton)).setOnCheckedChangeListener(setting.toggleButtonListener);
			}
		}
		return convertView;
	}
	
	@Override
	public int getCount() {
		return settings.size();
	}
}
