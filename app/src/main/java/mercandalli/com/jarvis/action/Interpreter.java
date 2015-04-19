package mercandalli.com.jarvis.action;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 */
public abstract class Interpreter {

    protected Application app;

    public Interpreter(Application app) {
        this.app = app;
    }

    public abstract String interpret(String input);

}
