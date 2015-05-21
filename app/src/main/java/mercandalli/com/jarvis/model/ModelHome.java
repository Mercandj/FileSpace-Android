/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.listener.IModelHomeListener;

public class ModelHome extends Model {

	private CharSequence title1, title2;
	public int id, viewType = Const.TAB_VIEW_TYPE_NORMAL;
    public ModelServerMessage serverMessage;
    public ModelForm modelForm;

    public View.OnClickListener listener1, listener2;
    public IModelHomeListener listenerHome1;

	public ModelHome() {
		super();
	}

	public ModelHome(int id, String title1, View.OnClickListener listener1, String title2, View.OnClickListener listener2, int viewType) {
		super();
		this.id = id;
		this.title1 = title1;
        this.listener1 = listener1;
        this.title2 = title2;
        this.listener2 = listener2;
        this.viewType = viewType;
    }

    public ModelHome(int id, String title1, int viewType) {
        super();
		this.id = id;
		this.title1 = title1;
		this.viewType = viewType;
	}

    public ModelHome(int id, String title1, String title2, int viewType) {
        super();
		this.id = id;
        this.title1 = title1;
        this.title2 = title2;
        this.viewType = viewType;
    }

    public ModelHome(int id, String title1, IModelHomeListener listenerHome1, CharSequence title2, int viewType) {
        super();
        this.id = id;
        this.listenerHome1 = listenerHome1;
        this.title1 = title1;
        this.title2 = title2;
        this.viewType = viewType;
    }

    public ModelHome(int id, String title1, IModelHomeListener listenerHome1, ModelForm modelForm, int viewType) {
        super();
        this.id = id;
        this.listenerHome1 = listenerHome1;
        this.title1 = title1;
        this.modelForm = modelForm;
        this.viewType = viewType;
    }

    public ModelHome(int id, String title1, IModelHomeListener listenerHome1, ModelServerMessage serverMessage, int viewType) {
        super();
        this.id = id;
        this.listenerHome1 = listenerHome1;
        this.title1 = title1;
        this.serverMessage = serverMessage;
        this.viewType = viewType;
    }

	public ModelHome(Application app, JSONObject json) {
		super();
		this.app = app;
		try {
			if(json.has("title1"))
				this.title1 = json.getString("title1");
			if(json.has("title2"))
				this.title2 = json.getString("title2");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

    @Override
    public boolean equals(Object o) {
        if(o==null)
            return false;
        if(!(o instanceof ModelHome))
            return false;
        ModelHome obj = (ModelHome)o;
        if((this.serverMessage != null && obj.serverMessage == null) || (this.serverMessage == null && obj.serverMessage != null))
            return false;
        if((this.modelForm != null && obj.modelForm == null) || (this.modelForm == null && obj.modelForm != null))
            return false;
        if((this.title1 != null && obj.title1 == null) || (this.title1 == null && obj.title1 != null))
            return false;
        if((this.title2 != null && obj.title2 == null) || (this.title2 == null && obj.title2 != null))
            return false;
        if(this.modelForm != null && this.title1 != null && this.title2 != null)
            return this.id == obj.id && this.modelForm.equals(obj.modelForm) && this.title1.equals(obj.title1) && this.title2.equals(obj.title2);
        if(this.title1 != null && this.serverMessage != null)
            return this.id == obj.id && this.title1.equals(obj.title1) && this.serverMessage.equals(obj.serverMessage);
        if(this.title1 != null && this.modelForm != null)
            return this.id == obj.id && this.title1.equals(obj.title1) && this.modelForm.equals(obj.modelForm);
        if(this.title1 != null && this.title2 != null)
            return this.id == obj.id && this.title1.equals(obj.title1) && this.title2.equals(obj.title2);
        if(this.title1 != null)
            return this.id == obj.id && this.title1.equals(obj.title1);
        if(this.title2 != null)
            return this.id == obj.id && this.title2.equals(obj.title2);
        return this.id == obj.id;
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }

    public CharSequence getTitle1() {
        return title1;
    }

    public CharSequence getTitle2() {
        if(serverMessage!=null)
            return serverMessage.getContent();
        return title2;
    }
}
