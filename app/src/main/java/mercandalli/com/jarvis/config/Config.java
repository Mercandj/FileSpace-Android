/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.jarvis.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.listener.IBitmapListener;
import mercandalli.com.jarvis.model.Model;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.model.ModelServerMessage;
import mercandalli.com.jarvis.model.ModelUser;
import mercandalli.com.jarvis.net.TaskGetDownloadImage;

/**
 * Created by Jonathan on 10/12/2014.
 */
public class Config {

    private Application app;
    private List<ModelServerMessage> listServerMessage_1;
    public String currentToken					= null;

    // Local routes
    public final String localFolderName			= "Jarvis";
    private static final String fileName        = "settings_json_1.txt";

    // Server routes
    public final String aboutURL 				= "http://mercandalli.com/";
    public final String webApplication			= "http://mercandalli.com/jarvis";
    public final String routeFile	 			= "file";
    public final String routeInformation		= "information";
    public final String routeRobotics       	= "robotics";
    public final String routeUser 		        = "user";
    public final String routeUserPut	        = "user_put";
    public final String routeUserMessage        = "user_message";
    public final String routeUserConversation   = "user_conversation";

    /**
     * Static int to save/load
     */
    private enum ENUM_Int {
        INTEGER_LAST_TAB				        (0, 	"int_last_tab_1"			            ),
        INTEGER_USER_ID     	                (-1, 	"int_user_id_1"     		            ),
        INTEGER_USER_ID_FILE_PROFILE_PICTURE    (-1, 	"int_user_id_file_profile_picture_1"   	),
        ;

        int value;
        String key;
        ENUM_Int(int init, String key) {
            this.value = init;
            this.key = key;
        }
    }

    /**
     * Static boolean to save/load
     */
    private enum ENUM_Boolean {
        BOOLEAN_AUTO_CONNECTION	        (true, 		"boolean_auto_connection_1"		),
        BOOLEAN_USER_ADMIN  	        (false,		"boolean_user_admin_1"  		),
        BOOLEAN_HOME_WELCOME_MESSAGE  	(true,		"boolean_home_welcome_message_1"),
        ;

        boolean value;
        String key;
        ENUM_Boolean(boolean init, String key) {
            this.value = init;
            this.key = key;
        }
    }

    /**
     * Static Sctring to save/load
     */
    private enum ENUM_String {
        STRING_URL_SERVER		("http://mercandalli.com/Jarvis-API/", 	"string_url_server_1"			),
        STRING_USER_USERNAME	("",                                    "string_user_username_1"		),
        STRING_USER_PASSWORD	("", 			                        "string_user_password_1"		),
        STRING_USER_REGID	    ("", 			                        "string_user_regid_1"   		),
        ;

        String value;
        String key;
        ENUM_String(String init, String key) {
            this.value = init;
            this.key = key;
        }
    }

    public Config(Application app) {
        this.app = app;
        load();
    }

