package mercandalli.com.jarvis.model;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.config.Const;

public class Model {
	
	protected Application app;
	public int viewType = Const.TAB_VIEW_TYPE_NORMAL;
	
	public Model(Application app) {
		this.app = app;
	}

}
