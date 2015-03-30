/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.config.Const;

public class ModelInformation extends Model {

	public String title;
	public String value;
	public int viewType = Const.TAB_VIEW_TYPE_NORMAL;
	
	public ModelInformation() {
		super();
	}
	
	public ModelInformation(String title, String value) {
		super();
		this.title = title;
		this.value = value;
	}
	
	public ModelInformation(String title, int viewType) {
		super();
		this.title = title;
		this.viewType = viewType;
	}
	
	public ModelInformation(Application app, JSONObject json) {
		super();
		this.app = app;
		try {
			if(json.has("title"))
				this.title = json.getString("title");
			if(json.has("value"))
				this.value = json.getString("value");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
