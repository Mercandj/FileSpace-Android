/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

import org.json.JSONObject;

import mercandalli.com.jarvis.listener.IModelFormListener;

public class ModelForm extends Model {

	public String input1EditText, input2EditText, input3EditText, input1Text, input2Text, input3Text;
    public IModelFormListener sendListener;

	public ModelForm() {
		super();
	}

    public void send() {
        if(this.sendListener != null)
            this.sendListener.execute(this);
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}
