package mercandalli.com.jarvis.action;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterMain extends Interpreter {

    private Interpreter interpreterActionEquals,
            interpreterActionContains;

    public InterpreterMain(Application app) {
        super(app);
        interpreterActionEquals = new InterpreterActionEquals(app);
        interpreterActionContains = new InterpreterActionContains(app);
    }

    @Override
    public String interpret(String input) {
        String output = "";

        String outputActionEquals = interpreterActionEquals.interpret(input);
        if(outputActionEquals != null)
            return outputActionEquals;

        String outputActionContains = interpreterActionContains.interpret(input);
        if(outputActionContains != null)
            return outputActionContains;

        output = input;

        return output;
    }

}
