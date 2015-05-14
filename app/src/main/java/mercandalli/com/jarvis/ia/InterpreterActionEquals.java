package mercandalli.com.jarvis.ia;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.ia.action.ENUM_Action;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterActionEquals extends Interpreter {

    public InterpreterActionEquals(Application app, Resource res) {
        super(app, res);
    }

    @Override
    public InterpreterResult interpret(String input) {
        String output = null;

        if(input.equals("recherche") || input.equals("recherche google") || input.equals("google") || input.equals("ouvre google"))
            output = ENUM_Action.WEB_SEARCH.action.action(this.app, "http://www.google.com/");

        return new InterpreterResult(output);
    }

}
