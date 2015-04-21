package mercandalli.com.jarvis.action;

import java.util.List;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 * Just funny responses
 */
public class InterpreterDialogEquals extends Interpreter {

    public InterpreterDialogEquals(Application app, List<QA> qas) {
        super(app, qas);
    }

    @Override
    public String interpret(String input) {
        String output = null;
        for(QA qa:this.qas) {
            String answer = qa.getAnswer(input);
            if(answer != null)
                return answer;
        }
        return output;
    }

}