    public static void write_txt(Context context, String file, String txt) {
        try {
            FileOutputStream output = context.openFileOutput(file, Context.MODE_PRIVATE);
            output.write((txt).getBytes());
            if(output != null) output.close();
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }

    public static String read_txt(Context context, String file) {
        String res="";
        try {
            FileInputStream input = context.openFileInput(file);
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

    private void save() {
        try {
            JSONObject tmp_json = new JSONObject();
            JSONObject tmp_settings_1 = new JSONObject();
            for(ENUM_Int enum_int : ENUM_Int.values())
                tmp_settings_1.put(enum_int.key, enum_int.value);
            for(ENUM_Boolean enum_boolean : ENUM_Boolean.values())
                tmp_settings_1.put(enum_boolean.key, enum_boolean.value);
            for(ENUM_String enum_string : ENUM_String.values())
                tmp_settings_1.put(enum_string.key, enum_string.value);

            if(listServerMessage_1 != null) {
                JSONArray array_listServerMessage_1 = new JSONArray();
                for (Model model : listServerMessage_1)
                    array_listServerMessage_1.put(model.toJSONObject());
                tmp_settings_1.put("listServerMessage_1", array_listServerMessage_1);
            }

            tmp_json.put("settings_1", tmp_settings_1);
            write_txt(app, fileName, tmp_json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        this.listServerMessage_1 = new ArrayList<>();
        try {
            JSONObject tmp_json = new JSONObject(read_txt(app, fileName));
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

                if(tmp_settings_1.has("listServerMessage_1")) {
                    JSONArray array_listServerMessage_1 = tmp_settings_1.getJSONArray("listServerMessage_1");
                    for(int i=0; i<array_listServerMessage_1.length(); i++)
                        this.listServerMessage_1.add(new ModelServerMessage(app,array_listServerMessage_1.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getLastTab() {
        return ENUM_Int.INTEGER_LAST_TAB.value;
    }

    public void setDisplayPosition(int value) {
        if(ENUM_Int.INTEGER_LAST_TAB.value!=value) {
            ENUM_Int.INTEGER_LAST_TAB.value = value;
            save();
        }
    }

    public int getUserId() {
        return ENUM_Int.INTEGER_USER_ID.value;
    }

    public void setUserId(int value) {
        if(ENUM_Int.INTEGER_USER_ID.value!=value) {
            ENUM_Int.INTEGER_USER_ID.value = value;
            save();
        }
    }

    public String getUrlServer() {
        return ENUM_String.STRING_URL_SERVER.value;
    }

    public void setUrlServer(String value) {
        if(ENUM_String.STRING_URL_SERVER.value!=value) {
            ENUM_String.STRING_URL_SERVER.value = value;
            save();
        }
    }

    public String getUserUsername() {
        return ENUM_String.STRING_USER_USERNAME.value;
    }

    public void setUserUsername(String value) {
        if(ENUM_String.STRING_USER_USERNAME.value!=value) {
            ENUM_String.STRING_USER_USERNAME.value = value;
            save();
        }
    }

    public String getUserPassword() {
        return ENUM_String.STRING_USER_PASSWORD.value;
    }

    public void setUserPassword(String value) {
        if(ENUM_String.STRING_USER_PASSWORD.value!=value) {
            ENUM_String.STRING_USER_PASSWORD.value = value;
            save();
        }
    }

    public String getUserRegId() {
        return ENUM_String.STRING_USER_REGID.value;
    }

    public void setUserRegId(String value) {
        if(ENUM_String.STRING_USER_REGID.value!=value) {
            ENUM_String.STRING_USER_REGID.value = value;
            save();
        }
    }

    public Bitmap getUserProfiePicture() {
        File file = new File(this.app.getFilesDir()+"/file_"+this.getUserIdFileProfilePicture());
        if(file.exists())
            return BitmapFactory.decodeFile(file.getPath());
        else if(this.app.isInternetConnection()) {
            ModelFile modelFile = new ModelFile(app);
            modelFile.id = this.getUserIdFileProfilePicture();
            modelFile.onlineUrl = this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+this.getUserIdFileProfilePicture();
            new TaskGetDownloadImage(app, this.app.getConfig().getUser(), modelFile, new IBitmapListener() {
                @Override
                public void execute(Bitmap bitmap) {
                    //TODO
                }
            }).execute();
        }
        return null;
    }

    public int getUserIdFileProfilePicture() {
        return ENUM_Int.INTEGER_USER_ID_FILE_PROFILE_PICTURE.value;
    }

    public void setUserIdFileProfilePicture(int value) {
        if(ENUM_Int.INTEGER_USER_ID_FILE_PROFILE_PICTURE.value!=value) {
            ENUM_Int.INTEGER_USER_ID_FILE_PROFILE_PICTURE.value = value;
            save();
        }
    }

    public boolean isUserAdmin() {
        return ENUM_Boolean.BOOLEAN_USER_ADMIN.value;
    }

    public void setUserAdmin(boolean value) {
        if(ENUM_Boolean.BOOLEAN_USER_ADMIN.value!=value) {
            ENUM_Boolean.BOOLEAN_USER_ADMIN.value = value;
            save();
        }
    }
    
    public boolean isHomeWelcomeMessage() {
        return ENUM_Boolean.BOOLEAN_HOME_WELCOME_MESSAGE.value;
    }

    public void setHomeWelcomeMessage(boolean value) {
        if(ENUM_Boolean.BOOLEAN_HOME_WELCOME_MESSAGE.value!=value) {
            ENUM_Boolean.BOOLEAN_HOME_WELCOME_MESSAGE.value = value;
            save();
        }
    }

    public boolean isAutoConncetion() {
        return ENUM_Boolean.BOOLEAN_AUTO_CONNECTION.value;
    }

    public void setAutoConnection(boolean value) {
        if(ENUM_Boolean.BOOLEAN_AUTO_CONNECTION.value!=value) {
            ENUM_Boolean.BOOLEAN_AUTO_CONNECTION.value = value;
            save();
        }
    }

    public ModelUser getUser() {
        return new ModelUser(app, getUserId(), getUserUsername(), getUserPassword(), currentToken, getUserRegId(), isUserAdmin());
    }

    public void addServerMessage(ModelServerMessage serverMessage) {
        if(listServerMessage_1 == null)
            listServerMessage_1 = new ArrayList<>();
        if(serverMessage == null)
            return;
        boolean add = true;
        for(ModelServerMessage s:listServerMessage_1)
            if(serverMessage.equals(s))
                add = false;
        if(add)
            listServerMessage_1.add(serverMessage);
        save();
    }

    public void removeServerMessage(ModelServerMessage serverMessage) {
        if(listServerMessage_1 == null) {
            listServerMessage_1 = new ArrayList<>();
            return;
        }
        if(serverMessage == null)
            return;
        List<Integer> l = new ArrayList<>();
        for(int i=0; i<listServerMessage_1.size();i++) {
            if(listServerMessage_1.get(i).equals(serverMessage))
                l.add(i);
        }
        for (int i = l.size()-1; i>=0 ; i--)
            listServerMessage_1.remove(i);
        save();
    }

    public static String getFileName() {
        return fileName;
    }

    public List<ModelServerMessage> getListServerMessage_1() {
        this.load();
        return listServerMessage_1;
    }
    
    /**
     * Reset the saved values
     * (When the user log out)
     */
    public void reset() {
        setUserRegId("");
        setUserUsername("");
        setUserPassword("");
        setAutoConnection(false);
        setUserId(-1);
        setUserAdmin(false);
        setUserIdFileProfilePicture(-1);
        setHomeWelcomeMessage(true);
    }
}
