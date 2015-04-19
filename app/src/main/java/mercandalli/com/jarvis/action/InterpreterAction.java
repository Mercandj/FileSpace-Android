package mercandalli.com.jarvis.action;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterAction extends Interpreter {

    public InterpreterAction(Application app) {
        super(app);
    }

    @Override
    public String interpret(String input) {
        String output = null;

        if(input.contains("quit"))
            output = ENUM_Action.QUIT.action.action(this.app) + "Bye.";

        return output;
    }

}
