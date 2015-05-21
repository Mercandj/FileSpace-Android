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

    @Override
    public boolean equals(Object o) {
        if(o==null)
            return false;
        if(!(o instanceof ModelForm))
            return false;
        ModelForm obj = (ModelForm)o;

        if((this.input1EditText != null && obj.input1EditText == null) || (this.input1EditText == null && obj.input1EditText != null))
            return false;
        if((this.input2EditText != null && obj.input2EditText == null) || (this.input2EditText == null && obj.input2EditText != null))
            return false;
        if((this.input3EditText != null && obj.input3EditText == null) || (this.input3EditText == null && obj.input3EditText != null))
            return false;
        if((this.input1Text != null && obj.input1Text == null) || (this.input1Text == null && obj.input1Text != null))
            return false;
        if((this.input2Text != null && obj.input2Text == null) || (this.input2Text == null && obj.input2Text != null))
            return false;
        if((this.input3Text != null && obj.input3Text == null) || (this.input3Text == null && obj.input3Text != null))
            return false;

        if(this.input1EditText != null &&
                this.input2EditText != null &&
                this.input3EditText != null &&
                this.input1Text != null &&
                this.input2Text != null &&
                this.input3Text != null)
            return this.input1EditText.equals(obj.input1EditText) &&
                    this.input2EditText.equals(obj.input2EditText) &&
                    this.input3EditText.equals(obj.input3EditText) &&
                    this.input1Text.equals(obj.input1Text) &&
                    this.input2Text.equals(obj.input2Text) &&
                    this.input3Text.equals(obj.input3Text);

        if(this.input1EditText != null &&
                this.input2EditText != null &&
                this.input1Text != null &&
                this.input2Text != null)
            return this.input1EditText.equals(obj.input1EditText) &&
                    this.input2EditText.equals(obj.input2EditText) &&
                    this.input1Text.equals(obj.input1Text) &&
                    this.input2Text.equals(obj.input2Text);

        if(this.input1EditText != null &&
                this.input1Text != null)
            return this.input1EditText.equals(obj.input1EditText) &&
                    this.input1Text.equals(obj.input1Text);

        if(this.input1Text != null)
            return this.input1Text.equals(obj.input1Text);

        return false;
    }
}
