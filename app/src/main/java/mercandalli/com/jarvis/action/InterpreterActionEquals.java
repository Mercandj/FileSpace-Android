package mercandalli.com.jarvis.action;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterActionEquals extends Interpreter {

    public InterpreterActionEquals(Application app) {
        super(app);
    }

    @Override
    public String interpret(String input) {
        String output = null;

        if(input.equals("recherche google") || input.equals("google") || input.equals("ouvre google"))
            output = ENUM_Action.WEB_SEARCH.action.action(this.app, "http://www.google.com/");

        return output;
    }

}
