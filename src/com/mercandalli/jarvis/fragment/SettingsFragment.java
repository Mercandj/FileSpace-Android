/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.adapter.AdapterModelSetting;
import com.mercandalli.jarvis.model.ModelSetting;


public class SettingsFragment extends Fragment {

	Application app;
	View rootView;
	ListView listView;
	List<ModelSetting> listModelSetting;

	public SettingsFragment(Application app) {
		this.app = app;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		
		listView = (ListView) rootView.findViewById(R.id.listView);
		refreshList();
		
        return rootView;
	}
	
	public void refreshList() {
		listModelSetting = new ArrayList<ModelSetting>();
		listModelSetting.add(new ModelSetting(app, "AutoConnection"));
		
		updateAdapter();		
	}
	
	public void updateAdapter() {
		if(listView!=null && listModelSetting!=null) {			
			listView.setAdapter(new AdapterModelSetting(app, R.layout.tab_setting, listModelSetting, null, null));
		}
	}
}
