package mercandalli.com.jarvis.action;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 */
public abstract class Interpreter {

    protected Application app;
    protected Resource res;

    public Interpreter(Application app) {
        this.app = app;
        this.res = new Resource(this.app, "jarvis_resources_fr.json");
    }

    public Interpreter(Application app, Resource res) {
        this.app = app;
        this.res = res;
    }

    public abstract String interpret(String input);

}
