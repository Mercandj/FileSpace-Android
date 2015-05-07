/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.navdrawer;

import mercandalli.com.jarvis.listener.IListener;

/**
 * Sliding Menu stuff
 * @author Jonathan
 *
 */
public class NavDrawerItem {
	
	public String title;
	public String subtitle;
	public int icon, icon_pressed = -1;
	public int viewType;
	public boolean containsImage;
	public boolean isSelected = false;
	public IListener listenerClick = null;
	
	public boolean initChecked = false;
	//public OnCheckedChangeListener onCheckedChangeListener = null;
	
	public NavDrawerItem(String title, int icon, int viewType) {
		super();
		this.title = title;
		this.icon = icon;
		this.viewType = viewType;
		this.containsImage = true;
	}
	
	public NavDrawerItem(String title, String subtitle, int icon, int viewType) {
		super();
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
		this.viewType = viewType;
		this.containsImage = true;
	}

	public NavDrawerItem(String title, String subtitle, IListener listenerClick, int icon, int viewType) {
		super();
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
		this.viewType = viewType;
		this.containsImage = true;
		this.listenerClick = listenerClick;
	}
	
	public NavDrawerItem(String title, IListener listenerClick, int icon, int viewType) {
		super();
		this.title = title;
		this.icon = icon;
		this.viewType = viewType;
		this.containsImage = true;
		this.listenerClick = listenerClick;
	}

	public NavDrawerItem(String title, IListener listenerClick, int icon, int icon_pressed, int viewType) {
		super();
		this.title = title;
		this.icon = icon;
		this.icon_pressed = icon_pressed;
		this.viewType = viewType;
		this.containsImage = true;
		this.listenerClick = listenerClick;
	}

	public NavDrawerItem(String title, int viewType) {
		super();
		this.title = title;
		this.viewType = viewType;
		this.containsImage = false;
	}
	
	public NavDrawerItem(String title, IListener listenerClick, int SLIDING_MENU_TAB) {
		super();
		this.title = title;
		this.viewType = SLIDING_MENU_TAB;
		this.containsImage = false;
		this.listenerClick = listenerClick;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof NavDrawerItem)) return false;
		return this.title.equals(((NavDrawerItem)o).title) && this.icon==((NavDrawerItem)o).icon;
	}
}
