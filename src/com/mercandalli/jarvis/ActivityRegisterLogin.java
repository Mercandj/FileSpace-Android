package com.mercandalli.jarvis;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mercandalli.jarvis.listener.IPostExecuteListener;
import com.mercandalli.jarvis.model.ModelUser;
import com.mercandalli.jarvis.net.TaskGet;
import com.mercandalli.jarvis.net.TaskPost;

public class ActivityRegisterLogin extends Application {
	
	private boolean firstUse = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.view_register_login);
		super.onCreate(savedInstanceState);
		
		// Back button at the top left of the action bar
        final ActionBar actionBar = getActionBar();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        
        if(this.config.getUrlServer()!=null)
        	if(!this.config.getUrlServer().equals(""))
        		((EditText) this.findViewById(R.id.server)).setText(this.config.getUrlServer());
        
        if(this.config.getUserUsername()!=null)
        	if(!this.config.getUserUsername().equals("")) {
        		((EditText) this.findViewById(R.id.username)).setText(this.config.getUserUsername());
        		firstUse = false;
        	}
        
        if(this.config.getUserPassword()!=null)
        	if(!this.config.getUserPassword().equals("")) {
        		((EditText) this.findViewById(R.id.password)).setHint("Hash Saved");  
		        firstUse = false;
			}
        
        bindRegisterLogin();
        
        ((ToggleButton) this.findViewById(R.id.toggleButton)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				firstUse = isChecked;
				bindRegisterLogin();
			}        	
        });
	    
        ((ImageView) this.findViewById(R.id.signin)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ModelUser user = new ModelUser();
				
				if(!((EditText) ActivityRegisterLogin.this.findViewById(R.id.username)).getText().toString().equals("")) {
					user.username = ((EditText) ActivityRegisterLogin.this.findViewById(R.id.username)).getText().toString();
					ActivityRegisterLogin.this.config.setUserUsername(user.username);
				}
				else
					user.username = ActivityRegisterLogin.this.config.getUserUsername();
				
				if(!((EditText) ActivityRegisterLogin.this.findViewById(R.id.password)).getText().toString().equals("")) {
					user.password = SHA1.execute(((EditText) ActivityRegisterLogin.this.findViewById(R.id.password)).getText().toString());
					ActivityRegisterLogin.this.config.setUserPassword(user.password);
				}
				else
					user.password = ActivityRegisterLogin.this.config.getUserPassword();
				
				if(!((EditText) ActivityRegisterLogin.this.findViewById(R.id.server)).getText().toString().equals(""))
					ActivityRegisterLogin.this.config.setUrlServer(((EditText) ActivityRegisterLogin.this.findViewById(R.id.server)).getText().toString());				
				
				
				// Register : POST /user
				if(firstUse) {
					List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
					parameters.add(new BasicNameValuePair("username",""+user.username));
					parameters.add(new BasicNameValuePair("password",""+user.password));
					(new TaskPost(ActivityRegisterLogin.this, ActivityRegisterLogin.this.config.getUrlServer()+ActivityRegisterLogin.this.config.routeUserRegister, new IPostExecuteListener() {
						@Override
						public void execute(JSONObject json, String body) {
							try {
								if(json!=null) {
									if(json.has("succeed"))
										if(json.getBoolean("succeed")) {
											connectionSucceed();
										}
								}
								else
									Toast.makeText(ActivityRegisterLogin.this, ActivityRegisterLogin.this.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {e.printStackTrace();}
						}						
					}, parameters)).execute();
				}
				// Login : GET /user
				else
					(new TaskGet(ActivityRegisterLogin.this, ActivityRegisterLogin.this.config.getUrlServer()+ActivityRegisterLogin.this.config.routeUserLogin, new IPostExecuteListener() {
						@Override
						public void execute(JSONObject json, String body) {
							try {
								if(json!=null) {
									if(json.has("succeed"))
										if(json.getBoolean("succeed")) {
											connectionSucceed();
										}
								}
								else
									Toast.makeText(ActivityRegisterLogin.this, ActivityRegisterLogin.this.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {e.printStackTrace();}
						}
					})).execute();				
			}        	
        });
        
        InputMethodManager mgr = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(((EditText) this.findViewById(R.id.server)).getWindowToken(), 0);        
	}
	
	public void bindRegisterLogin() {
		((ToggleButton) this.findViewById(R.id.toggleButton)).setChecked(firstUse);
		if(firstUse)
			((TextView) this.findViewById(R.id.label)).setText(R.string.dialog_register);
		else
			((TextView) this.findViewById(R.id.label)).setText(R.string.dialog_login);
	}
	
	public void connectionSucceed() {
		Intent intent = new Intent(this, ActivityMain.class);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		this.finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		 case android.R.id.home:
			 this.finish();
	         return true;
		 }
		 return super.onOptionsItemSelected(item);
	}
}
