package mercandalli.com.jarvis.ia.action;

import mercandalli.com.jarvis.activity.Application;

public interface Action {
	public abstract String action(Application app, String input);
}
