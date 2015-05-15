/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.jarvis.ui.activity.Application;

public class ModelServerMessage extends Model {

	private String content, id_conversation;

	public ModelServerMessage() {
		super();
	}

    public ModelServerMessage(Application app, JSONObject json) {
        super(app);

        try {
            if(json.has("content"))
                this.content = json.getString("content");
            if(json.has("id_conversation"))
                this.id_conversation = json.getString("id_conversation");
        } catch (JSONException e) {
            Log.e("ModelServerMessage", "JSONException");
            e.printStackTrace();
        }
    }

    public ModelServerMessage(JSONObject json) {
        try {
            if(json.has("content"))
                this.content = json.getString("content");
            if(json.has("id_conversation"))
                this.id_conversation = json.getString("id_conversation");
        } catch (JSONException e) {
            Log.e("ModelServerMessage", "JSONException");
            e.printStackTrace();
        }
    }

    public ModelServerMessage(String content) {
        super();
        this.content = content;
    }

    public ModelServerMessage(String content, String id_conversation) {
        super();
        this.content = content;
        this.id_conversation = id_conversation;
    }

    public boolean isConversationMessage() {
        if(this.id_conversation==null)
            return false;
        return !this.id_conversation.equals("");
    }

    public String getContent() {
        return content;
    }

    public String getId_conversation() {
        return id_conversation;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        try {
            result.put("content", this.content);
            result.put("id_conversation", this.id_conversation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(o==null)
            return false;
        if(!(o instanceof ModelServerMessage))
            return false;
        ModelServerMessage obj = (ModelServerMessage)o;
        if((obj.content == null && this.content!=null) || (obj.content != null && this.content==null))
            return false;
        if((obj.id_conversation == null && this.id_conversation!=null) || (obj.id_conversation != null && this.id_conversation==null))
            return false;
        return (obj.content.equals(this.content)) && (obj.id_conversation.equals(this.id_conversation));
    }
}
