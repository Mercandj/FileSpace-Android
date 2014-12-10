package mercandalli.com.jarvis_android.model;

import mercandalli.com.jarvis_android.Application;
import mercandalli.com.jarvis_android.config.Const;

public class Model {
	
	protected Application app;
	public int viewType = Const.TAB_VIEW_TYPE_NORMAL;
	
	public Model(Application app) {
		this.app = app;
	}

}
