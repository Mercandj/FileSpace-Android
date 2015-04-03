/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.SHA1;
import mercandalli.com.jarvis.activity.ActivityMain;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelUser;
import mercandalli.com.jarvis.net.TaskGet;

public class LoginFragment extends Fragment {

	private Application app;

    private boolean requestLaunch = false; // Block the second task if one launch

    EditText username, password;

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.app = (Application) activity;
    }

	public LoginFragment() {
		super();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inscription, container, false);
        this.username = (EditText) rootView.findViewById(R.id.username);
        this.password = (EditText) rootView.findViewById(R.id.password);

        if(this.app.getConfig().getUserUsername()!=null)
            if(!this.app.getConfig().getUserUsername().equals("")) {
                this.username.setText(this.app.getConfig().getUserUsername());
            }

        if(this.app.getConfig().getUserPassword()!=null)
            if(!this.app.getConfig().getUserPassword().equals("")) {
                this.password.setHint("•••••••••");
            }

        return rootView;
    }

    public void connectionSucceed() {
        Intent intent = new Intent(getActivity(), ActivityMain.class);
        this.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
        getActivity().finish();
    }

    public void clickSignIn() {
        if (requestLaunch)
            return;
        requestLaunch = true;

        ModelUser user = new ModelUser();

        if (!username.getText().toString().equals("")) {
            user.username = username.getText().toString();
            app.getConfig().setUserUsername(user.username);
        } else
            user.username = app.getConfig().getUserUsername();

        if (!password.getText().toString().equals("")) {
            user.password = SHA1.execute(password.getText().toString());
            app.getConfig().setUserPassword(user.password);
        } else
            user.password = app.getConfig().getUserPassword();

        if (app.getConfig().getUrlServer() == null) {
            requestLaunch = false;
            return;
        }
        if (app.getConfig().getUrlServer().equals("")) {
            requestLaunch = false;
            return;
        }

        // Login : POST /user
        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("login", "true"));
        (new TaskGet(app, app.getConfig().getUser(), app.getConfig().getUrlServer() + app.getConfig().routeUser, new IPostExecuteListener() {
            @Override
            public void execute(JSONObject json, String body) {
                try {
                    if (json != null) {
                        if (json.has("succeed"))
                            if (json.getBoolean("succeed")) {
                                connectionSucceed();
                            }
                        if (json.has("user")) {
                            JSONObject user = json.getJSONObject("user");
                            if (user.has("id"))
                                app.getConfig().setUserId(user.getInt("id"));
                        }
                    } else
                        Toast.makeText(app, app.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestLaunch = false;
            }
        })).execute();


    }
}
