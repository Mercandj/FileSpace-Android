/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.config.Const;

public class ModelHome extends Model {

	public String title1, title2;
	public int viewType = Const.TAB_VIEW_TYPE_NORMAL;

    public View.OnClickListener listener1, listener2;

	public ModelHome() {
		super();
	}

	public ModelHome(String title1, View.OnClickListener listener1, String title2, View.OnClickListener listener2, int viewType) {
		super();
		this.title1 = title1;
        this.listener1 = listener1;
        this.title2 = title2;
        this.listener2 = listener2;
        this.viewType = viewType;
	}

	public ModelHome(String title1, int viewType) {
		super();
		this.title1 = title1;
		this.viewType = viewType;
	}

	public ModelHome(Application app, JSONObject json) {
		super();
		this.app = app;
		try {
			if(json.has("title1"))
				this.title1 = json.getString("title1");
			if(json.has("title2"))
				this.title2 = json.getString("title2");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
