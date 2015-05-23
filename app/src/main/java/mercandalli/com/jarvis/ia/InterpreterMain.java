package mercandalli.com.jarvis.ia;

import android.util.Log;

import mercandalli.com.jarvis.ui.activity.Application;

import static mercandalli.com.jarvis.util.StringUtils.nomalizeString;

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
        interpreterActionEquals = new InterpreterActionEquals(app, this.res);
        interpreterActionContains = new InterpreterActionContains(app, this.res);
        interpreterRoboticsEquals = new InterpreterRoboticsEquals(app, this.res);
        interpreterDialogEquals = new InterpreterDialogEquals(app, this.res);
    }

    @Override
    public InterpreterResult interpret(String input) {
        Log.d("InterpreterMain", "input : "+input);

        input = nomalizeString(input);

        InterpreterResult outputActionEquals = interpreterActionEquals.interpret(input);
        if(outputActionEquals != null)
            if(!outputActionEquals.isEmpty())
                return outputActionEquals;

        if(this.app.getConfig().getUser().isAdmin()) {
            InterpreterResult outputRoboticsEquals = interpreterRoboticsEquals.interpret(input);
            if (outputRoboticsEquals != null)
                if(!outputRoboticsEquals.isEmpty())
                    return outputRoboticsEquals;
        }

        InterpreterResult outputDialogEquals = interpreterDialogEquals.interpret(input);
        if(outputDialogEquals != null)
            if(!outputDialogEquals.isEmpty())
                return outputDialogEquals;

        InterpreterResult outputActionContains = interpreterActionContains.interpret(input);
        if(outputActionContains != null)
            if(!outputActionContains.isEmpty())
                return outputActionContains;

        return new InterpreterResult(input);
    }

}
