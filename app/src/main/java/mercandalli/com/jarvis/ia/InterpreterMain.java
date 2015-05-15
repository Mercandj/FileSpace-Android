package mercandalli.com.jarvis.ia;

import android.util.Log;

import mercandalli.com.jarvis.ui.activity.Application;

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

        input = normalisationText(input);

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

    public String normalisationText(String messageOrig) {
        String message = messageOrig.toLowerCase();

        if (message.contains("é"))			message = message.replaceAll("é", "e");
        if (message.contains("è"))			message = message.replaceAll("è", "e");
        if (message.contains("ê"))			message = message.replaceAll("ê", "e");
        if (message.contains("ë"))			message = message.replaceAll("ë", "e");

        if (message.contains("à"))			message = message.replaceAll("à", "a");
        if (message.contains("â"))			message = message.replaceAll("â", "a");

        if (message.contains("ù"))			message = message.replaceAll("ù", "u");
        if (message.contains("û"))			message = message.replaceAll("û", "u");

        if (message.contains("ï"))			message = message.replaceAll("ï", "i");
        if (message.contains("ô"))			message = message.replaceAll("ô", "o");
        if (message.contains("ç"))			message = message.replaceAll("ç", "c");

        return message;
    }

}
