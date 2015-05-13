package mercandalli.com.jarvis.model;

import org.json.JSONObject;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.config.Const;

public abstract class Model {
	
	protected Application app;
	public int viewType = Const.TAB_VIEW_TYPE_NORMAL;
	
	public Model(Application app) {
		this.app = app;
	}
    public Model() {  }

	public abstract JSONObject toJSONObject();

}
