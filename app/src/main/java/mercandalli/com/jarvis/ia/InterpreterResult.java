package mercandalli.com.jarvis.ia;

import mercandalli.com.jarvis.model.ModelForm;

/**
 * Created by Jonathan on 14/05/2015.
 */
public class InterpreterResult {
    public String content;
    public ModelForm modelForm;

    public InterpreterResult() {}
    public InterpreterResult(String content) {
        this.content = content;
    }
    public InterpreterResult(InterpreterResult o) {
        this.content = o.content;
    }

    public boolean isEmpty() {
        return this.content == null && this.modelForm == null;
    }
}
