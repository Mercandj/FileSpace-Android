/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.mercandalli.jarvis.model.ModelUser;

public class Config {

	public final String localFolderName			= "Jarvis";
	public final String aboutURL 				= "http://mercandalli.com/";
	public boolean isLoginSucceed 				= false;
	public final String routeFile	 			= "file";
	public final String routeUserRegister 		= "user";
	public final String routeUserLogin 			= "user";
	public String currentToken					= null;
	
	private Activity activity;
	private String file = "settings_json_1.txt";	
	
	private enum ENUM_Int {
		LAST_TAB				(0, 								"int_last_tab"				),
		;
		
		int value;
		String key;
		ENUM_Int(int init, String key) {
			this.value = init;
			this.key = key;			
		}
	}
	
	private enum ENUM_Boolean {
		DISPLAY_FPS				(true, 								"boolean_display_fps"			),
		;
		
		boolean value;
		String key;
		ENUM_Boolean(boolean init, String key) {
			this.value = init;
			this.key = key;			
		}
	}
	
	private enum ENUM_String {		
		STRING_URL_SERVER		("", 								"string_url_server_1"			),
		STRING_USER_USERNAME	("", 								"string_user_username_1"		),
		STRING_USER_PASSWORD	("", 								"string_user_password_1"		),
		;
		
		String value;
		String key;
		ENUM_String(String init, String key) {
			this.value = init;
			this.key = key;			
		}
	}	
	
	public Config(Activity my_activity) {
		this.activity = my_activity;
		load(my_activity);
	}
	
	private static void write_txt(Activity activity, String file, String txt) {
		try {
    		FileOutputStream output = activity.openFileOutput(file, Context.MODE_PRIVATE);
    		output.write((txt).getBytes());
    		if(output != null) output.close();    		
    	} 
    	catch (FileNotFoundException e) {e.printStackTrace();} 
    	catch (IOException e) {e.printStackTrace();}
	}
	
	private static String read_txt(Activity activity, String file) {
		String res="";
	    try {
	    	FileInputStream input = activity.openFileInput(file);
	        int value;
	        StringBuffer lu = new StringBuffer();
	        while((value = input.read()) != -1)
	        	lu.append((char)value);
	        if(input != null) {
	        	input.close();
	        	if(lu.toString()!=null)
	        		res=lu.toString();
	        }
	    } 
	    catch (FileNotFoundException e) {e.printStackTrace();} 
	    catch (IOException e) {e.printStackTrace();}
	    if(res==null) return "";
	    return res;
	}

	private void save(Activity activity) {
		try {
			JSONObject tmp_json = new JSONObject();			
			JSONObject tmp_settings_1 = new JSONObject();	
			for(ENUM_Int enum_int : ENUM_Int.values())
				tmp_settings_1.put(enum_int.key, enum_int.value);
			for(ENUM_Boolean enum_boolean : ENUM_Boolean.values())
				tmp_settings_1.put(enum_boolean.key, enum_boolean.value);
			for(ENUM_String enum_string : ENUM_String.values())
				tmp_settings_1.put(enum_string.key, enum_string.value);
			tmp_json.put("settings_1", tmp_settings_1);
			write_txt(activity, file, tmp_json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void load(Activity activity) {
		try {
			JSONObject tmp_json = new JSONObject(read_txt(activity, file));			
			if(tmp_json.has("settings_1")) {
				JSONObject tmp_settings_1 = tmp_json.getJSONObject("settings_1");				
				for(ENUM_Int enum_int : ENUM_Int.values())
					if(tmp_settings_1.has(enum_int.key))
						enum_int.value = tmp_settings_1.getInt(enum_int.key);				
				for(ENUM_Boolean enum_boolean : ENUM_Boolean.values())
					if(tmp_settings_1.has(enum_boolean.key))
						enum_boolean.value = tmp_settings_1.getBoolean(enum_boolean.key);
				for(ENUM_String enum_string : ENUM_String.values())
					if(tmp_settings_1.has(enum_string.key))
						enum_string.value = tmp_settings_1.getString(enum_string.key);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public int getLastTab() {
		return ENUM_Int.LAST_TAB.value;
	}
	
	public void setDisplayPosition(int value) {
		if(ENUM_Int.LAST_TAB.value!=value) {
			ENUM_Int.LAST_TAB.value = value;
			save(activity);
		}
	}
	
	public String getUrlServer() {
		return ENUM_String.STRING_URL_SERVER.value;
	}
	
	public void setUrlServer(String value) {
		if(ENUM_String.STRING_URL_SERVER.value!=value) {
			ENUM_String.STRING_URL_SERVER.value = value;
			save(activity);
		}
	}
	
	public String getUserUsername() {
		return ENUM_String.STRING_USER_USERNAME.value;
	}
	
	public void setUserUsername(String value) {
		if(ENUM_String.STRING_USER_USERNAME.value!=value) {
			ENUM_String.STRING_USER_USERNAME.value = value;
			save(activity);
		}
	}
	
	public String getUserPassword() {
		return ENUM_String.STRING_USER_PASSWORD.value;
	}
	
	public void setUserPassword(String value) {
		if(ENUM_String.STRING_USER_PASSWORD.value!=value) {
			ENUM_String.STRING_USER_PASSWORD.value = value;
			save(activity);
		}
	}
	
	public ModelUser getUser() {		
		return new ModelUser(getUserUsername(), getUserPassword(), currentToken);
	}
}
