package mercandalli.com.jarvis.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.ToggleButton;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.net.TaskGet;
import mercandalli.com.jarvis.net.TaskPost;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class RoboticsFragment extends Fragment {

    private Application app;
    private View rootView;
    private ToggleButton buttonLED;
    private ProgressBar circularProgressBar;
    private EditText output, id, value;
    Switch order;

    public RoboticsFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_robotics, container, false);

        this.circularProgressBar = (ProgressBar) this.rootView.findViewById(R.id.circularProgressBar);
        this.circularProgressBar.setVisibility(View.VISIBLE);

        this.buttonLED = (ToggleButton) this.rootView.findViewById(R.id.toggleButtonLED);
        this.buttonLED.setVisibility(View.INVISIBLE);
        this.buttonLED.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
                parameters.add(new BasicNameValuePair("value", (isChecked) ? "1" : "0"));
                new TaskPost(
                        RoboticsFragment.this.app,
                        RoboticsFragment.this.app.getConfig().getUrlServer() + RoboticsFragment.this.app.getConfig().routeRobotics + "/18",
                        null,
                        parameters
                ).execute();
            }
        });

        this.output = (EditText) this.rootView.findViewById(R.id.output);
        this.id = (EditText) this.rootView.findViewById(R.id.id);
        this.value = (EditText) this.rootView.findViewById(R.id.value);
        this.order = (Switch) this.rootView.findViewById(R.id.order);

        this.order.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    value.setText("");
            }
        });

        if(this.app.isInternetConnection())
            new TaskGet(
                    this.app,
                    this.app.getConfig().getUser(),
                    this.app.getConfig().getUrlServer() + RoboticsFragment.this.app.getConfig().routeRobotics + "/18",
                    new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            try {
                                if (json.has("result")) {
                                    JSONArray result = json.getJSONArray("result");
                                    if(result != null )
                                        if (result.getJSONObject(0).has("value")) {
                                            JSONObject value = new JSONObject(result.getJSONObject(0).getString("value"));
                                            if (value.has("value")) {
                                                RoboticsFragment.this.buttonLED.setChecked(value.getInt("value") == 1);

                                                RoboticsFragment.this.buttonLED.setVisibility(View.VISIBLE);
                                                RoboticsFragment.this.circularProgressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    null
            ).execute();

        ((Button) this.rootView.findViewById(R.id.launch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(app.isInternetConnection()) {
                    List<BasicNameValuePair> parameters = new ArrayList<>();
                    String id_ = id.getText().toString();
                    String value_ = value.getText().toString();
                    String order_ = order.isChecked() ? "ordre_id" : "mesure_id";
                    if(id_!=null)
                        if(!id_.equals(""))
                            parameters.add(new BasicNameValuePair(order_, ""+id_));
                    if(value_!=null)
                        if(!value_.equals(""))
                            parameters.add(new BasicNameValuePair("value", ""+value_));
                    new TaskGet(
                            app,
                            app.getConfig().getUser(),
                            app.getConfig().getUrlServer() + RoboticsFragment.this.app.getConfig().routeRobotics,
                            new IPostExecuteListener() {
                                @Override
                                public void execute(JSONObject json, String body) {
                                    output.setText(""+body);
                                }
                            },
                            parameters
                    ).execute();
                }
            }
        });

        return rootView;
    }

    @Override
    public boolean back() {
        return false;
    }
}
