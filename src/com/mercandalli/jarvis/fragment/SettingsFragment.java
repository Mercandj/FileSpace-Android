/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;


public class SettingsFragment extends Fragment {

	Application app;
	View rootView;

	public SettingsFragment(Application app) {
		this.app = app;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		
        return rootView;
	}
}
