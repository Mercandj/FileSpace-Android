/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mercandalli.com.jarvis.activity.Application;

public class ModelUser extends Model {

    public int id;
	public String username;
	public String password;
	public String currentToken;
    public String regId;
    public Date date_creation, date_last_connection;
    public long size_files;
	
	public ModelUser() {
		
	}

	public ModelUser(Application app, int id, String username, String password, String currentToken, String regId) {
		super();
        this.id = id;
		this.username = username;
		this.password = password;
		this.currentToken = currentToken;
        this.regId = regId;
	}

    public ModelUser(Application app, JSONObject json) {
        super();
        this.app = app;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if(json.has("id"))
                this.id = json.getInt("id");
            if(json.has("username"))
                this.username = json.getString("username");
            if(json.has("password"))
                this.password = json.getString("password");
            if(json.has("currentToken"))
                this.currentToken = json.getString("currentToken");
            if(json.has("regId"))
                this.regId = json.getString("regId");
            if(json.has("date_creation") && !json.isNull("date_creation"))
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
            if(json.has("date_last_connection") && !json.isNull("date_last_connection"))
                this.date_last_connection = dateFormat.parse(json.getString("date_last_connection"));
            if(json.has("size_files") && !json.isNull("size_files"))
                this.size_files = json.getLong("size_files");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getAdapterTitle() {
        return this.username;
    }

    public String getAdapterSubtitle() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String date = dateFormat.format(date_last_connection.getTime());
        return "#" + this.id + "   " + date + "   " + this.app.getLibrary().humanReadableByteCount(size_files);
    }

	public JSONObject getJsonRegister() {
		if(username!=null && password!=null) {
			JSONObject json = new JSONObject();			
			try {
				json.put("username", username);
				json.put("password", password);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			return json;
		}
		return null;
	}
	
	public JSONObject getJsonLogin() {
		return getJsonRegister();
	}
	
	public String getAccessLogin() {
		if(currentToken==null)
			return username;
		return currentToken;
	}
	
	public String getAccessPassword() {
		if(currentToken==null)
			return password;
		return "empty";
	}
}
