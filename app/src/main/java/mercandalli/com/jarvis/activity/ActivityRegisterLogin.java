/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.SHA1;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelUser;
import mercandalli.com.jarvis.net.TaskGet;
import mercandalli.com.jarvis.net.TaskPost;

public class ActivityRegisterLogin extends Application {
	
	private boolean firstUse = true;
	private boolean requestLaunch = false; // Block the second task if one launch
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.view_register_login);
		super.onCreate(savedInstanceState);

        if(this.getConfig().isAutoConncetion() && this.getConfig().getUrlServer()!=null && this.getConfig().getUserUsername()!=null && this.getConfig().getUserPassword()!=null)
        	connectionSucceed();
        
        ((CheckBox) this.findViewById(R.id.autoconnection)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ActivityRegisterLogin.this.getConfig().setAutoConnection(isChecked);				
			}
		});
        
        if(this.getConfig().getUrlServer()!=null)
        	if(!this.getConfig().getUrlServer().equals(""))
        		((EditText) this.findViewById(R.id.server)).setText(this.getConfig().getUrlServer());
        
        if(this.getConfig().getUserUsername()!=null)
        	if(!this.getConfig().getUserUsername().equals("")) {
        		((EditText) this.findViewById(R.id.username)).setText(this.getConfig().getUserUsername());
        		firstUse = false;
        	}
        
        if(this.getConfig().getUserPassword()!=null)
        	if(!this.getConfig().getUserPassword().equals("")) {
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
				if(requestLaunch)
					return;
				requestLaunch = true;
					
				ModelUser user = new ModelUser();
				
				if(!((EditText) ActivityRegisterLogin.this.findViewById(R.id.username)).getText().toString().equals("")) {
					user.username = ((EditText) ActivityRegisterLogin.this.findViewById(R.id.username)).getText().toString();
					ActivityRegisterLogin.this.getConfig().setUserUsername(user.username);
				}
				else
					user.username = ActivityRegisterLogin.this.getConfig().getUserUsername();
				
				if(!((EditText) ActivityRegisterLogin.this.findViewById(R.id.password)).getText().toString().equals("")) {
					user.password = SHA1.execute(((EditText) ActivityRegisterLogin.this.findViewById(R.id.password)).getText().toString());
					ActivityRegisterLogin.this.getConfig().setUserPassword(user.password);
				}
				else
					user.password = ActivityRegisterLogin.this.getConfig().getUserPassword();
				
				if(!((EditText) ActivityRegisterLogin.this.findViewById(R.id.server)).getText().toString().equals(""))
					ActivityRegisterLogin.this.getConfig().setUrlServer(((EditText) ActivityRegisterLogin.this.findViewById(R.id.server)).getText().toString());				
				
				if(ActivityRegisterLogin.this.getConfig().getUrlServer()==null) {
					requestLaunch = false;
					return;
				}
				if(ActivityRegisterLogin.this.getConfig().getUrlServer().equals("")) {
					requestLaunch = false;
					return;
				}
				
				// Register : POST /user
				if(firstUse) {
					List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
					parameters.add(new BasicNameValuePair("username",""+user.username));
					parameters.add(new BasicNameValuePair("password",""+user.password));
					(new TaskPost(ActivityRegisterLogin.this, ActivityRegisterLogin.this.getConfig().getUrlServer()+ActivityRegisterLogin.this.getConfig().routeUser, new IPostExecuteListener() {
						@Override
						public void execute(JSONObject json, String body) {
							try {
								if(json!=null) {
									if(json.has("succeed")) {
										if(json.getBoolean("succeed"))
											connectionSucceed();
                                    }
                                    if(json.has("user")) {
                                        JSONObject user = json.getJSONObject("user");
                                        if(user.has("id"))
                                            ActivityRegisterLogin.this.getConfig().setUserId(user.getInt("id"));
                                    }
								}
								else
									Toast.makeText(ActivityRegisterLogin.this, ActivityRegisterLogin.this.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {e.printStackTrace();}
							requestLaunch = false;
						}						
					}, parameters)).execute();
				}
				// Login : GET /user
				else {
                    List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
                    parameters.add(new BasicNameValuePair("login","true"));
                    (new TaskGet(ActivityRegisterLogin.this, ActivityRegisterLogin.this.getConfig().getUser(), ActivityRegisterLogin.this.getConfig().getUrlServer() + ActivityRegisterLogin.this.getConfig().routeUser, new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            try {
                                if (json != null) {
                                    if (json.has("succeed"))
                                        if (json.getBoolean("succeed")) {
                                            connectionSucceed();
                                        }
                                    if (json.has("user")) {
                                        JSONObject user = json.getJSONObject("user");
                                        if (user.has("id"))
                                            ActivityRegisterLogin.this.getConfig().setUserId(user.getInt("id"));
                                    }
                                } else
                                    Toast.makeText(ActivityRegisterLogin.this, ActivityRegisterLogin.this.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            requestLaunch = false;
                        }
                    })).execute();
                }
				
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
	public void updateAdapters() {
		
	}

	@Override
	public void refreshAdapters() {
		
	}
}
