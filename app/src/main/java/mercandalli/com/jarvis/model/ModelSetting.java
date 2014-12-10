/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

import android.widget.CompoundButton.OnCheckedChangeListener;

import mercandalli.com.jarvis.Application;

public class ModelSetting extends Model {

	public OnCheckedChangeListener toggleButtonListener = null;
	public boolean toggleButtonInitValue = false;	
	public String title;	
	
	public ModelSetting(Application app, String title) {
		super(app);
		this.title = title;
	}
	
	public ModelSetting(Application app, String title, int viewType) {
		super(app);
		this.title = title;
		this.viewType = viewType;
	}
	
	public ModelSetting(Application app, String title, OnCheckedChangeListener toggleButtonListener, boolean toggleButtonInitValue) {
		super(app);
		this.title = title;
		this.toggleButtonListener = toggleButtonListener;
		this.toggleButtonInitValue = toggleButtonInitValue;
	}	
}
