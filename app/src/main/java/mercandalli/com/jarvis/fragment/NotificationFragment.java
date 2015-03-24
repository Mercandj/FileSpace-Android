package mercandalli.com.jarvis.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.net.TaskPost;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class NotificationFragment extends Fragment {

    private Application app;
    private View rootView;
    private EditText message, user_id;

    public NotificationFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        this.message = (EditText) this.rootView.findViewById(R.id.message);
        this.user_id = (EditText) this.rootView.findViewById(R.id.user_id);

        ((Button) this.rootView.findViewById(R.id.send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = NotificationFragment.this.app.getConfig().getUrlServer() + NotificationFragment.this.app.getConfig().routeNotification + "/" + NotificationFragment.this.user_id.getText().toString();
                List < BasicNameValuePair > parameters = new ArrayList<>();
                parameters.add(new BasicNameValuePair("message", NotificationFragment.this.message.getText().toString()));

                new TaskPost(NotificationFragment.this.app, url, new IPostExecuteListener() {
                    @Override
                    public void execute(JSONObject json, String body) {

                    }
                }, parameters).execute();

            }
        });

        return rootView;
    }
}
