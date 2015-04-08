package mercandalli.com.jarvis.config;

import android.app.Activity;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.model.ModelUser;

/**
 * Created by Jonathan on 10/12/2014.
 */
public class Config {

    public final String localFolderName			= "Jarvis";
    public final String aboutURL 				= "http://mercandalli.com/";
    public final String routeFile	 			= "file";
    public final String routeInformation		= "information";
    public final String routeHome       		= "home";
    public final String routeUser 		        = "user";
    public final String routeUserMessage        = "user_message";
    public String currentToken					= null;

    private Application app;
    private final String file = "settings_json_1.txt";

    private enum ENUM_Int {
        LAST_TAB				(0, 			"int_last_tab"				),
        INTEGER_USER_ID     	(-1, 			"int_user_id_1"     		),
        ;

        int value;
        String key;
        ENUM_Int(int init, String key) {
            this.value = init;
            this.key = key;
        }
    }

    private enum ENUM_Boolean {
        BOOLEAN_AUTO_CONNECTION	(true, 		"boolean_auto_connection"		),
        ;

        boolean value;
        String key;
        ENUM_Boolean(boolean init, String key) {
            this.value = init;
            this.key = key;
        }
    }

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
        load(app);
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

    private void load(Application app) {
        try {
            JSONObject tmp_json = new JSONObject(read_txt(app, file));
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
            save(app);
        }
    }

    public int getUserId() {
        return ENUM_Int.INTEGER_USER_ID.value;
    }

    public void setUserId(int value) {
        if(ENUM_Int.INTEGER_USER_ID.value!=value) {
            ENUM_Int.INTEGER_USER_ID.value = value;
            save(app);
        }
    }

    public String getUrlServer() {
        return ENUM_String.STRING_URL_SERVER.value;
    }

    public void setUrlServer(String value) {
        if(ENUM_String.STRING_URL_SERVER.value!=value) {
            ENUM_String.STRING_URL_SERVER.value = value;
            save(app);
        }
    }

    public String getUserUsername() {
        return ENUM_String.STRING_USER_USERNAME.value;
    }

    public void setUserUsername(String value) {
        if(ENUM_String.STRING_USER_USERNAME.value!=value) {
            ENUM_String.STRING_USER_USERNAME.value = value;
            save(app);
        }
    }

    public String getUserPassword() {
        return ENUM_String.STRING_USER_PASSWORD.value;
    }

    public void setUserPassword(String value) {
        if(ENUM_String.STRING_USER_PASSWORD.value!=value) {
            ENUM_String.STRING_USER_PASSWORD.value = value;
            save(app);
        }
    }

    public String getUserRegId() {
        return ENUM_String.STRING_USER_REGID.value;
    }

    public void setUserRegId(String value) {
        if(ENUM_String.STRING_USER_REGID.value!=value) {
            ENUM_String.STRING_USER_REGID.value = value;
            save(app);
        }
    }

    public boolean isAutoConncetion() {
        return ENUM_Boolean.BOOLEAN_AUTO_CONNECTION.value;
    }

    public void setAutoConnection(boolean value) {
        if(ENUM_Boolean.BOOLEAN_AUTO_CONNECTION.value!=value) {
            ENUM_Boolean.BOOLEAN_AUTO_CONNECTION.value = value;
            save(app);
        }
    }

    public ModelUser getUser() {
        return new ModelUser(app, getUserId(), getUserUsername(), getUserPassword(), currentToken, getUserRegId());
    }

    public void reset() {
        setUserRegId("");
        setUserUsername("");
        setUserPassword("");
        setAutoConnection(false);
        setUserId(-1);
    }
}