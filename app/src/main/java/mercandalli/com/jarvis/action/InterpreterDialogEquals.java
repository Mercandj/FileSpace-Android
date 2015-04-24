package mercandalli.com.jarvis.action;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 * Just funny responses
 */
public class InterpreterDialogEquals extends Interpreter {

    public InterpreterDialogEquals(Application app, Resource res) {
        super(app, res);
    }

    @Override
    public String interpret(String input) {
        String output = null;
        for(QA qa:this.res.getQas()) {
            String answer = qa.getAnswer(input);
            if(answer != null)
                return answer;
        }
        return output;
    }

}
