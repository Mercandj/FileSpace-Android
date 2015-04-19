package mercandalli.com.jarvis.action;

import android.util.Log;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterMain extends Interpreter {

    private Interpreter interpreterActionEquals,
            interpreterActionContains,
            interpreterRoboticsEquals,
            interpreterDialogEquals;

    public InterpreterMain(Application app) {
        super(app);
        interpreterActionEquals = new InterpreterActionEquals(app);
        interpreterActionContains = new InterpreterActionContains(app);
        interpreterRoboticsEquals = new InterpreterRoboticsEquals(app);
        interpreterDialogEquals = new InterpreterDialogEquals(app);
    }

    @Override
    public String interpret(String input) {
        Log.d("InterpreterMain", "input : "+input);

        input = input.toLowerCase();

        String outputActionEquals = interpreterActionEquals.interpret(input);
        if(outputActionEquals != null)
            return outputActionEquals;

        String outputRoboticsEquals = interpreterRoboticsEquals.interpret(input);
        if(outputRoboticsEquals != null)
            return outputRoboticsEquals;

        String outputDialogEquals = interpreterDialogEquals.interpret(input);
        if(outputDialogEquals != null)
            return outputDialogEquals;

        String outputActionContains = interpreterActionContains.interpret(input);
        if(outputActionContains != null)
            return outputActionContains;

        return input;
    }

}
