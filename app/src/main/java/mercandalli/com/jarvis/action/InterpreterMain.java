package mercandalli.com.jarvis.action;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterMain extends Interpreter {

    private InterpreterAction interpreterAction;

    public InterpreterMain(Application app) {
        super(app);
        interpreterAction = new InterpreterAction(app);
    }

    @Override
    public String interpret(String input) {
        String output = "";

        String outputAction = interpreterAction.interpret(input);
        if(outputAction != null)
            return outputAction;

        output = input;

        return output;
    }

}
