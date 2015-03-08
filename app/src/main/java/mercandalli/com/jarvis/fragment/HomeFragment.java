package mercandalli.com.jarvis.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.net.TaskPost;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class HomeFragment extends Fragment {

    private Application app;
    private View rootView;

    public HomeFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dev, container, false);
        ToggleButton buttonLED = (ToggleButton) rootView.findViewById(R.id.toggleButtonLED);
        buttonLED.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List < BasicNameValuePair > parameters = new ArrayList< BasicNameValuePair >();
                parameters.add(new BasicNameValuePair("value", (isChecked) ? "1" : "0"));
                new TaskPost(
                        HomeFragment.this.app,
                        HomeFragment.this.app.getConfig().getUrlServer() + HomeFragment.this.app.getConfig().routeHome,
                        null,
                        parameters
                ).execute();
            }
        });
        return rootView;
    }
}
