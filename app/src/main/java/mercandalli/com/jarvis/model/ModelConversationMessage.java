/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mercandalli.com.jarvis.activity.Application;

public class ModelConversationMessage extends Model {

    public int id, id_conversation, id_user;
    public Date date_creation;
    public String content;

	public ModelConversationMessage() {

	}

    public ModelConversationMessage(Application app, JSONObject json) {
        super();
        this.app = app;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if(json.has("id"))
                this.id = json.getInt("id");
            if(json.has("id_conversation"))
                this.id_conversation = json.getInt("id_conversation");
            if(json.has("id_user"))
                this.id_user = json.getInt("id_user");
            if(json.has("content"))
                this.content = json.getString("content");
            if(json.has("date_creation") && !json.isNull("date_creation"))
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getAdapterTitle() {
        return this.content;
    }

    public String getAdapterSubtitle() {
        return "conv:"+this.id + "  conv_us:"+this.id_conversation;
    }
}
