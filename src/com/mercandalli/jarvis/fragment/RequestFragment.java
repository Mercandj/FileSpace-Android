/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.fragment;

import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.dialog.DialogRequest;
import com.mercandalli.jarvis.listener.IPostExecuteListener;


public class RequestFragment extends Fragment {

	Application app;
	View rootView;

	public RequestFragment(Application app) {
		this.app = app;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_request, container, false);
		
		((ImageView) rootView.findViewById(R.id.circle)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.dialog = new DialogRequest(app, new IPostExecuteListener() {
					@Override
					public void execute(JSONObject json, String body) {
						if(json!=null)
							((EditText) rootView.findViewById(R.id.console)).setText(((EditText) rootView.findViewById(R.id.console)).getText().toString()+"JSON : "+json+"\n\n");
						else
							((EditText) rootView.findViewById(R.id.console)).setText(((EditText) rootView.findViewById(R.id.console)).getText().toString()+"BODY : "+body+"\n\n");
					}
				});
			}
		});
		
        return rootView;
	}
	
	public void deleteConsole() {
		((EditText) rootView.findViewById(R.id.console)).setText("");
	}
}
