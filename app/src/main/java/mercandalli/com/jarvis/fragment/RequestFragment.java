/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONObject;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.dialog.DialogRequest;
import mercandalli.com.jarvis.listener.IPostExecuteListener;


public class RequestFragment extends Fragment {

	private Application app;
	private View rootView;

	public RequestFragment(Application app) {
		this.app = app;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_request, container, false);

        Animation animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);
        ((ImageButton) rootView.findViewById(R.id.circle)).startAnimation(animOpen);

		((ImageButton) rootView.findViewById(R.id.circle)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.dialog = new DialogRequest(app, new IPostExecuteListener() {
					@Override
					public void execute(JSONObject json, String body) {
						if(json!=null)
							((EditText) rootView.findViewById(R.id.console)).setText(((EditText) rootView.findViewById(R.id.console)).getText().toString()+"JSON : "+json+"\n\n");
						else
							((EditText) rootView.findViewById(R.id.console)).setText(((EditText) rootView.findViewById(R.id.console)).getText().toString()+"BODY : "+body+"\n\n");
					}
				});
			}
		});
		
        return rootView;
	}
	
	public void deleteConsole() {
		((EditText) rootView.findViewById(R.id.console)).setText("");
	}

    @Override
    public boolean back() {
        return false;
    }
}
