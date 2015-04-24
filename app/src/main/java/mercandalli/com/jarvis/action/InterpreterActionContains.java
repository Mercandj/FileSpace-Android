package mercandalli.com.jarvis.action;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterActionContains extends Interpreter {

    public InterpreterActionContains(Application app, Resource res) {
        super(app, res);
    }

    @Override
    public String interpret(String input) {
        String output = null;

        if(input.contains("quit") || input.contains("kit l'app"))
            output = ENUM_Action.QUIT.action.action(this.app, "Bye.");

        return output;
    }

}
